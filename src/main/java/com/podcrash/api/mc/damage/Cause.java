package com.podcrash.api.mc.damage;

public enum Cause {
    /*
    MELEE - a regular melee hit
    MELEESKILL - a skill cause where the primary trigger was via a melee hit, ex: successful ripo, crippling blow, overwhelm etc
    SKILL - literally everything else
    BOW - duh
    NULL - for nothing
     */
    CUSTOM, MELEE, MELEESKILL, PROJECTILE, NULL;
    private Cause() {

    }
}
