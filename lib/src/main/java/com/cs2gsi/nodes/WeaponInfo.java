package com.cs2gsi.nodes;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * The weapons the game knows, looked up by the name from the payload.
 */
public enum WeaponInfo {
    /** A name this list does not know, or no weapon at all. */
    Undefined("", "", FireMode.Undefined, 0),

    Glock("weapon_glock", "Glock-18", FireMode.SemiAutomatic, 2),
    P2000("weapon_hkp2000", "P2000", FireMode.SemiAutomatic, 2),
    UspS("weapon_usp_silencer", "USP-S", FireMode.SemiAutomatic, 2),
    P250("weapon_p250", "P250", FireMode.SemiAutomatic, 2),
    DualBerettas("weapon_elite", "Dual Berettas", FireMode.SemiAutomatic, 2),
    FiveSeven("weapon_fiveseven", "Five-SeveN", FireMode.SemiAutomatic, 2),
    Tec9("weapon_tec9", "Tec-9", FireMode.SemiAutomatic, 2),
    Cz75Auto("weapon_cz75a", "CZ75-Auto", FireMode.Automatic, 2),
    DesertEagle("weapon_deagle", "Desert Eagle", FireMode.SemiAutomatic, 2),
    R8Revolver("weapon_revolver", "R8 Revolver", FireMode.Revolver, 2),

    Mac10("weapon_mac10", "MAC-10", FireMode.Automatic, 1),
    Mp9("weapon_mp9", "MP9", FireMode.Automatic, 1),
    Mp7("weapon_mp7", "MP7", FireMode.Automatic, 1),
    Mp5Sd("weapon_mp5sd", "MP5-SD", FireMode.Automatic, 1),
    Ump45("weapon_ump45", "UMP-45", FireMode.Automatic, 1),
    P90("weapon_p90", "P90", FireMode.Automatic, 1),
    PpBizon("weapon_bizon", "PP-Bizon", FireMode.Automatic, 1),

    GalilAr("weapon_galilar", "Galil AR", FireMode.Automatic, 1),
    Famas("weapon_famas", "FAMAS", FireMode.Automatic, 1),
    Ak47("weapon_ak47", "AK-47", FireMode.Automatic, 1),
    M4a4("weapon_m4a1", "M4A4", FireMode.Automatic, 1),
    M4a1S("weapon_m4a1_silencer", "M4A1-S", FireMode.Automatic, 1),
    Sg553("weapon_sg556", "SG 553", FireMode.Automatic, 1),
    Aug("weapon_aug", "AUG", FireMode.Automatic, 1),

    Ssg08("weapon_ssg08", "SSG 08", FireMode.BoltAction, 1),
    Awp("weapon_awp", "AWP", FireMode.BoltAction, 1),
    Scar20("weapon_scar20", "SCAR-20", FireMode.Automatic, 1),
    G3sg1("weapon_g3sg1", "G3SG1", FireMode.Automatic, 1),

    Nova("weapon_nova", "Nova", FireMode.SemiAutomatic, 1),
    Xm1014("weapon_xm1014", "XM1014", FireMode.Automatic, 1),
    Mag7("weapon_mag7", "MAG-7", FireMode.SemiAutomatic, 1),
    SawedOff("weapon_sawedoff", "Sawed-Off", FireMode.SemiAutomatic, 1),

    M249("weapon_m249", "M249", FireMode.Automatic, 1),
    Negev("weapon_negev", "Negev", FireMode.Automatic, 1),

    Knife("weapon_knife", "Knife", FireMode.Automatic, 3),
    Zeus("weapon_taser", "Zeus x27", FireMode.SemiAutomatic, 3, 11),
    HeGrenade("weapon_hegrenade", "HE Grenade", FireMode.Undefined, 4, 6),
    Flashbang("weapon_flashbang", "Flashbang", FireMode.Undefined, 4, 7),
    SmokeGrenade("weapon_smokegrenade", "Smoke Grenade", FireMode.Undefined, 4, 8),
    DecoyGrenade("weapon_decoy", "Decoy Grenade", FireMode.Undefined, 4, 9),
    Molotov("weapon_molotov", "Molotov", FireMode.Undefined, 4, 10),
    IncendiaryGrenade("weapon_incgrenade", "Incendiary Grenade", FireMode.Undefined, 4, 10),
    C4("weapon_c4", "C4", FireMode.Undefined, 5);
    
    // toUnmodifiableMap throws on a duplicate gsiName, so a copied line fails at class load.
    // Fully qualified because this package has its own Map node.
    private static final java.util.Map<String, WeaponInfo> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(info -> info.gsiName, info -> info));

    /**
     * The name the game sends, e.g. {@code weapon_ak47}.
     */
    public final String gsiName;

    /**
     * The name a player knows, e.g. {@code AK-47}.
     */
    public final String displayName;

    /**
     * How the primary fire reacts to the attack button.
     */
    public final FireMode fireMode;

    /**
     * The number of the {@code slotN} command that selects the group of the weapon: 1 primary,
     * 2 pistol, 3 knife and Zeus, 4 grenades, 5 C4. 0 for {@code Undefined}.<br>
     * Knife and Zeus share slot 3 and all grenades share slot 4, so the game decides which one
     * the command draws. Not the same as {@link Weapon#index}, which is the position in the payload.
     */
    public final int slot;

    /**
     * The number of the {@code slotN} command that selects exactly this weapon, 0 if the game has
     * none: 6 HE grenade, 7 flashbang, 8 smoke grenade, 9 decoy grenade, 10 molotov and incendiary
     * grenade, 11 Zeus. A player can leave these commands unbound, {@link #slot} is the fallback.
     */
    public final int directSlot;

    WeaponInfo(String gsiName, String displayName, FireMode fireMode, int slot) {
        this(gsiName, displayName, fireMode, slot, 0);
    }

    WeaponInfo(String gsiName, String displayName, FireMode fireMode, int slot, int directSlot) {
        this.gsiName = gsiName;
        this.displayName = displayName;
        this.fireMode = fireMode;
        this.slot = slot;
        this.directSlot = directSlot;
    }

    /**
     * Finds the weapon for a payload name. Never throws, unknown names give {@code Undefined}.
     */
    public static WeaponInfo fromName(String name) {
        if (name == null || name.isEmpty()) {
            return Undefined;
        }

        // Every knife skin has its own name (weapon_knife_karambit, weapon_bayonet, ...).
        if (name.startsWith("weapon_knife") || name.equals("weapon_bayonet")) {
            return Knife;
        }

        return BY_NAME.getOrDefault(name, Undefined);
    }
}
