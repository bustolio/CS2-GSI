package com.cs2gsi.nodes;

import com.cs2gsi.nodes.helpers.Vector3D;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Information about the Player.
 */
public class Player extends Node {
    private static final Pattern WEAPON_PATTERN = Pattern.compile("weapon_(\\d+)");

    /**
     * The player's Steam ID.
     */
    public final String steamId;

    /**
     * The player's name.
     */
    public final String name;

    /**
     * The player's XP Overload level.
     */
    public final int xpOverloadLevel;

    /**
     * The player's clan.
     */
    public final String clan;

    /**
     * The current observed player.
     */
    public final int observerSlot;

    /**
     * The player's team.
     */
    public final PlayerTeam team;

    /**
     * The player's activity.
     */
    public final PlayerActivity activity;

    /**
     * The player's current state.
     */
    public final PlayerState state;

    /**
     * The player's weapons. The list is unmodifiable, the library compares it against the next game state.
     */
    public final List<Weapon> weapons;

    /**
     * The player's match statistics.
     */
    public final MatchStats matchStats;

    /**
     * The player's spectation target. (SPECTATOR ONLY)
     */
    public final String spectationTarget;

    /**
     * The player's position. (SPECTATOR ONLY)
     */
    public final Vector3D position;

    /**
     * The player's forward direction. (SPECTATOR ONLY)
     */
    public final Vector3D forwardDirection;

    public Player() {
        this(null, "");
    }

    public Player(JsonObject parsedData) {
        this(parsedData, "");
    }

    public Player(JsonObject parsedData, String fallbackSteamId) {
        super(parsedData);

        String retrievedSteamId = getString("steamid");
        steamId = (retrievedSteamId == null || retrievedSteamId.isBlank()) ? fallbackSteamId : retrievedSteamId;

        clan = getString("clan");
        name = getString("name");
        xpOverloadLevel = getInt("xpoverload");
        observerSlot = getInt("observer_slot");
        team = getEnum(PlayerTeam.class, "team");
        activity = getEnum(PlayerActivity.class, "activity");
        state = new PlayerState(getJObject("state"));

        List<Weapon> parsedWeapons = new ArrayList<>();
        getMatchingObjects(getJObject("weapons"), WEAPON_PATTERN, (matcher, obj) ->
                parsedWeapons.add(new Weapon(obj, Integer.parseInt(matcher.group(1)))));
        weapons = List.copyOf(parsedWeapons);

        matchStats = new MatchStats(getJObject("match_stats"));
        spectationTarget = getString("spectarget");
        position = new Vector3D(getString("position"));
        forwardDirection = new Vector3D(getString("forward"));
    }

    /**
     * Gets the active weapon.
     *
     * @return The active weapon.
     */
    public Weapon getActiveWeapon() {
        for (Weapon weapon : weapons) {
            if (weapon.state == WeaponState.Active || weapon.state == WeaponState.Reloading) {
                return weapon;
            }
        }

        // No active weapon.
        return new Weapon();
    }

    @Override
    public String toString() {
        return "["
                + "SteamID: " + steamId + ", "
                + "Clan: " + clan + ", "
                + "Name: " + name + ", "
                + "XPOverloadLevel: " + xpOverloadLevel + ", "
                + "ObserverSlot: " + observerSlot + ", "
                + "Team: " + team + ", "
                + "Activity: " + activity + ", "
                + "State: " + state + ", "
                + "Weapons: " + weapons + ", "
                + "MatchStats: " + matchStats + ", "
                + "SpectationTarget: " + spectationTarget + ", "
                + "Position: " + position + ", "
                + "ForwardDirection: " + forwardDirection
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Player other
                && steamId.equals(other.steamId)
                && clan.equals(other.clan)
                && name.equals(other.name)
                && xpOverloadLevel == other.xpOverloadLevel
                && observerSlot == other.observerSlot
                && team == other.team
                && activity == other.activity
                && state.equals(other.state)
                && weapons.equals(other.weapons)
                && matchStats.equals(other.matchStats)
                && spectationTarget.equals(other.spectationTarget)
                && position.equals(other.position)
                && forwardDirection.equals(other.forwardDirection);
    }

    @Override
    public int hashCode() {
        int hashCode = 654302810;
        hashCode = hashCode * -658414789 + steamId.hashCode();
        hashCode = hashCode * -658414789 + clan.hashCode();
        hashCode = hashCode * -658414789 + name.hashCode();
        hashCode = hashCode * -658414789 + Integer.hashCode(xpOverloadLevel);
        hashCode = hashCode * -658414789 + Integer.hashCode(observerSlot);
        hashCode = hashCode * -658414789 + team.hashCode();
        hashCode = hashCode * -658414789 + activity.hashCode();
        hashCode = hashCode * -658414789 + state.hashCode();
        hashCode = hashCode * -658414789 + weapons.hashCode();
        hashCode = hashCode * -658414789 + matchStats.hashCode();
        hashCode = hashCode * -658414789 + spectationTarget.hashCode();
        hashCode = hashCode * -658414789 + position.hashCode();
        hashCode = hashCode * -658414789 + forwardDirection.hashCode();
        return hashCode;
    }
}
