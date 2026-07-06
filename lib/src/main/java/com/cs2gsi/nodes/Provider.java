package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the provider of this GameState.
 */
public class Provider extends Node {
    /**
     * Game name.
     */
    public final String name;

    /**
     * Game Steam AppID.
     */
    public final int appId;

    /**
     * Game version.
     */
    public final int version;

    /**
     * Local player's Steam ID.
     */
    public final String steamId;

    /**
     * Timestamp of the GameState data.
     */
    public final int timestamp;

    public Provider() {
        this(null);
    }

    public Provider(JsonObject parsedData) {
        super(parsedData);

        name = getString("name");
        appId = getInt("appid");
        version = getInt("version");
        steamId = getString("steamid");
        timestamp = getInt("timestamp");
    }

    @Override
    public String toString() {
        return "["
                + "Name: " + name + ", "
                + "AppID: " + appId + ", "
                + "Version: " + version + ", "
                + "SteamID: " + steamId + ", "
                + "Timestamp: " + timestamp
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Provider other
                && name.equals(other.name)
                && appId == other.appId
                && version == other.version
                && steamId.equals(other.steamId)
                && timestamp == other.timestamp;
    }

    @Override
    public int hashCode() {
        int hashCode = 854512546;
        hashCode = hashCode * -845579214 + name.hashCode();
        hashCode = hashCode * -845579214 + Integer.hashCode(appId);
        hashCode = hashCode * -845579214 + Integer.hashCode(version);
        hashCode = hashCode * -845579214 + steamId.hashCode();
        hashCode = hashCode * -845579214 + Integer.hashCode(timestamp);
        return hashCode;
    }
}
