package com.cs2gsi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CS2GSIFileTest {
    @Test
    void writesConfigFileIntoGivenFolder(@TempDir Path tempDir) {
        assertTrue(CS2GSIFile.createFile("Test", "http://localhost:4000/", tempDir));

        assertTrue(Files.exists(tempDir.resolve("gamestate_integration_Test.cfg")));
    }

    @Test
    void writtenConfigRoundTripsThroughAcf(@TempDir Path tempDir) {
        assertTrue(CS2GSIFile.createFile("Test", "http://localhost:4000/", tempDir));

        ACF parsed = new ACF(tempDir.resolve("gamestate_integration_Test.cfg"));
        // The ACF parser lowercases keys on read.
        ACF configuration = parsed.getChildren().get("test integration configuration");
        assertNotNull(configuration);

        assertEquals("http://localhost:4000/", configuration.getItems().get("uri"));
        assertEquals("5.0", configuration.getItems().get("timeout"));
        assertEquals("0.1", configuration.getItems().get("buffer"));
        assertEquals("0.1", configuration.getItems().get("throttle"));
        assertEquals("10.0", configuration.getItems().get("heartbeat"));

        ACF data = configuration.getChildren().get("data");
        assertNotNull(data);
        assertEquals("1", data.getItems().get("provider"));
        assertEquals("1", data.getItems().get("map"));
        assertEquals("1", data.getItems().get("round"));
        assertEquals("1", data.getItems().get("allgrenades"));
        assertEquals("1", data.getItems().get("bomb"));
    }

    @Test
    void createsMissingDirectories(@TempDir Path tempDir) {
        Path nested = tempDir.resolve("game").resolve("csgo").resolve("cfg");

        assertTrue(CS2GSIFile.createFile("Nested", "http://localhost:4000/", nested));

        assertTrue(Files.exists(nested.resolve("gamestate_integration_Nested.cfg")));
    }
}
