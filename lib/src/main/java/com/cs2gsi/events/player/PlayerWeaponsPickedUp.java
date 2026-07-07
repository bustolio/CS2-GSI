package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerEvent;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

import java.util.List;

/**
 * Event for specific player picking up weapons.
 */
public class PlayerWeaponsPickedUp extends PlayerEvent {
    /**
     * The picked up weapons.
     */
    public final List<Weapon> weapons;

    public PlayerWeaponsPickedUp(List<Weapon> weapons, Player player) {
        super(player);
        this.weapons = weapons;
    }
}
