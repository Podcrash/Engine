package com.podcrash.api.mc.game.objects.objectives;

public enum ObjectiveType {
    EMERALD(1), RESTOCK(2), CAPTURE_POINT(3), LANDMINE(4), STAR(5), FLAG(11);

    private int id;

    ObjectiveType(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
