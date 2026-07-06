package com.cs2gsi.events;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

/**
 * Event for a specific weapon's clip ammo change.
 */
public class PlayerWeaponAmmoClipChanged extends PlayerUpdateEvent<Integer> {
    /**
     * The affected weapon (new state).
     */
    public final Weapon weapon;

    public PlayerWeaponAmmoClipChanged(Integer newValue, Integer previousValue, Weapon weapon, Player player) {
        super(newValue, previousValue, player);
        this.weapon = weapon;
    }
}
