package com.podcrash.api.mc.damage;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Damage {
    private final LivingEntity victim;
    private final LivingEntity attacker;

    private double damage;

    private final ItemStack item;
    private final Cause damageCause;
    private final Arrow arrow;
    private final List<DamageSource> source;

    private final boolean applyKnockback;

    private final long time;

    /**
     * Dataclass for processing in the
     * @see DamageQueue#run()
     * @param victim - the entity that was attacked
     * @param attacker - the entity who attacked the victim
     * @param damage - how much damage the attacker dealt to the victim
     * @param itemStack - what item was the attacker holding (if applicable)
     * @param damageCause - what cause? This part is very loose however
     * @param arrow - what arrow from the attacker? (if applicable)
     * @param source - the custom ability that the attacker used
     * @param applyKnockback - if true, deal knockback
     */
    public Damage(LivingEntity victim, LivingEntity attacker, double damage, ItemStack itemStack, Cause damageCause, Arrow arrow, List<DamageSource> source, boolean applyKnockback) {
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
        this.item = itemStack;
        this.damageCause = damageCause;
        this.arrow = arrow;
        this.source = source;
        this.applyKnockback = applyKnockback;
        this.time = System.currentTimeMillis();
    }

    public Damage(LivingEntity victim, LivingEntity attacker, double damage, ItemStack item, Cause damageCause, Arrow arrow, DamageSource source, boolean applyKnockback) {
        this(victim, attacker, damage, item, damageCause, arrow, new ArrayList<>(Collections.singletonList(source)), applyKnockback);
    }



    public LivingEntity getVictim() {
        return victim;
    }
    public LivingEntity getAttacker() {
        return attacker;
    }

    public double getDamage() {
        return damage;
    }
    public void setDamage(double damage) {
        this.damage = damage;
    }

    public ItemStack getItem() {
        return item;
    }
    public Cause getCause() {
        return damageCause;
    }
    public Arrow getArrow() {
        return arrow;
    }
    public List<DamageSource> getSource() {
        return source;
    }

    public boolean isApplyKnockback() {
        return applyKnockback;
    }

    public long getTime() {
        return time;
    }

    @Override
    public String toString() {
        return "Damage{" +
                "victim=" + victim +
                ", attacker=" + attacker +
                ", damage=" + damage +
                ", damageCause=" + damageCause +
                '}';
    }
}
