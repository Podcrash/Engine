package com.podcrash.api.mc.damage;

import org.bukkit.ChatColor;

/**
 * Simple interface to figure out what damaged
 * If a function takes a DamageSource, it is a lot more useful.
 * Ex: damage(LivingEntity victim, LivingEntity attacker, double damage, **() -> "Custom Source"**, boolean applyKb)
 *
 * It is encouraged to overwrite getPrefix()
 */
public interface DamageSource {
    String getName();
    default String getPrefix() {
        return ChatColor.DARK_AQUA.toString();
    }
}
