package com.podcrash.api.effect.status;

import com.podcrash.api.effect.status.custom.*;
import com.podcrash.api.events.StatusApplyEvent;
import com.podcrash.api.events.StatusRemoveEvent;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.plugin.PodcrashSpigot;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.Consumer;

/**
 * To make this class better, there has to be less structure here
 * TODO: Rewrite
 */
public class StatusApplier {
    private static final Map<UUID, StatusApplier> appliers = new HashMap<>();
    private final Object invisLock;
    
    private final UUID entityUUID;
    private final UUID worldUUID;

    private long cloaked;
    private long marked;
    private long silenced;
    private long shocked;
    private long rooted;
    private long grounded;
    private long inept;
    private long bleeded;
    
    private StatusApplier(LivingEntity entity) {
        this.entityUUID = entity.getUniqueId();
        this.worldUUID = entity.getWorld().getUID();
        this.invisLock = new Object();
    }


    public static StatusApplier getOrNew(LivingEntity entity) {
        if (!appliers.containsKey(entity.getUniqueId())) {
            appliers.put(entity.getUniqueId(), new StatusApplier(entity));
        }

        StatusApplier applier = appliers.get(entity.getUniqueId());
        if (applier.getEntity() == null) {
            StatusApplier newApplier = new StatusApplier(entity);
            appliers.put(entity.getUniqueId(), newApplier);
            return newApplier;
        } else return applier;
    }

    public static void remove(Player entity) {
        appliers.remove(entity.getUniqueId());
    }

