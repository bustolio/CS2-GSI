package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;

/**
 * Information about the authentication of this GameState.
 */
public class Auth extends LinkedHashMap<String, String> {
    private static final long serialVersionUID = 1L;

    public Auth() {
    }

    public Auth(JsonObject parsedData) {
        if (parsedData != null) {
            for (var property : parsedData.entrySet()) {
                if (property.getValue().isJsonPrimitive()
                        && property.getValue().getAsJsonPrimitive().isString()) {
                    put(property.getKey(), property.getValue().getAsString());
                }
            }
        }
    }
}
