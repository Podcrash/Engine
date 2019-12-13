package com.podcrash.test;

import com.google.gson.JsonObject;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.mc.map.BaseGameMap;
import com.podcrash.api.mc.map.JsonHelper;
import com.podcrash.api.mc.map.MapManager;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.redis.Communicator;
import org.junit.jupiter.api.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.*;

public class MapTest {
    private static int i = 0;
    private static final String WORLDNAME = "yes";
    @BeforeAll
    public static void start() {
        try {
            Communicator.setup(Pluginizer.getService()).get();
            TableOrganizer.initConnections();
            TableOrganizer.createMongoTables(true);
        } catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }
    }

    @Order(1)
    @Test
    @DisplayName("Basic AbstractMap Implementation Test")
    public void map() {
        System.out.println(i++);
        JsonObject json = new JsonObject();
        String name = "wadawd";
        double[] spawn = new double[] {99, 23, 21};
        String environment = "awdawda";
        String worldtype = "232312";
        boolean animals = false;
        boolean monsters = true;
        boolean pvp = false;

        json.addProperty("name", name);
        json.add("spawn", JsonHelper.wrapXYZ(spawn));
        json.addProperty("environment", environment);
        json.addProperty("worldtype", worldtype);
        json.addProperty("allowAnimals", animals);
        json.addProperty("allowMonsters", monsters);
        json.addProperty("allowPvP", pvp);

        json.addProperty("randomdata", Math.random());
        BaseGameMap map = new BaseGameMap() {};

        map.fromJson(json);

        assertEquals(map.getName(), name);
        assertArrayEquals(map.getWorldSpawn(), spawn);
        assertEquals(map.getEnvironment(), environment);
        assertEquals(map.getWorldType(), map.getWorldType());

        assertEquals(map.allowAnimals(), animals);
        assertEquals(map.allowMonsters(), monsters);
        assertEquals(map.allowPvP(), pvp);
    }


    @Order(5)
    @Test
    @DisplayName("Using Redis to cache AbstractMap")
    public void insertTest() {
        System.out.println(i++);
        MapManager.insert(WORLDNAME, "custompoint", true);
        System.out.println(MapManager.getMapJSON(WORLDNAME).toString());
        assertTrue(MapManager.getMapJSON(WORLDNAME).get("custompoint").getAsBoolean());
        MapManager.insert(WORLDNAME, "spawns", 1, new double[]{0, 2, 3});
        MapManager.insert(WORLDNAME, "spawns", 9, new double[]{34, 12, 54, 34, 12});
        MapManager.insert(WORLDNAME, "spawns", 1, new double[]{3, 5, 7});

        assertArrayEquals(new double[]{34, 12, 54, 34, 12}, MapManager.get(WORLDNAME, "spawns", 9, 0));
        assertArrayEquals(new double[]{0, 2, 3}, MapManager.get(WORLDNAME, "spawns", 1, 0));
        assertArrayEquals(new double[]{3, 5, 7}, MapManager.get(WORLDNAME, "spawns", 1, 1));

        MapManager.insert(WORLDNAME, "randomStringDataValue", "waoidnawdawdadda");
        assertEquals(MapManager.getMapJSON(WORLDNAME).get("randomStringDataValue").getAsString(), "waoidnawdawdadda");
        MapManager.insert(WORLDNAME, "allowAnimals", true);
    }


    @Order(10)
    @Test
    @DisplayName("Using Redis to convert data to AbstractMap, then save to mongo")
    public void ontoMap() {
        final String sample = "TestWorld";
        BaseGameMap baseGameMap = new BaseGameMap(MapManager.getMapJSON(sample));
        assertTrue(baseGameMap.getName().equalsIgnoreCase(sample));
        assertTrue(baseGameMap.allowAnimals()); //default values work

        MapManager.insert(baseGameMap);
        MapManager.insert(sample, "intdata", 23);

        System.out.println("test2");
        System.out.println(MapManager.getMapJSON(sample));
        assertDoesNotThrow(() -> {
            Future<Void> future =  MapManager.save(sample);
            future.get();
        });


        MapManager.getMap(BaseGameMap.class, sample.toUpperCase(), map -> {
            System.out.println("ya" + map);
            assertTrue(map.allowAnimals());
        });

    }
    @AfterAll
    public static void end() {
        MapManager.delete(WORLDNAME);
        TableOrganizer.deleteTables(true);
        TableOrganizer.deleteConnections();
    }
}
