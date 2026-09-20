package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the Weapon.
 */
public class Weapon extends Node {
    /**
     * The position of the weapon in the payload, taken from the {@code weapon_N} key
     * (e.g. 0 for {@code weapon_0}). -1 when unknown.<br>
     * This is not the slot a {@code slotN} command selects, see {@link WeaponInfo#slot}.
     */
    public final int index;

    /**
     * The weapon name.
     */
    public final String name;

    /**
     * What the library knows about the weapon behind {@link #name}.
     * {@code Undefined} for a name it does not know and for an empty weapon.
     */
    public final WeaponInfo info;

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

    public Weapon(JsonObject parsedData, int index) {
        super(parsedData);

        this.index = index;
        name = getString("name");
        info = WeaponInfo.fromName(name);
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
                + "Index: " + index + ", "
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
                && index == other.index
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
        hashCode = hashCode * -659784304 + Integer.hashCode(index);
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
