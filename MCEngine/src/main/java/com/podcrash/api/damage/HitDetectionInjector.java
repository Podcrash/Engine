package com.podcrash.api.damage;

import com.google.common.collect.EvictingQueue;
import com.packetwrapper.abstractpackets.WrapperPlayClientUseEntity;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.podcrash.api.disguise.Disguise;
import com.podcrash.api.disguise.Disguiser;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.plugin.PodcrashSpigot;
import net.minecraft.server.v1_8_R3.EntityLiving;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.UUID;

/**
 * This class is focused on attaching the injectors to **attackers**.
 * This will remove the default Bukkit system and use our own.
 */
public final class HitDetectionInjector {
    private static final HashMap<String, HitDetectionInjector> injectors = new HashMap<>();
    public static long delay = 400; //this is in milliseconds to ticks
    private final PacketListener listener;
    private final HashMap<Integer, Long> delays = new HashMap<>();
    private final HashMap<String, Long> deathDelay = new HashMap<>();
    private final Player player;

    public static HitDetectionInjector getHitDetection(Player p) {
        return injectors.get(p.getName());
    }

    public HitDetectionInjector(Player p) {
        this.player = p;
        this.listener = new PacketAdapter(PodcrashSpigot.getInstance(), ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
                EnumWrappers.EntityUseAction action = packet.getType();
                Entity entity = packet.getTarget(player.getWorld());
                Player attacker = event.getPlayer();
                if (event.getPlayer() != player)
                    return;
                //if the action is not attack, cancel (allow living entities this time)
                if (action != EnumWrappers.EntityUseAction.ATTACK)
                    return;
                if (entity == null)
                    entity = findEntity(packet.getTargetID());
                if (!(entity instanceof LivingEntity))
                    return;
                LivingEntity victim = (LivingEntity) entity;
                //if the player is in spectator mode, don't bother
                if (attacker.getGameMode() == GameMode.SPECTATOR ||
                    (victim instanceof Player && ((Player) victim).getGameMode() == GameMode.SPECTATOR))
                    return;
                if (event.isCancelled()) //if the event is cancelled already, read it
                    return;

                event.setCancelled(true);
                //if there is still a delay, cancel
                if (delays.containsKey(victim.getEntityId())) {
                    long deltaTime = System.currentTimeMillis() - delays.get(victim.getEntityId());
                    if ((deltaTime) < delay)
                        return;
                }
                delays.put(victim.getEntityId(), System.currentTimeMillis());
                //victim has no death delay (avoids hitting a person while dead
                //the victim is invis
                //if the attacker is blocking
                //don't do anything
                if (/*isDeathDelay(victim) ||*/ isInvis(victim))
                    return;

                //Find the base damage
                double damage = findDamage(attacker);
                //Put melee damage in the queue
                DamageApplier.damage(victim, attacker, damage, true);
                //if the player does die
                if (!(victim instanceof Player))
                    return;
                if (calculateIfDeath(damage, victim)) {
                    Player playerVictim = (Player) victim;
                    //Give both death delays to avoid hitting each other.
                    manualDeathDelay(playerVictim);
                    getHitDetection(playerVictim).manualDeathDelay(player);
                }

                //Bukkit.broadcastMessage(String.format("Damager: %s Victim: %s", player.getName(), entityPlayer.getName()));
            }
        };
        injectors.put(p.getName(), this);
    }

    private Entity findEntity(int targetID) {
        Disguise possDisguise = Disguiser.getSeenDisguises().get(targetID);
        return possDisguise.getEntity();
    }
    /**
     * Inject the custom hit detection to any user
     */
    public void injectHitDetection() {
        ProtocolLibrary.getProtocolManager().addPacketListener(listener);
        PodcrashSpigot.getInstance().getLogger().info(player.getName() + " injected with hit detection.");
    }

    public void deinject() {
        ProtocolLibrary.getProtocolManager().removePacketListener(listener);
        injectors.remove(player.getName());
    }

    /**
     * Put a delay on a player's hits if they are dead
     * @param player To delay
     */
    public void manualDeathDelay(Player player) {
        deathDelay.put(player.getName(), System.currentTimeMillis() + delay);
    }

    /**
     * Check if the player still has a delay.
     * @param player - the player we are checking
     * @return if the player still has a delay.
     */
    private boolean isDeathDelay(LivingEntity player) {
        long time = deathDelay.getOrDefault(player.getName(), -1L);
        return time != -1L && System.currentTimeMillis() < time;
    }

    /**
     * See if the player is invisible
     * @param player The player to check
     * @return if they are invisible.
     */
    private boolean isInvis(LivingEntity player) {
        if (!(player instanceof Player)) return false;
        return StatusApplier.getOrNew((Player) player).isCloaked();
    }

    /**
     * Go through the formula and see if the player wiil die.
     * @param damage The damage dealt to victim
     * @param victim The victim getting damaged
     * @return if the victim will die
     */
    private boolean calculateIfDeath(double damage, LivingEntity victim) {
        EntityLiving entityLiving = ((CraftLivingEntity) victim).getHandle();
        int aV = entityLiving.br();
        double damageFormula = damage * (1D - 0.04D * aV);
        return victim.getHealth() - damageFormula <= 0;
    }
    /**
     * Find the amount of damage that the player can deal
     * This uses the original mineplex's strength/weakness system (+1/-1 respectively)
     * This means we are dividing out the 130% boost for strength, and etc
     *
     * If there is no item, return 1
     * @param attacker The player in question
     * @return the amount of damage
     */
    private double findDamage(LivingEntity attacker) {
        Material mat = attacker.getEquipment().getItemInHand().getType();
        if (mat == null || mat == Material.AIR)
            return 1D;
        double unfiltered = ((CraftLivingEntity) attacker).getHandle().getAttributeInstance(GenericAttributes.ATTACK_DAMAGE).getValue();
        PodcrashSpigot.getInstance().getLogger().info(unfiltered + "");
        if (unfiltered <= 0)
            return 0;
        for(PotionEffect effect : attacker.getActivePotionEffects()) {
            if (effect.getType().equals(PotionEffectType.INCREASE_DAMAGE))
                unfiltered /= 1D + (1.3D * (effect.getAmplifier() + 1D));
            if (effect.getType().equals(PotionEffectType.WEAKNESS))
                unfiltered += 0.5 * (effect.getAmplifier() + 1D);
        }
        PodcrashSpigot.getInstance().getLogger().info(unfiltered - 1 + "");
        return unfiltered - 1;
    }
}
