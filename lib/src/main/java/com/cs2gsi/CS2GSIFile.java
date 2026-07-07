package com.cs2gsi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Class handling Game State Integration configuration file generation.
 */
public final class CS2GSIFile {
    private CS2GSIFile() {
    }

    /**
     * Attempts to create a Game State Integration configuration file.<br>
     * The configuration will target the {@code http://localhost:{port}/} address.
     *
     * @param name The name of your integration.
     * @param port The port for your integration.
     * @return Returns true on success, false otherwise.
     */
    public static boolean createFile(String name, int port) {
        return createFile(name, "http://localhost:" + port + "/");
    }

    /**
     * Attempts to create a Game State Integration configuration file.<br>
     * The configuration will target the specified URI address.
     *
     * @param name The name of your integration.
     * @param uri  The URI for your integration.
     * @return Returns true on success, false otherwise.
     */
    public static boolean createFile(String name, String uri) {
        String csgoPath = SteamUtils.getGamePath(730);

        if (csgoPath == null || csgoPath.isBlank()) {
            return false;
        }

        return createFile(name, uri, Paths.get(csgoPath, "game", "csgo", "cfg"));
    }

    static boolean createFile(String name, String uri, Path gsiFolder) {
        try {
            Files.createDirectories(gsiFolder);
            Path gsiFile = gsiFolder.resolve("gamestate_integration_" + name + ".cfg");

            ACF providerConfiguration = new ACF();

            // Providers and the version this integration would like to use.
            // As of 6/23/2025 all providers only offer version 1.

            providerConfiguration.getItems().put("provider", "1");
            providerConfiguration.getItems().put("tournamentdraft", "1");
            providerConfiguration.getItems().put("map", "1");
            providerConfiguration.getItems().put("map_round_wins", "1");
            providerConfiguration.getItems().put("round", "1");
            providerConfiguration.getItems().put("player_id", "1");
            providerConfiguration.getItems().put("player_state", "1");
            providerConfiguration.getItems().put("player_weapons", "1");
            providerConfiguration.getItems().put("player_match_stats", "1");
            providerConfiguration.getItems().put("player_position", "1");
            providerConfiguration.getItems().put("phase_countdowns", "1");
            providerConfiguration.getItems().put("allplayers_id", "1");
            providerConfiguration.getItems().put("allplayers_state", "1");
            providerConfiguration.getItems().put("allplayers_match_stats", "1");
            providerConfiguration.getItems().put("allplayers_weapons", "1");
            providerConfiguration.getItems().put("allplayers_position", "1");
            providerConfiguration.getItems().put("allgrenades", "1");
            providerConfiguration.getItems().put("bomb", "1");

            ACF gsiConfiguration = new ACF();
            gsiConfiguration.getItems().put("uri", uri);
            gsiConfiguration.getItems().put("timeout", "5.0"); // Default is 60.0, Min value 1.1
            gsiConfiguration.getItems().put("buffer", "0.1"); // Default is 0.1, Min value 0.0
            gsiConfiguration.getItems().put("throttle", "0.1"); // Default is 1.0, Min value 0.0
            gsiConfiguration.getItems().put("heartbeat", "10.0"); // Default is 60.0, Min value 0.0

            // Precision value adjustment for time and vector values.

            gsiConfiguration.getItems().put("output/precision_time", "1"); // Default is 1
            gsiConfiguration.getItems().put("output/precision_position", "2"); // Default is 2
            gsiConfiguration.getItems().put("output/precision_vector", "2"); // Default is 2

            gsiConfiguration.getChildren().put("data", providerConfiguration);

            ACF gsi = new ACF();
            gsi.getChildren().put(name + " Integration Configuration", gsiConfiguration);

            Files.writeString(gsiFile, gsi.toString(), StandardCharsets.UTF_8);

            return true;
        } catch (Exception ignored) {
        }

        return false;
    }
}
