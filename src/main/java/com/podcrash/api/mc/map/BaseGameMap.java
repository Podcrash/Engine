package com.podcrash.api.mc.map;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.grinderwolf.swm.api.world.properties.SlimeProperties;
import com.grinderwolf.swm.api.world.properties.SlimePropertyMap;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Extend from this class
 * Override fromJson and getJSON to get your desired values.
 * @see #getJSON()
 * @see #fromJson(JsonObject)
 * Please call the super methods!
 */
public class BaseGameMap implements IMap, Serializable {
    private static final long serialVersionUID = 1L;

    protected String name;
    protected List<double[][]> spawns;
    protected double[] worldSpawn;
    protected String environment;
    protected String worldType;

    protected boolean allowAnimals;
    protected boolean allowPvP;
    protected boolean allowMonsters;

    public BaseGameMap() {

    }

    public static SlimePropertyMap getSlimeProperties(JsonObject json) {
        double[] worldSpawn = JsonHelper.getArray(json.get("spawn").getAsJsonArray());
        String environment = json.get("environment").getAsString();
        String worldType = json.get("worldtype").getAsString();

        boolean allowAnimals = json.get("allowAnimals").getAsBoolean();
        boolean allowPvP = json.get("allowMonsters").getAsBoolean();
        boolean allowMonsters = json.get("allowPvP").getAsBoolean();

        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, allowAnimals);
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, allowMonsters);
        slimePropertyMap.setBoolean(SlimeProperties.PVP, allowPvP);
        slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, worldType);
        slimePropertyMap.setString(SlimeProperties.ENVIRONMENT, environment);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_X, (int) worldSpawn[0]);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, (int) worldSpawn[1]);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, (int) worldSpawn[2]);

        return slimePropertyMap;
    }


    public static SlimePropertyMap getSlimeMap(World world) {
        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, world.getAllowAnimals());
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, world.getAllowMonsters());
        slimePropertyMap.setBoolean(SlimeProperties.PVP, world.getPVP());
        slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, world.getWorldType().getName().toLowerCase());
        slimePropertyMap.setString(SlimeProperties.ENVIRONMENT, world.getEnvironment().name().toLowerCase());

        Location spawn = world.getSpawnLocation();
        slimePropertyMap.setInt(SlimeProperties.SPAWN_X, spawn.getBlockX());
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, spawn.getBlockY());
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, spawn.getBlockZ());

        return slimePropertyMap;
    }

    public BaseGameMap(World world) {
        this(world.getName(), new ArrayList<>(), JsonHelper.getDoubleArray(world.getSpawnLocation()), world.getEnvironment().name().toLowerCase(), world.getWorldType().getName().toLowerCase(), world.getAllowAnimals(), world.getPVP(), world.getAllowMonsters());
    }
    public BaseGameMap(String name, List<double[][]> spawns, double[] worldSpawn, String environment, String worldType, boolean allowAnimals, boolean allowPvP, boolean allowMonsters) {
        this.name = name.toUpperCase();
        this.spawns = spawns;
        this.worldSpawn = worldSpawn;
        this.environment = environment;
        this.worldType = worldType;
        this.allowAnimals = allowAnimals;
        this.allowPvP = allowPvP;
        this.allowMonsters = allowMonsters;
    }

    public BaseGameMap(String name, List<double[][]> spawns, double[] worldSpawn) {
        this(name, spawns, worldSpawn, "normal", "default", true, true, true);
    }

    /**
     * Please note:
     * THIS CONSTRUCTOR IS NECESSARY TO BE ADDED ON SUBCLASSES
     * Override the method aswell
     * @param json
     */
    public BaseGameMap(JsonObject json) {
        fromJson(json);
    }

    /**
     * This method is usually overriden to include this + any custom fields that is used for a specific gamemode.
     * @param json
     */
    @Override
    public void fromJson(JsonObject json) {
        //bruh this is so bad
        this.name = json.get("name").getAsString();
        JsonElement spawnElement = json.get("spawn");
        this.worldSpawn = spawnElement == null ? new double[3] : JsonHelper.getArray(spawnElement.getAsJsonArray());
        this.environment = json.get("environment").getAsString();
        this.worldType = json.get("worldtype").getAsString();

        this.allowAnimals = json.get("allowAnimals").getAsBoolean();
        this.allowPvP = json.get("allowPvP").getAsBoolean();
        this.allowMonsters = json.get("allowMonsters").getAsBoolean();


        List<double[][]> spawns = new ArrayList<>();

        //this is 3rd dimension
        JsonArray spawnsArray = json.getAsJsonArray("spawns");

        if(spawnsArray == null || spawnsArray.size() == 0) {
            this.spawns = spawns;
            return;
        }
        //this is 2nd dimension
        for(int i = 0; i < spawnsArray.size(); i++) {
            //this is 2nd dimension
            JsonArray team1 = spawnsArray.get(i).getAsJsonArray();
            double[][] dspawns = new double[team1.size()][];
            for(int j = 0; j < team1.size(); j++) {
                JsonArray spawn2 = team1.get(j).getAsJsonArray();
                //3rd dimension
                double[] spawns3 = JsonHelper.getArray(spawn2);
                dspawns[j] = spawns3;
            }
            spawns.add(dspawns);
        }

        this.spawns = spawns;
    }

    @Override
    public final String getName() {
        return name;
    }

    @Override
    public List<double[][]> getSpawns() {
        return spawns;
    }

    @Override
    public final double[] getWorldSpawn() {
        return worldSpawn;
    }

    @Override
    public final String getEnvironment() {
        return environment;
    }
    @Override
    public final String getWorldType() {
        return worldType;
    }

    @Override
    public final boolean allowAnimals() {
        return allowAnimals;
    }
    @Override
    public final boolean allowPvP() {
        return allowPvP;
    }
    @Override
    public final boolean allowMonsters() {
        return allowMonsters;
    }

    @Override
    public JsonObject getJSON() {
        JsonObject result = new JsonObject();

        result.addProperty("name", getName());

        if(getSpawns() == null || getSpawns().size() == 0) {
            JsonArray array = new JsonArray();
            JsonArray array1 = new JsonArray();
            array.add(array1);
            result.add("spawns", array);
        }else {
            JsonArray spawnObject = JsonHelper.wrapTeam(getSpawns());
            result.add("spawns", spawnObject);
        }

        result.add("spawn", JsonHelper.wrapXYZ(getWorldSpawn()));
        result.addProperty("allowAnimals", allowAnimals());
        result.addProperty("allowMonsters", allowMonsters());
        result.addProperty("allowPvP", allowPvP());

        result.addProperty("environment", getEnvironment());
        result.addProperty("worldtype", getWorldType());

        return result;
    }

    public final SlimePropertyMap getSlimeProperties() {

        SlimePropertyMap slimePropertyMap = new SlimePropertyMap();
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_ANIMALS, allowAnimals);
        slimePropertyMap.setBoolean(SlimeProperties.ALLOW_MONSTERS, allowMonsters);
        slimePropertyMap.setBoolean(SlimeProperties.PVP, allowPvP);
        slimePropertyMap.setString(SlimeProperties.WORLD_TYPE, worldType);
        slimePropertyMap.setString(SlimeProperties.ENVIRONMENT, environment);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_X, (int) worldSpawn[0]);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Y, (int) worldSpawn[1]);
        slimePropertyMap.setInt(SlimeProperties.SPAWN_Z, (int) worldSpawn[2]);

        return slimePropertyMap;
    }

    public final void setName(String name) {
        this.name = name.toUpperCase();
    }
    public final void setSpawns(List<double[][]> spawns) {
        this.spawns = spawns;
    }
    public final void setWorldSpawn(double[] worldSpawn) {
        this.worldSpawn = worldSpawn;
    }

    public final void setEnvironment(String environment) {
        this.environment = environment;
    }
    public final void setWorldType(String worldType) {
        this.worldType = worldType;
    }

    public final void setAllowAnimals(boolean allowAnimals) {
        this.allowAnimals = allowAnimals;
    }
    public final void setAllowPvP(boolean allowPvP) {
        this.allowPvP = allowPvP;
    }
    public final void setAllowMonsters(boolean allowMonsters) {
        this.allowMonsters = allowMonsters;
    }

    @Override
    public String toString() {
        return "AbstractMap{" + "name='" + name + '\'' +
                ", spawns=" + spawns +
                ", worldSpawn=" + Arrays.toString(worldSpawn) +
                ", environment='" + environment + '\'' +
                ", worldType='" + worldType + '\'' +
                ", allowAnimals=" + allowAnimals +
                ", allowPvP=" + allowPvP +
                ", allowMonsters=" + allowMonsters +
                '}';
    }
}
