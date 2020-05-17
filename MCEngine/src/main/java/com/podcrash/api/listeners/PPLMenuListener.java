package com.podcrash.api.listeners;

import com.podcrash.api.commands.helpers.GameCommands;
import com.podcrash.api.commands.helpers.PPLCommands;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.util.ItemStackUtil;
import net.md_5.bungee.protocol.packet.Chat;
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
                String.format("%s%s", ChatColor.AQUA, PPLCommands.getStateMsg()),
                Arrays.asList(
                        String.format("%sClick %sto toggle the whitelist", ChatColor.YELLOW, ChatColor.GRAY)
                )
        );

        ItemStack setMap = ItemStackUtil.createItem(
                Material.EMPTY_MAP,
                String.format("%sMap: %s%s", ChatColor.AQUA, ChatColor.GOLD, GameManager.getGame().getMapName()),
                Arrays.asList(
                        String.format("%sClick %sto change the map", ChatColor.YELLOW, ChatColor.GRAY)
                )
        );


        ItemStack setGame = new ItemStack(Material.WOOL, 1, DyeColor.MAGENTA.getData());
        ItemMeta setGameMeta = setGame.getItemMeta();
        setGameMeta.setDisplayName(String.format("%sGame: %s%s", ChatColor.AQUA, ChatColor.GOLD, GameManager.getGame().getMode()));
        setGameMeta.setLore(Arrays.asList(
                String.format("%sClick %sto change the game", ChatColor.YELLOW, ChatColor.GRAY)
        ));
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

    private Inventory createMapMenu() {
        Inventory inv = Bukkit.createInventory(null, 5 * 9, "Set Map");

        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        Set<String> validMaps = new HashSet<>(table.getWorlds(GameManager.getGame().getMode()));

        int i = 0;
        for (String mapName : validMaps) {
            if (mapName == null) continue;
            ItemStack map = ItemStackUtil.createItem(
                    Material.PAPER,
                    niceLookingMapName(mapName),
                    null);
            //Make a 5 wide table of maps
            int row = i / 5;
            int col = i % 5;
            //Translate table down 1, over 2
            inv.setItem(((row+1) * 9) + (col + 2), map);
            i++;
        }

        return inv;
    }

    private String niceLookingMapName(String s) {
        String[] splitName = s.split("(?=\\p{Upper})");
        return ChatColor.YELLOW + String.join(" ", splitName);
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
        ItemStack selected = event.getCurrentItem();
        if (selected == null || selected.getItemMeta() == null) return;
        if(!(event.getWhoClicked() instanceof Player)) return;
        String dispName = selected.getItemMeta().getDisplayName();
        Player player = (Player) event.getWhoClicked();

        if (event.getInventory().getName().equals("PPL Settings")) {
            if (dispName.contains("Change Max Players")) {
                if (event.getClick().isLeftClick()) {
                    PPLCommands.increaseMaxPlayers();
                } else if (event.getClick().isRightClick()) {
                    PPLCommands.decreaseMaxPlayers();
                }

            } else if (dispName.contains("Start Game")) {
                GameCommands.startGame(player, false);
                //Change the "start" item

            } else if (dispName.contains("Stop Game")) {
                GameCommands.endGame(player);
                //Change the "start" item


            } else if (dispName.contains("General PPL Settings")) {
                sendNotImplementedMessage(player);

            } else if (dispName.contains("Whitelist")) {
                PPLCommands.toggleWhitelist();
            } else if (dispName.contains("Map")) {
                player.openInventory(createMapMenu());
                event.setCancelled(true);
                return;
            } else if (dispName.contains("Game")) {
                sendNotImplementedMessage(player);

            } else if (dispName.contains("Manage Co-hosts")) {
                sendNotImplementedMessage(player);

            }

            //Update the menu
            player.openInventory(createMenu());
            event.setCancelled(true);
        } else if (event.getInventory().getName().equals("Set Map")) {
            GameManager.setGameMap(ChatColor.stripColor(dispName).replace(" ", ""));
            player.openInventory(createMenu());
            String msg = String.format(
                    "%sPPL Menu> %sYou have set the map to %s.",
                    ChatColor.BLUE,
                    ChatColor.GRAY,
                    dispName
            );
            player.sendMessage(msg);
            event.setCancelled(true);
        }
    }

    //TODO: Implement the features
    private void sendNotImplementedMessage(Player player) {
        String msg = String.format(
                "%sPPL Menu> %sThis feature has not been implemented yet.",
                ChatColor.BLUE,
                ChatColor.YELLOW
        );
        player.sendMessage(msg);
    }


}
