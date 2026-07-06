package com.cs2gsi.viewer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

/**
 * Catalog of all concrete game event types, grouped by category.
 */
final class EventCatalog {
    private EventCatalog() {
    }

    /**
     * All concrete event class simple names, grouped by category (in display order).
     */
    static final LinkedHashMap<String, List<String>> CATEGORIES = new LinkedHashMap<>();

    /**
     * Events that fire on nearly every game tick. These start unchecked so the
     * log is not flooded by default.
     */
    static final Set<String> NOISY = Set.of(
            "AllGrenadesUpdated", "GrenadeUpdated", "GrenadePositionChanged", "GrenadeVelocityChanged",
            "GrenadeLifetimeChanged", "GrenadeEffectTimeChanged",
            "AllPlayersUpdated", "PlayerUpdated", "PlayerPositionChanged", "PlayerForwardDirectionChanged",
            "MapUpdated", "PhaseCountdownsUpdated", "PhaseEndTimeChanged",
            "BombUpdated", "BombPositionChanged", "RoundUpdated",
            "ProviderUpdated", "ProviderTimestampChanged");

    static {
        CATEGORIES.put("AllGrenades", List.of(
                "AllGrenadesUpdated",
                "GrenadeUpdated",
                "NewGrenade",
                "ExpiredGrenade",
                "GrenadePositionChanged",
                "GrenadeVelocityChanged",
                "GrenadeLifetimeChanged",
                "GrenadeEffectTimeChanged",
                "GrenadeFlamesChanged"));

        CATEGORIES.put("AllPlayers", List.of(
                "AllPlayersUpdated",
                "PlayerConnected",
                "PlayerDisconnected"));

        CATEGORIES.put("Auth", List.of(
                "AuthUpdated"));

        CATEGORIES.put("Bomb", List.of(
                "BombUpdated",
                "BombPlanting",
                "BombPlanted",
                "BombDefused",
                "BombDefusing",
                "BombDropped",
                "BombPickedup",
                "BombExploded",
                "BombPositionChanged",
                "BombPlayerChanged"));

        CATEGORIES.put("Killfeed", List.of(
                "KillFeed"));

        CATEGORIES.put("Map", List.of(
                "MapUpdated",
                "GamemodeChanged",
                "TeamStatisticsUpdated",
                "TeamScoreChanged",
                "TeamRemainingTimeoutsChanged",
                "RoundChanged",
                "RoundConcluded",
                "RoundStarted",
                "LevelChanged",
                "MapPhaseChanged",
                "WarmupStarted",
                "WarmupOver",
                "IntermissionStarted",
                "IntermissionOver",
                "FreezetimeStarted",
                "FreezetimeOver",
                "PauseStarted",
                "PauseOver",
                "TimeoutStarted",
                "TimeoutOver",
                "MatchStarted",
                "Gameover",
                "RoundWinsChanged",
                "CurrentSpectatorsChanged",
                "SouvenirsTotalChanged"));

        CATEGORIES.put("PhaseCountdowns", List.of(
                "PhaseCountdownsUpdated",
                "PhaseEndTimeChanged"));

        CATEGORIES.put("Player", List.of(
                "PlayerUpdated",
                "PlayerSteamIDChanged",
                "PlayerNameChanged",
                "PlayerClanChanged",
                "PlayerXPOverloadLevelChanged",
                "PlayerObserverSlotChanged",
                "PlayerSpectationTargetChanged",
                "PlayerPositionChanged",
                "PlayerForwardDirectionChanged",
                "PlayerTeamChanged",
                "PlayerActivityChanged",
                "PlayerStateChanged",
                "PlayerHealthChanged",
                "PlayerDied",
                "PlayerRespawned",
                "PlayerTookDamage",
                "PlayerArmorChanged",
                "PlayerHelmetChanged",
                "PlayerFlashAmountChanged",
                "PlayerSmokedAmountChanged",
                "PlayerBurningAmountChanged",
                "PlayerMoneyAmountChanged",
                "PlayerRoundKillsChanged",
                "PlayerRoundHeadshotKillsChanged",
                "PlayerRoundTotalDamageChanged",
                "PlayerEquipmentValueChanged",
                "PlayerDefusekitChanged",
                "PlayerWeaponChanged",
                "PlayerActiveWeaponChanged",
                "PlayerWeaponAmmoClipChanged",
                "PlayerWeaponAmmoReserveChanged",
                "PlayerWeaponsPickedUp",
                "PlayerWeaponsDropped",
                "PlayerStatsChanged",
                "PlayerKillsChanged",
                "PlayerGotKill",
                "PlayerAssistsChanged",
                "PlayerDeathsChanged",
                "PlayerMVPsChanged",
                "PlayerScoreChanged"));

        CATEGORIES.put("Provider", List.of(
                "ProviderUpdated",
                "ProviderNameChanged",
                "ProviderTimestampChanged"));

        CATEGORIES.put("Round", List.of(
                "RoundUpdated",
                "RoundPhaseUpdated",
                "BombStateUpdated",
                "TeamRoundVictory",
                "TeamRoundLoss"));
    }
}
