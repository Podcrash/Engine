package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.ui.TeamSelectGUI;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

public class PlayerInventoryListener extends ListenerBase {

    public PlayerInventoryListener(PodcrashSpigot plugin) {
        super(plugin);
    }

    @EventHandler
    public void playerTeamSelectEvent(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();
        Game g = GameManager.getGame();
        if (!g.contains(p) || e.getCurrentItem() == null) { return; }
        e.setCancelled(true);
        // If the game is not active, then perform corresponding actions.
        if (!g.isOngoing()) {
            // TODO: Change to listen to items with team color.
            if (e.getSlot() == 21 && e.getCurrentItem().getType().equals(Material.NAME_TAG)) {
                p.openInventory(TeamSelectGUI.selectTeam(g, p));
            } else if (e.getSlot() == 23 && e.getCurrentItem().getType().equals(Material.STAINED_GLASS) && e.getCurrentItem().getDurability() == 7) {
                g.leaveTeam(p);
            } else if (e.getSlot() == 25 && (e.getCurrentItem().getType().equals(Material.POTION) || e.getCurrentItem().getType().equals(Material.GLASS_BOTTLE))) {
                g.toggleSpec(p);
            }
            // For teams
            Inventory inv = e.getClickedInventory();
            if (inv.getTitle().equals(TeamSelectGUI.inventory_name)) {
                if (!e.getCurrentItem().getType().equals(Material.WOOL)) { return; }
                g.joinTeam(p, TeamEnum.getByData((int) e.getCurrentItem().getDurability()));
            }
        }
    }
}
