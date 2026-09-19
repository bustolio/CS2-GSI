package com.cs2gsi;

import com.cs2gsi.GSIConfigResult.Status;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CS2GSIFileTest {
    @Test
    void reportsCreatedWhenFileWasMissing(@TempDir Path tempDir) {
        GSIConfigResult result = CS2GSIFile.installFile("Test", "http://localhost:4000/", tempDir);

        assertEquals(Status.CREATED, result.status());
        assertNull(result.cause());
        assertEquals(tempDir.resolve("gamestate_integration_Test.cfg"), result.file());
        assertTrue(Files.exists(result.file()));
    }

    @Test
    void reportsUpdatedWhenContentDiffers(@TempDir Path tempDir) throws IOException {
        CS2GSIFile.installFile("Test", "http://localhost:4000/", tempDir);

        GSIConfigResult result = CS2GSIFile.installFile("Test", "http://localhost:5000/", tempDir);

        assertEquals(Status.UPDATED, result.status());
        assertTrue(Files.readString(result.file()).contains("http://localhost:5000/"));
    }

    @Test
    void reportsUnchangedWhenContentMatches(@TempDir Path tempDir) {
        CS2GSIFile.installFile("Test", "http://localhost:4000/", tempDir);

        GSIConfigResult result = CS2GSIFile.installFile("Test", "http://localhost:4000/", tempDir);

        assertEquals(Status.UNCHANGED, result.status());
    }

    @Test
    void reportsFailedWhenFileCannotBeWritten(@TempDir Path tempDir) throws IOException {
        // A directory in place of the file makes the write fail.
        Files.createDirectory(tempDir.resolve("gamestate_integration_Test.cfg"));

        GSIConfigResult result = CS2GSIFile.installFile("Test", "http://localhost:4000/", tempDir);

        assertEquals(Status.FAILED, result.status());
        assertInstanceOf(IOException.class, result.cause());
    }

    @Test
    void rejectsNamesThatWouldLeaveTheFolderOrBreakTheBlockName(@TempDir Path tempDir) throws IOException {
        Path cfg = Files.createDirectory(tempDir.resolve("cfg"));

        for (String name : new String[] {"x/../../escaped", "x\\..\\..\\escaped", "My \"HUD\"", " ", null}) {
            GSIConfigResult result = CS2GSIFile.installFile(name, "http://localhost:4000/", cfg);

            assertEquals(Status.FAILED, result.status(), "name: " + name);
            assertInstanceOf(IllegalArgumentException.class, result.cause(), "name: " + name);
            assertNull(result.file(), "name: " + name);
        }

        try (var files = Files.list(tempDir)) {
            assertEquals(1, files.count(), "nothing may be written next to the cfg folder");
        }
    }

    @Test
    void writesAnAuthBlockOnlyWhenATokenIsGiven(@TempDir Path tempDir) {
        CS2GSIFile.installFile("Plain", "http://localhost:4000/", tempDir);
        CS2GSIFile.installFile("Secured", "http://localhost:4000/", "s3cret", tempDir);

        ACF plain = new ACF(tempDir.resolve("gamestate_integration_Plain.cfg"))
                .getChildren().get("plain integration configuration");
        ACF secured = new ACF(tempDir.resolve("gamestate_integration_Secured.cfg"))
                .getChildren().get("secured integration configuration");

        assertNull(plain.getChildren().get("auth"));
        assertEquals("s3cret", secured.getChildren().get("auth").getItems().get("token"));
    }

    @Test
    void writtenConfigRoundTripsThroughAcf(@TempDir Path tempDir) {
        CS2GSIFile.installFile("Test", "http://localhost:4000/", tempDir);

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

        assertEquals(Status.CREATED, CS2GSIFile.installFile("Nested", "http://localhost:4000/", nested).status());

        assertTrue(Files.exists(nested.resolve("gamestate_integration_Nested.cfg")));
    }
}
