package com.podcrash.api.game.objects.action;

import com.podcrash.api.db.pojos.PojoHelper;
import com.podcrash.api.db.pojos.map.Point2Point;
import org.bukkit.util.Vector;

public class ActionBlock {
    private final Vector vector1, vector2;
    private Type type;
    public enum Type {
        NULL, SLIME, TELEPORT
    }
    public ActionBlock(Point2Point point2Point) {
        this.vector1 = PojoHelper.convertPoint2Vector(point2Point.getPoint1());
        this.vector2 = PojoHelper.convertPoint2Vector(point2Point.getPoint2());
        this.type = Type.NULL;
    }

    public Vector getVector1() {
        return vector1;
    }

    public Vector getVector2() {
        return vector2;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }
}
