package com.cs2gsi;

import java.nio.file.Path;

/**
 * Outcome of installing a Game State Integration configuration file.<br>
 * Counter-Strike 2 only reads the file at startup, so {@link Status#CREATED} and
 * {@link Status#UPDATED} mean a running game needs a restart.
 *
 * @param status What happened to the file.
 * @param file   The configuration file, or null if the game folder was not found or the name was rejected.
 * @param cause  Why the installation failed, or null unless the status is {@link Status#FAILED}.
 */
public record GSIConfigResult(Status status, Path file, Throwable cause) {
    public enum Status {
        /** No file existed, so the library wrote one. */
        CREATED,
        /** The file had different content, so the library overwrote it. */
        UPDATED,
        /** The file already had the expected content, so the library left it alone. */
        UNCHANGED,
        /** The library found no game folder, rejected the name or could not write the file. */
        FAILED
    }
}
