package com.podcrash.api.listeners;

import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.TeamEnum;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.plugin.java.JavaPlugin;

public class GameLobbyListener extends ListenerBase {
    public GameLobbyListener(JavaPlugin plugin) {
        super(plugin);
    }

    /**
     * Right clicking a beacon in your hand
     */
    @EventHandler
    public void clickBeacon(PlayerInteractEvent event) {
        ItemStack itemStack = event.getPlayer().getItemInHand();
        if(itemStack.getType() == Material.WOOL && GameManager.hasPlayer(event.getPlayer())) {
            if(itemStack.getData() instanceof Wool) {
                event.setCancelled(true);
                Wool woolData = (Wool) itemStack.getData();
                Game game = GameManager.getGame();
                int id = game.getId();
                if(woolData.getColor() == DyeColor.BLUE) {
                    GameManager.joinTeam(event.getPlayer(), TeamEnum.BLUE);
                }else if(woolData.getColor() == DyeColor.RED) {
                    GameManager.joinTeam(event.getPlayer(), TeamEnum.RED);
                }
            }

        }
    }
}