    private LivingEntity getEntity() {
        //find the player's:
        Player p = Bukkit.getPlayer(entityUUID);
        if (p != null)
            return p;
        World world = Bukkit.getWorld(worldUUID);
        if (world == null) return null;
        for (Chunk chunk : world.getLoadedChunks()) {
            Entity[] entities = chunk.getEntities();
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity && entity.getUniqueId().equals(entityUUID))
                    return (LivingEntity) entity;
            }
        }
        //check to see if this part happens a lot
        PodcrashSpigot.getInstance().getLogger().info("NULL! getEntity");
        return null;
    }
    /**
     * Apply a status to a entity.
     *
     * @param status
     * @param duration
     * @param potency
     * @param ambient
     * @param override if true, override the current effect
     */
    public void applyStatus(Status status, float duration, int potency, boolean ambient, boolean override) {
        LivingEntity entity = getEntity();
        if (entity == null && status == null) return;
        StatusApplyEvent statusApplyEvent = new StatusApplyEvent(entity, status, duration, potency);
        Bukkit.getPluginManager().callEvent(statusApplyEvent);
        if (statusApplyEvent.isCancelled()) return;
        int iduration = (int) (duration * 20f);
        int ipotency = potency;
        if (statusApplyEvent.isModified()) {
            iduration = (int) (statusApplyEvent.getDuration() * 20f);
            ipotency = statusApplyEvent.getPotency();
        }
        if (status.isVanilla()) {
            applyVanilla(status, iduration, ipotency, ambient, override);
        } else {
            applyCustom(status, iduration, ipotency);
        }

        //only work for vanilla effects.
        if (override && status.isVanilla()) {
            StatusWrapper wrapper = fromPotionEffect(status);// 30 seconds
            PodcrashSpigot.getInstance().getLogger().info("statusppaplier wrapper: " + wrapper);
            if (wrapper == null)
                return;
            float newDuration = (wrapper.getDuration() - duration);
            PodcrashSpigot.getInstance().getLogger().info("statusppaplier duration: " + newDuration);
            if (newDuration < 0)
                return;
            StatusWrapper newWrapper = new StatusWrapper(wrapper.getStatus(), newDuration, wrapper.getPotency(), wrapper.isAmbience(), wrapper.isOverride());

            PodcrashSpigot.getInstance().getLogger().info("statusppaplier new wrapper: " + newWrapper);
            TimeHandler.delayTime((long) (20L * (duration + 0.5)), () -> applyStatus(newWrapper));
        }
    }

    public void applyStatus(Status status, float duration, int potency, boolean ambient) {
        applyStatus(status, duration, potency, ambient, false);
    }

    public void applyStatus(Status status, float duration, int potency) {
        applyStatus(status, duration, potency, false);
    }

    public void applyStatus(StatusWrapper statusWrapper) {
        applyStatus(statusWrapper.getStatus(), statusWrapper.getDuration(), statusWrapper.getPotency(), statusWrapper.isAmbience(), statusWrapper.isOverride());
    }

    public void applyStatus(StatusWrapper... statusWrappers) {
        for (StatusWrapper statusWrapper : statusWrappers)
            applyStatus(statusWrapper.getStatus(), statusWrapper.getDuration(), statusWrapper.getPotency(), statusWrapper.isAmbience());
    }

    public void removeStatus(Status status) {
        if (status.isVanilla()) {
            removeVanilla(status);
        } else {
            removeCustom(status);
        }
    }

    public void removeStatus(Status... statuses) {
        for (Status status : statuses) {
            removeStatus(status);
        }
    }

    private void applyVanilla(@Nonnull Status status, int duration, int potency, boolean ambient, boolean override) {
        LivingEntity entity = getEntity();

        final PotionEffect addpotion = new PotionEffect(status.getPotionEffectType(), duration, potency, ambient);

        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            PodcrashSpigot.debugLog(entity + " is recieving " + status.getName());
            if (!entity.addPotionEffect(addpotion, override)) {
                if (duration == Integer.MAX_VALUE && status == Status.SPEED)
                    PodcrashSpigot.getInstance().getLogger().info("speed not applied");
            } else if (duration == Integer.MAX_VALUE && status == Status.SPEED)
                PodcrashSpigot.getInstance().getLogger().info("speed applied");
        });

    }

    public void removeVanilla(Status... statuses) {
        for (Status status : statuses) {
            removeVanilla(status);
        }
    }

    public void removeVanilla(Status status) {
        LivingEntity entity = getEntity();
        if (entity == null && status == null) return;
        PotionEffect effect = status.getPotionEffectType().createEffect(0, 0);
        StatusRemoveEvent removeEvent = new StatusRemoveEvent(entity, status);
        Bukkit.getPluginManager().callEvent(removeEvent);
        if (removeEvent.isCancelled()) return;
        
        if (entity.hasPotionEffect(status.getPotionEffectType())) {
            new BukkitRunnable() {
                @Override
                public void run() {
                    entity.addPotionEffect(effect, true);
                }
            }.runTaskLater(PodcrashSpigot.getInstance(), 1L);
        }
    }

    public void removeCustom(Status status) {
        LivingEntity entity = getEntity();
        if (entity == null) return;
        StatusRemoveEvent removeEvent = new StatusRemoveEvent(entity, status);
        Bukkit.getPluginManager().callEvent(removeEvent);
        if (removeEvent.isCancelled()) return;
        switch (status) {
            case FIRE:
                entity.setFireTicks(0);
                break;
            case CLOAK:
                removeCloak();
                break;
            case MARKED:
                removeMark();
                break;
            case SILENCE:
                removeSilence();
                break;
            case SHOCK:
                removeShock();
                break;
            case ROOTED:
                removeRoot();
                break;
            case INEPTITUDE:
                removeInept();
                break;
            case GROUND:
                removeGround();
                break;
            case BLEED:
                removeBleed();
                break;
        }
    }

    /*
    potency is pretty irrelevant to these effects
     */
    private void applyCustom(Status status, int duration, int potency) {
        LivingEntity entity = getEntity();
        if (entity == null) return;
        duration = duration * 50; // duration seconds * 1000millis/1 seconds * 1/ 20 ticks
        switch (status) {
            case FIRE:
                entity.setFireTicks(duration / 50);
                break;
            case CLOAK:
                applyCloak(duration);
                break;
            case MARKED:
                applyMarked(duration, potency);
                break;
            case SILENCE:
                applySilence(duration);
                break;
            case SHOCK:
                applyShock(duration);
                break;
            case ROOTED:
                applyRoot(duration);
                break;
            case GROUND:
                applyGround(duration);
                if (entity instanceof Player)
                    ((Player) entity).setSprinting(false);
                break;
            case INEPTITUDE:
                applyInept(duration);
                break;
            case BLEED:
                applyBleed(duration);
                break;
        }
    }

    private void applyCloak(final int duration) {
        LivingEntity entity = getEntity();
        if (!(entity instanceof Player)) return;
        Player player = (Player) entity;
        if (!isCloaked()) {
            Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
                for (Player p : player.getWorld().getPlayers()) {
                    p.hidePlayer(player);
                }

            });
            cloaked = System.currentTimeMillis() + duration; //Duplicating code b/c it needs to happen after isCloaked check, but before cloakstatus creation
            entity.sendMessage(String.format("%sCondition> %sYou are now invisible.", ChatColor.BLUE, ChatColor.GRAY));
            TimeHandler.repeatedTime(1L, 0, new CloakStatus(player));
        } else {
            cloaked = System.currentTimeMillis() + duration;
        }
    }

    private void applyMarked(int duration, int potency) {
        LivingEntity entity = getEntity();
        entity.sendMessage(String.format("%sCondition> %sYou are now marked for %s%d %sseconds!", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        if (!isMarked()) {
            marked = System.currentTimeMillis() + duration;
            TimeHandler.repeatedTime(1, 0, new MarkedStatus(entity));
        } else {
            marked = System.currentTimeMillis() + duration;
        }
    }

    private void applySilence(int duration) {
        LivingEntity entity = getEntity();
        entity.sendMessage(String.format("%sCondition> %sYou are now silenced for %s%d %sseconds!", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        if (!isSilenced()) {
            silenced = System.currentTimeMillis() + duration;
            TimeHandler.repeatedTime(1, 0, new SilenceStatus(entity));
        } else {
            silenced = System.currentTimeMillis() + duration;
        }
    }

    private void applyShock(int duration) {
        LivingEntity entity = getEntity();
        if (!isShocked()) {
            shocked = System.currentTimeMillis() + duration;
            //entity.sendMessage(String.format("%sCondition> %sYou are now shocked for %s%d %sseconds!",ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
            TimeHandler.repeatedTime(1, 0, new ShockStatus(entity));
        } else {
            shocked = System.currentTimeMillis() + duration;
        }
    }

    private void applyRoot(int duration) {
        LivingEntity entity = getEntity();
        entity.sendMessage(String.format("%sCondition> %sYou are now rooted for %s%d %sseconds!", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        if (!isRooted()) {
            rooted = System.currentTimeMillis() + duration;
            TimeHandler.repeatedTime(1, 1, new RootedStatus(entity));
        } else {
            rooted = System.currentTimeMillis() + duration;
        }
    }

    private void applyGround(int duration) {
        LivingEntity entity = getEntity();
        if (!isGrounded()) {
            grounded = System.currentTimeMillis() + duration;
            TimeHandler.repeatedTime(1, 1, new GroundStatus(entity));
        } else {
            grounded = System.currentTimeMillis() + duration;
        }
    }

    private void applyInept(int duration) {
        LivingEntity entity = getEntity();
        if (!isInept()) {
            inept = System.currentTimeMillis() + duration;
            TimeHandler.repeatedTime(1, 1, new IneptStatus(entity));
        } else {
            inept = System.currentTimeMillis() + duration;
        }
    }

    private void applyBleed(int duration) {
        LivingEntity entity = getEntity();
        if (!isBleeding()) {
            bleeded = System.currentTimeMillis() + duration;
            TimeHandler.repeatedTime(1, 1, new BleedStatus(entity));
        } else {
            bleeded = System.currentTimeMillis() + duration;
        }
    }

    public boolean isCloaked() {
        return cloaked > System.currentTimeMillis();
    }

    public boolean isMarked() {
        return marked > System.currentTimeMillis();
    }

    public boolean isSilenced() {
        return silenced > System.currentTimeMillis();
    }

    public boolean isShocked() {
        return shocked  > System.currentTimeMillis();
    }

    public boolean isRooted() {
        return rooted  > System.currentTimeMillis();
    }

    public boolean isGrounded() {
        return grounded > System.currentTimeMillis();
    }

    public boolean isInept() {
        return inept > System.currentTimeMillis();
    }

    public boolean isBleeding() {
        return bleeded > System.currentTimeMillis();
    }

    public void removeCloak() {
        LivingEntity e = getEntity();
        if (!(e instanceof Player))
            return;
        cloaked = 0;
        List<Player> entitys = e.getWorld().getPlayers();
        for (Player entity : entitys) {
            Player player = (Player) e;
            synchronized (invisLock) {
                if (entity != player && !entity.canSee(player)) {
                    entity.showPlayer(player);
                }
            }
        }
    }

    public void removeMark() {
        marked = 0;
    }

    public void removeSilence() {
        silenced = 0;

    }

    public void removeShock() {
        shocked = 0;
    }

    public void removeRoot() {
        rooted = 0;
        LivingEntity entity = getEntity();
        if (!(entity instanceof Player))
            return;
        ((Player) getEntity()).setSaturation(20);
    }

    public void removeGround() {
        grounded = 0;
    }

    public void removeInept() {
        inept = 0;
    }

    public void removeBleed() {
        bleeded = 0;
    }
    /**
     * @param status the status in question
     * @return the duration of a custom effect
     */
    public float getRemainingDuration(Status status) {
        long dura = 0;
        switch (status) {
            case CLOAK:
                if (isCloaked()) dura = cloaked;
                break;
            case SHOCK:
                if (isShocked()) dura = shocked;
                break;
            case MARKED:
                if (isMarked()) dura = marked;
                break;
            case SILENCE:
                if (isSilenced()) dura = silenced;
                break;
            case ROOTED:
                if (isRooted()) dura = rooted;
                break;
            case GROUND:
                if (isGrounded()) dura = grounded;
                break;
            case INEPTITUDE:
                if (isInept()) dura = inept;
                break;
            case BLEED:
                if (isBleeding()) dura = bleeded;
                break;
        }
        return (dura != 0) ? dura - System.currentTimeMillis() : 0;
    }

    public float getRemainingDurationSeconds(Status status) {
        return getRemainingDuration(status) / 1000F;
    }

    public List<Status> getEffects() {
        List<Status> statuses = new ArrayList<>();
        getEntity().getActivePotionEffects().forEach((potionEffect) -> {
            for (Status status : Status.values()) {
                if (potionEffect.getType().hashCode() == status.getId()) {
                    statuses.add(status);
                }
            }
        });
        if (getEntity().getFireTicks() > 0) statuses.add(Status.FIRE);
        if (isCloaked()) statuses.add(Status.CLOAK);
        if (isMarked()) statuses.add(Status.MARKED);
        if (isShocked()) statuses.add(Status.SHOCK);
        if (isSilenced()) statuses.add(Status.SILENCE);
        if (isRooted()) statuses.add(Status.ROOTED);
        if (isInept()) statuses.add(Status.INEPTITUDE);
        if (isGrounded()) statuses.add(Status.GROUND);
        if (isBleeding()) statuses.add(Status.BLEED);
        return statuses;
    }

    /**
     * TODO: Rewrite the class so this is possible
     * @param statusConsumer
     */
    public void getEffectGenerator(Consumer<Status> statusConsumer) {

    }

    public boolean has(Status status) {
        return getEffects().contains(status);
    }
    private StatusWrapper fromPotionEffect(Status status) {
        PotionEffect e = null;
        for(PotionEffect effect : getEntity().getActivePotionEffects()) {
            if (effect.getType().hashCode() == status.getId()) {
                e = effect;
                break;
            }
        }
        if (e == null) return null;
        return new StatusWrapper(status, e.getDuration()/20F, e.getAmplifier(), e.isAmbient());
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusApplier that = (StatusApplier) o;
        return getEntity().getName().equals(that.getEntity().getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getEntity().getName());
    }
}
