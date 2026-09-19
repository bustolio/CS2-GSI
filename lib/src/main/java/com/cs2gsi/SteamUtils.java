package com.cs2gsi;

import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

/**
 * A class for handling Steam games.
 */
final class SteamUtils {
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

            if (steamPath != null && Files.isDirectory(Paths.get(steamPath))) {
                return steamPath;
            }

            // ponytail: reg.exe answers in the console code page, so a path with non-ASCII characters
            // arrives garbled. Fall back to the default locations, read the registry through JNA if that
            // stops being enough.
            for (String programFiles : new String[] {"ProgramFiles(x86)", "ProgramFiles"}) {
                String folder = System.getenv(programFiles);

                if (folder != null && Files.isDirectory(Paths.get(folder, "Steam"))) {
                    return Paths.get(folder, "Steam").toString();
                }
            }

            return null;
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
            // The full path, because Windows looks for a bare "reg" in the working directory before System32.
            String systemRoot = System.getenv().getOrDefault("SystemRoot", "C:\\Windows");
            String regExe = Paths.get(systemRoot, "System32", "reg.exe").toString();

            Process process = new ProcessBuilder(regExe, "query", key, "/v", valueName)
                    .redirectErrorStream(true)
                    .start();

            // Waiting before reading is safe here, the few lines reg prints fit into the pipe buffer.
            if (!process.waitFor(5, TimeUnit.SECONDS)) {
                process.destroyForcibly();
                return null;
            }

            String output = new String(process.getInputStream().readAllBytes(), Charset.defaultCharset());

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
