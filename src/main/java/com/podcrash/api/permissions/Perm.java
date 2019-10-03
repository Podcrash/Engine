package com.podcrash.api.permissions;

import org.bukkit.permissions.Permissible;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Perm {
    DEVELOPER("champions.developer", 0),
    ANTICHEAT("champions.anticheat", 1),
    HOST("champions.host", 2),
    BUILD("champions.can.break.map", 8),
    MUTE("champions.mute", 10);

    private String permName;
    private int dbId;

    Perm(String permName, int dbId) {
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


    public static Perm getBy(String name) {
        for(Perm perm : Perm.values())
            if(perm.name().equalsIgnoreCase(name))
                return perm;
        return null;
    }
    public static Perm getBy(int id) {
        for(Perm perm : Perm.values())
            if(perm.getDbId() == id)
                return perm;
        return null;
    }

    /**
     * Check if the permission name is valid
     * @param name
     * @return
     */
    public static boolean contains(String name) {
        for(Perm perm : Perm.values())
            if(perm.getPermName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    //we might want to store this in something like a redis instance
    public List<String> getPermissions() {
        switch (this) {
            case DEVELOPER:
                return Arrays.asList(
                    "champions.developer",
                    "plugman.admin",
                    "minecraft.command.*");
            case ANTICHEAT:
                return Arrays.asList(
                        "champions.anticheat");
            case BUILD:
                return Arrays.asList(
                        "champions.can.break.map");
            case HOST:
                return Arrays.asList(
                        "champions.host",
                        "minecraft.command.whitelist",
                        "minecraft.command.ban");
            case MUTE:
                return Arrays.asList(
                        "champions.mute");
            default:
                return Collections.emptyList();
        }
    }
}
