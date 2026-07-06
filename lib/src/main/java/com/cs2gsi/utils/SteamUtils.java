package com.cs2gsi.utils;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A class for handling Steam games.
 */
public final class SteamUtils {
    private SteamUtils() {
    }

    /**
     * Retrieves a path to a specified AppID.
     *
     * @param gameId The game's AppID.
     * @return Path to the location of AppID's install, or null when not found.
     */
    public static String getGamePath(int gameId) {
        try {
            String steamPath = findSteamPath();

            if (steamPath == null || steamPath.isBlank()) {
                return null;
            }

            Path librariesFile = Paths.get(steamPath, "SteamApps", "libraryfolders.vdf");

            if (!Files.exists(librariesFile)) {
                librariesFile = Paths.get(steamPath, "steamapps", "libraryfolders.vdf");
            }

            if (Files.exists(librariesFile)) {
                ACF libData = new ACF(librariesFile);
                ACF libraryFolders = libData.getChildren().get("libraryfolders");

                if (libraryFolders == null) {
                    return null;
                }

                for (ACF libraryEntry : libraryFolders.getChildren().values()) {
                    String libraryPath = libraryEntry.getItems().get("path");

                    if (libraryPath == null) {
                        continue;
                    }

                    Path manifestFile = Paths.get(libraryPath, "steamapps", "appmanifest_" + gameId + ".acf");

                    if (Files.exists(manifestFile)) {
                        ACF manifestData = new ACF(manifestFile);
                        ACF appState = manifestData.getChildren().get("appstate");

                        if (appState == null) {
                            continue;
                        }

                        String installDir = appState.getItems().get("installdir");

                        if (installDir == null) {
                            continue;
                        }

                        Path appIdPath = Paths.get(libraryPath, "steamapps", "common", installDir);

                        if (Files.isDirectory(appIdPath)) {
                            return appIdPath.toString();
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }

    private static String findSteamPath() {
        String os = System.getProperty("os.name", "").toLowerCase();

        if (os.contains("win")) {
            String steamPath = readWindowsRegistry("HKLM\\SOFTWARE\\Valve\\Steam", "InstallPath");

            if (steamPath == null || steamPath.isBlank()) {
                steamPath = readWindowsRegistry("HKLM\\SOFTWARE\\Wow6432Node\\Valve\\Steam", "InstallPath");
            }

            return steamPath;
        }

        String userHome = System.getProperty("user.home", "");

        if (os.contains("mac")) {
            Path steamPath = Paths.get(userHome, "Library", "Application Support", "Steam");

            if (Files.isDirectory(steamPath)) {
                return steamPath.toString();
            }

            return null;
        }

        // Linux and others.
        Path[] candidates = {
                Paths.get(userHome, ".steam", "steam"),
                Paths.get(userHome, ".local", "share", "Steam")
        };

        for (Path candidate : candidates) {
            if (Files.isDirectory(candidate)) {
                return candidate.toString();
            }
        }

        return null;
    }

    private static String readWindowsRegistry(String key, String valueName) {
        try {
            Process process = new ProcessBuilder("reg", "query", key, "/v", valueName)
                    .redirectErrorStream(true)
                    .start();

            String output = new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());
            process.waitFor();

            for (String line : output.split("\\R")) {
                line = line.trim();

                if (line.startsWith(valueName)) {
                    int typeIndex = line.indexOf("REG_SZ");

                    if (typeIndex >= 0) {
                        return line.substring(typeIndex + "REG_SZ".length()).trim();
                    }
                }
            }
        } catch (Exception ignored) {
        }

        return null;
    }
}
