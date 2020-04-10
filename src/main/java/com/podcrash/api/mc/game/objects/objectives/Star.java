package com.podcrash.api.mc.game.objects.objectives;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Star extends ItemObjective {
    public Star(Vector vector) {
        super(Material.NETHER_STAR, Material.BEDROCK, vector);
        fireworkEffect = FireworkEffect.builder().withColor(Color.WHITE).with(FireworkEffect.Type.BALL_LARGE).build();
    }
    public Star(Point point) {
        this(PojoHelper.convertPoint2Vector(point));
    }


    @Override
    public ObjectiveType getObjectiveType() {
        return ObjectiveType.STAR;
    }

    @Override
    public String getName() {
        return "Star";
    }

    @Override
    public void spawnFirework() {
        super.spawnFirework();
        SoundPlayer.sendSound(getLocation(), "fireworks.launch", 1, 63);
    }

    @Override
    public long getDurationMilles() {
        return 90000L;
    }
}
