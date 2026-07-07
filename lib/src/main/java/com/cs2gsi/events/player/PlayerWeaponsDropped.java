package com.cs2gsi.events.player;

import com.cs2gsi.events.PlayerEvent;

import com.cs2gsi.nodes.Player;
import com.cs2gsi.nodes.Weapon;

import java.util.List;

/**
 * Event for specific player dropping weapons.
 */
public class PlayerWeaponsDropped extends PlayerEvent {
    /**
     * The dropped weapons.
     */
    public final List<Weapon> weapons;

    public PlayerWeaponsDropped(List<Weapon> weapons, Player player) {
        super(player);
        this.weapons = weapons;
    }
}
