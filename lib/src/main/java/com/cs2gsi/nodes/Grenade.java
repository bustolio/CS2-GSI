package com.cs2gsi.nodes;

import com.cs2gsi.nodes.helpers.Vector3D;
import com.google.gson.JsonObject;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

/**
 * Information about a Grenade.
 */
public class Grenade extends Node {
    private static final Pattern FLAME_PATTERN = Pattern.compile("flame_.+");

    /**
     * The owner of the grenade.
     */
    public final String owner;

    /**
     * The grenade position.
     */
    public final Vector3D position;

    /**
     * The grenade velocity.
     */
    public final Vector3D velocity;

    /**
     * The grenade lifetime (in seconds).
     */
    public final float lifetime;

    /**
     * The grenade type.
     */
    public final GrenadeType type;

    /**
     * The flame locations. Key is the flame ID, value is the flame location.
     */
    public final LinkedHashMap<String, Vector3D> flames = new LinkedHashMap<>();

    /**
     * The grenade's effect time (in seconds).
     */
    public final float effectTime;

    public Grenade() {
        this(null);
    }

    public Grenade(JsonObject parsedData) {
        super(parsedData);

        owner = getString("owner");
        position = new Vector3D(getString("position"));
        velocity = new Vector3D(getString("velocity"));
        lifetime = getFloat("lifetime");
        type = getEnum(GrenadeType.class, "type");

        getMatchingStrings(getJObject("flames"), FLAME_PATTERN, (matcher, str) -> {
            String flameId = matcher.group(0);
            Vector3D location = new Vector3D(str);

            flames.put(flameId, location);
        });

        effectTime = getFloat("effecttime");
    }

    @Override
    public String toString() {
        return "["
                + "Owner: " + owner + ", "
                + "Position: " + position + ", "
                + "Velocity: " + velocity + ", "
                + "Lifetime: " + lifetime + ", "
                + "Type: " + type + ", "
                + "Flames: " + flames + ", "
                + "EffectTime: " + effectTime
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Grenade other
                && owner.equals(other.owner)
                && position.equals(other.position)
                && velocity.equals(other.velocity)
                && lifetime == other.lifetime
                && type == other.type
                && flames.equals(other.flames)
                && effectTime == other.effectTime;
    }

    @Override
    public int hashCode() {
        int hashCode = 120353541;
        hashCode = hashCode * -849620394 + owner.hashCode();
        hashCode = hashCode * -849620394 + position.hashCode();
        hashCode = hashCode * -849620394 + velocity.hashCode();
        hashCode = hashCode * -849620394 + Float.hashCode(lifetime);
        hashCode = hashCode * -849620394 + type.hashCode();
        hashCode = hashCode * -849620394 + flames.hashCode();
        hashCode = hashCode * -849620394 + Float.hashCode(effectTime);
        return hashCode;
    }
}
