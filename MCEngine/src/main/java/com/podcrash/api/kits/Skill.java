package com.podcrash.api.kits;

import com.podcrash.api.damage.DamageSource;
import com.podcrash.api.game.TeamEnum;
import com.podcrash.api.events.skill.SkillCooldownEvent;
import com.podcrash.api.game.Game;
import com.podcrash.api.kits.iskilltypes.champion.ISkill;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;

import java.util.*;

public abstract class Skill implements ISkill, DamageSource {
    private String playerName;
    protected final Skill instance = this;
    private long lastUsed = 0L;
    public double price = 1500;

    private final Object playerLock;
    public Skill() {
        this.playerLock = new Object();
    }

    /**
     * OVERRIDE THIS
     */
    public void init() {

    }

    public int getID() {
        return Objects.hash(getName());
    }

    public boolean isInGame() {
        return getChampionsPlayer().isInGame();
    }
    public Game getGame() {
        return getChampionsPlayer().getGame();
    }
    public TeamEnum getTeam() {
        return (isInGame()) ? getChampionsPlayer().getTeam() : null;
    }

    /**
     * @see KitPlayer#isAlly(Player)
     * If the player is not in a game, just assume that every player is an ally.
     * @param player
     * @return
     */
    public boolean isAlly(LivingEntity player) {
        if(player instanceof Player) {
            return !isInGame() || getChampionsPlayer().isAlly((Player) player);
        }else return false;
    }

    /**
     * Get all the players within a game, if the game exists. If not, get all the players within the world.
     * @return
     */
    public List<Player> getPlayers(){
        return getGame() == null ? getPlayer().getWorld().getPlayers() : getGame().getBukkitPlayers();
    }
    /*
    check if person is in water
     */
    protected boolean isInWater() {
        Material m = getPlayer().getLocation().getBlock().getType();
        return (m.equals(Material.STATIONARY_WATER) || m.equals(Material.WATER));
    }
    protected boolean isHolding() {
        String name = getItemType().getName();
        String upperCasedItemName = getPlayer().getItemInHand().getType().name().toUpperCase();
        return (name == null) || upperCasedItemName.contains(name);
    }

    //getters
    protected String getWaterMessage(){

        return String.format("%s%s> %sYou cannot use %s%s %sin water.",
                ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }

    public String getUsedMessage() {
        return String.format("%s%s> %sYou used %s%s%s.",
                ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }
    public String getUsedMessage(LivingEntity entity) {
        return String.format("%s%s> %sYou used %s%s %son %s%s%s.",
                ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY, ChatColor.YELLOW, entity.getName(), ChatColor.GRAY);
    }

    //TODO: change verb to something else
    public String getDurationMessage(LivingEntity entity, String verb, double duration) {
        return String.format("%s%s> %sYou %s %s%s %sfor %s%f.",
                ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GRAY, verb, ChatColor.GREEN, entity.getName(), ChatColor.GRAY, ChatColor.GREEN, duration);
    }

    public String getMustGroundMessage() {
        return String.format("%s%s> %sYou cannot use %s%s%s while grounded.",
                ChatColor.BLUE, getChampionsPlayer().getName() , ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }

    public String getMustAirborneMessage() {
        return String.format("%s%s> %sYou cannot use %s%s%s while airborne.",
                ChatColor.BLUE, getChampionsPlayer().getName() , ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }

    public String getFailedMessage() {
        return String.format("%s%s> %sYou failed %s%s%s.",
                ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GRAY, ChatColor.GREEN, getName(), ChatColor.GRAY);
    }

    public Player getPlayer() {
        synchronized (playerLock) {
            return Bukkit.getPlayer(playerName);
        }
    }
    public KitPlayer getChampionsPlayer() {
        return KitPlayerManager.getInstance().getChampionsPlayer(getPlayer());
    }
    public void setPlayer(Player player) {
        synchronized (playerLock) {
            this.playerName = player.getName();
        }
    }

    public long getLastUsed() {
        return lastUsed;
    }
    public void setLastUsed(long lastUsed) {
        setLastUsedDirect(lastUsed);
        coolDownEvent();
    }
    protected void setLastUsedDirect(long lastUsed) {
        this.lastUsed = lastUsed;
    }
    public void coolDownEvent() {
        SkillCooldownEvent event = new SkillCooldownEvent(this);
        Bukkit.getPluginManager().callEvent(event);
    }

    protected boolean rightClickCheck(Action action) {
        return (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK);
    }

    @Override
    public String toString() {
        return "{Skill " +
                getName() + " " +
                getItemType() + " }";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Skill)) return false;

        Skill skill = (Skill) o;

        return playerName != null ?
                playerName.equals(skill.playerName) && skill.getItemType() == getItemType() && getID() == skill.getID()
                : skill.playerName == null;
    }

    @Override
    public int hashCode() {
        if(playerName == null) return getID();
        else return Objects.hash(playerName, getItemType(), getID());
    }
}
