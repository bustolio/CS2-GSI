package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.player.KillFeed;
import com.cs2gsi.events.player.PlayerDied;
import com.cs2gsi.events.player.PlayerGotKill;
import com.cs2gsi.events.round.RoundChanged;
import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class KillfeedHandlerTest {
    private EventDispatcher<CS2GameEvent> dispatcher;

    private final List<KillFeed> killFeedEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        new KillfeedHandler(dispatcher);

        dispatcher.subscribe(KillFeed.class, e -> killFeedEvents.add((KillFeed) e));
    }

    private static Player player(String steamId) {
        String json = "{\"steamid\": \"" + steamId + "\", \"name\": \"p" + steamId + "\"}";
        return new Player(JsonParser.parseString(json).getAsJsonObject());
    }

    private static Weapon ak47() {
        return new Weapon(JsonParser.parseString("{\"name\": \"weapon_ak47\", \"type\": \"Rifle\"}").getAsJsonObject(), 1);
    }

    @Test
    void pairsAKillWithTheDeathThatFollows() {
        dispatcher.broadcast(new PlayerGotKill(true, ak47(), false, player("1")));
        dispatcher.broadcast(new PlayerDied(0, 80, player("2")));

        assertEquals(1, killFeedEvents.size());
        assertEquals("1", killFeedEvents.get(0).killer.steamId);
        assertEquals("2", killFeedEvents.get(0).victim.steamId);
        assertEquals("weapon_ak47", killFeedEvents.get(0).weapon.name);
        assertTrue(killFeedEvents.get(0).isHeadshot);
    }

    @Test
    void sameKillerAndVictimIsNotAKillFeedEntry() {
        dispatcher.broadcast(new PlayerGotKill(false, ak47(), false, player("1")));
        dispatcher.broadcast(new PlayerDied(0, 80, player("1")));

        assertTrue(killFeedEvents.isEmpty());
    }

    @Test
    void roundChangeDropsAHalfFinishedPair() {
        dispatcher.broadcast(new PlayerGotKill(false, ak47(), false, player("1")));
        dispatcher.broadcast(new RoundChanged(6, 5));
        dispatcher.broadcast(new PlayerDied(0, 80, player("2")));

        assertTrue(killFeedEvents.isEmpty());
    }
}
