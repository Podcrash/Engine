package com.podcrash.api.mc.map;

import com.google.gson.*;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.MapTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.mc.location.Coordinate;
import com.podcrash.api.redis.Communicator;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

public final class MapManager {
    //**'tag' IS THE FIELD**
    private static final String MAPS_CACHE = "MAPS-MAKE";

    public static boolean containsMap(String worldName) {
        return Communicator.containsKey(MAPS_CACHE, worldName);
    }
    /**
     * Get the json as stored on redis from the world's redis map
     * @param worldName
     * @return the map
     */
    public static JsonObject getMapJSON(String worldName) {
        String cacheValue = Communicator.getCacheValue(MAPS_CACHE, worldName);
        if(cacheValue == null || cacheValue.isEmpty()) {
            BaseGameMap map;
            if(Bukkit.getServer() != null) {
                World w = Bukkit.getWorld(worldName);
                map = new BaseGameMap(w);
            }else map = new BaseGameMap(worldName, new ArrayList<>(), new double[3]);
            return map.getJSON();

        }else return new JsonParser().parse(cacheValue).getAsJsonObject();
    }

    /**
     * These methods are modelled like so just in case we need recursion
     * @param prefix
     * @param object
     * @return
     */
    public static String getFriendlyInfo(String prefix, JsonObject object) {
        StringBuilder builder = new StringBuilder();
        for(Map.Entry<String, JsonElement> entry : object.entrySet()) {
            builder.append(entry.getKey());
            builder.append(" : ");
            JsonElement value = entry.getValue();
            String stringValue = value.toString();
            builder.append(stringValue);
            builder.append('\n');
        }
        return builder.toString();
    }
    public static String getFriendlyInfo(String worldName) {
        if(!containsMap(worldName)) return "Map " + worldName + " does not exist!";
        JsonObject object = getMapJSON(worldName);
        return "Current Information about " + worldName + '\n' +
                getFriendlyInfo("", object);
    }

    public static void insert(IMap map) {
        Communicator.cache(MAPS_CACHE, map.getName(), map.getJSON().toString());
    }

    /**
     * Caches the tag, value data onto redis
     * @param worldName - the world
     * @param tag - the tag name
     * @param value - must be a boolean, number or a string
     */
    /*
    public static <T> void insert(String worldName, String tag, T value) {
        if(value instanceof Boolean)
            insert(worldName, tag, (boolean) value);
        else if(value instanceof Number)
            insert(worldName, tag, (double) value);
        else if(value instanceof String)
            insert(worldName, tag, (String) value);
        else if(value instanceof Coordinate) {
            insert(worldName, tag, (Coordinate) value);
        }
    }

     */
    //Assorted insert methods (equivalent to put(key, value)
    public static void insert(String worldName, String tag, Boolean value) {
        JsonObject json = getMapJSON(worldName);
        json.addProperty(tag, value);
        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void insert(String worldName, String tag, Number value) {
        JsonObject json = getMapJSON(worldName);
        json.addProperty(tag, value);
        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void insert(String worldName, String tag, String value) {
        JsonObject json = getMapJSON(worldName);
        json.addProperty(tag, value);
        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void insert(String worldName, String tag, double[] value) {
        JsonObject json = getMapJSON(worldName);
        json.add(tag, JsonHelper.wrapXYZ(value));
        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void insert(String worldName, String tag, Coordinate value) {
        insert(worldName, tag, new double[] {value.getX(), value.getY(), value.getZ(), value.getYaw(), value.getPitch()});
    }

    public static void appendObject(String worldName, String tag, String key, JsonElement element) {
        JsonObject json = getMapJSON(worldName);
        System.out.println(tag);
        JsonElement elem = json.get(tag);
        JsonObject tagJson = (elem == null) ? new JsonObject() : elem.getAsJsonObject();
        tagJson.add(key, element);
        json.add(tag, tagJson);
        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void remove(String worldName, String tag) {
        JsonObject json = getMapJSON(worldName);
        json.remove(tag);

        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }

    /**
     * This is most likely a spawn, so we are going to make it a list insert.
     * @param worldName
     * @param tag
     * @param index
     * @param value
     */
    public static void insert(String worldName, String tag, int index, double[] value) {
        JsonObject json = getMapJSON(worldName);
        JsonElement possArr = json.get(tag);
        if(possArr != null && !possArr.isJsonArray()) throw new IllegalArgumentException(tag + " must be a json array!");

        JsonArray coordinates = JsonHelper.wrapXYZ(value);
        JsonArray possible = possArr.getAsJsonArray();
        int currentLength = possible.size();
        //if the index is higher than the current length size
        //ex: index = 8, current size = 5 (0, 1, 2, 3, 4, 5)
        //Add empty arrays in until the size is 8
        while(possible.size() < index + 1)
            possible.add(new JsonArray());

        possible.get(index).getAsJsonArray().add(coordinates);
        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void insertNext(String worldName, String tag, double[] value) {
        JsonArray coordinates = JsonHelper.wrapXYZ(value);

        JsonObject json = getMapJSON(worldName);
        JsonElement possArr = json.get(tag);
        JsonArray arr;
        if(possArr == null) {
            arr = new JsonArray();
            arr.add(coordinates);

            json.add(tag, arr);
        } else {
            arr = possArr.getAsJsonArray();
            arr.getAsJsonArray().add(coordinates);
        }

        Communicator.cache(MAPS_CACHE, worldName, json.toString());
    }
    public static void insert(String worldName, String tag, int index, Coordinate value) {
        insert(worldName, tag, index, new double[] {value.getX(), value.getY(), value.getZ(), value.getYaw(), value.getPitch()});
    }

    public static double[] get(String worldName, String tag, int index, int index2) {
        JsonObject json = getMapJSON(worldName);
        JsonElement possArr = json.get(tag);
        if(possArr != null && !possArr.isJsonArray()) throw new IllegalArgumentException(tag + " must be a json array!");
        return JsonHelper.getArray(possArr.getAsJsonArray().get(index).getAsJsonArray().get(index2).getAsJsonArray());
    }

    /**
     * Puts the cache onto mongo
     * @param worldName
     * @throws IllegalAccessException
     */
    public static CompletableFuture<Void> save(String worldName) throws IllegalAccessException {
        String value = Communicator.getCacheValue(MAPS_CACHE, worldName);
        if(value == null || value.isEmpty()) throw new IllegalAccessException("the value stored in redis cannnot be empty or null! Start storing some data!");
        JsonObject json = new JsonParser().parse(value).getAsJsonObject();
        MapTable maps = TableOrganizer.getTable(DataTableType.MAPS);
        return maps.upsertMetaData(json);
    }
    public static void delete(String worldName) {
        Communicator.removeCache(MAPS_CACHE, worldName);
    }

    /**
     * Put in your custom implementation to get a generic version of the map you want
     * @param mapClass the (sub)class of your map
     * @param worldName the name of the world
     * @param mapsumer the callback
     * @param <T> generic
     */
    public static <T extends IMap> void getMap(Class<T> mapClass, String worldName, Consumer<T> mapsumer) {
        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        table.findWorld(worldName, (map) -> {
            try {
                Constructor<T> constructor = mapClass.getConstructor(JsonObject.class);
                T abstractMap = constructor.newInstance(map);
                mapsumer.accept(abstractMap);
            } catch (NoSuchMethodException| InstantiationException | IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
    }
}
