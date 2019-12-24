package com.podcrash.api.db;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.grinderwolf.swm.api.SlimePlugin;
import com.grinderwolf.swm.api.exceptions.*;
import com.grinderwolf.swm.api.world.SlimeWorld;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.podcrash.api.mc.map.BaseGameMap;
import com.podcrash.api.mc.map.IMap;
import com.podcrash.api.mc.world.WorldManager;
import com.podcrash.api.plugin.Pluginizer;
import com.sun.org.apache.bcel.internal.generic.ARETURN;
import jodd.util.collection.StringKeyedMapAdapter;
import org.apache.commons.io.FileUtils;
import org.bson.Document;
import org.bukkit.Bukkit;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

/**
 * This should return GSONS
 */
public class MapTable extends MongoBaseTable {

    public MapTable(boolean test) {
        super("worldmaps", test);

        //TODO: custom make a collection easily and be able to modify its settings maybe
        //getDatabase().createCollection(getName());
    }

    @Override
    public DataTableType getDataTableType() {
        return DataTableType.MAPS;
    }

    /**
     * Find the world metadata, uses a json string.
     * This is needed since the child project don't have mongo as a dependency
     * @param worldName
     * @param callback - once the function is completed, it calls the callback
     */
    public void findWorld(@Nonnull String worldName, Consumer<JsonObject> callback) {
        findWorld(worldName, (res, throwable) -> {
            if(throwable != null) Pluginizer.getSpigotPlugin().getLogger().info(throwable.getLocalizedMessage());
            JsonObject json = (res == null) ? null : new JsonParser().parse(res.toJson()).getAsJsonObject();
            callback.accept(json);
        });
    }
    /**
     * Find the world metadata, uses document
     * @param worldName
     * @param callback - once the function is completed, it calls the callback
     */
    private void findWorld(@Nonnull String worldName, SingleResultCallback<Document> callback) {
        getCollection("maps")
        .find(Filters.eq("name", worldName.toUpperCase()))
        .first(callback);
    }

    /**
     * Download a world
     * @param worldName
     */
    public void downloadWorld(@Nonnull String worldName) {
        SlimePlugin slimePlugin = getSlimePlugin();

        findWorld(worldName, ((result, t) -> {
            if (t != null) t.printStackTrace();
            SlimePropertyMap slimeMap;
            if (result == null) {
                System.out.println(worldName + " doesn't exist!");
                return;
            }
            JsonObject json = new JsonParser().parse(result.toJson()).getAsJsonObject();
            slimeMap = BaseGameMap.getSlimeProperties(json);
            final SlimeWorld slimeWorld;
            try {
                Bukkit.unloadWorld(worldName, false);
                slimeWorld = slimePlugin.loadWorld(slimePlugin.getLoader("mongodb"), worldName, true, slimeMap);
            } catch (UnknownWorldException | IOException | CorruptedWorldException | NewerFormatException | WorldInUseException e) {
                e.printStackTrace();
                return;
            }
            System.out.println("Loading " + slimeWorld.getName());
            Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(), () -> {
                slimePlugin.generateWorld(slimeWorld);
                System.out.println("Generating " + slimeWorld.getName());
            }, 1L);

        }));

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
                slimePlugin.importWorld(worldFolder, worldName, slimePlugin.getLoader("mongodb"));
            } catch (WorldAlreadyExistsException | InvalidWorldException | WorldLoadedException | WorldTooBigException | IOException e) {
                e.printStackTrace();
            }
        }, SERVICE);
    }

    /**
     * Update mapdata
     * @see {@link BaseGameMap#getJSON()}
     *
     * @param mapdata - json map of the map object
     */
    public CompletableFuture<Void> upsertMetaData(@Nonnull JsonObject mapdata) {
        //TODO: instead of having null callbacks, make a general all purpose callback (esp for player permissions)
        String name = mapdata.get("name").getAsString();
        CompletableFuture<Void> complete = new CompletableFuture<>();
        findWorld(name, (res, throwable) -> {
            if(throwable != null) {
                throwable.printStackTrace();
                complete.complete(null);
            }
            MongoCollection<Document> mapsCol = getCollection("maps");


            Document mapDoc = Document.parse(mapdata.toString());

            Iterator<Map.Entry<String, Object>> entries = mapDoc.entrySet().iterator();
            while(entries.hasNext()) {
                Map.Entry<String, Object> entry = entries.next();
                Object value = entry.getValue();
                if (!(value instanceof List)) continue;
                List list = (List) value;
                if (list.get(0) == null || !(list.get(0) instanceof Number)) continue;
                list.sort((o1, o2) -> {
                    int o1h = o1.hashCode();
                    int o2h = o2.hashCode();
                    if (o2h > o1h)
                        return 1;
                    else if (o2h < o1h)
                        return -1;
                    else return 0;
                });

                mapDoc.put(entry.getKey(), list);


            }
            if(res == null) mapsCol.insertOne(mapDoc, (res1, throwable1) -> {
                if(throwable1 != null) throwable1.printStackTrace();
                complete.complete(res1);
            });
            else {
                mapsCol.replaceOne(
                    Filters.eq("name", name),
                    mapDoc,
                    (res1, throwable1) -> {
                        if(throwable1 != null) throwable1.printStackTrace();
                        complete.complete(null);
                    });
            }
        });
        return complete;
    }

    /**
     * @see {@link #upsertMetaData(JsonObject)}
     * @param mapdata
     */
    public void upsertMetaData(@Nonnull IMap mapdata) {
        upsertMetaData(mapdata.getJSON());
    }

    private static class RegularCallback implements SingleResultCallback<Void> {
        @Override
        public void onResult(Void result, Throwable t) {
            if(t != null) t.printStackTrace();
        }
    }

    private SlimePlugin getSlimePlugin() {
        return (SlimePlugin) Bukkit.getPluginManager().getPlugin("SlimeWorldManager");
    }
}
