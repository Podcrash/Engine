package com.podcrash.api.mc.map;

import com.google.gson.JsonObject;

import java.util.List;

/**
 * Much of this part is based on
 * https://github.com/Grinderwolf/Slime-World-Manager/blob/develop/.docs/config/configure-world.md
 */
public interface IMap {

    /**
     * This is effectively the unique key; UUID; unique identifer, etc.
     * @return the name of the map.
     */
    String getName();
    /**
     * This should be 3 dimensional.
     * [x, y, z]
     * They should also be in order like ex:
     * spawns[x] = spawns.get(x);
     * spawns[0] = red team
     * spawns[0][2] = red team's third spawn
     * spawns[0][2][2] = red team's third spawn's z coordinate
     *
     * spawns[1] = blue team
     * ...
     * @return List of teams with [spawn0, spawn1, spawn2, spawn3....][x, y, z]
     */
    List<double[][]> getSpawns();

    /**
     * The spawn location
     * @return [x, y, z] of where everyone spawns
     */
    double[] getWorldSpawn();
    /**
     * @return allow animals to spawn
     */
    boolean allowAnimals();
    /**
     * @return allow monsters to spawn
     */
    boolean allowMonsters();

    /**
     * @return allow pvp to spawn
     */
    boolean allowPvP();

    /**
     * Valid Environments: normal, nether, the_end
     * @return
     */
    default String getEnvironment() {
        return "normal";
    }

    /**
     * Level types: default, flat, large_biomes, amplified, customized, debug_all_block_states, default_1_1
     * @return the type
     */
    default String getWorldType() {
        return "default";
    }

    JsonObject getJSON();

    void fromJson(JsonObject json);
}
