package com.podcrash.api.mc.game.objects.objectives;

import com.podcrash.api.mc.game.objects.ItemObjective;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class Restock extends ItemObjective {
    private static ObjectiveType otype = ObjectiveType.RESTOCK;

    public Restock(Location spawnLocation){
        super(Material.CHEST, Material.GOLD_BLOCK, spawnLocation);
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
    }

    public ObjectiveType getObjectiveType(){
        return otype;
    }

    @Override
    public String getName() {
        return "Restock";
    }
}
