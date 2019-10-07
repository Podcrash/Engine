package com.podcrash.api.mc.game.objects;

import com.podcrash.api.mc.mob.CustomEntityFirework;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class WinObjective implements IObjective {
    private final Location location;
    protected FireworkEffect fireworkEffect;
    private Player lastCaptured;
    private String lastTeamCaptured;

    public WinObjective(Location location){
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BURST).build();
        this.location = location;
    }

    @Override
    public void spawnFirework() {
        CustomEntityFirework.spawn(location.clone().add(new Vector(0, 2, 0)), this.fireworkEffect);
    }

    public Player acquiredByPlayer(){
        return lastCaptured;
    }
    public void setAcquiredByPlayer(Player lastCaptured) {
        this.lastCaptured = lastCaptured;
    }

    public String acquiredByTeam(){
        return lastTeamCaptured;
    }

    public Location getLocation(){ return this.location; }

    @Override
    public String toString(){
        return String.format("(%s:{%f, %f, %f})", getName(), this.location.getX(), this.location.getY(), this.location.getZ());
    }
}
