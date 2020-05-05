package com.podcrash.api.listeners;

import com.podcrash.api.commands.IncreaseMaxPlayersCommand;
import com.podcrash.api.game.GTeam;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.util.ItemStackUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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

        ItemStack playerCount = ItemStackUtil.createItem(Material.DIODE,
                String.format("%sChange Max Players", ChatColor.AQUA),
                Arrays.asList(
                    String.format("%sLeft click %sto increase the maximum number of players", ChatColor.YELLOW, ChatColor.GRAY),
                    String.format("%sRight click %sto decrease the maximum number of players", ChatColor.YELLOW, ChatColor.GRAY)
                )
        );
        inv.setItem(0 * 4 + 4, playerCount);
        return inv;
    }

    @EventHandler
    public void openMenu(PlayerInteractEvent event) {

        if (validActions.contains(event.getAction()) && event.getPlayer().getUniqueId().equals(PodcrashSpigot.getInstance().getPPLOwner())) {
            Player player = event.getPlayer();
            ItemMeta meta = player.getItemInHand().getItemMeta();
            if (meta.hasDisplayName() && meta.getDisplayName().toLowerCase().contains("ppl settings")) {
                player.openInventory(createMenu());
            }
        }
    }

    @EventHandler
    public void onMenuPress(InventoryClickEvent event) {
        if (event.getInventory().getName().equals("PPL Settings")) {
            ItemStack selected = event.getCurrentItem();
            if (selected.getItemMeta().getDisplayName().contains("Change Max Players")) {
                if (event.getClick().isLeftClick()) {
                    //TODO: get rid of nasty copy-and-paste code and refactor increase/decrease command
                    Game game = GameManager.getGame();
                    int currMax = game.getMaxPlayers() + 1;
                    int possibleMax = game.getTeam(0).getMaxPlayers() * game.getTeams().size();
                    game.setMaxPlayers(currMax);
                    if (currMax > possibleMax) {
                        for (GTeam team : game.getTeams()) {
                            team.setMaxPlayers(team.getMaxPlayers() + 1);
                        }
                    }
                } else if (event.getClick().isRightClick()) {
                    Game game = GameManager.getGame();
                    int currMax = game.getMaxPlayers();
                    int target = currMax - 1;

                    int currMaxForSingleTeam = 0;
                    for (GTeam team : game.getTeams()) {
                        if (team.getPlayers().size() > currMaxForSingleTeam) currMaxForSingleTeam = team.getPlayers().size();
                    }

                    //If we can even decrease at all
                    if (target >= currMaxForSingleTeam * game.getTeams().size() && target >= game.getMinPlayers()) {
                        game.setMaxPlayers(target);

                        for (GTeam team : game.getTeams()) {
                            team.setMaxPlayers((game.getMaxPlayers() + game.getTeams().size() - 1) / game.getTeams().size());
                        }
                    }
                }
            }

            event.setCancelled(true);
        }
    }


}
