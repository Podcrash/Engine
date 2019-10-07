package com.podcrash.api.mc.game.resources;

import com.podcrash.api.mc.game.objects.ItemObjective;

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
        for (int i = 0; i < this.itemObjectives.length; i++) {
            respawn(this.itemObjectives[i]);
        }
    }

    public void setItemTime(ItemObjective objective){
        setItemTime(objective, System.currentTimeMillis());
    }
    public void setItemTime(ItemObjective objective, long time) {
        for(int i = 0; i < itemObjectives.length; i++){
            if(itemObjectives[i] == objective) {
                this.itemTimes[i] = time;
            }
        }
    }

    /**
     * Summon the item as well as make a firework
     * @param itemObjective
     */
    private void respawn(ItemObjective itemObjective) {
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
            if(!itemObjective.getItem().isValid() && System.currentTimeMillis() - itemTimes[i] >= 60000L) {
                respawn(itemObjective);
            }
        }
    }

    @Override
    public void cleanup() {
        this.itemTimes = null;
    }
}
