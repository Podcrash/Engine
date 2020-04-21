package com.podcrash.api.mc.world;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import org.bukkit.Bukkit;
import org.bukkit.World;

public final class SpawnWorldSetter {

    //TODO: string version?
    private String currentWorldName;
    public SpawnWorldSetter() {
        this.currentWorldName = null;
    }

    public void loadFromEnvVariable(String key) {
        String worldName = System.getenv(key);
        if (worldName == null) return;
        setWorld(worldName);
    }

    public void setWorld(String worldName) {
        if (worldName == null) throw new IllegalStateException("worldName cannot be null!");
        //if the world is not null, try unloading it
        if (currentWorldName != null) {
            World unload = Bukkit.getWorld(currentWorldName);
            if (unload != null) Bukkit.unloadWorld(unload, false);
        }

        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        table.downloadWorld(worldName, "regular", BaseMap.class).thenAccept(map -> {
            if (map == null) return;
            this.currentWorldName = map.getName();
        });
    }

    public String getCurrentWorldName() {
        return currentWorldName;
    }
}
