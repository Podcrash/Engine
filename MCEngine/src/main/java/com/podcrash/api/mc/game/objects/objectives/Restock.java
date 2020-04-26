package com.podcrash.api.mc.game.objects.objectives;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.mc.game.objects.ItemObjective;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.util.Vector;

public class Restock extends ItemObjective {
    private static final ObjectiveType otype = ObjectiveType.RESTOCK;

    public Restock(Location location) {
        this(location.toVector());
    }
    public Restock(Vector spawnVector){
        super(Material.CHEST, Material.GOLD_BLOCK, spawnVector);
        this.fireworkEffect = FireworkEffect.builder().withColor(Color.YELLOW).with(FireworkEffect.Type.BURST).build();
    }
    public Restock(Point point) {
        this(PojoHelper.convertPoint2Vector(point));
    }

    public ObjectiveType getObjectiveType(){
        return otype;
    }

    @Override
    public String getName() {
        return "Restock";
    }
}
