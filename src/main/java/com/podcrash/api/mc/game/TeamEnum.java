package com.podcrash.api.mc.game;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum TeamEnum {
    RED("Red", 14, 14,(byte) 1, ChatColor.RED, Color.RED, 57379),
    BLUE("Blue", 11, 11, (byte) -1, ChatColor.BLUE, Color.BLUE, 45091),
    WHITE("White", 0, 0, (byte) 0, ChatColor.WHITE, Color.WHITE, 35),
    NULL(null, -1, -1, (byte) 0, ChatColor.GRAY, null, 0);
    //TODO

    private String name;
    private int woolData;
    private int glassData;
    private byte byteData;
    private int intData;
    private ChatColor chatColor;
    private Color color;
    private int protocolData;

    TeamEnum(String name, int woolData, int glassData, byte byteData, ChatColor chatColor, Color color, int protocolData) {
        this.name = name;
        this.woolData = woolData;
        this.byteData = byteData;
        this.glassData = glassData;
        this.intData = (int) byteData;
        this.chatColor = chatColor;
        this.color = color;
        this.protocolData = protocolData;
    }

    public String getName() {
        return name;
    }

    public int getWoolData() {
        return woolData;
    }
    public int getGlassData() {
        return glassData;
    }

    public byte getByteData() {
        return byteData;
    }
    public int getIntData() {
        return intData;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }
    public Color getColor() {
        return color;
    }
    public int getProtocolData() {
        return protocolData;
    }

    public static TeamEnum getByColor(String teamColor) {
        if(teamColor == null) return NULL;
        for (TeamEnum teamEnum : TeamEnum.values()) {
            if (teamEnum.getName().equalsIgnoreCase(teamColor)) return teamEnum;
        }
        return NULL;
    }
}
