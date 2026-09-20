package com.cs2gsi;

import com.cs2gsi.GSIConfigResult.Status;

import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Class handling Game State Integration configuration file generation.
 */
public final class CS2GSIFile {
    private CS2GSIFile() {
    }

    /**
     * Attempts to create a Game State Integration configuration file.<br>
     * The configuration will target the {@code http://localhost:{port}/} address.
     * Writes only if the file is missing or its content differs.
     *
     * @param name The name of your integration.
     * @param port The port for your integration.
     * @return Returns true if the file was written or already had the expected content, false otherwise.
     * @deprecated Use {@link #installFile(String, String)}, which also tells whether the game needs
     *             a restart and why an installation failed.
     */
    @Deprecated
    public static boolean createFile(String name, int port) {
        return createFile(name, "http://localhost:" + port + "/");
    }

    /**
     * Attempts to create a Game State Integration configuration file.<br>
     * The configuration will target the specified URI address.
     * Writes only if the file is missing or its content differs.
     *
     * @param name The name of your integration.
     * @param uri  The URI for your integration.
     * @return Returns true if the file was written or already had the expected content, false otherwise.
     * @deprecated Use {@link #installFile(String, String)}, which also tells whether the game needs
     *             a restart and why an installation failed.
     */
    @Deprecated
    public static boolean createFile(String name, String uri) {
        return installFile(name, uri).status() != Status.FAILED;
    }

    /**
     * Installs a Game State Integration configuration file.<br>
     * The configuration will target the specified URI address.
     * Writes only if the file is missing or its content differs.
     *
     * @param name The name of your integration.
     * @param uri  The URI for your integration.
     * @return Returns what happened to the file and where it is.
     */
    public static GSIConfigResult installFile(String name, String uri) {
        return installFile(name, uri, (String) null);
    }

    /**
     * Installs a Game State Integration configuration file with an authentication token.<br>
     * The game sends the token back with every game state, see {@link GameStateListener#setAuthToken(String)}.
     *
     * @param name      The name of your integration.
     * @param uri       The URI for your integration.
     * @param authToken The token to write into the file, or null for none.
     * @return Returns what happened to the file and where it is.
     */
    public static GSIConfigResult installFile(String name, String uri, String authToken) {
        String csgoPath = SteamUtils.getGamePath(730);

        if (csgoPath == null || csgoPath.isBlank()) {
            return new GSIConfigResult(Status.FAILED, null,
                    new FileNotFoundException("Counter-Strike 2 installation not found"));
        }

        return installFile(name, uri, authToken, Paths.get(csgoPath, "game", "csgo", "cfg"));
    }

    static GSIConfigResult installFile(String name, String uri, Path gsiFolder) {
        return installFile(name, uri, null, gsiFolder);
    }

    static GSIConfigResult installFile(String name, String uri, String authToken, Path gsiFolder) {
        // The name becomes part of a file name. A separator would let it leave the cfg folder,
        // and the game does not read a block name with a quote in it.
        if (name == null || name.isBlank() || name.matches(".*[/\\\\\"].*")) {
            return new GSIConfigResult(Status.FAILED, null,
                    new IllegalArgumentException("Not a valid integration name: " + name));
        }

        Path gsiFile = null;

        try {
            Files.createDirectories(gsiFolder);
            gsiFile = gsiFolder.resolve("gamestate_integration_" + name + ".cfg");

            ACF providerConfiguration = new ACF();

            // Providers and the version this integration would like to use.
            // Checked against client.dll of build 25218825 (2026-09-10). The game knows these
            // 18 providers and offers each of them only as version 1.

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
            // The game reads them from an "output" block and ignores a flat "output/precision_time" key.

            ACF output = new ACF();
            output.getItems().put("precision_time", "1"); // Default is 1
            output.getItems().put("precision_position", "2"); // Default is 2
            output.getItems().put("precision_vector", "2"); // Default is 2
            gsiConfiguration.getChildren().put("output", output);

            if (authToken != null && !authToken.isEmpty()) {
                ACF auth = new ACF();
                auth.getItems().put("token", authToken);
                gsiConfiguration.getChildren().put("auth", auth);
            }

            gsiConfiguration.getChildren().put("data", providerConfiguration);

            ACF gsi = new ACF();
            gsi.getChildren().put(name + " Integration Configuration", gsiConfiguration);

            // Compare bytes, not strings. Decoding an existing file with a broken encoding would throw
            // and end in FAILED, although overwriting it works.
            byte[] content = gsi.toString().getBytes(StandardCharsets.UTF_8);
            boolean existed = Files.isRegularFile(gsiFile);

            if (existed && Arrays.equals(Files.readAllBytes(gsiFile), content)) {
                return new GSIConfigResult(Status.UNCHANGED, gsiFile, null);
            }

            Files.write(gsiFile, content);

            return new GSIConfigResult(existed ? Status.UPDATED : Status.CREATED, gsiFile, null);
        } catch (Exception e) {
            return new GSIConfigResult(Status.FAILED, gsiFile, e);
        }
    }
}
