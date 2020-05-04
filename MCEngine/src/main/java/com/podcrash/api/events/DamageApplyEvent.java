package com.podcrash.api.events;

import com.podcrash.api.damage.Cause;

import com.podcrash.api.damage.DamageSource;
import net.minecraft.server.v1_8_R3.ItemArmor;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Similar to the EntityDamageByEntityEvent
 */
public class DamageApplyEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;
    private boolean modified;

    private final LivingEntity victim;
    private final LivingEntity attacker;

    private double damage;
    private double changeXP;
    private double armorValueVictim;

    private final Cause cause;
    private final Arrow arrow;
    private final List<DamageSource> sources;

    private boolean doKnockback;
    //velocity multipliers that are multiplied onto the kb vectors
    private final double[] velocityModifier;

    /**
     * Dataclass for processing in the
     * @param victim - the entity that was attacked
     * @param attacker - the entity who attacked the victim
     * @param damage - how much damage the attacker dealt to the victim
     * @param arrow - what arrow from the attacker? (if applicable)
     * @param sources - the custom abilities that the attacker used
     * @param applyKnockback - if true, deal knockback
     */
    public DamageApplyEvent(LivingEntity victim, LivingEntity attacker, double damage, Cause cause, Arrow arrow, List<DamageSource> sources, boolean applyKnockback) {
        super(true);
        this.modified = false;
        this.victim = victim;
        this.attacker = attacker;
        this.damage = damage;
        this.changeXP = damage;
        this.cause = cause;
        this.arrow = arrow;
        this.sources = sources;
        this.doKnockback = applyKnockback;
        this.velocityModifier = new double[]{1, 1, 1};
        this.armorValueVictim = armorValue(victim);
    }

    /**
     * See above
     * @param victim
     * @param attacker
     * @param damage
     * @param cause
     * @param arrow
     * @param source - the custom ability the attacker used
     * @param doKnockback
     */
    public DamageApplyEvent(LivingEntity victim, LivingEntity attacker, double damage, Cause cause, Arrow arrow, DamageSource source, boolean doKnockback) {
        this(victim, attacker, damage, cause, arrow, new ArrayList<>(Collections.singletonList(source)), doKnockback);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }
    @Override
    public void setCancelled(boolean b) {
        cancelled = b;
    }

    /**
     * If a damage is changed, then this must be set to true for the damage to go through.
     * This implementation is done so that we can cancel damage modifications.
     */
    public boolean isModified() {
        return modified;
    }
    public void setModified(boolean modified) {
        this.modified = modified;
    }

    /**
     * XP is what's shown in the XP bar.
     * It is usually how much damage you dealt, but it can be changed with
     * @see #setChangeXP(double)
     *
     */

    public double getChangeXP() {
        return changeXP;
    }
    public void setChangeXP(double changeXP) {
        this.changeXP = changeXP;
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
        this.damage = this.changeXP = damage;
    }

    public double getArmorValue() {
        return armorValueVictim;
    }

    public void setArmorValue(double newArmorValue) {
        this.armorValueVictim = newArmorValue;
    }

    public Cause getCause() {
        return cause;
    }
    public Arrow getArrow() {
        return arrow;
    }

    public void addSource(DamageSource source) {
        sources.add(source);
    }
    public boolean containsSource(DamageSource source) {
        return sources.contains(source);
    }
    public boolean isDoKnockback() {
        return doKnockback;
    }
    public void setDoKnockback(boolean knockback) {
        this.doKnockback = knockback;
    }

    public double[] getVelocityModifiers() {
        return velocityModifier;
    }
    public double getVelocityModifierX() {
        return velocityModifier[0];
    }
    public double getVelocityModifierY() {
        return velocityModifier[1];
    }
    public double getVelocityModifierZ() {
        return velocityModifier[2];
    }

    public void setVelocityModifierX(double value) {
        this.velocityModifier[0] = value;
    }
    public void setVelocityModifierY(double value) {
        this.velocityModifier[1] = value;
    }
    public void setVelocityModifierZ(double value) {
        this.velocityModifier[2] = value;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
    public static HandlerList getHandlerList() {
        return handlers;
    }


    /**
     * Calculate the armor value of the entity.
     * Very important in finding how much damage an entity should recieve.
     * @param entity the entity
     * @return armor value of the entity
     */
    private int armorValue(LivingEntity entity) {
        int i = 0;
        for(ItemStack armor : entity.getEquipment().getArmorContents()) {
            net.minecraft.server.v1_8_R3.ItemStack nmsArmor = CraftItemStack.asNMSCopy(armor);
            if (nmsArmor != null && nmsArmor.getItem() instanceof ItemArmor)
                i += ((ItemArmor) nmsArmor.getItem()).c;
        }
        return i;
    }
}

