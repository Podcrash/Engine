package com.podcrash.api.mc.damage;

import org.bukkit.event.entity.EntityDamageEvent;

public enum Cause {
    /*
    MELEE - a regular melee hit
    MELEESKILL - a skill cause where the primary trigger was via a melee hit, ex: successful ripo, crippling blow, overwhelm etc
    SKILL - literally everything else
    BOW - duh
    NULL - for nothing
     */
    CUSTOM,
    FALL("FALL", "Fall"),
    FIRE_TICK("FIRE_TICK", "Fire"),
    FIRE("FIRE", "Fire"),
    WITHER("WITHER", "Wither"),
    SUICIDE,
    MELEE,
    MELEESKILL,
    PROJECTILE,
    DROWNING("DROWNING", "Drowning"),
    CONTACT("CONTACT", "Spiking XD"),
    POISON("POISON", "Poison"),
    SUFFOCATION("SUFFOCATION", "Suffocation"),
    VOID("VOID", "Void"),
    LAVA("LAVA", "Lava"),
    NULL;

    private String bukkitName;
    private String displayName;

    Cause() {
        this(null, "CUSTOM");
    }

    Cause(String bukkitName, String displayName) {
        this.bukkitName = bukkitName;
        this.displayName = displayName;
    }
    private static Cause[] details = Cause.values();

    public static Cause findByEntityDamageCause(EntityDamageEvent.DamageCause damageCause) {
        for(Cause cause : details) {
            if (damageCause.name().equals(cause.bukkitName))
                return cause;
        }
        return NULL;
    }

    public String getDisplayName() {
        return displayName;
    }
}
