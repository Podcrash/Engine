package com.podcrash.api.game.objects.objectives;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.game.objects.ItemObjective;
import com.podcrash.api.sound.SoundPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Landmine extends ItemObjective {
    public Landmine(Vector vector) {
        super(Material.TNT, Material.REDSTONE_BLOCK, vector);
        fireworkEffect = FireworkEffect.builder().withColor(Color.RED).with(FireworkEffect.Type.BURST).build();

    }

    public Landmine(Point point) {
        this(PojoHelper.convertPoint2Vector(point));
    }

    @Override
    public ObjectiveType getObjectiveType() {
        return ObjectiveType.LANDMINE;
    }

    @Override
    public String getName() {
        return "Landmine";
    }

    @Override
    public void spawnFirework() {
        super.spawnFirework();
        SoundPlayer.sendSound(getLocation(), "fireworks.launch", 1, 63);
    }
}
