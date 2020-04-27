package com.podcrash.api.kits.iskilltypes.champion;

import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import com.podcrash.api.kits.enums.ItemType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface ISkill extends Listener {
    int getID();
    String getName();
    ItemType getItemType();

    Player getPlayer();
    void setPlayer(Player player);
    default <T extends KitPlayer> T getChampionsPlayer() {
        return (T) KitPlayerManager.getInstance().getKitPlayer(getPlayer());
    }

}
