package com.cs2gsi;

import com.cs2gsi.nodes.BombState;
import com.cs2gsi.nodes.GameMode;
import com.cs2gsi.nodes.GrenadeType;
import com.cs2gsi.nodes.Phase;
import com.cs2gsi.nodes.PlayerActivity;
import com.cs2gsi.nodes.PlayerTeam;
import com.cs2gsi.nodes.RoundConclusion;
import com.cs2gsi.nodes.helpers.Vector3D;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateParsingTest {
    private static GameState gameState;

    static JsonObject loadSampleGameState() throws IOException {
        try (InputStream stream = GameStateParsingTest.class.getResourceAsStream("/sample-gamestate.json")) {
            String json = new String(Objects.requireNonNull(stream, "fixture missing").readAllBytes(), StandardCharsets.UTF_8);
            return JsonParser.parseString(json).getAsJsonObject();
        }
    }

    @BeforeAll
    static void parseFixture() throws IOException {
        gameState = new GameState(loadSampleGameState());
    }

    @Test
    void emptyGameStateIsInvalid() {
        assertFalse(new GameState().isValid());
    }

    @Test
    void parsedGameStateIsValid() {
        assertTrue(gameState.isValid());
    }

    @Test
    void parsesAuth() {
        assertEquals("s3cr3t", gameState.auth.get("token"));
    }

    @Test
    void parsesProvider() {
        assertEquals("Counter-Strike: Global Offensive", gameState.provider.name);
        assertEquals(730, gameState.provider.appId);
        assertEquals("76561198000000001", gameState.provider.steamId);
        assertEquals(1751900000, gameState.provider.timestamp);
    }

    @Test
    void parsesMap() {
        assertEquals(GameMode.Competitive, gameState.map.mode);
        assertEquals("de_dust2", gameState.map.name);
        assertEquals(Phase.Live, gameState.map.phase);
        assertEquals(5, gameState.map.round);
        assertEquals(3, gameState.map.ctStatistics.score);
        assertEquals(2, gameState.map.tStatistics.score);
        assertEquals(5, gameState.map.roundWins.size());
        assertEquals(RoundConclusion.CT_Win_Elimination, gameState.map.roundWins.get(1));
        assertEquals(RoundConclusion.T_Win_Bomb, gameState.map.roundWins.get(2));
    }

    @Test
    void parsesRound() {
        assertEquals(Phase.Live, gameState.round.phase);
        assertEquals(BombState.Planted, gameState.round.bombState);
        assertEquals(PlayerTeam.Undefined, gameState.round.winningTeam);
    }

    @Test
    void parsesPlayer() {
        assertEquals("76561198000000001", gameState.player.steamId);
        assertEquals("PlayerOne", gameState.player.name);
        assertEquals("TeamClan", gameState.player.clan);
        assertEquals(4, gameState.player.observerSlot);
        assertEquals(PlayerTeam.CT, gameState.player.team);
        assertEquals(PlayerActivity.Playing, gameState.player.activity);
        assertEquals(100, gameState.player.state.health);
        assertEquals(3500, gameState.player.state.money);
        assertTrue(gameState.player.state.hasHelmet);
        assertEquals(2, gameState.player.weapons.size());
        assertEquals("weapon_ak47", gameState.player.getActiveWeapon().name);
        assertEquals(12, gameState.player.matchStats.kills);
        assertEquals(new Vector3D(100.50f, -200.25f, 64.00f), gameState.player.position);
    }

    @Test
    void parsesPhaseCountdowns() {
        assertEquals(Phase.Live, gameState.phaseCountdowns.phase);
        assertEquals(45.1f, gameState.phaseCountdowns.phaseEndTime);
    }

    @Test
    void parsesAllPlayersKeyedBySteamId() {
        assertEquals(2, gameState.allPlayers.size());
        assertEquals("PlayerOne", gameState.allPlayers.getPlayer("76561198000000001").name);
        assertEquals("PlayerTwo", gameState.allPlayers.getPlayer("76561198000000002").name);
        assertEquals(PlayerTeam.T, gameState.allPlayers.getPlayer("76561198000000002").team);
        assertEquals(82, gameState.allPlayers.getPlayer("76561198000000002").state.health);
        // The steamid is recovered from the map key when absent from the player object itself.
        assertEquals("76561198000000002", gameState.allPlayers.getPlayer("76561198000000002").steamId);
    }

    @Test
    void parsesAllGrenadesKeyedByEntityId() {
        assertEquals(1, gameState.allGrenades.size());
        assertEquals(GrenadeType.Smoke, gameState.allGrenades.get("129").type);
        assertEquals("76561198000000002", gameState.allGrenades.get("129").owner);
        assertEquals(2.1f, gameState.allGrenades.get("129").lifetime);
    }

    @Test
    void parsesBomb() {
        assertEquals(BombState.Planted, gameState.bomb.state);
        assertEquals("76561198000000001", gameState.bomb.player);
        assertEquals(35.5f, gameState.bomb.countdown);
        assertEquals(new Vector3D(1000.00f, 2000.00f, 5.00f), gameState.bomb.position);
    }
}
