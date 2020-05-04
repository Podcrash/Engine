package com.podcrash.api.game.objects.objectives;

import com.podcrash.api.game.objects.WinObjective;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.util.Vector;

public class Flag extends WinObjective {
    private static final ObjectiveType otype = ObjectiveType.CAPTURE_POINT;
    private Location locationNow;
    private final Color color;

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
