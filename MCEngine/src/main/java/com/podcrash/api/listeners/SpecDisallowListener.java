package com.podcrash.api.listeners;

import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SpecDisallowListener extends ListenerBase {
    public SpecDisallowListener(JavaPlugin plugin) {
        super(plugin);
    }

    private void spectatorDisallow(Player player, Cancellable cancellable) {
        Game game = GameManager.getGame();
        if (game == null) return;
        if (!game.isSpectating(player) && !game.isRespawning(player)) return;
        cancellable.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.LOWEST)
    public void blockPlace(BlockPlaceEvent e) {
        spectatorDisallow(e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void blockBreak(BlockBreakEvent e) {
        spectatorDisallow(e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void itemPickUp(PlayerPickupItemEvent e) {
        spectatorDisallow(e.getPlayer(), e);

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void itemDrop(PlayerDropItemEvent e) {
        spectatorDisallow(e.getPlayer(), e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void itemDrop(DamageApplyEvent e) {
        if (e.getAttacker() instanceof Player)
            spectatorDisallow((Player) e.getAttacker(), e);
    }

}
