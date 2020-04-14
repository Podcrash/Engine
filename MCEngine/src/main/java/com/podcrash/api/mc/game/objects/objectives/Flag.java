package com.podcrash.api.mc.game.objects.objectives;

import com.podcrash.api.mc.game.objects.WinObjective;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Flag extends WinObjective {
    private static ObjectiveType otype = ObjectiveType.CAPTURE_POINT;
    private Location locationNow;
    private Color color;

    public Flag(Vector vector, Color color) {
        super(vector);
        this.color = color;
    }

    public ObjectiveType getObjectiveType(){
        return otype;
    }

    @Override
    public String getName() {
        return "Flag";
    }
}
