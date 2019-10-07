package com.podcrash.api.mc.game.objects;

import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.mob.CustomEntityFirework;
import com.podcrash.api.mc.world.BlockUtil;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public abstract class ItemObjective implements IObjective {
    protected FireworkEffect fireworkEffect;
    private final Location locationPlus1;
    private final Location location;

    private Material baseMaterial;
    private Material blockMaterial;
    private boolean canBeAcquired;
    private Item item;
    private Player player;

    public ItemObjective(Material material, Material block, Location location) {
        this.baseMaterial = material;
        this.blockMaterial = block;
        this.location = location.add(new Vector(0.5, 0, 0.5));
        this.locationPlus1 = location.clone().add(new Vector(0, 1, 0));
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BURST).build();
    }

    @Override
    public void spawnFirework() {
        CustomEntityFirework.spawn(locationPlus1, this.fireworkEffect);
    }

    /**
     * Spawn an item with a vertical velocity, set its block
     */
    public void respawn() {
        if(this.item != null && this.item.isValid()) this.item.remove();
        this.item = ItemManipulationManager.regular(baseMaterial, this.locationPlus1, new Vector(0, 1, 0));
        BlockUtil.setBlock(location, blockMaterial);
    }

    /**
     * If the item is to be collected, kill the entity and set its block to its base
     * block ( which is an iron block)
     */
    public void die() {
        if(this.item != null) this.item.remove();
        BlockUtil.setBlock(location, Material.IRON_BLOCK);
    }

    public Item getItem(){
        return this.item;
    }
    public Location getLocation() {return this.location; }

    @Override
    public Player acquiredByPlayer() {
        return player;
    }

    @Override
    public String acquiredByTeam() {
        return GameManager.getGame().getTeamColor(player);
    }

    @Override
    public void setAcquiredByPlayer(Player acquirer) {
        this.player = acquirer;
    }

    @Override
    public String toString(){//TODO use tellraw for these type of things (click on point to teleport to it)?
        return String.format("(%s:{%f, %f, %f})", getName(), this.location.getX(), this.location.getY(), this.location.getZ());
    }
}
