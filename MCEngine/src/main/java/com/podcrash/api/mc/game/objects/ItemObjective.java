package com.podcrash.api.mc.game.objects;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.mob.CustomEntityFirework;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class ItemObjective implements IObjective {
    private String worldName;
    protected FireworkEffect fireworkEffect;
    private final Vector vectorPlus1;
    private final Vector vector;

    private final Material baseMaterial;
    private final Material blockMaterial;
    private boolean canBeAcquired;
    private Item item;
    private Player player;

    public ItemObjective(Material base, Material block, Vector vector) {
        this.baseMaterial = base;
        this.blockMaterial = block;
        this.vector = vector;
        this.vectorPlus1 = vector.clone().add(new Vector(0, 1, 0));
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BURST).build();
    }

    @Override
    public World getWorld() {
        return Bukkit.getWorld(worldName);
    }
    @Override
    public void setWorld(String worldName) {
        this.worldName = worldName;
    }
    @Override
    public void setWorld(World world) {
        this.worldName = world.getName();
    }

    @Override
    public void spawnFirework() {
        CustomEntityFirework.spawn(vectorPlus1.toLocation(getWorld()), this.fireworkEffect);
    }

    /**
     * Spawn an item with a vertical velocity, set its block
     */
    public void respawn() {
        if(this.item != null && this.item.isValid()) return;
        this.item = ItemManipulationManager.regular(baseMaterial, vectorPlus1.toLocation(getWorld()), new Vector(0, 1, 0));
        BlockUtil.setBlock(getLocation(), blockMaterial);
        BlockUtil.setBlock(getLocation().add(0, 1, 0), Material.AIR);
    }

    /**
     * If the item is to be collected, kill the entity and set its block to its base
     * block ( which is an iron block)
     */
    public void die() {
        if(this.item != null) this.item.remove();
        BlockUtil.setBlock(getLocation(), Material.IRON_BLOCK);
    }

    public Item getItem(){
        return this.item;
    }
    public Vector getVector() {return this.vector; }

    @Override
    public Player acquiredByPlayer() {
        return player;
    }

    @Override
    public TeamEnum acquiredByTeam() {
        return GameManager.getGame().getTeamEnum(player);
    }

    @Override
    public void setAcquiredByPlayer(Player acquirer) {
        this.player = acquirer;
    }

    @Override
    public String toString(){//TODO use tellraw for these type of things (click on point to teleport to it)?
        return String.format("(%s:{%f, %f, %f})", getName(), this.vector.getX(), this.vector.getY(), this.vector.getZ());
    }

    public long getDurationMilles() {
        return 60000L;
    }
}
