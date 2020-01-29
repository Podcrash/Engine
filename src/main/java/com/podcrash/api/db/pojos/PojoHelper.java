package com.podcrash.api.db.pojos;

import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

/**
 * Utility class to help with other logic that cannot be contained within the pojo getters/setters
 */
public final class PojoHelper {

    public static void addGameData(InvictaPlayer player, GameData data) {
        player.getGameData().put(data.getName(), data);
    }
    public static void removeGameData(InvictaPlayer player, GameData data) {
        player.getGameData().remove(data.getName());
    }

    public static void addExtraPerm(InvictaPlayer player, String perm) {
        player.getExtraPerms().add(perm);
    }
    public static void removeExtraPerm(InvictaPlayer player, String perm) {
        player.getExtraPerms().remove(perm);
    }

    public static ConquestGameData createConquestGameData() {
        ConquestGameData conquestData = new ConquestGameData();
        conquestData.setAllowedSkills(new ArrayList<>());
        conquestData.setBuilds(new HashMap<>());
        return conquestData;
    }

    public static InvictaPlayer createInvictaPlayer(UUID uuid) {
        InvictaPlayer player = new InvictaPlayer();
        player.setCurrency(new Currency());
        player.setExtraPerms(new ArrayList<>());
        player.setRank("");
        player.setUuid(uuid);
        player.setGameData(new HashMap<>());
        player.setLastUsername(Bukkit.getPlayer(uuid).getName());

        return player;
    }
}
