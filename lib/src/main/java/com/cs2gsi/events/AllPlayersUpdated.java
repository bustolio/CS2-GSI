package com.cs2gsi.events;

import com.cs2gsi.nodes.*;

/**
 * Event for overall All Players update.
 */
public class AllPlayersUpdated extends UpdateEvent<AllPlayers> {
    public AllPlayersUpdated(AllPlayers newValue, AllPlayers previousValue) {
        super(newValue, previousValue);
    }
}
