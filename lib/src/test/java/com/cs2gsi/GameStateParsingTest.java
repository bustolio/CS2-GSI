package com.cs2gsi;

import com.cs2gsi.nodes.BombState;
import com.cs2gsi.nodes.FireMode;
import com.cs2gsi.nodes.GameMode;
import com.cs2gsi.nodes.GrenadeType;
import com.cs2gsi.nodes.Node;
import com.cs2gsi.nodes.Phase;
import com.cs2gsi.nodes.PlayerActivity;
import com.cs2gsi.nodes.PlayerTeam;
import com.cs2gsi.nodes.RoundConclusion;
import com.cs2gsi.nodes.Weapon;
import com.cs2gsi.nodes.WeaponInfo;
import com.cs2gsi.nodes.WeaponType;
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
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    void equalsHoldsForEmptyAndForReparsedGameStates() throws IOException {
        GameState empty = new GameState();

        assertEquals(empty, empty);
        assertEquals(new GameState(), new GameState());
        assertEquals(gameState, new GameState(loadSampleGameState()));
        assertNotEquals(empty, gameState);
    }

    @Test
    void weaponsCannotBeModifiedByAHandler() {
        assertFalse(gameState.player.weapons.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> gameState.player.weapons.clear());
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
    void parsesWeaponTypesTheGameWritesWithASpace() {
        assertEquals(WeaponType.SubmachineGun, Node.toEnum(WeaponType.class, "Submachine Gun"));
        assertEquals(WeaponType.MachineGun, Node.toEnum(WeaponType.class, "Machine Gun"));
        assertEquals(WeaponType.SniperRifle, Node.toEnum(WeaponType.class, "SniperRifle"));
    }

    @Test
    void findsWeaponInfoByPayloadName() {
        assertEquals(WeaponInfo.Ak47, gameState.player.getActiveWeapon().info);
        assertEquals("AK-47", WeaponInfo.Ak47.displayName);
        assertEquals(FireMode.Automatic, WeaponInfo.Ak47.fireMode);
        assertEquals(WeaponInfo.UspS, WeaponInfo.fromName("weapon_usp_silencer"));
        assertEquals(FireMode.BoltAction, WeaponInfo.fromName("weapon_awp").fireMode);
        assertEquals(FireMode.Revolver, WeaponInfo.fromName("weapon_revolver").fireMode);
    }

    @Test
    void everyKnifeSkinIsAKnife() {
        assertEquals(WeaponInfo.Knife, WeaponInfo.fromName("weapon_knife"));
        assertEquals(WeaponInfo.Knife, WeaponInfo.fromName("weapon_knife_t"));
        assertEquals(WeaponInfo.Knife, WeaponInfo.fromName("weapon_knife_karambit"));
        assertEquals(WeaponInfo.Knife, WeaponInfo.fromName("weapon_bayonet"));
    }

    @Test
    void unknownAndMissingWeaponNamesAreUndefined() {
        assertEquals(WeaponInfo.Undefined, WeaponInfo.fromName("weapon_not_released_yet"));
        assertEquals(WeaponInfo.Undefined, WeaponInfo.fromName(""));
        assertEquals(WeaponInfo.Undefined, WeaponInfo.fromName(null));
        assertEquals(WeaponInfo.Undefined, new Weapon().info);
        assertEquals(FireMode.Undefined, new Weapon().info.fireMode);
    }

    @Test
    void weaponInfoCarriesTheSlotCommandsOfTheGame() {
        assertEquals(1, WeaponInfo.Ak47.slot);
        assertEquals(2, WeaponInfo.Glock.slot);
        assertEquals(3, WeaponInfo.Knife.slot);
        assertEquals(0, WeaponInfo.Knife.directSlot);
        assertEquals(3, WeaponInfo.Zeus.slot);
        assertEquals(11, WeaponInfo.Zeus.directSlot);
        assertEquals(4, WeaponInfo.SmokeGrenade.slot);
        assertEquals(8, WeaponInfo.SmokeGrenade.directSlot);
        assertEquals(WeaponInfo.Molotov.directSlot, WeaponInfo.IncendiaryGrenade.directSlot);
        assertEquals(5, WeaponInfo.C4.slot);
        assertEquals(0, WeaponInfo.Undefined.slot);
        assertEquals(0, WeaponInfo.Undefined.directSlot);

        for (WeaponInfo info : WeaponInfo.values()) {
            if (info != WeaponInfo.Undefined) {
                assertTrue(info.slot >= 1 && info.slot <= 5, info + " has slot " + info.slot);
            }
        }
    }

    @Test
    void gameModeCarriesDisplayNameAndTimers() {
        assertEquals("Wingman", GameMode.Scrimcomp2v2.displayName);
        assertEquals("", GameMode.Undefined.displayName);
        assertEquals(115, GameMode.Competitive.roundSeconds);
        assertEquals(90, GameMode.Scrimcomp2v2.roundSeconds);
        assertEquals(135, GameMode.Casual.roundSeconds);
        assertEquals(40, GameMode.Competitive.bombSeconds);
        assertEquals(0, GameMode.Deathmatch.roundSeconds);
        assertEquals(0, GameMode.Deathmatch.bombSeconds);
        // The numeric fallback in toEnum depends on the order of the constants.
        assertEquals(GameMode.Competitive, Node.toEnum(GameMode.class, "2"));
    }

    @Test
    void weaponInfoKnowsWhichWeaponsReactToSecondaryFire() {
        assertTrue(WeaponInfo.Knife.hasSecondaryFire);
        assertTrue(WeaponInfo.UspS.hasSecondaryFire);
        assertTrue(WeaponInfo.Awp.hasSecondaryFire);
        assertTrue(WeaponInfo.SmokeGrenade.hasSecondaryFire);
        assertFalse(WeaponInfo.Ak47.hasSecondaryFire);
        assertFalse(WeaponInfo.Zeus.hasSecondaryFire);
        assertFalse(WeaponInfo.C4.hasSecondaryFire);
        assertFalse(WeaponInfo.Undefined.hasSecondaryFire);
    }

    @Test
    void playerIsLocalOnlyWhenTheProviderNamesTheSameAccount() throws IOException {
        assertTrue(gameState.isLocalPlayer());

        JsonObject spectating = loadSampleGameState();
        spectating.getAsJsonObject("player").addProperty("steamid", "76561198000000002");
        assertFalse(new GameState(spectating).isLocalPlayer());

        JsonObject withoutProvider = loadSampleGameState();
        withoutProvider.remove("provider");
        assertFalse(new GameState(withoutProvider).isLocalPlayer());

        assertFalse(new GameState().isLocalPlayer());
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
