package com.podcrash.api.damage;

import com.comphenix.protocol.PacketType;
import com.packetwrapper.abstractpackets.WrapperPlayServerEntityStatus;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.events.DeathApplyEvent;
import com.podcrash.api.events.SoundApplyEvent;
import com.podcrash.api.events.game.GameDamageEvent;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.PacketUtil;
import com.podcrash.api.plugin.PodcrashSpigot;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

public final class DamageQueue implements Runnable {
    public static boolean active = false;
    private static final Deque<Damage> damages = new ArrayDeque<>();
    private static final Deque<String> deadPlayers = new ArrayDeque<>();
    //String = victim, stack damage - past damage instances -> used to find last damage cause for death events
    private static final Map<String, Deque<Damage>> damageHistory = new HashMap<>();

    public DamageQueue() {
        PodcrashSpigot.getInstance().getLogger().info("Starting the Damage Queue!");
    }

    /**
     * Processes the damages using Thread.
     * @see Thread#run()
     */
    @Override
    public void run() {
        try {
            while (active && damages.peek() != null)
                processDamage(damages.poll());
        }catch (Exception e) {
            e.printStackTrace();
        }
    }


    //TODO FIX THESE
    private void addDeath(Player player) {
        deadPlayers.add(player.getName());
    }
    private void removeDeath(Player player) {
        deadPlayers.remove(player.getName());
    }
    private static boolean hasDeath(Player player) {
        return deadPlayers.contains(player.getName());
    }

    /**
     * Gets a name to be used as a key for {@link DamageQueue#damageHistory}.
     * If the entity is a player, just return its player name.
     * If it's a mob, return its type and concat its id.
     * @param entity  The entity whose name will be extracted
     * @return the name of the entity to be used for a key
     */
    private static String getNameFor(Entity entity) {
        if (entity instanceof Player) {
            return entity.getName();
        } else {
            String name = entity.getName();
            if (name == null)
                name = entity.getCustomName();
            if (name == null)
                name = entity.getType().name() + entity.getEntityId();
            return name;
        }
    }

    /**
     * Adds as a cache to the victim, used for player deaths.
     * @param victim - the entity that is getting attacked
     * @param damageWrapper - the damage dataclass concerning the victim
     */
    private void addHistory(Entity victim, Damage damageWrapper) {
        String name = getNameFor(victim);
        if (!damageHistory.containsKey(name))
            damageHistory.put(name, new ArrayDeque<>());
        damageHistory.get(name).add(damageWrapper);
    }

    /**
     * Self-explanatory, clear the victim's history (presumably after the victim dies)
     * @param victimName the key for the {@link DamageQueue#damageHistory)
     */
    private void clearHistory(String victimName) {
        if (damageHistory.containsKey(victimName))
            damageHistory.get(victimName).clear();
    }

    /**
     * Clears the history of the Entity, presumably after it dies.
     * @see DamageQueue#clearHistory(String)
     * @param victim The victim to clear
     */
    private void clearHistory(Entity victim) {
        clearHistory(getNameFor(victim));
    }

    /**
     * Damage the entity with an amount of damage specified.
     * This uses setHealth, instead of damage, so that some stuff are skipped by the Bukkit API.
     * Because of that, a lot of this stuff is experimental.
     * This method also handles the damaging part synchronously, which is required.
     * @param entity the entity that will be damaged
     * @param damage the amount of damage
     *
     * @return if the entity will die
     */
    private boolean damageEntity(LivingEntity entity, double damage) {
        //handle absorption health
        double absorp = getAbsorptionHealth(entity);
        double nowAbsorp = absorp - damage;

        //set absorption hp
        setAbsorptionHealth(entity, nowAbsorp);

        //handle damage, only if nowAbsorp turns out to be negative
        if (nowAbsorp > 0)
            return false;
        double nowHealth = entity.getHealth() + nowAbsorp;
        if (nowHealth > entity.getMaxHealth()) //this will never happen, but just in case
            nowHealth = entity.getMaxHealth();

        if (nowHealth <= 0) {
            if (entity instanceof Player) {
                PlayerInventory inventory = ((Player) entity).getInventory();
                inventory.clear();
                inventory.setArmorContents(new ItemStack[]{null, null, null, null});
            }else entity.setHealth(0);
            SoundPlayer.sendSound(entity.getLocation(), "game.neutral.die", 1, 75);
            die(entity);
            return true;
        } else {
            entity.setHealth(nowHealth);
        }

        return false;
    }

    private double getAbsorptionHealth(Entity player) {
        CraftEntity craftEntity = (CraftEntity) player;
        EntityLiving livingCraft = (EntityLiving) craftEntity.getHandle();
        return livingCraft.getAbsorptionHearts();
    }
    private void setAbsorptionHealth(Entity player, double health) {
        CraftEntity craftEntity = (CraftEntity) player;
        EntityLiving livingCraft = (EntityLiving) craftEntity.getHandle();
        livingCraft.setAbsorptionHearts((float) health);
    }

