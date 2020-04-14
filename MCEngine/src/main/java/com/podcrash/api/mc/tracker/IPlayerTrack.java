package com.podcrash.api.mc.tracker;


import org.bukkit.entity.Player;

public interface IPlayerTrack<T> extends Tracker {
    T get(Player player);
}
