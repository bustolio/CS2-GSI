package com.cs2gsi.viewer;

import com.cs2gsi.CS2GSIFile;
import com.cs2gsi.GSIConfigResult;
import com.cs2gsi.GameStateListener;
import com.cs2gsi.events.CS2GameEvent;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Separator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JavaFX UI for live-viewing selected Counter-Strike 2 GSI events.
 * <p>
 * The tree on the left selects which event types are shown; the log on the
 * right displays matching events as they arrive from the game.
 */
public class EventViewerApp extends Application {
    private static final int DEFAULT_PORT = 4000;
    private static final int LOG_CAP = 2000;
    private static final int PENDING_CAP = 10_000;
    private static final int DRAIN_PER_FRAME = 500;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private final Set<String> enabledEvents = ConcurrentHashMap.newKeySet();
    private final LinkedBlockingQueue<String> pendingLines = new LinkedBlockingQueue<>(PENDING_CAP);
    private final ObservableList<String> logEntries = FXCollections.observableArrayList();
    private final AtomicLong receivedCount = new AtomicLong();
    private final AtomicLong shownCount = new AtomicLong();
    private final AtomicLong droppedCount = new AtomicLong();
    private final AtomicReference<String> latestGameStateJson = new AtomicReference<>("");
    private final Gson prettyGson = new GsonBuilder().setPrettyPrinting().create();

    private GameStateListener listener;
    private CheckBoxTreeItem<String> treeRoot;
    private ListView<String> logView;
    private TextArea rawView;
    private Tab rawTab;
    private String lastRenderedRaw = "";
    private CheckBox autoScroll;
    private Label statusLabel;
    private Label listenerLabel;
    private TextField portField;
    private Button startStopButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BorderPane root = new BorderPane();
        root.setTop(buildToolbar());
        root.setLeft(buildEventSelector());
        root.setCenter(buildLogPane());
        root.setBottom(buildStatusBar());

