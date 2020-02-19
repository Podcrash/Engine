package com.podcrash.api.mc.game.objects.objectives;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.sound.SoundPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Emerald extends ItemObjective {
    private static ObjectiveType otype = ObjectiveType.EMERALD;

    public Emerald(Location location) {
        this(location.toVector());
    }
    public Emerald(Vector spawnVector){
        super(Material.NETHER_STAR, Material.ENDER_PORTAL_FRAME, spawnVector);
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.GREEN).with(FireworkEffect.Type.BALL_LARGE).build();
    }
    public Emerald(Point point) {
        this(PojoHelper.convertPoint2Vector(point));
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
