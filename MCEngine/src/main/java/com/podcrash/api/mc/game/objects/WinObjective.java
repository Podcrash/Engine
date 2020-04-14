package com.podcrash.api.mc.game.objects;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.mob.CustomEntityFirework;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class WinObjective implements IObjective {
    private String worldName;
    private final Vector vector;
    protected FireworkEffect fireworkEffect;
    private Player lastCaptured;
    private TeamEnum lastTeamCaptured;

    public WinObjective(Vector vector){
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BURST).build();
        this.vector = vector;
    }

    @Override
    public void spawnFirework() {
        CustomEntityFirework.spawn(getLocation().clone().add(new Vector(0, 2, 0)), this.fireworkEffect);
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }

    @Override
    public void setWorld(World world) {
        worldName = world.getName();
    }

    @Override
    public void setWorld(String worldName) {
        this.worldName = worldName;
    }
    public Player acquiredByPlayer(){
        return lastCaptured;
    }
    public void setAcquiredByPlayer(Player lastCaptured) {
        this.lastCaptured = lastCaptured;
        this.lastTeamCaptured = GameManager.getGame().getTeamEnum(lastCaptured);
    }

    public TeamEnum acquiredByTeam(){
        return lastTeamCaptured;
    }

    public Vector getVector(){ return this.vector; }

    @Override
    public String toString(){
        return String.format("(%s:{%f, %f, %f})", getName(), this.vector.getX(), this.vector.getY(), this.vector.getZ());
    }
}
