package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

/**
 * Event for a specific weapon's reserve ammo change.
 */
public class PlayerWeaponAmmoReserveChanged extends PlayerUpdateEvent<Integer> {
    /**
     * The affected weapon (new state).
     */
    public final Weapon weapon;

    public PlayerWeaponAmmoReserveChanged(Integer newValue, Integer previousValue, Weapon weapon, Player player) {
        super(newValue, previousValue, player);
        this.weapon = weapon;
    }
}
