package com.podcrash.api.mc.effect.status;

import com.podcrash.api.mc.effect.status.custom.*;
import com.podcrash.api.mc.events.StatusApplyEvent;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.plugin.Pluginizer;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.function.Consumer;

/**
 * To make this class better, there has to be less structure here
 */
public class StatusApplier {
    private Player player;
    private static Map<String, StatusApplier> appliers = new HashMap<>();
    private static HashMap<String, Long> cloakMap = new HashMap<>();//dis gotta be refactored into long
    private static HashMap<String, Long> markedMap = new HashMap<>();
    private static HashMap<String, Long> silenceMap = new HashMap<>();
    private static HashMap<String, Long> shockMap = new HashMap<>();
    private static HashMap<String, Long> rootMap = new HashMap<>();
    private static HashMap<String, Long> groundMap = new HashMap<>();
    private static HashMap<String, Long> ineptMap = new HashMap<>();
    private static HashMap<String, Long> bleedMap = new HashMap<>();

    private StatusApplier(Player p) {
        this.player = p;
    }

    public static StatusApplier getOrNew(Player player) {
        if (appliers.get(player.getName()) == null) {
            appliers.put(player.getName(), new StatusApplier(player));
        }
        return appliers.get(player.getName());
    }
    public static StatusApplier getOrNew(LivingEntity entity) {
        throw new IllegalArgumentException("This is current a stub!");
    }

    public static void remove(Player player) {
        appliers.remove(player.getName());
    }

    /**
     * Apply a status to a player.
     *
     * @param status
     * @param duration
     * @param potency
     * @param ambient
     * @param override if true, override the current effect
     */
    public void applyStatus(Status status, float duration, int potency, boolean ambient, boolean override) {
        if (player == null && status == null) return;
        StatusApplyEvent statusApplyEvent = new StatusApplyEvent(player, status, duration, potency);
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
    }

    public void applyStatus(Status status, float duration, int potency, boolean ambient) {
        applyStatus(status, duration, potency, ambient, false);
    }

    public void applyStatus(Status status, float duration, int potency) {
        applyStatus(status, duration, potency, false);
    }

