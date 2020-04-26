package com.podcrash.api.world;

import com.podcrash.api.plugin.PodcrashSpigot;
import org.apache.commons.io.FileUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;

/**
 * From https://bukkit.org/threads/unload-delete-copy-worlds.182814/, thanks!
 * Well, most of the inspiration anyway.
 * This is a singleton that manages worlds.
 */
public class WorldManager {
    private static volatile WorldManager worldManager;
    private final HashSet<String> worlds = new HashSet<>();

    /**
     * Load the world, then return if it was actually loaded.
     *
     * @param string name of the world to load
     * @return whether the loading was successful
     */
    public boolean loadWorld(String string) {
        World world = Bukkit.getWorld(string);
        if (world == null) {
            log(String.format("%s was nothing, now loading", string));
            world = Bukkit.getServer().createWorld(new WorldCreator(string));
        } else if (Bukkit.getWorlds().contains(world)) {
            log(String.format("%s was already loaded? returning false for loadWorld(%s)", string, string));
            return false;
        }
        return Bukkit.getWorlds().add(world);
    }

    /**
     * Copy an existing world and paste it.
     * By default, this uses the copied world's name + the current time.
     *
     * @param worldName name of the world
     * @return World that is returned after copying it
     */
    public World copyWorld(String worldName) {
        World get = Bukkit.getWorld(worldName);
        if (get == null) {
            log(String.format("%s does not exist!", worldName));
        } else {
            log("Copying world " + worldName);
            String copiedName = worldName + System.currentTimeMillis();
            String dirName = String.format("%s%s%s", Bukkit.getWorldContainer().toString(), File.separator, copiedName);
            File world2File = new File(dirName);
            if (!world2File.exists())
                if(!world2File.mkdir()) {
                    PodcrashSpigot.getInstance().getLogger().log(Level.SEVERE, "[WorldManager] Could not make world directory!");
                    return null;
                }
            log("Made the directory " + dirName + ", proceeding to copy");
            try {
                FileUtils.copyDirectory(get.getWorldFolder(), world2File);
                String[] avoid = new String[]{"uid.dat", "session.dat"};
                if (world2File.isDirectory()) { //<-- this will always be true
                    log("Deleting useless files for " + worldName);
                    for (File file : world2File.listFiles()) {
                        if (!file.isDirectory() && (file.getName().equalsIgnoreCase(avoid[0]) || file.getName().equalsIgnoreCase(avoid[1]))) {
                            String name = file.getName();
                            if (FileUtils.deleteQuietly(file))
                                log(String.format("Successfully removed %s from world %s", name, worldName));
                        }
                    }
                }
                log("Now attempting to load the world");
                if (loadWorld(copiedName)) {
                    World world = Bukkit.getWorld(copiedName);
                    worlds.add(world.getName());
                    return world;
                }
            } catch (IOException | NullPointerException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * On startup, load up worlds that are not 'plugins' or 'logs'
     *
     */
    public void loadWorlds() {
        try {
            final List<String> ignoreList = Arrays.asList("logs", "plugins", "timings", "crash-reports", "slime_worlds");
            for (File file : Bukkit.getWorldContainer().listFiles()) {
                if (file.isDirectory() && !ignoreList.contains(file.getName())) {
                    World poss = Bukkit.getWorld(file.getName());
                    log(file.getName() + " being loaded");
                    if (poss == null) {
                        log(file.getName() + " turned out to be nothing");
                        poss = Bukkit.getServer().createWorld(new WorldCreator(file.getName()));
                    }
                    log(poss.toString());
                    if (Bukkit.getWorlds().add(poss)) {
                        log(file.getName() + " was successful");
                    } else log(file.getName() + " was a failure");
                }
            }
            log("All worlds are now loaded!");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }
    public void unloadWorlds() {
        unloadWorlds(false);
    }
    /**
     * Delete all the temporary worlds.
     */
    public void unloadWorlds(boolean perm){
        for(String worldName : worlds){
            World world = Bukkit.getWorld(worldName);
            deleteWorld(world, perm);
        }
    }

    /**
     * Unload a world from bukkit (don't delete)
     *
     * @param string name of the world
     * @return if the world was successfully unloaded
     */
    public boolean unloadWorld(String string, boolean force) {
        log("Attempting to unload world " + string);
        World world = Bukkit.getWorld(string);
        if (world == null) return false;
        CraftBlockUpdater.getMassBlockUpdater(world).stop();
        if (Bukkit.unloadWorld(string, true)) {
            log("Successfully unloaded world " + string);
            if (!wasMade(world) && force) {
                log("force unloading this world " + string + " regardless of the means for the world");
            } else if (wasMade(world)) worlds.remove(world.getName());
            return true;
        } else return false;
    }
    public boolean unloadWorld(String string) {
        return unloadWorld(string, false);
    }

    /**
     * Delete a world off the bukkit system or off the file system.
     *
     * @param world     Bukkit world of the world you want to delete
     * @param permanent if it is permanent, delete entirely off the file system
     */
    public void deleteWorld(World world, boolean permanent) {
        if (world == null) {
            log("world was null.");
            return;
        }
        log("Attempting to delete " + world.getName());
        if (world.getPlayers().size() > 0) {
            //teleport players out first, there should be a 'default' world instead of specifying "world"
            log(String.format("Teleporting players out of %s to safely delete it", world.getName()));
            for (Player player : world.getPlayers()) player.teleport(Bukkit.getWorld("world").getSpawnLocation());
        }
        if (unloadWorld(world.getName()) && permanent) {
            log("Permanent was specified, deleting completely");
            FileUtils.deleteQuietly(world.getWorldFolder());
        }
    }

    /**
     * Teleport a player to another world
     *
     * @param p player to teleport
     * @param s the name of the world to teleport @p to
     */
    public void teleport(Player p, String s) {
        log(String.format("Teleported %s to %s", p.getName(), s));
        World world = Bukkit.getWorld(s);
        if (world == null) {
            p.sendMessage(String.format("%sWorldTeleporter> %s%s %sis not a valid world!", ChatColor.GREEN, ChatColor.WHITE, s, ChatColor.AQUA));
            return;
        }
        Location location = world.getSpawnLocation();
        p.teleport(location);
    }

    /**
     * Check if the world was made via a copy
     *
     * @param world to check
     * @return whether or not the world was not made through natural means, such as copying it
     */
    private boolean wasMade(World world) {
        return worlds.contains(world.getName());
    }
    public HashSet<String> getWorlds() {
        return this.worlds;
    }
    public List<World> getLoadedWorldsList() {
        return Bukkit.getWorlds();
    }

    public static WorldManager getInstance() {
        if (worldManager == null) {
            synchronized (WorldManager.class) {
                if (worldManager == null) {
                    worldManager = new WorldManager();
                }
            }

        }
        return worldManager;
    }
    private void log(String string) {
        PodcrashSpigot.getInstance().getLogger().info(String.format("[WorldManager] %s", string));
    }
}
