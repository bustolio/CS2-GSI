package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Clock;
import java.time.Instant;
import java.time.InstantSource;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Game State Listener for Counter-Strike 2 Game State Integration events.
 * <p>
 * Once started, it continuously listens for HTTP POST requests made by the game
 * on a specific address and port. When a request is received, the JSON data is
 * parsed into a {@link GameState} object and offered to your application through
 * the {@link #onNewGameState(Consumer)} listeners. More granular game events can
 * be subscribed to via {@link #subscribe(Class, Consumer)}.
 * <p>
 * All handlers run on one daemon thread named {@code CS2GSI-GameStateListener}, in the order the
 * game sent its updates. A handler that blocks delays every later update, and the game gives up on
 * a request after the timeout in its configuration file (5 seconds in the generated one). Hand slow
 * work to another thread, and switch to the UI thread yourself before touching JavaFX or Swing.
 * Because the thread is a daemon, a running listener does not keep the JVM alive.
 * <p>
 * Only POST requests of up to 4 MiB are accepted, and requests that carry an {@code Origin} header
 * are refused, so a web page open in a browser on the same machine cannot feed the listener.
 */
public class GameStateListener extends CS2EventsInterface implements AutoCloseable {
    // The host must not contain a quote, it ends up inside a quoted value of the configuration file.
    private static final Pattern URI_PATTERN =
            Pattern.compile("^https?://([^\\s\"/]+):([0-9]+)/$", Pattern.CASE_INSENSITIVE);

    // Real payloads are tens of kilobytes.
    private static final int MAX_BODY_BYTES = 4 * 1024 * 1024;

    private final Object gamestateLock = new Object();

    private volatile boolean running = false;
    private volatile int boundPort;
    private volatile String authToken;
    private volatile Instant lastGameStateTime;
    private final InstantSource clock;
    private final int port;
    private final String uri;
    private final String host;
    private HttpServer httpServer;
    private ExecutorService executor;
    private GameState previousGameState = new GameState();
    private GameState currentGameState = new GameState();

    private final List<Consumer<GameState>> newGameStateListeners = new CopyOnWriteArrayList<>();

    // Dispatcher for game events.
    private final EventDispatcher<CS2GameEvent> dispatcher = new EventDispatcher<>(CS2GameEvent.class);

    // Game State and custom handlers subscribe themselves to the dispatcher and
    // are kept alive by those subscriptions; no field references are needed.
    {
        new ProviderHandler(dispatcher);
        new MapHandler(dispatcher);
        new RoundHandler(dispatcher);
        new PlayerHandler(dispatcher);
        new PhaseCountdownsHandler(dispatcher);
        new AllPlayersHandler(dispatcher);
        new AllGrenadesHandler(dispatcher);
        new BombHandler(dispatcher);
        new KillfeedHandler(dispatcher);
    }

    // Overall GameState handler.
    private final GameStateHandler gameStateHandler = new GameStateHandler(dispatcher);

    /**
     * A GameStateListener that listens for connections on http://localhost:{port}/.
     *
     * @param port The port to listen on.
     */
    public GameStateListener(int port) {
        this(port, Clock.systemUTC());
    }

    /**
     * A GameStateListener that listens for connections on http://localhost:{port}/
     * and reads the time for {@link #getLastGameStateTime()} from the given clock.
     *
     * @param port  The port to listen on.
     * @param clock The source of the current time, e.g. a fixed one in a test.
     */
    public GameStateListener(int port, InstantSource clock) {
        this.port = port;
        this.host = "localhost";
        this.uri = "http://localhost:" + port + "/";
        this.clock = clock;

        dispatcher.onGameEvent(this::onNewGameEvent);
    }

    /**
     * A GameStateListener that listens for connections to the specified URI.
     *
     * @param uri The URI to listen to.
     */
    public GameStateListener(String uri) {
        this(uri, Clock.systemUTC());
    }

    /**
     * A GameStateListener that listens for connections to the specified URI
     * and reads the time for {@link #getLastGameStateTime()} from the given clock.
     *
     * @param uri   The URI to listen to.
     * @param clock The source of the current time, e.g. a fixed one in a test.
     */
    public GameStateListener(String uri, InstantSource clock) {
        if (!uri.endsWith("/")) {
            uri += "/";
        }

        Matcher matcher = URI_PATTERN.matcher(uri);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Not a valid URI: " + uri);
        }

        this.host = matcher.group(1);
        this.port = Integer.parseInt(matcher.group(2));
        this.uri = uri;
        this.clock = clock;

        dispatcher.onGameEvent(this::onNewGameEvent);
    }

    /**
     * The previous game state.
     */
    public GameState getPreviousGameState() {
        synchronized (gamestateLock) {
            return previousGameState;
        }
    }

    /**
     * The current game state.
     */
    public GameState getCurrentGameState() {
        synchronized (gamestateLock) {
            return currentGameState;
        }
    }

    /**
     * When the game last sent a game state that the listener accepted.<br>
     * A heartbeat that repeats the previous state counts as well, although it raises no event. With
     * the generated configuration file the game sends at least every 10 seconds, so a much older
     * time means the game stopped sending. Refused and malformed requests do not count.
     *
     * @return The time of the last accepted game state, empty before the first one.
     */
    public Optional<Instant> getLastGameStateTime() {
        return Optional.ofNullable(lastGameStateTime);
    }

    /**
     * Gets the port that is being listened.<br>
     * While the listener runs this is the bound port, which differs from the requested one for port 0.
     */
    public int getPort() {
        return running ? boundPort : port;
    }

    /**
     * Gets the URI that is being listened.
     */
    public String getUri() {
        return uri;
    }

    /**
     * Returns whether or not the listener is running.
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Registers a handler for newly received game states.
     * Guaranteed to fire before more granular game events.
     */
    public void onNewGameState(Consumer<GameState> handler) {
        newGameStateListeners.add(handler);
    }

    /**
     * Removes a previously registered game state handler.
     */
    public void offNewGameState(Consumer<GameState> handler) {
        newGameStateListeners.remove(handler);
    }

    /**
     * Sets a token the game has to send with every game state.<br>
     * Call this before {@link #installGSIConfigFile(String)}, which writes the token into the
     * configuration file. From then on the listener answers 401 to game states without the token
     * and does not hand them to any handler, so other programs on the machine cannot feed it.
     *
     * @param authToken The token, or null to accept every game state. Use letters and digits.
     */
    public void setAuthToken(String authToken) {
        this.authToken = (authToken == null || authToken.isEmpty()) ? null : authToken;
    }

    /**
     * Attempts to create a Game State Integration configuration file.<br>
     * Writes only if the file is missing or its content differs.
     *
     * @param name The name of your integration.
     * @return Returns true if the file was written or already had the expected content, false otherwise.
     * @deprecated Use {@link #installGSIConfigFile(String)}, which also tells whether the game needs
     *             a restart and why an installation failed.
     */
    @Deprecated
    public boolean generateGSIConfigFile(String name) {
        return installGSIConfigFile(name).status() != GSIConfigResult.Status.FAILED;
    }

    /**
     * Installs a Game State Integration configuration file and reports whether it changed.<br>
     * Counter-Strike 2 only reads the file at startup, so a created or updated file needs a game restart.
     *
     * @param name The name of your integration.
     * @return Returns what happened to the file and where it is.
     */
    public GSIConfigResult installGSIConfigFile(String name) {
        return CS2GSIFile.installFile(name, uri, authToken);
    }

    /**
     * Starts listening for GameState requests.
     *
     * @return Returns true on success. Returns false if the listener already runs or the address
     *         could not be bound, which usually means another program uses the port.
     */
    public synchronized boolean start() {
        if (running) {
            return false;
        }

        try {
            InetSocketAddress address;

            if (host.equalsIgnoreCase("localhost") || host.equals("127.0.0.1")) {
                address = new InetSocketAddress(InetAddress.getLoopbackAddress(), port);
            } else if (host.equals("*") || host.equals("+") || host.equals("0.0.0.0")) {
                address = new InetSocketAddress(port);
            } else {
                address = new InetSocketAddress(host, port);
            }

            httpServer = HttpServer.create(address, 0);
        } catch (IOException e) {
            return false;
        }

        // A single-threaded executor preserves the order of game state updates.
        executor = Executors.newSingleThreadExecutor(runnable -> {
            Thread thread = new Thread(runnable, "CS2GSI-GameStateListener");
            thread.setDaemon(true);
            return thread;
        });

        httpServer.createContext("/", this::receiveGameState);
        httpServer.setExecutor(executor);
        httpServer.start();
        boundPort = httpServer.getAddress().getPort();
        running = true;

        return true;
    }

    /**
     * Stops listening for GameState requests.
     */
    public synchronized void stop() {
        running = false;

        if (httpServer != null) {
            httpServer.stop(0);
            httpServer = null;
        }

        if (executor != null) {
            executor.shutdown();
            executor = null;
        }
    }

    private void receiveGameState(HttpExchange exchange) throws IOException {
        int rejection = rejectionStatus(exchange);
        byte[] body = new byte[0];

        if (rejection == 0) {
            try (InputStream inputStream = exchange.getRequestBody()) {
                body = inputStream.readNBytes(MAX_BODY_BYTES + 1);
            }

            if (body.length > MAX_BODY_BYTES) {
                rejection = 413;
            }
        }

        GameState gameState = null;

        if (rejection == 0) {
            try {
                String jsonData = new String(body, StandardCharsets.UTF_8);
                gameState = new GameState(JsonParser.parseString(jsonData).getAsJsonObject());
            } catch (RuntimeException ignored) {
                // Malformed game state data, nothing to do here.
            }

            if (gameState != null && !hasExpectedToken(gameState)) {
                rejection = 401;
            }
        }

        boolean accepted = rejection == 0 && gameState != null;

        if (accepted) {
            // Here and not in setCurrentGameState, which drops a heartbeat that repeats the last state.
            lastGameStateTime = clock.instant();
        }

        exchange.sendResponseHeaders(rejection == 0 ? 200 : rejection, -1);
        exchange.close();

        if (accepted) {
            setCurrentGameState(gameState);
        }
    }

    private boolean hasExpectedToken(GameState gameState) {
        String expected = authToken;

        if (expected == null) {
            return true;
        }

        // Constant time, so response timing does not leak how much of a guessed token matched.
        String received = gameState.auth.getOrDefault("token", "");
        return MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8), received.getBytes(StandardCharsets.UTF_8));
    }

    private static int rejectionStatus(HttpExchange exchange) {
        if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
            return 405;
        }

        // Browsers add Origin to every cross-site POST, the game never sends it.
        if (exchange.getRequestHeaders().containsKey("Origin")) {
            return 403;
        }

        return 0;
    }

    private void setCurrentGameState(GameState value) {
        synchronized (gamestateLock) {
            if (currentGameState.equals(value)) {
                return;
            }

            previousGameState = currentGameState;
            currentGameState = value;
        }

        // Outside the lock, so a handler that waits for a thread calling getCurrentGameState() cannot deadlock.
        raiseOnNewGameState(value);
    }

    private void raiseOnNewGameState(GameState gameState) {
        for (Consumer<GameState> handler : newGameStateListeners) {
            deliver(handler, gameState);
        }

        gameStateHandler.onNewGameState(gameState);
    }

    /**
     * Stops the listener and frees up resources.
     */
    @Override
    public void close() {
        stop();
    }
}
