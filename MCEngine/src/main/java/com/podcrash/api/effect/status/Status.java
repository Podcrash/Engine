package com.podcrash.api.effect.status;

import org.bukkit.potion.PotionEffectType;

public enum Status {
    SATURATION("SATURATION", PotionEffectType.SATURATION, true, false, 23),
    HASTE("HASTE", PotionEffectType.FAST_DIGGING, true, false, 3),
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
    WEAKNESS("Weakness", PotionEffectType.WEAKNESS, true, true, 18),
    RESISTANCE("Resistance", PotionEffectType.DAMAGE_RESISTANCE, true, false, 11),
    REGENERATION("Regeneration", PotionEffectType.REGENERATION, true, false, 10),
    ABSORPTION("Absorption", PotionEffectType.ABSORPTION, true, false, 22),
    HEALTH_BOOST("HEALTH BOOST", PotionEffectType.HEALTH_BOOST, true, false, 21),

    FIRE("FIRE", null, false, true, 100),
    CLOAK("Cloak", null, false, false, 101),
    SILENCE("Silence", null, false, true, 102),
    SHOCK("Shock", null, false, true, 103),
    MARKED("Marked", null, false, true, 104),
    ROOTED("Rooted", null, false, true, 105),
    INEPTITUDE("Ineptitude", null, false, false, 107),
    GROUND("Crippled", null, false, true, 108),
    BLEED("Bleed", null, false, true, 109);

    private final String name;
    private final PotionEffectType potionEffectType;
    private final boolean isVanilla;
    private final boolean isNegative;
    private final int id;

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
        if (getId() < 99)
            return PotionEffectType.getById(getId());
        return null;
    }

    PotionEffectType getPotionEffectType() {
        if (!isVanilla)
            throw new IllegalStateException("Vanilla potions must have isVanilla");
        return potionEffectType;
    }

    public static Status getStatus(String name) {
        for (Status status : Status.values()) {
            if (name.equalsIgnoreCase(status.getName())) {
                return status;
            }
        }
        return null;
    }

    public static Status getStatus(int id) {
        for (Status status : Status.values()) {
            if (id == status.getId())
                return status;
        }
        return null;
    }


}
