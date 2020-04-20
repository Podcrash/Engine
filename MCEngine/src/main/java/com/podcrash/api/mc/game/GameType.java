package com.podcrash.api.mc.game;

public enum GameType {
    DOM("DOMINATION"), CTF("CAPTURE THE FLAG"), TDM("TEAM DEATHMATCH");

    private final String name;

    GameType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