    /**
     * Calls the GameDamageEvent to see if the damage should be processed.
     * @return if the damage should be processed
     */
    private boolean evaluateGame(LivingEntity victim, LivingEntity attacker) {
        Game game = GameManager.getGame();
        //if there's no game, then it's fine to process the damage
        //if it isn't ongoing, return false;
        if (game == null)
            return true;
        if (game.getGameState() == GameState.LOBBY)
            return true;
        //if the entities involved aren't players, then process the damage
        if (!(victim instanceof Player))
            return true; //process non players as normal enemies (may need refactor)
        GameDamageEvent event = new GameDamageEvent(game, (Player) victim, (Player) attacker);
        Bukkit.getPluginManager().callEvent(event);
        return !event.isCancelled();
    }
    /**
     * Custom strength/resistance/weakness handling.
     * Where x is the potency of each potion:
     * attacker's strength = + x
     * attacker's weakness = - x
     * victim's resistance = - x
     * @param victim The victim
     * @param attacker The attacker
     * @return the bonus for dealing more or less damage.
     */
    private int findPotionBonus(LivingEntity victim, LivingEntity attacker) {
        int bonus = 0;
        for (PotionEffect potion : attacker.getActivePotionEffects()) {
            if (potion.getType().equals(PotionEffectType.INCREASE_DAMAGE))
                bonus += potion.getAmplifier() + 1;
            if (potion.getType().equals(PotionEffectType.WEAKNESS))
                bonus -= potion.getAmplifier() + 1;
        }
        for (PotionEffect potion : victim.getActivePotionEffects()) {
            if (potion.getType().equals(PotionEffectType.DAMAGE_RESISTANCE))
                bonus -= potion.getAmplifier() + 1;
        }
        return bonus;
    }

    /**
     * Damage calculations for {@link DamageQueue#damageEntity(LivingEntity, double)}
     * @param entity the victim
     * @param damage the unfiltered damage
     * @param damageEvent the event to process the damage
     */
    private void damage(LivingEntity entity, double damage, double armorValue, DamageApplyEvent damageEvent) {
        if (damage < 0)
            damage = 0;
        double damageFormula = damage * (1D - 0.04D * armorValue);
        //Bukkit.broadcastMessage("AV: " + armorValue  + " " + damage + " --> " + damageFormula);
        if (damageEntity(entity, damageFormula))
            return;
        if (!damageEvent.isDoKnockback())
            return;
        //if rooted don't deal the kb
        if (StatusApplier.getOrNew(entity).isRooted())
            return;
        Cause cause = damageEvent.getCause();
        LivingEntity victim = damageEvent.getVictim();
        LivingEntity attacker = damageEvent.getAttacker();
        double[] modifiers = findVectorModifiers(damageEvent.getVelocityModifiers(), cause, damage);
        applyKnockback(victim, attacker, modifiers);
    }

    /**
     * On the offchance that an entity dies right after they get damaged.
     * Note: this does not cover fire damage, wither damage, and the like.
     * @param victim The entity that possibly dies
     * @return
     */
    private boolean die(LivingEntity victim) {
        String name = getNameFor(victim);
        Deque<Damage> history = damageHistory.get(name);
        if (history.size() == 0)
            return false;
        if (!(victim instanceof Player))
            return false;
        addDeath((Player) victim);
        Damage damage = history.getLast();

        DeathApplyEvent deathEvent = new DeathApplyEvent(damage, history);
        Bukkit.getPluginManager().callEvent(deathEvent);
        removeDeath((Player) victim);
        if (deathEvent.isCancelled())
            return false;
        clearHistory(name);
        return true;
    }

    /**
     * Plays a sound for the victim.
     * If the attacker shot the victim with a bow, play a satisfying ding sound.
     *
     * Might just use the game damage event to put in the sounds!
     * @param victim The victim who gets shot
     * @param attacker The attacker to
     * @param cause The cause of the sound
     */
    private void playSound(LivingEntity victim, LivingEntity attacker, Cause cause) {
     //   if (victim instanceof Player) {
            //ChampionsPlayer championVictim = ChampionsPlayerManager.getInstance().getKitPlayer((Player) victim);
            /*if (championVictim != null)
                SoundPlayer.sendSound(victim.getLocation(), championVictim.getSound());*/
      //  }
        //if it was a bow arrow hit, play the shooter a satisfying ding sound we all know.
        if (attacker instanceof Player && cause == Cause.PROJECTILE)
            SoundPlayer.sendSound((Player) attacker, "random.successful_hit", 0.8F, 20);

        SoundApplyEvent sound = new SoundApplyEvent(victim, attacker, null);

        Bukkit.getPluginManager().callEvent(sound);
        //By default, the sound is null, but if we want to make another sound, do so.
        if (sound.isCancelled() || sound.getSound() == null || !sound.getSound().isValid())
            return;
        SoundPlayer.sendSound(victim.getLocation(), sound.getSound());
    }

