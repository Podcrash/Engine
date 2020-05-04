package com.podcrash.api.kits.iskilltypes.action;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerDropItemEvent;

public interface IDropPassive {

    @EventHandler( priority = EventPriority.LOW )
    void drop(PlayerDropItemEvent e);


    void doSkill();
}
