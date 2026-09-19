package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.player.AllPlayersUpdated;
import com.cs2gsi.events.player.PlayerUpdated;
import com.cs2gsi.nodes.AllPlayers;
import com.cs2gsi.nodes.Player;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AllPlayersHandlerTest {
    private static final String OBSERVED = "76561198000000001";
    private static final String OTHER = "76561198000000002";

    private EventDispatcher<CS2GameEvent> dispatcher;

    private final List<String> updatedPlayerIds = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        new AllPlayersHandler(dispatcher);

        dispatcher.subscribe(PlayerUpdated.class, e -> updatedPlayerIds.add(((PlayerUpdated) e).playerId));
    }

    private static String playerJson(String steamId, int health) {
        return "{\"steamid\": \"" + steamId + "\", \"name\": \"p\", \"state\": {\"health\": " + health + "}}";
    }

    private static Player player(String steamId, int health) {
        return new Player(JsonParser.parseString(playerJson(steamId, health)).getAsJsonObject());
    }

    private static AllPlayers allPlayers(int observedHealth, int otherHealth) {
        String json = "{\"" + OBSERVED + "\": " + playerJson(OBSERVED, observedHealth)
                + ", \"" + OTHER + "\": " + playerJson(OTHER, otherHealth) + "}";
        return new AllPlayers(JsonParser.parseString(json).getAsJsonObject());
    }

    @Test
    void spectatedPlayerIsNotReportedASecondTimeFromAllPlayers() {
        // An observer payload, the player block goes first and carries the spectated player.
        dispatcher.broadcast(new PlayerUpdated(player(OBSERVED, 50), player(OBSERVED, 100), OBSERVED));
        dispatcher.broadcast(new AllPlayersUpdated(allPlayers(50, 80), allPlayers(100, 100)));

        assertEquals(List.of(OBSERVED, OTHER), updatedPlayerIds);
    }

    @Test
    void laterPayloadWithoutAPlayerBlockChangeStillReportsEveryone() {
        dispatcher.broadcast(new PlayerUpdated(player(OBSERVED, 50), player(OBSERVED, 100), OBSERVED));
        dispatcher.broadcast(new AllPlayersUpdated(allPlayers(50, 80), allPlayers(100, 100)));
        updatedPlayerIds.clear();

        dispatcher.broadcast(new AllPlayersUpdated(allPlayers(20, 80), allPlayers(50, 80)));

        assertEquals(List.of(OBSERVED), updatedPlayerIds);
    }

    @Test
    void switchingTheSpectatedPlayerKeepsTheAllPlayersUpdate() {
        // PlayerHandler skips the diff when the steam id changes, so allplayers has to deliver it.
        dispatcher.broadcast(new PlayerUpdated(player(OBSERVED, 50), player(OTHER, 100), OBSERVED));
        dispatcher.broadcast(new AllPlayersUpdated(allPlayers(50, 100), allPlayers(100, 100)));

        assertEquals(List.of(OBSERVED, OBSERVED), updatedPlayerIds);
    }
}
