package com.podcrash.api.game.objects.objectives;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.game.objects.ItemObjective;
import com.podcrash.api.sound.SoundPlayer;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Diamond extends ItemObjective {
    private static final ObjectiveType otype = ObjectiveType.EMERALD;

    public Diamond(Location location) {
        this(location.toVector());
    }
    public Diamond(Vector spawnVector){
        super(Material.DIAMOND, Material.DIAMOND_BLOCK, spawnVector);
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.AQUA).with(FireworkEffect.Type.BURST).build();
    }
    public Diamond(Point point) {
        this(PojoHelper.convertPoint2Vector(point));
    }
    public ObjectiveType getObjectiveType(){
        return otype;
    }

    @Override
    public String getName() {
        return "Diamond";
    }

    @Override
    public void spawnFirework() {
        super.spawnFirework();
        SoundPlayer.sendSound(getLocation(), "fireworks.launch", 1, 63);
    }


}
