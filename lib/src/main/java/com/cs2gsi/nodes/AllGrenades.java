package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;

/**
 * Information about grenades. Key is grenade ID, Value is grenade information.
 */
public class AllGrenades extends LinkedHashMap<String, Grenade> {
    private static final long serialVersionUID = 1L;

    public AllGrenades() {
    }

    public AllGrenades(JsonObject parsedData) {
        if (parsedData != null) {
            for (var property : parsedData.entrySet()) {
                if (property.getValue().isJsonObject()) {
                    put(property.getKey(), new Grenade(property.getValue().getAsJsonObject()));
                }
            }
        }
    }
}
