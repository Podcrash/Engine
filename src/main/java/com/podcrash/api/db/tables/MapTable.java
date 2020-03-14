package com.podcrash.api.db.tables;

import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.loaders.SlimeLoader;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.mongodb.Block;
import com.mongodb.client.model.*;
import com.podcrash.api.db.DBUtils;
import com.podcrash.api.db.MongoBaseTable;
import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.mc.world.WorldManager;
import com.podcrash.api.plugin.Pluginizer;
import org.apache.commons.io.FileUtils;
import org.bson.conversions.Bson;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;

/**
 * This manages both the metadata of the world and the actual data of the map.
 */
public class MapTable extends MongoBaseTable {

    public MapTable() {
        super("worldmaps");
    }

    @Override
    public void createTable() {
        CompletableFuture<String> future = new CompletableFuture<>();
        getCollection().createIndex(Indexes.descending("name", "gamemode"), new IndexOptions().unique(true), (res, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(res);
        });
        futureGuaranteeGet(future);
    }

    /**
     *
     * @param mapName
     * @param <T> type of BaseMap
     * @return
     */
    public <T> CompletableFuture<T> getMetadataAsync(String mapName, String mode, Class<T> mapClass) {
        CompletableFuture<T> future = new CompletableFuture<>();
        Bson filter = Filters.and(Filters.eq("name", mapName), Filters.eq("gamemode", mode));
        getCollection(mapClass).find(filter).first((result, t) -> {
            DBUtils.handleThrowables(t);
            Pluginizer.getLogger().info("found map metadata async: " + result);
            future.complete(result);
        });
        return future;
    }
    public <T> T getMetadataSync(String mapName, String mode, Class<T> mapClass) {
        return futureGuaranteeGet(getMetadataAsync(mapName, mode, mapClass));
    }

    private <T> CompletableFuture<Boolean> exists(String mapName, String mode, Class<T> mapClass) {
        return getMetadataAsync(mapName, mode, mapClass).thenApplyAsync(Objects::nonNull, SERVICE);
    }

    /**
     * super expensive quer(ies), maybe with morphia it could be better?
     * @param map
     */
    public <T extends BaseMap> CompletableFuture<Void> saveMetadataAsync(T map, Class<T> mapClass) {
        String name = map.getName();
        String mode = map.getGamemode();
        CompletableFuture<Void> future = new CompletableFuture<>();

        Bson filter = Filters.and(Filters.eq("name", name), Filters.eq("gamemode", mode));
        //delete if exists:
        CountDownLatch countDownLatch = new CountDownLatch(1);
        getCollection(mapClass).deleteOne(filter, (result, t) -> {
            DBUtils.handleThrowables(t);
            countDownLatch.countDown();
        });
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //insert
        getCollection(mapClass).insertOne(map, ((result, t) -> {
            DBUtils.handleThrowables(t);
            future.complete(result);
        }));
        return future;
    }

    public <T extends BaseMap> void saveMetadataSync(T map, Class<T> mapClass) {
        futureGuaranteeGet(saveMetadataAsync(map, mapClass));
    }
    @Override
    public DataTableType getDataTableType() {
        return DataTableType.MAPS;
    }

    /**
     * Download a world
     * @param worldName
     */
    public <T extends BaseMap> CompletableFuture<T> downloadWorld(@Nonnull String worldName, String mode, Class<T> mapClass) {
        SlimePlugin slimePlugin = getSlimePlugin();

        return getMetadataAsync(worldName, mode, mapClass).thenApplyAsync(map -> {
            if(map == null) {
                System.out.println(worldName + " doesn't exist!");
                return null;
            }
            SlimePropertyMap slimeMap = getSlimeProperties(map);
            final SlimeWorld slimeWorld;
            try {
                Bukkit.unloadWorld(worldName, false);
                slimeWorld = slimePlugin.loadWorld(getSlimeLoader(), worldName, true, slimeMap);
            } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException e) {
                e.printStackTrace();
                return map;
            }
            System.out.println("Loading " + slimeWorld.getName());
            Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(), () -> {
                try {
                    slimePlugin.generateWorld(slimeWorld);
                }catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("Generating " + slimeWorld.getName());
            }, 0L);

            return map;
        } , SERVICE);
    }

    private SlimePropertyMap getSlimeProperties(BaseMap map) {
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, map.isAllowAnimals());
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, map.isAllowMonsters());
        slimePropertyMap.setString(SlimeProperties.DIFFICULTY, "hard");
        slimePropertyMap.setBoolean(SlimeProperties.PVP, map.isAllowPvP());
        slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, map.getWorldType());
        slimePropertyMap.setString(SlimeProperties.ENVIRONMENT, map.getEnvironment());

        Point spawn = map.getDefaultSpawn();
        slimePropertyMap.setInt(SlimeProperties.SPAWN_X, (int) spawn.getX());
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, (int) spawn.getY());
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, (int) spawn.getZ());
        return slimePropertyMap;
    }

    /**
     * Upload a world to slimeworld system
     * @param worldName
     */
    public CompletableFuture<Void> uploadWorld(@Nonnull String worldName) {
        WorldManager.getInstance().unloadWorld(worldName);
        return CompletableFuture.runAsync(() -> {
            File folder = Bukkit.getWorldContainer().getAbsoluteFile();
            File worldFolder = FileUtils.getFile(folder.getAbsoluteFile() + File.separator + worldName);
            System.out.println(worldFolder.toString());
            System.out.println(worldFolder.getAbsoluteFile());
            SlimePlugin slimePlugin = getSlimePlugin();

            try {
                slimePlugin.importWorld(worldFolder, worldName, getSlimeLoader());
            } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException | WorldTooBigException | IOException e) {
                e.printStackTrace();
            }
        }, SERVICE);
    }

    public List<String> getWorlds(String mode) {
        final List<String> worlds = new ArrayList<>();
        Bson select = Projections.fields(Projections.include("name"), Projections.excludeId());
        Block<BaseMap> addToList = document -> {
            worlds.add(document.getName());
        };
        CountDownLatch latch = new CountDownLatch(1);
        getCollection(BaseMap.class)
            .find(Filters.eq("gamemode", mode))
            .projection(select)
            .forEach(addToList, (result, t) -> {
                DBUtils.handleThrowables(t);
                latch.countDown();
            });
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return worlds;
    }
    private SlimePlugin getSlimePlugin() {
        return (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }
    private SlimeLoader getSlimeLoader() {
       return getSlimePlugin().getLoader("podcrash");
    }
}