        AnimationTimer drainTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drainPendingLines();
                updateRawView();
                updateStatus();
            }
        };
        drainTimer.start();

        stage.setTitle("CS2 GSI Event Viewer");
        stage.setScene(new Scene(root, 1150, 720));
        stage.setOnCloseRequest(event -> stopListener());
        stage.show();
    }

    // --- UI construction ---------------------------------------------------

    private ToolBar buildToolbar() {
        portField = new TextField(String.valueOf(DEFAULT_PORT));
        portField.setPrefColumnCount(5);

        startStopButton = new Button("Start");
        startStopButton.setDefaultButton(true);
        startStopButton.setOnAction(event -> toggleListener());

        Button generateConfigButton = new Button("Generate GSI config");
        generateConfigButton.setOnAction(event -> generateConfig());

        Button clearButton = new Button("Clear log");
        clearButton.setOnAction(event -> {
            logEntries.clear();
            pendingLines.clear();
        });

        autoScroll = new CheckBox("Auto-scroll");
        autoScroll.setSelected(true);

        listenerLabel = new Label("Stopped");

        return new ToolBar(
                new Label("Port:"), portField,
                startStopButton,
                new Separator(Orientation.VERTICAL),
                generateConfigButton,
                new Separator(Orientation.VERTICAL),
                clearButton, autoScroll,
                new Separator(Orientation.VERTICAL),
                listenerLabel);
    }

    private VBox buildEventSelector() {
        treeRoot = new CheckBoxTreeItem<>("All events");
        treeRoot.setExpanded(true);

        for (var category : EventCatalog.CATEGORIES.entrySet()) {
            CheckBoxTreeItem<String> categoryItem = new CheckBoxTreeItem<>(category.getKey());

            for (String eventName : category.getValue()) {
                CheckBoxTreeItem<String> leaf = new CheckBoxTreeItem<>(eventName);
                leaf.selectedProperty().addListener((observable, wasSelected, isSelected) -> {
                    if (isSelected) {
                        enabledEvents.add(eventName);
                    } else {
                        enabledEvents.remove(eventName);
                    }
                });
                leaf.setSelected(!EventCatalog.NOISY.contains(eventName));
                categoryItem.getChildren().add(leaf);
            }

            treeRoot.getChildren().add(categoryItem);
        }

        TreeView<String> tree = new TreeView<>(treeRoot);
        tree.setCellFactory(CheckBoxTreeCell.forTreeView());
        tree.setShowRoot(true);
        VBox.setVgrow(tree, Priority.ALWAYS);

        Button selectAll = new Button("Select all");
        selectAll.setOnAction(event -> treeRoot.setSelected(true));

        Button selectNone = new Button("Select none");
        selectNone.setOnAction(event -> {
            treeRoot.setSelected(true);
            treeRoot.setSelected(false);
        });

        HBox buttons = new HBox(8, selectAll, selectNone);
        buttons.setPadding(new Insets(8));

        VBox box = new VBox(buttons, tree);
        box.setPrefWidth(320);
        return box;
    }

    private TabPane buildLogPane() {
        logView = new ListView<>(logEntries);
        logView.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");

        Tab logTab = new Tab("Event log", logView);
        logTab.setClosable(false);

        rawView = new TextArea();
        rawView.setEditable(false);
        rawView.setStyle("-fx-font-family: 'Consolas', 'Courier New', monospace; -fx-font-size: 12px;");
        rawView.setPromptText("The most recent raw game state JSON sent by the game appears here once the listener is running.");

        rawTab = new Tab("Latest game state", rawView);
        rawTab.setClosable(false);

        return new TabPane(logTab, rawTab);
    }

    private HBox buildStatusBar() {
        statusLabel = new Label("Received: 0  |  Shown: 0");

        HBox bar = new HBox(statusLabel);
        bar.setPadding(new Insets(6, 10, 6, 10));
        return bar;
    }

    // --- Listener control --------------------------------------------------

    private void toggleListener() {
        if (listener != null) {
            stopListener();
            return;
        }

        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            listenerLabel.setText("Invalid port");
            return;
        }

        GameStateListener gsl = new GameStateListener(port);
        gsl.onGameEvent(this::onGameEvent);
        gsl.onNewGameState(gameState -> {
            String raw = gameState.toString();
            try {
                raw = prettyGson.toJson(JsonParser.parseString(raw));
            } catch (RuntimeException ignored) {
                // Keep the compact form if pretty printing fails.
            }
            latestGameStateJson.set(raw);
        });

        if (!gsl.start()) {
            listenerLabel.setText("Failed to start on port " + port + " (in use?)");
            return;
        }

        listener = gsl;
        portField.setDisable(true);
        startStopButton.setText("Stop");
        listenerLabel.setText("Listening on " + gsl.getUri());
    }

    private void stopListener() {
        if (listener != null) {
            listener.stop();
            listener = null;
        }

        if (portField != null) {
            portField.setDisable(false);
        }

        if (startStopButton != null) {
            startStopButton.setText("Start");
        }

        if (listenerLabel != null) {
            listenerLabel.setText("Stopped");
        }
    }

    private void generateConfig() {
        int port;
        try {
            port = Integer.parseInt(portField.getText().trim());
        } catch (NumberFormatException e) {
            listenerLabel.setText("Invalid port");
            return;
        }

        GSIConfigResult result = CS2GSIFile.installFile("EventViewer", "http://localhost:" + port + "/");

        listenerLabel.setText(switch (result.status()) {
            case CREATED, UPDATED -> "GSI config written for port " + port + ", restart CS2";
            case UNCHANGED -> "GSI config already up to date for port " + port;
            case FAILED -> "Could not write GSI config: " + result.cause().getMessage();
        });
    }

    // --- Event flow ----------------------------------------------------------

    private void onGameEvent(CS2GameEvent e) {
        receivedCount.incrementAndGet();

        String eventName = e.getClass().getSimpleName();

        if (!enabledEvents.contains(eventName)) {
            return;
        }

        String line = LocalTime.now().format(TIME_FORMAT) + "  " + EventFormatter.describe(e);

        if (pendingLines.offer(line)) {
            shownCount.incrementAndGet();
        } else {
            droppedCount.incrementAndGet();
        }
    }

    private void drainPendingLines() {
        if (pendingLines.isEmpty()) {
            return;
        }

        List<String> batch = new ArrayList<>();
        pendingLines.drainTo(batch, DRAIN_PER_FRAME);
        logEntries.addAll(batch);

        int overflow = logEntries.size() - LOG_CAP;
        if (overflow > 0) {
            logEntries.remove(0, overflow);
        }

        if (autoScroll.isSelected() && !logEntries.isEmpty()) {
            logView.scrollTo(logEntries.size() - 1);
        }
    }

    private void updateRawView() {
        if (!rawTab.isSelected()) {
            return;
        }

        String raw = latestGameStateJson.get();

        if (!raw.equals(lastRenderedRaw)) {
            rawView.setText(raw);
            lastRenderedRaw = raw;
        }
    }

    private void updateStatus() {
        String status = "Received: " + receivedCount.get()
                + "  |  Shown: " + shownCount.get()
                + "  |  Selected event types: " + enabledEvents.size();

        long dropped = droppedCount.get();
        if (dropped > 0) {
            status += "  |  Dropped (UI backlog): " + dropped;
        }

        statusLabel.setText(status);
    }

    @Override
    public void stop() {
        stopListener();
    }
}
