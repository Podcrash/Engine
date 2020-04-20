package com.podcrash.api.permissions;

import net.md_5.bungee.api.CommandSender;
import org.bukkit.permissions.Permissible;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Permission {
    DEVELOPER("champions.developer", 0),
    ANTICHEAT("champions.anticheat", 1),
    HOST("champions.host", 2),
    BUILD("champions.can.break.map", 8),
    MUTE("champions.mute", 10);

    private final String permName;
    private final int dbId;

    Permission(String permName, int dbId) {
        this.permName = permName;
        this.dbId = dbId;
    }

    public String getPermName() {
        return permName;
    }
    public int getDbId() {
        return dbId;
    }

    public boolean has(Permissible p) {
        return p.hasPermission(this.getPermName());
    }
    public boolean has(CommandSender s) {
        return s.hasPermission(this.getPermName());
    }

    public static Permission getBy(String name) {
        for(Permission perm : Permission.values())
            if(perm.name().equalsIgnoreCase(name))
                return perm;
        return null;
    }
    public static Permission getBy(int id) {
        for(Permission perm : Permission.values())
            if(perm.getDbId() == id)
                return perm;
        return null;
    }

    /**
     * Check if the permission name is valid
     * @param name The permission to check if exists
     * @return if the permission exists
     */
    public static boolean contains(String name) {
        for(Permission perm : Permission.values())
            if(perm.getPermName().equalsIgnoreCase(name))
                return true;
        return false;
    }


    /**
     * @return The list of bukkit permissions this permission contains
     */
    public List<String> getPermissions() {
        //we might want to store this in something like a redis instance
        switch (this) {
            case DEVELOPER:
                return Arrays.asList(
                    "champions.developer",
                    "plugman.admin",
                    "minecraft.command.*");
            case ANTICHEAT:
                return Collections.singletonList(
                        "champions.anticheat");
            case BUILD:
                return Collections.singletonList(
                        "champions.can.break.map");
            case HOST:
                return Arrays.asList(
                        "champions.host",
                        "minecraft.command.whitelist",
                        "minecraft.command.ban");
            case MUTE:
                return Collections.singletonList(
                        "champions.mute");
            default:
                return Collections.emptyList();
        }
    }

    @Override
    public String toString() {
        return name();
    }
}
