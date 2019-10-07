package com.podcrash.api.mc.game.objects.objectives;

import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;

public class Emerald extends ItemObjective {
    private static ObjectiveType otype = ObjectiveType.EMERALD;
    public Emerald(Location spawnLocation){
        super(Material.EMERALD, Material.EMERALD_BLOCK, spawnLocation);
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.BALL_LARGE).build();
    }
    public ObjectiveType getObjectiveType(){
        return otype;
    }

    @Override
    public String getName() {
        return "Emerald";
    }

    @Override
    public void spawnFirework() {
        super.spawnFirework();
        SoundPlayer.sendSound(getLocation(), "fireworks.launch", 1, 63);
    }
}
