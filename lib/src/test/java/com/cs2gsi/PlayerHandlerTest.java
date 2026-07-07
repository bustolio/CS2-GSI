package com.cs2gsi;

import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.PlayerDied;
import com.cs2gsi.events.PlayerHealthChanged;
import com.cs2gsi.events.PlayerRespawned;
import com.cs2gsi.events.PlayerStateChanged;
import com.cs2gsi.events.PlayerTookDamage;
import com.cs2gsi.events.PlayerUpdated;
import com.cs2gsi.events.PlayerWeaponsDropped;
import com.cs2gsi.events.PlayerWeaponsPickedUp;
import com.cs2gsi.nodes.Player;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PlayerHandlerTest {
    private EventDispatcher<CS2GameEvent> dispatcher;

    private final List<PlayerHealthChanged> healthEvents = new ArrayList<>();
    private final List<PlayerTookDamage> damageEvents = new ArrayList<>();
    private final List<PlayerDied> deathEvents = new ArrayList<>();
    private final List<PlayerRespawned> respawnEvents = new ArrayList<>();
    private final List<PlayerStateChanged> stateEvents = new ArrayList<>();
    private final List<PlayerWeaponsPickedUp> pickupEvents = new ArrayList<>();
    private final List<PlayerWeaponsDropped> dropEvents = new ArrayList<>();

    @BeforeEach
    void setUp() {
        dispatcher = new EventDispatcher<>(CS2GameEvent.class);
        new PlayerHandler(dispatcher);

        dispatcher.subscribe(PlayerHealthChanged.class, e -> healthEvents.add((PlayerHealthChanged) e));
        dispatcher.subscribe(PlayerTookDamage.class, e -> damageEvents.add((PlayerTookDamage) e));
        dispatcher.subscribe(PlayerDied.class, e -> deathEvents.add((PlayerDied) e));
        dispatcher.subscribe(PlayerRespawned.class, e -> respawnEvents.add((PlayerRespawned) e));
        dispatcher.subscribe(PlayerStateChanged.class, e -> stateEvents.add((PlayerStateChanged) e));
        dispatcher.subscribe(PlayerWeaponsPickedUp.class, e -> pickupEvents.add((PlayerWeaponsPickedUp) e));
        dispatcher.subscribe(PlayerWeaponsDropped.class, e -> dropEvents.add((PlayerWeaponsDropped) e));
    }

    private static Player player(int health, boolean withRifle) {
        String rifle = withRifle
                ? ", \"weapon_1\": {\"name\": \"weapon_ak47\", \"paintkit\": \"default\", \"type\": \"Rifle\","
                        + " \"ammo_clip\": 30, \"ammo_clip_max\": 30, \"ammo_reserve\": 90, \"state\": \"active\"}"
                : "";

        String json = """
                {
                  "steamid": "76561198000000001",
                  "clan": "TeamClan",
                  "name": "PlayerOne",
                  "observer_slot": 4,
                  "team": "CT",
                  "activity": "playing",
                  "state": {
                    "health": %d,
                    "armor": 100,
                    "helmet": true,
                    "flashed": 0,
                    "smoked": 0,
                    "burning": 0,
                    "money": 3500,
                    "round_kills": 0,
                    "round_killhs": 0,
                    "round_totaldmg": 0,
                    "equip_value": 4750
                  },
                  "weapons": {
                    "weapon_0": {"name": "weapon_knife", "paintkit": "default", "type": "Knife", "state": "holstered"}%s
                  },
                  "match_stats": {"kills": 0, "assists": 0, "deaths": 0, "mvps": 0, "score": 0},
                  "position": "0.00, 0.00, 0.00",
                  "forward": "1.00, 0.00, 0.00"
                }
                """.formatted(health, rifle);

        return new Player(JsonParser.parseString(json).getAsJsonObject());
    }

    @Test
    void healthDropToZeroCascadesIntoDamageAndDeath() {
        Player previous = player(100, true);
        Player current = player(0, true);

        // One PlayerUpdated triggers PlayerHandler's own PlayerStateChanged cascade on the same dispatcher.
        dispatcher.broadcast(new PlayerUpdated(current, previous, current.steamId));

        assertEquals(1, stateEvents.size());

        assertEquals(1, healthEvents.size());
        assertEquals(0, healthEvents.get(0).newValue);
        assertEquals(100, healthEvents.get(0).previousValue);

        assertEquals(1, damageEvents.size());
        assertEquals(1, deathEvents.size());
        assertEquals(0, deathEvents.get(0).newValue);

        assertTrue(respawnEvents.isEmpty(), "A death must not raise PlayerRespawned");
    }

    @Test
    void healthRecoveryFromZeroRaisesRespawn() {
        Player previous = player(0, true);
        Player current = player(100, true);

        dispatcher.broadcast(new PlayerUpdated(current, previous, current.steamId));

        assertEquals(1, healthEvents.size());
        assertEquals(1, respawnEvents.size());
        assertEquals(100, respawnEvents.get(0).newValue);

        assertTrue(damageEvents.isEmpty(), "Healing must not raise PlayerTookDamage");
        assertTrue(deathEvents.isEmpty(), "Healing must not raise PlayerDied");
    }

    @Test
    void newWeaponRaisesPickedUp() {
        Player previous = player(100, false);
        Player current = player(100, true);

        dispatcher.broadcast(new PlayerUpdated(current, previous, current.steamId));

        assertEquals(1, pickupEvents.size());
        assertEquals(1, pickupEvents.get(0).weapons.size());
        assertEquals("weapon_ak47", pickupEvents.get(0).weapons.get(0).name);
        assertTrue(dropEvents.isEmpty());
    }

    @Test
    void removedWeaponRaisesDropped() {
        Player previous = player(100, true);
        Player current = player(100, false);

        dispatcher.broadcast(new PlayerUpdated(current, previous, current.steamId));

        assertEquals(1, dropEvents.size());
        assertEquals(1, dropEvents.get(0).weapons.size());
        assertEquals("weapon_ak47", dropEvents.get(0).weapons.get(0).name);
        assertTrue(pickupEvents.isEmpty());
    }

    @Test
    void identicalPlayersBroadcastNothing() {
        Player previous = player(100, true);
        Player current = player(100, true);

        dispatcher.broadcast(new PlayerUpdated(current, previous, current.steamId));

        assertTrue(stateEvents.isEmpty());
        assertTrue(healthEvents.isEmpty());
        assertTrue(pickupEvents.isEmpty());
        assertTrue(dropEvents.isEmpty());
    }
}
