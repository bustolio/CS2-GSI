package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Information about all players. Key is the Steam ID, Value is the player data.
 */
public class AllPlayers extends LinkedHashMap<String, Player> {
    private static final long serialVersionUID = 1L;

    private static final Pattern PLAYER_STEAMID_PATTERN = Pattern.compile("(\\d+)");

    public AllPlayers() {
    }

    public AllPlayers(JsonObject parsedData) {
        if (parsedData != null) {
            for (var property : parsedData.entrySet()) {
                Matcher matcher = PLAYER_STEAMID_PATTERN.matcher(property.getKey());

                if (matcher.find() && property.getValue().isJsonObject()) {
                    String playerSteamId = matcher.group(1);
                    Player playerData = new Player(property.getValue().getAsJsonObject(), playerSteamId);

                    put(playerSteamId, playerData);
                }
            }
        }
    }

    /**
     * Gets the player with the given player ID, or an empty Player if not found.
     */
    public Player getPlayer(String playerId) {
        Player player = get(playerId);

        if (player != null) {
            return player;
        }

        return new Player();
    }
}
