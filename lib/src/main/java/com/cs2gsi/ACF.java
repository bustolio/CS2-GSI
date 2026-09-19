package com.cs2gsi;

import java.io.IOException;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Class that serializes and deserializes the ACF (Valve KeyValues) file format.
 */
class ACF {
    private static final Map<Character, Character> ESCAPE_CHARACTERS = Map.of(
            'r', '\r',
            'n', '\n',
            't', '\t',
            '\'', '\'',
            '"', '"',
            '\\', '\\',
            'b', '\b',
            'f', '\f',
            'v', (char) 11 // vertical tab
    );

    private final LinkedHashMap<String, String> items = new LinkedHashMap<>();
    private final LinkedHashMap<String, ACF> children = new LinkedHashMap<>();

    ACF() {
    }

    ACF(Path filename) {
        if (Files.exists(filename)) {
            try (Reader reader = Files.newBufferedReader(filename, StandardCharsets.UTF_8);
                 PushbackReader pushbackReader = new PushbackReader(reader)) {
                parseStream(pushbackReader);
            } catch (IOException ignored) {
            }
        }
    }

    ACF(PushbackReader stream) throws IOException {
        parseStream(stream);
    }

    /**
     * The items of this ACF element.
     */
    public Map<String, String> getItems() {
        return items;
    }

    /**
     * The children of this ACF element.
     */
    public Map<String, ACF> getChildren() {
        return children;
    }

    private static int peek(PushbackReader stream) throws IOException {
        int character = stream.read();

        if (character != -1) {
            stream.unread(character);
        }

        return character;
    }

    private void parseStream(PushbackReader stream) throws IOException {
        boolean seekingBrace = false;

        if (peek(stream) == '{') {
            // Consume {
            stream.read();
            seekingBrace = true;
        }

        while (peek(stream) != -1) {
            if (seekingBrace && peek(stream) == '}') {
                // Consume }
                stream.read();
                break;
            }

            // Attempt to read the item key
            Object key = readValue(stream);

            if (key instanceof String strKey) {
                Object value = readValue(stream);

                if (value instanceof String strValue) {
                    items.put(strKey.toLowerCase(), strValue);
                } else if (value instanceof ACF acfValue) {
                    children.put(strKey.toLowerCase(), acfValue);
                }
            }

            // Skip over any whitespace characters to get to next value
            skipWhitespace(stream);
        }
    }

    private static void skipWhitespace(PushbackReader stream) throws IOException {
        int character = peek(stream);

        while (character != -1 && Character.isWhitespace(character)) {
            stream.read();
            character = peek(stream);
        }
    }

    private Object readValue(PushbackReader stream) throws IOException {
        // Skip over any whitespace characters to get to next value
        skipWhitespace(stream);

        int peekChar = peek(stream);

        if (peekChar == '{') {
            return new ACF(stream);
        }

        if (peekChar == '/') {
            // Comment, read until end of line
            readLine(stream);
            return null;
        }

        if (peekChar == -1) {
            return null;
        }

        return readString(stream);
    }

    private static void readLine(PushbackReader stream) throws IOException {
        int character = stream.read();

        while (character != -1 && character != '\n') {
            character = stream.read();
        }
    }

    private static String readString(PushbackReader stream) throws IOException {
        StringBuilder builder = new StringBuilder();

        boolean isQuote = peek(stream) == '"';

        if (isQuote) {
            stream.read();
        }

        for (int chr = stream.read(); chr != -1; chr = stream.read()) {
            if ((isQuote && chr == '"') || (!isQuote && Character.isWhitespace(chr))) {
                // Arrived at end of string.
                break;
            }

            if (chr == '\\') {
                // Fix up escaped characters.
                int escape = stream.read();

                if (escape != -1) {
                    Character mapped = ESCAPE_CHARACTERS.get((char) escape);

                    if (mapped != null) {
                        builder.append((char) mapped);
                    } else {
                        // Not an escape sequence, e.g. a Windows path written with single backslashes.
                        builder.append('\\').append((char) escape);
                    }
                }
            } else {
                builder.append((char) chr);
            }
        }

        return builder.toString();
    }

    private static String escape(String text) {
        return text.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public String buildString(int indentAmount) {
        int longestKeyLength = 0;

        for (String key : items.keySet()) {
            longestKeyLength = Math.max(longestKeyLength, escape(key).length());
        }

        String indentation = "    ".repeat(indentAmount);

        StringBuilder stringBuilder = new StringBuilder();

        for (var itemEntry : items.entrySet()) {
            String key = escape(itemEntry.getKey());

            // Indent beginning
            stringBuilder.append(indentation);
            stringBuilder.append('"').append(key).append('"');
            // Pretty print
            stringBuilder.append(" ".repeat(longestKeyLength - key.length()));
            // Separator between the key and value
            stringBuilder.append("    ");
            stringBuilder.append('"').append(escape(itemEntry.getValue())).append('"');
            stringBuilder.append('\n');
        }

        for (var childEntry : children.entrySet()) {
            // Indent beginning
            stringBuilder.append(indentation);
            stringBuilder.append('"').append(escape(childEntry.getKey())).append('"').append('\n');
            // Opening {
            stringBuilder.append(indentation).append("{").append('\n');
            stringBuilder.append(childEntry.getValue().buildString(indentAmount + 1));
            // Closing }
            stringBuilder.append(indentation).append("}").append('\n');
        }

        return stringBuilder.toString();
    }

    @Override
    public String toString() {
        return buildString(0);
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof ACF other
                && items.equals(other.items)
                && children.equals(other.children);
    }

    @Override
    public int hashCode() {
        int hashCode = 610350854;
        hashCode = hashCode * -379045661 + items.hashCode();
        hashCode = hashCode * -379045661 + children.hashCode();
        return hashCode;
    }
}
