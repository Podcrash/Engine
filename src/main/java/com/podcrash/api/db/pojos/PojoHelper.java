package com.podcrash.api.db.pojos;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.db.tables.DataTableType;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.util.Vector;

import java.util.*;

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
        return createConquestGameData(new ArrayList<>());
    }
    public static ConquestGameData createConquestGameData(List<String> allowedSkills) {
        ConquestGameData conquestData = new ConquestGameData();
        conquestData.setAllowedSkills(allowedSkills);
        conquestData.setBuilds(new Document());
        return conquestData;
    }

    public static InvictaPlayer createInvictaPlayer(UUID uuid) {
        InvictaPlayer player = new InvictaPlayer();
        Currency currency = new Currency();
        currency.setGold(3000);
        player.setCurrency(currency);
        player.setExtraPerms(new HashSet<>());
        player.setRanks(new HashSet<>());
        player.setUuid(uuid);
        player.setDiscordID(-1);
        player.setGameData(new HashMap<>());
        player.setLastUsername(Bukkit.getPlayer(uuid).getName());

        return player;
    }


    public static Vector convertPoint2Vector(Point point) {
        return new Vector(point.getX(), point.getY(), point.getZ());
    }
    public static Point convertVector2Point(Vector vector) {
        Point p = new Point();
        p.setX(vector.getX());
        p.setY(vector.getY());
        p.setZ(vector.getZ());
        return p;
    }
}
