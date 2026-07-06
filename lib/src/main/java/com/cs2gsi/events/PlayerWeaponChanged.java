package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's weapon update.
 */
public class PlayerWeaponChanged extends PlayerUpdateEvent<Weapon> {
    public PlayerWeaponChanged(Weapon newValue, Weapon previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
