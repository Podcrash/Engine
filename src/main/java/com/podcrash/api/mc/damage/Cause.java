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
    FALL("FALL"),
    FIRE("FIRE_TICK"),
    WITHER("WITHER"),
    SUICIDE,
    MELEE,
    MELEESKILL,
    PROJECTILE,
    POISON("POISON"),
    NULL;

    private String bukkitName;

    Cause() {
        this(null);
    }

    Cause(String bukkitName) {
        this.bukkitName = bukkitName;
    }
    private static Cause[] details = Cause.values();

    public static Cause findByEntityDamageCause(EntityDamageEvent.DamageCause damageCause) {
        for(Cause cause : details) {
            if (damageCause.name().equals(cause.bukkitName))
                return cause;
        }
        return NULL;
    }
}
