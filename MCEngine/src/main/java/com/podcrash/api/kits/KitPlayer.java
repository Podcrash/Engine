package com.podcrash.api.kits;

import com.google.gson.JsonObject;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.TeamEnum;
import com.podcrash.api.sound.SoundWrapper;
import com.podcrash.api.kits.enums.ItemType;
import com.podcrash.api.kits.iskilltypes.action.IConstruct;
import com.podcrash.api.util.InventoryUtil;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class KitPlayer {
    protected Player player;
    protected JsonObject jsonObject;
    protected ItemStack[] defaultHotbar;
    protected Material[] armor;
    protected Set<Skill> skills;
    protected EnergyBar ebar = null;

    private double fallDamage = 0;
    private SoundWrapper sound; // sound when hit

    public KitPlayer(Player player) {
        this.player = player;
        this.skills = new HashSet<>();
        final ItemStack air = new ItemStack(Material.AIR);
        this.defaultHotbar = new ItemStack[] { air.clone(), air.clone(), air.clone(), air.clone(), air.clone(), air.clone(), air.clone(), air.clone(), air.clone() };
    }

    public abstract String getName();

    public int getHP() {
        return 20;
    }

    public boolean equip(){
        if(armor == null || armor.length == 0 || armor[0] == null) return false;
        ItemStack[] armors = new ItemStack[4];
        for(int i = 0; i < armor.length; i++){
            Material mat = armor[i];
            if(mat == null) continue;
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(mat));
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);
            nmsStack.setTag(tag);

            armors[i] = new ItemStack(CraftItemStack.asBukkitCopy(nmsStack));
        }
        player.getEquipment().setArmorContents(armors);
        return true;
    }

    public void resetCooldowns() {
        for(Skill skill : skills) {
            skill.setLastUsedDirect(0);
            if(skill instanceof IConstruct)
                ((IConstruct) skill).afterRespawn();
        }
    }

    public boolean isInGame() {
        return GameManager.hasPlayer(this.player);
    }
    public Game getGame() {
        return GameManager.getGame();
    }
    public TeamEnum getTeam() {
        if (isInGame()) return GameManager.getGame().getTeamEnum(getPlayer());
        else return null;
    }

    /**
     * Check if the player is allied with the player.
     * Both players must be in a game.
     * @param player the player to check
     * @return true/false
     */
    public boolean isAlly(Player player) {
        return getGame().isOnSameTeam(getPlayer(), player);
    }

    public void respawn(){//TODO: Respawn with the hotbar.
        player.setFallDistance(0);
        StatusApplier.getOrNew(player).removeStatus(Status.values());
        getInventory().clear();
        this.restockInventory();
        this.resetCooldowns();
        player.setAllowFlight(false);
        player.setFlying(false);
        List<Player> players = getGame() == null ? player.getWorld().getPlayers() : getGame().getBukkitPlayers();
        for(Player player : players){
            if(player != getPlayer()) player.showPlayer(getPlayer());
        }
        player.setHealth(player.getMaxHealth());

        //StatusApplier.getOrNew(player).removeStatus(Status.INEPTITUDE);
    }

    /**
     * TODO: make this method null
     * This method should be overridden to set up special effects that classes need.
     */
    public void effects() {}

    public void heal(double health){
        Player player = getPlayer();

        //call event
        EntityRegainHealthEvent event = new EntityRegainHealthEvent(player, health, EntityRegainHealthEvent.RegainReason.CUSTOM);
        Bukkit.getPluginManager().callEvent(event);
        if(event.isCancelled()) return;

        //heal player
        double current = player.getHealth();
        double expected = current + health;
        if(expected >= player.getMaxHealth()){
            player.setHealth(player.getMaxHealth());
        }else player.setHealth(expected);
    }

    public Player getPlayer() {
        return player;
    }
    public CraftPlayer getCraftPlayer() {
        return (CraftPlayer) player;
    }
    public EntityPlayer getEntityCraftPlayer() {
        return this.getCraftPlayer().getHandle();
    }

    public ItemStack[] getArmor() {
        return this.player.getEquipment().getArmorContents();
    }
    public double getArmorValue(double halfHearts) {
        return (halfHearts - getHP())/(-0.04*getHP());
    }
    public double getArmorValue() {
        return getArmorValue(20);
    }
    public Inventory getInventory() {
        return this.player.getInventory();
    }

    public void setDefaultHotbar(ItemStack[] items) {
        this.defaultHotbar = items;
    }
    public void setDefaultHotbar() {
        Inventory inventory = player.getInventory();
        ItemStack[] hotbar = new ItemStack[9];
        for(int i = 0; i < 9; i++) {
            ItemStack item;
            if((item = inventory.getItem(i)) != null) hotbar[i] = item.clone();
        }
        setDefaultHotbar(hotbar);
    }
    public ItemStack[] getHotBar() {
        ItemStack[] hotbar = new ItemStack[9];
        for (int i = 0; i <= 8; i++) {
            hotbar[i] = this.getInventory().getItem(i);
        }
        return hotbar;
    }


    private ItemStack getTNTStack(){
        int amount = 0;
        for(ItemStack content : getInventory().getContents()){
            if(content != null && content.getType().equals(Material.TNT)) amount += content.getAmount();
        }
        if(amount == 0) return null;
        return new ItemStack(Material.TNT, amount);
    }

    public void restockInventory() {
        if (this.defaultHotbar == null || this.defaultHotbar.length == 0)
            return;
        int size = this.defaultHotbar.length;
        //MOVE TO SOME TYPE OF RESTOCK EVENT
        ItemStack TNT = getTNTStack();
        InventoryUtil.clearHotbarSelection(player);
        int i = 0;
        for (; i < size; i++) {
            ItemStack item = this.defaultHotbar[i];
            if(item != null) this.getInventory().setItem(i, item.clone());
            else this.getInventory().setItem(i, null);
        }
        if(TNT != null) getInventory().addItem(TNT.clone());
        this.equip();
    }

    public double getFallDamage() {
        return fallDamage;
    }
    public void setFallDamage(double fallDamage) {
        this.fallDamage = fallDamage;
    }

    public void setUsesEnergy(boolean usesEnergy){
        setUsesEnergy(usesEnergy, 180);
    }
    public void setUsesEnergy(boolean usesEnergy, double maxEnergy){
        if(usesEnergy){
            ebar = new EnergyBar(player, maxEnergy);
        } else {
            if(ebar == null) return;
            ebar.unregister();
            ebar.stop();
            ebar = null;
        }
    }
    public EnergyBar getEnergyBar(){
        return ebar;
    }

    public SoundWrapper getSound() {
        return sound;
    }
    public void setSound(SoundWrapper sound) {
        this.sound = sound;
    }

    public boolean isCloaked() {
        return StatusApplier.getOrNew(this.player).isCloaked();
    }
    public boolean isMarked() {
        return StatusApplier.getOrNew(this.player).isMarked();
    }
    public boolean isSilenced() {
        return StatusApplier.getOrNew(this.player).isSilenced();
    }
    public boolean isShocked() {
        return StatusApplier.getOrNew(this.player).isShocked();
    }

    public Set<Skill> getSkills() {
        return skills;
    }
    public Skill getCurrentSkillInHand() {
        final Material material = player.getItemInHand().getType();
        for(Skill skill : skills) {
            if(skill.getItemType() == null || skill.getItemType() == ItemType.NULL) continue;
            String name = skill.getItemType().getName();
            if(name != null && material.name().contains(name)) return skill;
        }
        return null;
    }

    public abstract JsonObject serialize();
}

