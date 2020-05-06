package com.podcrash.api.listeners;

import com.podcrash.api.commands.helpers.GameCommands;
import com.podcrash.api.commands.helpers.PPLCommands;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class PPLMenuListener extends ListenerBase {

    private final Set<Action> validActions = new HashSet<>(Arrays.asList(
            Action.RIGHT_CLICK_AIR,
            Action.RIGHT_CLICK_BLOCK,
            Action.LEFT_CLICK_AIR,
            Action.LEFT_CLICK_BLOCK));

    public PPLMenuListener(JavaPlugin plugin) {
        super(plugin);
    }

    private Inventory createMenu() {
        Inventory inv = Bukkit.createInventory(null, 5 * 9, "PPL Settings");

        ItemStack playerCount = ItemStackUtil.createItem(
                Material.DIODE,
                String.format("%sChange Max Players", ChatColor.AQUA),
                Arrays.asList(
                    String.format("%sLeft click %sto increase the maximum number of players", ChatColor.YELLOW, ChatColor.GRAY),
                    String.format("%sRight click %sto decrease the maximum number of players", ChatColor.YELLOW, ChatColor.GRAY)
                )
        );

        ItemStack startGame = ItemStackUtil.createItem(
                Material.EMERALD_BLOCK,
                String.format("%s%sStart Game", ChatColor.GREEN, ChatColor.BOLD),
                null
        );

        ItemStack stopGame = ItemStackUtil.createItem(
                Material.REDSTONE_BLOCK,
                String.format("%s%sStop Game", ChatColor.RED, ChatColor.BOLD),
                null
        );

        ItemStack generalSettings = ItemStackUtil.createItem(
                Material.REDSTONE_COMPARATOR,
                String.format("%sGeneral PPL Settings", ChatColor.AQUA),
                null
        );

        ItemStack whitelist = ItemStackUtil.createItem(
                Material.PAPER,
                String.format("%sToggle Whitelist", ChatColor.AQUA),
                null
        );

        ItemStack setMap = ItemStackUtil.createItem(
                Material.EMPTY_MAP,
                String.format("%sChange Map", ChatColor.AQUA),
                null
        );


        ItemStack setGame = new ItemStack(Material.WOOL, 1, DyeColor.MAGENTA.getData());
        ItemMeta setGameMeta = setGame.getItemMeta();
        setGameMeta.setDisplayName(String.format("%sChange Game", ChatColor.AQUA));
        setGame.setItemMeta(setGameMeta);

        ItemStack cohosts = new ItemStack(Material.WOOL, 1, DyeColor.ORANGE.getData());
        ItemMeta cohostMeta = cohosts.getItemMeta();
        cohostMeta.setDisplayName(String.format("%sManage Co-hosts", ChatColor.AQUA));
        cohosts.setItemMeta(cohostMeta);

        //(ROW * 9 + COL) starting from 0
        inv.setItem(0 * 9 + 4, playerCount);
        inv.setItem(1 * 9 + 1, setGame);
        inv.setItem(3 * 9 + 1, setMap);
        inv.setItem(1 * 9 + 7, cohosts);
        inv.setItem(3 * 9 + 7, whitelist);
        inv.setItem(4 * 9 + 4, generalSettings);

        if (!GameManager.getGame().getTimer().isRunning()) {
            inv.setItem(2 * 9 + 4, startGame);
        } else {
            inv.setItem(2 * 9 + 4, stopGame);
        }

        return inv;
    }

    @EventHandler
    public void openMenu(PlayerInteractEvent event) {

        if (validActions.contains(event.getAction()) && event.getPlayer().getUniqueId().equals(PodcrashSpigot.getInstance().getPPLOwner())) {
            Player player = event.getPlayer();
            if (player.getItemInHand() == null || player.getItemInHand().getItemMeta() == null) return;
            ItemMeta meta = player.getItemInHand().getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().toLowerCase().contains("ppl settings")) {
                player.openInventory(createMenu());
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onMenuPress(InventoryClickEvent event) {
        if (event.getInventory().getName().equals("PPL Settings")) {
            ItemStack selected = event.getCurrentItem();
            if (selected == null || selected.getItemMeta() == null) return;
            if(!(event.getWhoClicked() instanceof Player)) return;
            String dispName = selected.getItemMeta().getDisplayName();
            Player player = (Player) event.getWhoClicked();
            if (dispName.contains("Change Max Players")) {
                if (event.getClick().isLeftClick()) {
                    PPLCommands.increaseMaxPlayers();
                } else if (event.getClick().isRightClick()) {
                    PPLCommands.decreaseMaxPlayers();
                }
            } else if (dispName.contains("Start Game")) {
                GameCommands.startGame(player, false);
                //Change the "start" item
                player.openInventory(createMenu());
            } else if (dispName.contains("Stop Game")) {
                GameCommands.endGame(player);
                //Change the "start" item
                player.openInventory(createMenu());
            } else if (dispName.contains("General PPL Settings")) {

            } else if (dispName.contains("Toggle Whitelist")) {

            } else if (dispName.contains("Change Map")) {

            } else if (dispName.contains("Change Game")) {

            } else if (dispName.contains("Manage Co-hosts")) {

            }

            event.setCancelled(true);
        }
    }


}
