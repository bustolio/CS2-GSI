package com.cs2gsi.nodes;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The base Node for GSI states.
 */
public class Node {
    /**
     * The json data for this Node.
     */
    protected final JsonObject parsedData;

    /**
     * Has this node been used to successfully read a value from json.
     */
    private boolean successfullyRetrievedAnyValue = false;

    protected Node(JsonObject parsedData) {
        this.parsedData = parsedData;
    }

    /**
     * Parses a string into the given enum type (case-insensitive, spaces ignored).
     * Falls back to the enum's {@code Undefined} constant.
     */
    public static <E extends Enum<E>> E toEnum(Class<E> enumClass, String str) {
        if (str != null && !str.isBlank()) {
            // The game writes some values with a space ("Submachine Gun", "Machine Gun").
            String compact = str.replace(" ", "");

            for (E value : enumClass.getEnumConstants()) {
                if (value.name().equalsIgnoreCase(compact)) {
                    return value;
                }
            }

            // Numeric values map to the constant declared after Undefined.
            try {
                int numeric = Integer.parseInt(str.trim());
                E[] values = enumClass.getEnumConstants();
                if (numeric >= 0 && numeric + 1 < values.length) {
                    return values[numeric + 1];
                }
            } catch (NumberFormatException ignored) {
            }
        }

        return Enum.valueOf(enumClass, "Undefined");
    }

    protected JsonElement getJToken(String propertyName) {
        if (parsedData != null && parsedData.has(propertyName)) {
            // Successfully retrieved a property, this must be a valid node.
            successfullyRetrievedAnyValue = true;
            return parsedData.get(propertyName);
        }

        return null;
    }

    protected JsonObject getJObject(String propertyName) {
        JsonElement token = getJToken(propertyName);

        if (token != null && token.isJsonObject()) {
            return token.getAsJsonObject();
        }

        return null;
    }

    protected String getString(String propertyName) {
        JsonElement value = getJToken(propertyName);

        if (value != null) {
            if (value.isJsonPrimitive()) {
                return value.getAsString();
            }
            return value.toString();
        }

        return "";
    }

    protected int getInt(String propertyName) {
        JsonElement value = getJToken(propertyName);

        if (value != null && value.isJsonPrimitive()) {
            try {
                return value.getAsInt();
            } catch (NumberFormatException ignored) {
            }
        }

        return -1;
    }

    protected long getLong(String propertyName) {
        JsonElement value = getJToken(propertyName);

        if (value != null && value.isJsonPrimitive()) {
            try {
                return value.getAsLong();
            } catch (NumberFormatException ignored) {
            }
        }

        return -1;
    }

    protected float getFloat(String propertyName) {
        JsonElement value = getJToken(propertyName);

        if (value != null && value.isJsonPrimitive()) {
            try {
                return value.getAsFloat();
            } catch (NumberFormatException ignored) {
            }
        }

        return -1;
    }

    protected boolean getBool(String propertyName) {
        JsonElement value = getJToken(propertyName);

        if (value != null && value.isJsonPrimitive()) {
            try {
                return value.getAsBoolean();
            } catch (UnsupportedOperationException ignored) {
            }
        }

        return false;
    }

    protected <E extends Enum<E>> E getEnum(Class<E> enumClass, String propertyName) {
        return toEnum(enumClass, getString(propertyName));
    }

    protected void getMatchingObjects(JsonObject data, Pattern pattern, BiConsumer<Matcher, JsonObject> matchCallback) {
        if (data == null) {
            return;
        }

        for (var property : data.entrySet()) {
            Matcher matcher = pattern.matcher(property.getKey());

            if (matcher.find() && property.getValue().isJsonObject()) {
                successfullyRetrievedAnyValue = true;
                matchCallback.accept(matcher, property.getValue().getAsJsonObject());
            }
        }
    }

    protected void getMatchingStrings(JsonObject data, Pattern pattern, BiConsumer<Matcher, String> matchCallback) {
        if (data == null) {
            return;
        }

        for (var property : data.entrySet()) {
            Matcher matcher = pattern.matcher(property.getKey());

            if (matcher.find()
                    && property.getValue().isJsonPrimitive()
                    && property.getValue().getAsJsonPrimitive().isString()) {
                successfullyRetrievedAnyValue = true;
                matchCallback.accept(matcher, property.getValue().getAsString());
            }
        }
    }

    protected void getMatchingIntegers(JsonObject data, Pattern pattern, BiConsumer<Matcher, Integer> matchCallback) {
        if (data == null) {
            return;
        }

        for (var property : data.entrySet()) {
            Matcher matcher = pattern.matcher(property.getKey());

            if (matcher.find()
                    && property.getValue().isJsonPrimitive()
                    && property.getValue().getAsJsonPrimitive().isNumber()) {
                successfullyRetrievedAnyValue = true;
                matchCallback.accept(matcher, property.getValue().getAsInt());
            }
        }
    }

    @Override
    public String toString() {
        return String.valueOf(parsedData);
    }

    /**
     * Returns validity of this node.
     *
     * @return True if the node is valid, false otherwise.
     */
    public boolean isValid() {
        return successfullyRetrievedAnyValue;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        return obj instanceof Node other
                && getClass() == other.getClass()
                && Objects.equals(parsedData, other.parsedData)
                && successfullyRetrievedAnyValue == other.successfullyRetrievedAnyValue;
    }

    @Override
    public int hashCode() {
        int hashCode = 898763153;
        hashCode = hashCode * -405816372 + (parsedData == null ? 0 : parsedData.hashCode());
        hashCode = hashCode * -405816372 + Boolean.hashCode(successfullyRetrievedAnyValue);
        return hashCode;
    }
}
