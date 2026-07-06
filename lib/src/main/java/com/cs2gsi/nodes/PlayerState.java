package com.cs2gsi.nodes;

import com.google.gson.JsonObject;

/**
 * Information about the Player State.
 */
public class PlayerState extends Node {
    /**
     * The player health amount.
     */
    public final int health;

    /**
     * The player armor amount.
     */
    public final int armor;

    /**
     * Does the player have a helmet?
     */
    public final boolean hasHelmet;

    /**
     * The amount the player is flashed. From 0 to 255.
     */
    public final int flashAmount;

    /**
     * The amount the player is smoked. From 0 to 255.
     */
    public final int smokedAmount;

    /**
     * The amount the player is burning. From 0 to 255.
     */
    public final int burningAmount;

    /**
     * The amount of money the player has.
     */
    public final int money;

    /**
     * The number of kills the player has in the current round.
     */
    public final int roundKills;

    /**
     * The number of headshot kills the player has in the current round.
     */
    public final int roundHSKills;

    /**
     * The total damage amount the player has earned in the current round.
     */
    public final int roundTotalDamage;

    /**
     * The total equipment value of the player.
     */
    public final int equipmentValue;

    /**
     * Does the player have a defuse kit?
     */
    public final boolean hasDefuseKit;

    public PlayerState() {
        this(null);
    }

    public PlayerState(JsonObject parsedData) {
        super(parsedData);

        health = getInt("health");
        armor = getInt("armor");
        hasHelmet = getBool("helmet");
        hasDefuseKit = getBool("defusekit");
        flashAmount = getInt("flashed");
        smokedAmount = getInt("smoked");
        burningAmount = getInt("burning");
        money = getInt("money");
        roundKills = getInt("round_kills");
        roundHSKills = getInt("round_killhs");
        roundTotalDamage = getInt("round_totaldmg");
        equipmentValue = getInt("equip_value");
    }

    @Override
    public String toString() {
        return "["
                + "Health: " + health + ", "
                + "Armor: " + armor + ", "
                + "HasHelmet: " + hasHelmet + ", "
                + "HasDefuseKit: " + hasDefuseKit + ", "
                + "FlashAmount: " + flashAmount + ", "
                + "SmokedAmount: " + smokedAmount + ", "
                + "BurningAmount: " + burningAmount + ", "
                + "Money: " + money + ", "
                + "RoundKills: " + roundKills + ", "
                + "RoundHSKills: " + roundHSKills + ", "
                + "RoundTotalDamage: " + roundTotalDamage + ", "
                + "EquipmentValue: " + equipmentValue
                + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        return obj instanceof PlayerState other
                && health == other.health
                && armor == other.armor
                && hasHelmet == other.hasHelmet
                && hasDefuseKit == other.hasDefuseKit
                && flashAmount == other.flashAmount
                && smokedAmount == other.smokedAmount
                && burningAmount == other.burningAmount
                && money == other.money
                && roundKills == other.roundKills
                && roundHSKills == other.roundHSKills
                && roundTotalDamage == other.roundTotalDamage
                && equipmentValue == other.equipmentValue;
    }

    @Override
    public int hashCode() {
        int hashCode = 871364069;
        hashCode = hashCode * -398756301 + Integer.hashCode(health);
        hashCode = hashCode * -398756301 + Integer.hashCode(armor);
        hashCode = hashCode * -398756301 + Boolean.hashCode(hasHelmet);
        hashCode = hashCode * -398756301 + Boolean.hashCode(hasDefuseKit);
        hashCode = hashCode * -398756301 + Integer.hashCode(flashAmount);
        hashCode = hashCode * -398756301 + Integer.hashCode(smokedAmount);
        hashCode = hashCode * -398756301 + Integer.hashCode(burningAmount);
        hashCode = hashCode * -398756301 + Integer.hashCode(money);
        hashCode = hashCode * -398756301 + Integer.hashCode(roundKills);
        hashCode = hashCode * -398756301 + Integer.hashCode(roundHSKills);
        hashCode = hashCode * -398756301 + Integer.hashCode(roundTotalDamage);
        hashCode = hashCode * -398756301 + Integer.hashCode(equipmentValue);
        return hashCode;
    }
}
