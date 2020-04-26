package com.podcrash.api.mc.game.objects;

import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.objectives.ObjectiveType;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public interface IObjective {

    /**
     * Spawn a firework!
     */
    void spawnFirework();
    Player acquiredByPlayer();
    TeamEnum acquiredByTeam();
    ObjectiveType getObjectiveType();
    void setAcquiredByPlayer(Player acquirer);
    Vector getVector();
    String getName();

    World getWorld();
    void setWorld(String worldName);
    void setWorld(World world);

    default Location getLocation() {
        return getVector().toLocation(getWorld());
    }
    default void log(String s){
        PodcrashSpigot.getInstance().getLogger().info(String.format("[%s{%s}]: %s", this.getClass().getSimpleName(), getName(), s));
    }
}
