package com.podcrash.api.listeners;

import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.events.EnableLobbyPVPEvent;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.sound.SoundPlayer;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class GeneralLobbyListener extends ListenerBase {
    public GeneralLobbyListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void enableGeneralLobbyPVP(PlayerInteractEvent event) {
        //if (event.isCancelled()) return;
        Player player = event.getPlayer();
        // Only run this code if there is no game going on; this will work even if engine is the only plugin present
        PodcrashSpigot.debugLog("test123");
        System.out.println("test123");
        if (GameManager.getGame() == null || player.getItemInHand().getType().equals(Material.AIR))
            return;
        System.out.println("test1234");

        String mode = GameManager.getGame() != null ? GameManager.getGame().getMode() : null;

        //System.out.println("tests");
        Set<Action> validActions = new HashSet<>(Arrays.asList(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_AIR, Action.LEFT_CLICK_BLOCK));
        boolean isActioning = validActions.contains(event.getAction());

        ItemMeta meta = player.getItemInHand().getItemMeta();
        boolean isHoldingItem = meta.hasDisplayName() && meta.getDisplayName().toLowerCase().contains("enable lobby pvp");


        System.out.println(isActioning + " " + isHoldingItem);
        if (isActioning && isHoldingItem) {
            SoundPlayer.sendSound(player, "random.pop", 1F, 63);
            DamageApplier.removeInvincibleEntity(player);
            event.setCancelled(true);
            EnableLobbyPVPEvent e = new EnableLobbyPVPEvent(player, mode);
            Bukkit.getPluginManager().callEvent(e);
        }
    }

    @EventHandler
    private void applyGeneralPVPGear(EnableLobbyPVPEvent event) {
        // Only apply this general PVP gear if there is no game currently running.
        if(event.getGameType() != null)
            return;

        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.spigot().setUnbreakable(true);
        sword.setItemMeta(meta);
        event.getPlayer().setItemInHand(sword);

        Material[] armor = {Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE , Material.IRON_HELMET};
        ItemStack[] armors = new ItemStack[4];
        for(int i = 0; i < armor.length; i++){
            Material mat = armor[i];
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(mat));
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);
            nmsStack.setTag(tag);

            armors[i] = new ItemStack(CraftItemStack.asBukkitCopy(nmsStack));
        }
        event.getPlayer().getEquipment().setArmorContents(armors);
    }
}
