package com.cs2gsi.events.player;

import com.cs2gsi.nodes.*;

/**
 * Event for specific player's active weapon update.
 */
public class PlayerActiveWeaponChanged extends PlayerWeaponChanged {
    public PlayerActiveWeaponChanged(Weapon newValue, Weapon previousValue, Player player) {
        super(newValue, previousValue, player);
    }
}
