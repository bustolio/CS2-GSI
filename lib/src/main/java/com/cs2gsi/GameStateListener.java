package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
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
 */
public class GameStateListener extends CS2EventsInterface implements AutoCloseable {
    private static final Pattern URI_PATTERN =
            Pattern.compile("^https?://(.+):([0-9]+)/$", Pattern.CASE_INSENSITIVE);

    private final Object gamestateLock = new Object();

    private volatile boolean running = false;
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
        new AuthHandler(dispatcher);
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
        this.port = port;
        this.host = "localhost";
        this.uri = "http://localhost:" + port + "/";

        dispatcher.onGameEvent(this::onNewGameEvent);
    }

    /**
     * A GameStateListener that listens for connections to the specified URI.
     *
     * @param uri The URI to listen to.
     */
    public GameStateListener(String uri) {
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

        dispatcher.onGameEvent(this::onNewGameEvent);
    }

    /**
     * The previous game state.
     */
    public GameState getPreviousGameState() {
        return previousGameState;
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
     * Gets the port that is being listened.
     */
    public int getPort() {
        return port;
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
     * Attempts to create a Game State Integration configuration file.
     *
     * @param name The name of your integration.
     * @return Returns true on success, false otherwise.
     */
    public boolean generateGSIConfigFile(String name) {
        return CS2GSIFile.createFile(name, uri);
    }

    /**
     * Starts listening for GameState requests.
     *
     * @return Returns true on success, false otherwise.
     */
    public boolean start() {
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
        running = true;

        return true;
    }

    /**
     * Stops listening for GameState requests.
     */
    public void stop() {
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
        String jsonData;

        try (InputStream inputStream = exchange.getRequestBody()) {
            jsonData = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }

        exchange.sendResponseHeaders(200, -1);
        exchange.close();

        try {
            JsonObject parsedData = JsonParser.parseString(jsonData).getAsJsonObject();
            setCurrentGameState(new GameState(parsedData));
        } catch (RuntimeException ignored) {
            // Malformed game state data, nothing to do here.
        }
    }

    private void setCurrentGameState(GameState value) {
        synchronized (gamestateLock) {
            if (currentGameState.equals(value)) {
                return;
            }

            previousGameState = currentGameState;
            currentGameState = value;
            raiseOnNewGameState(currentGameState);
        }
    }

    private void raiseOnNewGameState(GameState gameState) {
        for (Consumer<GameState> handler : newGameStateListeners) {
            handler.accept(gameState);
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
