package com.podcrash.api.mc.map;

import com.google.gson.JsonArray;
import org.bukkit.Location;

import java.util.List;

/**
 * Utility methods that revolve around converting [34, 34,23,4 1,4,12] to a JsonArray
 * Whatever that means.
 */
public final class JsonHelper {
    public static JsonArray wrapXYZ(double[] spawn) {
        JsonArray coordinateObject = new JsonArray();
        for(double s : spawn)
            coordinateObject.add(s);
        return coordinateObject;
    }
    public static JsonArray wrapTeam(List<double[][]> teamSpawns) {
        JsonArray spawnObject = new JsonArray();
        for(int i = 0; i < teamSpawns.size(); i++) {
            double[][] spawns = teamSpawns.get(i);
            JsonArray spawnsObject = wrapSpawnGroup(spawns);
            spawnObject.add(spawnsObject);
        }
        return spawnObject;
    }

    private static JsonArray wrapSpawnGroup(double[][] spawnGroup) {
        JsonArray spawnsObject = new JsonArray();
        for(int j = 0; j < spawnGroup.length; j++) {
            JsonArray coordinateObject = JsonHelper.wrapXYZ(spawnGroup[j]);
            spawnsObject.add(coordinateObject);
        }
        return spawnsObject;
    }

    public static double[] getDoubleArray(Location location) {
        return new double[] {location.getX(), location.getY(), location.getZ()};
    }

    public static double[] getArray(JsonArray jsonArray) {
        double[] arr = new double[jsonArray.size()];
        for(int i = 0; i < arr.length; i++) {
            arr[i] = jsonArray.get(i).getAsDouble();
        }
        return arr;
    }
}
