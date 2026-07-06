package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the Weapon.
 */
public class Weapon extends Node {
    /**
     * The inventory slot index, taken from the {@code weapon_N} key
     * (e.g. 0 for {@code weapon_0}). -1 when unknown.
     */
    public final int slot;

    /**
     * The weapon name.
     */
    public final String name;

    /**
     * The weapon skin name.
     */
    public final String paintKit;

    /**
     * The weapon type.
     */
    public final WeaponType type;

    /**
     * The amount of ammo in the weapon clip.
     */
    public final int ammoClip;

    /**
     * The maximum amount of ammo in the weapon clip.
     */
    public final int ammoClipMax;

    /**
     * The amount of ammo for the weapon in reserves.
     */
    public final int ammoReserve;

    /**
     * The weapon state.
     */
    public final WeaponState state;

    public Weapon() {
        this(null, -1);
    }

    public Weapon(JsonObject parsedData) {
        this(parsedData, -1);
    }

    public Weapon(JsonObject parsedData, int slot) {
        super(parsedData);

        this.slot = slot;
        name = getString("name");
        paintKit = getString("paintkit");
        type = getEnum(WeaponType.class, "type");
        ammoClip = getInt("ammo_clip");
        ammoClipMax = getInt("ammo_clip_max");
        ammoReserve = getInt("ammo_reserve");
        state = getEnum(WeaponState.class, "state");
    }

    @Override
    public String toString() {
        return "["
                + "Slot: " + slot + ", "
                + "Name: " + name + ", "
                + "PaintKit: " + paintKit + ", "
                + "Type: " + type + ", "
                + "AmmoClip: " + ammoClip + ", "
                + "AmmoClipMax: " + ammoClipMax + ", "
                + "AmmoReserve: " + ammoReserve + ", "
                + "State: " + state
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof Weapon other
                && slot == other.slot
                && name.equals(other.name)
                && paintKit.equals(other.paintKit)
                && type == other.type
                && ammoClip == other.ammoClip
                && ammoClipMax == other.ammoClipMax
                && ammoReserve == other.ammoReserve
                && state == other.state;
    }

    @Override
    public int hashCode() {
        int hashCode = 896140043;
        hashCode = hashCode * -659784304 + Integer.hashCode(slot);
        hashCode = hashCode * -659784304 + name.hashCode();
        hashCode = hashCode * -659784304 + paintKit.hashCode();
        hashCode = hashCode * -659784304 + type.hashCode();
        hashCode = hashCode * -659784304 + Integer.hashCode(ammoClip);
        hashCode = hashCode * -659784304 + Integer.hashCode(ammoClipMax);
        hashCode = hashCode * -659784304 + Integer.hashCode(ammoReserve);
        hashCode = hashCode * -659784304 + state.hashCode();
        return hashCode;
    }
}
