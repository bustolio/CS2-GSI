package com.cs2gsi.viewer;

import com.cs2gsi.events.BombDefusing;
import com.cs2gsi.events.BombPickedup;
import com.cs2gsi.events.BombPlanting;
import com.cs2gsi.events.CS2GameEvent;
import com.cs2gsi.events.EntityUpdateEvent;
import com.cs2gsi.events.EntityValueEvent;
import com.cs2gsi.events.KillFeed;
import com.cs2gsi.events.PlayerConnected;
import com.cs2gsi.events.PlayerDisconnected;
import com.cs2gsi.events.PlayerEvent;
import com.cs2gsi.events.PlayerGotKill;
import com.cs2gsi.events.PlayerUpdateEvent;
import com.cs2gsi.events.PlayerUpdated;
import com.cs2gsi.events.PlayerValueEvent;
import com.cs2gsi.events.PlayerWeaponAmmoClipChanged;
import com.cs2gsi.events.PlayerWeaponAmmoReserveChanged;
import com.cs2gsi.events.PlayerWeaponsDropped;
import com.cs2gsi.events.PlayerWeaponsPickedUp;
import com.cs2gsi.events.RoundConcluded;
import com.cs2gsi.events.RoundStarted;
import com.cs2gsi.events.TeamUpdateEvent;
import com.cs2gsi.events.TeamValueEvent;
import com.cs2gsi.events.TimeoutOver;
import com.cs2gsi.events.TimeoutStarted;
import com.cs2gsi.events.UpdateEvent;
import com.cs2gsi.events.ValueEvent;
import com.cs2gsi.nodes.Weapon;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Renders game events as short human-readable log lines.
 */
final class EventFormatter {
    private static final int MAX_DETAIL_LENGTH = 220;

    private EventFormatter() {
    }

    static String describe(CS2GameEvent e) {
        String name = e.getClass().getSimpleName();
        String detail = truncate(detailOf(e));

        return detail.isEmpty() ? name : name + "  |  " + detail;
    }

    private static String detailOf(CS2GameEvent e) {
        // Special cases first (most specific classes before their base classes).
        if (e instanceof KillFeed killFeed) {
            return killFeed.killer.name + " killed " + killFeed.victim.name
                    + " with " + killFeed.weapon.name + (killFeed.isHeadshot ? " (headshot)" : "");
        }

        if (e instanceof RoundConcluded roundConcluded) {
            return "round=" + roundConcluded.round
                    + " winner=" + roundConcluded.winningTeam
                    + " reason=" + roundConcluded.roundConclusionReason
                    + flags(roundConcluded.isFirstRound, roundConcluded.isLastRound);
        }

        if (e instanceof RoundStarted roundStarted) {
            return "round=" + roundStarted.round + flags(roundStarted.isFirstRound, roundStarted.isLastRound);
        }

        if (e instanceof TimeoutStarted timeoutStarted) {
            return "team=" + timeoutStarted.team;
        }

        if (e instanceof TimeoutOver timeoutOver) {
            return "team=" + timeoutOver.team;
        }

        if (e instanceof BombPlanting bombPlanting) {
            return "player=" + bombPlanting.player.name;
        }

        if (e instanceof BombDefusing bombDefusing) {
            return "player=" + bombDefusing.player.name;
        }

        if (e instanceof BombPickedup bombPickedup) {
            return "player=" + bombPickedup.player.name;
        }

        if (e instanceof PlayerGotKill gotKill) {
            return "player=" + gotKill.player.name + " weapon=" + gotKill.weapon.name
                    + (gotKill.isHeadshot ? " (headshot)" : "") + (gotKill.isAce ? " (ACE)" : "");
        }

        if (e instanceof PlayerWeaponsPickedUp pickedUp) {
            return "player=" + pickedUp.player.name + " weapons=" + weaponNames(pickedUp.weapons);
        }

        if (e instanceof PlayerWeaponsDropped dropped) {
            return "player=" + dropped.player.name + " weapons=" + weaponNames(dropped.weapons);
        }

        if (e instanceof PlayerWeaponAmmoClipChanged ammoClip) {
            return "player=" + ammoClip.player.name + " weapon=" + ammoClip.weapon.name
                    + " slot=" + ammoClip.weapon.slot
                    + "  " + ammoClip.previousValue + " -> " + ammoClip.newValue;
        }

        if (e instanceof PlayerWeaponAmmoReserveChanged ammoReserve) {
            return "player=" + ammoReserve.player.name + " weapon=" + ammoReserve.weapon.name
                    + " slot=" + ammoReserve.weapon.slot
                    + "  " + ammoReserve.previousValue + " -> " + ammoReserve.newValue;
        }

        if (e instanceof PlayerUpdated playerUpdated) {
            return "playerId=" + playerUpdated.playerId;
        }

        if (e instanceof PlayerConnected connected) {
            return "player=" + connected.value.name;
        }

        if (e instanceof PlayerDisconnected disconnected) {
            return "player=" + disconnected.value.name;
        }

        // Generic hierarchy fallbacks.
        if (e instanceof PlayerUpdateEvent<?> playerUpdate) {
            return "player=" + playerUpdate.player.name
                    + "  " + playerUpdate.previousValue + " -> " + playerUpdate.newValue;
        }

        if (e instanceof TeamUpdateEvent<?> teamUpdate) {
            return "team=" + teamUpdate.team
                    + "  " + teamUpdate.previousValue + " -> " + teamUpdate.newValue;
        }

        if (e instanceof EntityUpdateEvent<?> entityUpdate) {
            return "entity=" + entityUpdate.entityId
                    + "  " + entityUpdate.previousValue + " -> " + entityUpdate.newValue;
        }

        if (e instanceof UpdateEvent<?> update) {
            return update.previousValue + " -> " + update.newValue;
        }

        if (e instanceof PlayerValueEvent<?> playerValue) {
            return "player=" + playerValue.player.name + "  " + playerValue.value;
        }

        if (e instanceof TeamValueEvent<?> teamValue) {
            return "team=" + teamValue.team + "  " + teamValue.value;
        }

        if (e instanceof EntityValueEvent<?> entityValue) {
            return "entity=" + entityValue.entityId + "  " + entityValue.value;
        }

        if (e instanceof ValueEvent<?> value) {
            return String.valueOf(value.value);
        }

        if (e instanceof PlayerEvent playerEvent) {
            return "player=" + playerEvent.player.name;
        }

        return "";
    }

    private static String flags(boolean isFirstRound, boolean isLastRound) {
        return (isFirstRound ? " (first round)" : "") + (isLastRound ? " (last round)" : "");
    }

    private static String weaponNames(List<Weapon> weapons) {
        return weapons.stream().map(weapon -> weapon.name).collect(Collectors.joining(", ", "[", "]"));
    }

    private static String truncate(String detail) {
        if (detail.length() <= MAX_DETAIL_LENGTH) {
            return detail;
        }

        return detail.substring(0, MAX_DETAIL_LENGTH) + "...";
    }
}