    public void applyStatus(StatusWrapper statusWrapper) {
        applyStatus(statusWrapper.getStatus(), statusWrapper.getDuration(), statusWrapper.getPotency(), statusWrapper.isAmbience());
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

    private void applyVanilla(Status status, int duration, int potency, boolean ambient, boolean override) {
        PotionEffect potion = null;
        switch (status) {
            case SLOW:
                potion = new PotionEffect(PotionEffectType.SLOW, duration, potency, ambient);
                break;
            case SPEED:
                potion = new PotionEffect(PotionEffectType.SPEED, duration, potency, ambient);
                break;
            case POISON:
                potion = new PotionEffect(PotionEffectType.POISON, duration, potency, ambient);
                break;
            case JUMP_BOOST:
                potion = new PotionEffect(PotionEffectType.JUMP, duration, potency, ambient);
                break;
            case FIRE_RESISTANCE:
                potion = new PotionEffect(PotionEffectType.FIRE_RESISTANCE, duration, potency, ambient);
                break;
            case INVISIBILITY:
                potion = new PotionEffect(PotionEffectType.INVISIBILITY, duration, potency, ambient);
                break;
            case DIZZY:
                potion = new PotionEffect(PotionEffectType.CONFUSION, duration, potency, ambient);
                break;
            case BLIND:
                potion = new PotionEffect(PotionEffectType.BLINDNESS, duration, potency, ambient);
                break;
            case WITHER:
                potion = new PotionEffect(PotionEffectType.WITHER, duration, potency, ambient);
                break;
            case STRENGTH:
                potion = new PotionEffect(PotionEffectType.INCREASE_DAMAGE, duration, potency, ambient);
                break;
            case RESISTANCE:
                potion = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, duration, potency, ambient);
                break;
            case REGENERATION:
                potion = new PotionEffect(PotionEffectType.REGENERATION, duration, potency, ambient);
                break;
            case WEAKNESS:
                potion = new PotionEffect(PotionEffectType.WEAKNESS, duration, potency, ambient);
                break;
        }

        if (potion == null) {
            return;
        }
        final PotionEffect addpotion = potion;
        Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(), () -> {
            player.addPotionEffect(addpotion, override);
        }, 1L);

    }

    public void removeVanilla(Status... statuses) {
        for (Status status : statuses) {
            removeVanilla(status);
        }
    }

    public void removeVanilla(Status status) {
        if(status == null) return;
        if (player.hasPotionEffect(status.getPotionEffectType())) {
            PotionEffect effect = status.getPotionEffectType().createEffect(0, 0);
            new BukkitRunnable() {
                @Override
                public void run() {
                    player.addPotionEffect(effect, true);
                }
            }.runTaskLater(Pluginizer.getSpigotPlugin(), 1L);
        }
    }

    public void removeCustom(Status status) {
        switch (status) {
            case FIRE:
                this.player.setFireTicks(0);
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
        duration = duration * 50; // duration seconds * 1000millis/1 seconds * 1/ 20 ticks
        switch (status) {
            case FIRE:
                this.player.setFireTicks(duration / 50);
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
        removeCloak();
        Bukkit.getScheduler().runTask(Pluginizer.getSpigotPlugin(), () -> {
            for (Player p : player.getWorld().getPlayers()) {
                p.hidePlayer(player);
            }

        });
        //player.sendMessage(String.format("You are now invisible for %d seconds!", (duration/1000)));
        player.sendMessage(String.format("%sCondition> %sYou are now invisible.", ChatColor.BLUE, ChatColor.GRAY));
        cloakMap.put(player.getName(), System.currentTimeMillis() + duration);
        TimeHandler.repeatedTime(1L, 0, new CloakStatus(player));
    }

    private void applyMarked(int duration, int potency) {
        removeMark();
        markedMap.put(player.getName(), System.currentTimeMillis() + duration);
        player.sendMessage(String.format("%sCondition> %sYou are now marked for %s%d %sseconds!",ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        TimeHandler.repeatedTime(1, 0, new MarkedStatus(player));
    }

    private void applySilence(int duration) {
        removeSilence();
        silenceMap.put(player.getName(), System.currentTimeMillis() + duration);
        player.sendMessage(String.format("%sCondition> %sYou are now silenced for %s%d %sseconds!",ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        TimeHandler.repeatedTime(1, 0, new SilenceStatus(player));
    }

    private void applyShock(int duration) {
        removeShock();
        shockMap.put(player.getName(), System.currentTimeMillis() + duration);
        //player.sendMessage(String.format("%sCondition> %sYou are now shocked for %s%d %sseconds!",ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        TimeHandler.repeatedTime(1, 0, new ShockStatus(player));
    }

    private void applyRoot(int duration) {
        if (isRooted()) removeRoot();
        rootMap.put(player.getName(), System.currentTimeMillis() + duration);
        player.sendMessage(String.format("%sCondition> %sYou are now rooted for %s%d %sseconds!", ChatColor.BLUE, ChatColor.GRAY, ChatColor.GREEN, (duration / 1000), ChatColor.GRAY));
        TimeHandler.repeatedTime(1, 1, new RootedStatus(player));
    }

    private void applyGround(int duration) {
        if (isGrounded()) removeGround();
        groundMap.put(player.getName(), System.currentTimeMillis() + duration);
        TimeHandler.repeatedTime(1, 1, new GroundStatus(player));
    }

    private void applyInept(int duration) {
        if (isInept()) removeInept();
        ineptMap.put(player.getName(), System.currentTimeMillis() + duration);
        TimeHandler.repeatedTime(1, 1, new IneptStatus(player));
    }

    private void applyBleed(int duration) {
        if(isBleeding()) removeBleed();
        bleedMap.put(player.getName(), System.currentTimeMillis() + duration);
        TimeHandler.repeatedTime(1, 1, new BleedStatus(player));
    }

    public boolean isCloaked() {
        return cloakMap.containsKey(this.player.getName());
    }

    public boolean isMarked() {
        return markedMap.containsKey(this.player.getName());
    }

    public boolean isSilenced() {
        return silenceMap.containsKey(this.player.getName());
    }

    public boolean isShocked() {
        return shockMap.containsKey(this.player.getName());
    }

    public boolean isRooted() {
        return rootMap.containsKey(this.player.getName());
    }

    public boolean isGrounded() {
        return groundMap.containsKey(player.getName());
    }

    public boolean isInept() {
        return ineptMap.containsKey(player.getName());
    }

    public boolean isBleeding() {
        return bleedMap.containsKey(player.getName());
    }

    public void removeCloak() {
        if (isCloaked()) {
            cloakMap.remove(this.player.getName());
        }
        List<Player> players = this.player.getWorld().getPlayers();
        Bukkit.getScheduler().runTask(Pluginizer.getSpigotPlugin(), () -> {
            for (Player player : players) {
                if (this.player != player) {
                    player.showPlayer(this.player);
                }
            }
        });
    }

    public void removeMark() {
        if (isMarked()) {
            markedMap.remove(this.player.getName());
        }
    }

    public void removeSilence() {
        if (isSilenced()) {
            silenceMap.remove(this.player.getName());
        }
    }

    public void removeShock() {
        if (isShocked()) {
            shockMap.remove(this.player.getName());
        }
    }

    public void removeRoot() {
        if (isRooted()) {
            rootMap.remove(this.player.getName());
            this.player.setSaturation(20);
        }
    }

    public void removeGround() {
        if (isGrounded()) groundMap.remove(player.getName());
    }

    public void removeInept() {
        if (isInept()) ineptMap.remove(player.getName());
    }

    public void removeBleed() {
        if (isBleeding()) bleedMap.remove(player.getName());
    }
    /**
     * @param status the status in question
     * @return the duration of a custom effect
     */
    public float getRemainingDuration(Status status) {
        HashMap<String, Long> map = null;
        switch (status) {
            case CLOAK:
                if (isCloaked()) map = cloakMap;
                break;
            case SHOCK:
                if (isShocked()) map = shockMap;
                break;
            case MARKED:
                if (isMarked()) map = markedMap;
                break;
            case SILENCE:
                if (isSilenced()) map = silenceMap;
                break;
            case ROOTED:
                if (isRooted()) map = rootMap;
                break;
            case GROUND:
                if (isGrounded()) map = groundMap;
                break;
            case INEPTITUDE:
                if (isInept()) map = ineptMap;
                break;
            case BLEED:
                if(isBleeding()) map = bleedMap;
                break;
        }
        return (map != null) ? map.get(player.getName()) - System.currentTimeMillis() : 0;
    }

    public float getRemainingDurationSeconds(Status status) {
        return getRemainingDuration(status) / 1000F;
    }

    public List<Status> getEffects() {
        List<Status> statuses = new ArrayList<>();
        this.player.getActivePotionEffects().forEach((potionEffect) -> {
            for (Status status : Status.values()) {
                if (potionEffect.getType().hashCode() == status.getId()) {
                    statuses.add(status);
                }
            }
        });
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        StatusApplier that = (StatusApplier) o;
        return player.getName().equals(that.player.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(player.getName());
    }
}
