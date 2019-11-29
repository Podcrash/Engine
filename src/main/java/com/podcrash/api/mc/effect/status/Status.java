package com.podcrash.api.mc.effect.status;

import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;

public enum Status {
    BLIND("BLIND", PotionEffectType.BLINDNESS, true, true, 15),
    DIZZY("NAUSEA", PotionEffectType.CONFUSION, true, true, 9),
    POISON("POISON", PotionEffectType.POISON, true, true, 19),
    SPEED("SPEED", PotionEffectType.SPEED, true, false, 1),
    SLOW("SLOW", PotionEffectType.SLOW, true, true, 2),
    JUMP_BOOST("JUMP", PotionEffectType.JUMP, true, false, 8),
    FIRE_RESISTANCE("FIRE RESISTANCE", PotionEffectType.FIRE_RESISTANCE, true, false, 12),
    INVISIBILITY("INVISIBILITY", PotionEffectType.INVISIBILITY, true, false, 14),
    WITHER("WITHER", PotionEffectType.WITHER, true, true, 20),
    STRENGTH("Strength", PotionEffectType.INCREASE_DAMAGE, true, false, 5),
    WEAKNESS("Weakness", PotionEffectType.WEAKNESS, true, true, 5),
    RESISTANCE("Resistance", PotionEffectType.DAMAGE_RESISTANCE, true, false, 11),
    REGENERATION("Regeneration", PotionEffectType.REGENERATION, true, false, 10),
    ABSORPTION("Absorption", PotionEffectType.ABSORPTION, true, false, 22),

    FIRE("FIRE", null, false, true, 100),
    CLOAK("Cloak", null, false, false, 101),
    SILENCE("Silence", null, false, true, 102),
    SHOCK("Shock", null, false, true, 103),
    MARKED("Marked", null, false, true, 104),
    ROOTED("Rooted", null, false, true, 105),
    INEPTITUDE("Ineptitude", null, false, false, 107),
    GROUND("Crippled", null, false, true, 108),
    BLEED("Bleed", null, false, true, 109);

    private String name;
    private PotionEffectType potionEffectType;
    private boolean isVanilla;
    private boolean isNegative;
    private int id;

    Status(String name, PotionEffectType potionEffectType, boolean isVanilla, boolean isNegative, int id) {
        this.name = name;
        this.potionEffectType = potionEffectType;
        this.isVanilla = isVanilla;
        this.isNegative = isNegative;
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public boolean isVanilla() {
        return this.isVanilla;
    }

    public boolean isNegative() {
        return isNegative;
    }

    public int getId() {
        return id;
    }

    public PotionEffectType getVanilla() {
        if (getId() < 99) {
            PotionEffectType ptype = PotionEffectType.getById(getId());
            return ptype;
        }
        return null;
    }

    public PotionEffectType getPotionEffectType() {
        if(!isVanilla) return null;
        return potionEffectType;
    }

    public static Status getStatus(String name) {
        for (Status status : Arrays.asList(Status.values())) {
            if (name.equalsIgnoreCase(status.getName())) {
                return status;
            }
        }
        return null;
    }

    public static Status getStatus(int id) {
        for (Status status : Arrays.asList(Status.values())) {
            if (id == status.getId()) {
                return status;
            }
        }
        return null;
    }


}
