package com.cs2gsi.nodes;

import com.cs2gsi.nodes.helpers.Vector3D;
import com.google.gson.JsonObject;

/**
 * Information about the Bomb.
 */
public class Bomb extends Node {
    /**
     * The current bomb state.
     */
    public final BombState state;

    /**
     * The current bomb position.
     */
    public final Vector3D position;

    /**
     * The current player in possession of the bomb.
     */
    public final String player;

    /**
     * The current bomb countdown.
     */
    public final float countdown;

    public Bomb() {
        this(null);
    }

    public Bomb(JsonObject parsedData) {
        super(parsedData);

        state = getEnum(BombState.class, "state");
        position = new Vector3D(getString("position"));
        countdown = getFloat("countdown");
        player = getString("player");
    }

    @Override
    public String toString() {
        return "["
                + "State: " + state + ", "
                + "Position: " + position + ", "
                + "Countdown: " + countdown + ", "
                + "Player: " + player
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Bomb other
                && state == other.state
                && position.equals(other.position)
                && countdown == other.countdown
                && player.equals(other.player);
    }

    @Override
    public int hashCode() {
        int hashCode = 561468550;
        hashCode = hashCode * -604837261 + state.hashCode();
        hashCode = hashCode * -604837261 + position.hashCode();
        hashCode = hashCode * -604837261 + Float.hashCode(countdown);
        hashCode = hashCode * -604837261 + player.hashCode();
        return hashCode;
    }
}
