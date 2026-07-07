package com.cs2gsi;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.StringReader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ACFTest {
    private static ACF parse(String text) throws IOException {
        try (PushbackReader reader = new PushbackReader(new StringReader(text))) {
            return new ACF(reader);
        }
    }

    @Test
    void serializedAcfRoundTripsBackToAnEqualObject() throws IOException {
        // Keys are stored lowercase by the parser, so use lowercase keys for a faithful round trip.
        ACF child = new ACF();
        child.getItems().put("uri", "http://localhost:4000/");
        child.getItems().put("timeout", "5.0");

        ACF data = new ACF();
        data.getItems().put("provider", "1");
        data.getItems().put("bomb", "1");
        child.getChildren().put("data", data);

        ACF root = new ACF();
        root.getChildren().put("test configuration", child);

        ACF reparsed = parse(root.toString());

        assertEquals(root, reparsed);
    }

    @Test
    void parsesQuotedKeysValuesAndNestedBlocks() throws IOException {
        String text = """
                "root"
                {
                    "key"    "value with spaces"
                    "nested"
                    {
                        "inner"    "1"
                    }
                }
                """;

        ACF parsed = parse(text);
        ACF root = parsed.getChildren().get("root");

        assertEquals("value with spaces", root.getItems().get("key"));
        assertEquals("1", root.getChildren().get("nested").getItems().get("inner"));
    }

    @Test
    void missingFileYieldsEmptyAcf(@TempDir Path tempDir) {
        ACF acf = new ACF(tempDir.resolve("does-not-exist.acf"));

        assertTrue(acf.getItems().isEmpty());
        assertTrue(acf.getChildren().isEmpty());
    }
}
