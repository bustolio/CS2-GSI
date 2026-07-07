package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerUpdateEvent;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's weapon update.
 */
public class PlayerWeaponChanged extends PlayerUpdateEvent<Weapon> {
    public PlayerWeaponChanged(Weapon newValue, Weapon previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
