package com.podcrash.api.mc.game.objects;

import com.podcrash.api.mc.game.objects.objectives.ObjectiveType;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IObjective {

    /**
     * Spawn a firework!
     */
    void spawnFirework();
    Player acquiredByPlayer();
    String acquiredByTeam();
    ObjectiveType getObjectiveType();
    void setAcquiredByPlayer(Player acquirer);
    Location getLocation();
    String getName();

    default void log(String s){
        Pluginizer.getSpigotPlugin().getLogger().info(String.format("[%s{%s}]: %s", this.getClass().getSimpleName(), getName(), s));
    }
}
