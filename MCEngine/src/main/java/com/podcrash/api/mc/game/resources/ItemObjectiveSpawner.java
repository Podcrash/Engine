package com.podcrash.api.mc.game.resources;

import com.podcrash.api.mc.events.ItemObjectiveSpawnEvent;
import com.podcrash.api.mc.game.objects.ItemObjective;
import org.bukkit.Bukkit;

import java.util.List;

/**
 * Gems and restocks
 */
public class ItemObjectiveSpawner extends GameResource {
    private long[] itemTimes;
    private final ItemObjective[] itemObjectives;
    public ItemObjectiveSpawner(int gameID) {
        super(gameID, 1, 0);
        List<ItemObjective> itemObjectives = this.getGame().getItemObjectives();
        this.itemObjectives = itemObjectives.toArray(new ItemObjective[itemObjectives.size()]);
        this.itemTimes = new long[itemObjectives.size()];
        for (ItemObjective itemObjective : this.itemObjectives) {
            respawn(itemObjective);
        }
    }

    public void setItemTime(ItemObjective objective){
        setItemTime(objective, System.currentTimeMillis());
    }
    public void setItemTime(ItemObjective objective, long time) {
        for(int i = 0; i < itemObjectives.length; i++){
            if (itemObjectives[i] == objective)
                this.itemTimes[i] = time;
        }
    }

    /**
     * Summon the item as well as make a firework
     * @param itemObjective The item to respawn
     */
    private void respawn(ItemObjective itemObjective) {
        ItemObjectiveSpawnEvent e = new ItemObjectiveSpawnEvent(itemObjective);
        Bukkit.getPluginManager().callEvent(e);
        itemObjective.respawn();
        itemObjective.spawnFirework();
    }

    /**
     * Respawn every 60 seconds, but only if the item doesn't exist
     */
    @Override
    public void task() {
        for(int i = 0; i < itemObjectives.length; i++) {
            ItemObjective itemObjective = itemObjectives[i];
            if (!itemObjective.getItem().isValid() && System.currentTimeMillis() - itemTimes[i] >= itemObjective.getDurationMilles())
                respawn(itemObjective);
        }
    }

    @Override
    public void cleanup() {
        this.itemTimes = null;
    }
}