    private double[] findVectorModifiers(double[] velocity, Cause cause, double damage) {
        if (cause == Cause.PROJECTILE) {
            if (damage < 1) damage = 1;
            double multiplier = (Math.log(damage) / 3d);
            if (multiplier < 0) multiplier = 1; //uhs
            velocity[0] *= multiplier;
            velocity[2] *= multiplier;
        }//else if ((cause == Cause.MELEE || cause == Cause.MELEESKILL)) {
            /*
            double multiplier = .05 * damage + 0.60;
            velocity[0] *= multiplier;
            velocity[1] *= multiplier;
            velocity[2] *= multiplier;
            
             */
        //}
        return velocity;
    }

    /**
     * See {@link DamageApplier#nativeApplyKnockback(LivingEntity, LivingEntity, double[])}
     * @param victim Victim of the knockback
     * @param attacker Entity that deals the knockback
     * @param velocityModifiers Any modifiers to the velocity in the form of {x,y,z}
     */
    private void applyKnockback(LivingEntity victim, LivingEntity attacker, double[] velocityModifiers) {
        if (velocityModifiers == null)
            velocityModifiers = new double[] {1D, 1D, 1D};
        DamageApplier.nativeApplyKnockback(victim, attacker, velocityModifiers);
    }

    /**
     * Send the attack packets
     */
    private void sendUsePacket(LivingEntity victim){
        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(victim.getEntityId());
        packet.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);
        PacketUtil.syncSend(packet, victim.getWorld().getPlayers());
    }
    /**
     * The main method of this runnable. It does all of the above.
     * @param damageWrapper The damage object to process
     */
    private void processDamage(Damage damageWrapper) {
        LivingEntity victim = damageWrapper.getVictim();
        LivingEntity attacker = damageWrapper.getAttacker();
        Cause cause = damageWrapper.getCause();

        if (!evaluateGame(victim, attacker))
            return;
        if (!(attacker instanceof Player))
            return;
        if (victim instanceof Player && (hasDeath((Player) attacker) || hasDeath((Player) victim)))
            return; //if the attacker/victim is currently dead, don't process the damage at all
        if (cause == Cause.MELEE && StatusApplier.getOrNew(victim).isCloaked())
            return;

        double bonus = 0;
        if (cause == Cause.MELEE || cause == Cause.MELEESKILL)
            bonus = findPotionBonus(victim, attacker);
        DamageApplyEvent damageEvent = new DamageApplyEvent(victim, attacker, damageWrapper.getDamage() + bonus, cause,
                damageWrapper.getArrow(), damageWrapper.getSource(), damageWrapper.isApplyKnockback());
        double damage;
        Bukkit.getPluginManager().callEvent(damageEvent);

        if (damageEvent.isCancelled() || damageEvent.getAttacker() == damageEvent.getVictim()) return;
        if (damageEvent.isModified()) {
            damageWrapper.setDamage(damageEvent.getDamage());
        }
        damage = damageEvent.getDamage();
        addHistory(victim, damageWrapper);
        double armorValue = damageEvent.getArmorValue();
        playSound(victim, attacker, cause);
        if (attacker instanceof Player)
            ((Player) attacker).setLevel((int) damage);
        sendUsePacket(victim);
        damage(victim, damage, armorValue, damageEvent);
    }

    /**
     * For PlayerDeathEvents.
     * @param player The player you want to do it for
     */
    public static void artificialDie(Player player) {
        if (hasDeath(player))
            return;
        Deque<Damage> damages = damageHistory.get(player.getName());
        player.getInventory().clear();
        player.getInventory().setArmorContents(new ItemStack[]{null, null, null, null});

        Damage damage;
        final Damage nullDamage = new Damage(player, null, -99, null, Cause.NULL, null, (DamageSource) null,false);
        boolean a = false;
        if (damages == null ||
            damages.size() == 0) {
            damage = nullDamage;
        } else {
            damage = damages.getLast();
            a = true;
        }

        Bukkit.getPluginManager().callEvent(new DeathApplyEvent(damage, damages));
        if (a)
            damages.clear();
    }

    public static void artificialAddHistory(LivingEntity entity, double damage, Cause cause) {
        String name = getNameFor(entity);
        if (!damageHistory.containsKey(name))
            damageHistory.put(name, new ArrayDeque<>());
        Damage wrapper = new Damage(entity, null, damage, null, cause, null, (DamageSource) null, false);
        damageHistory.get(name).add(wrapper);
    }

    public static Deque<Damage> getDamages() {
        return damages;
    }
}
