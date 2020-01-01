package com.podcrash.api.mc.game;

import com.podcrash.api.mc.util.ChatUtil;
import org.bukkit.ChatColor;
import org.bukkit.Color;

/**
 * Team Enumeration
 *
 * @author RainDance & JJCunningCreeper
 */

public enum TeamEnum {

    // TODO: Fill the rest of the colours out appropriately for byteData and protocolData.
    RED("Red", 14,(byte) 1, ChatColor.RED, Color.RED, 57379),
    BLUE("Blue", 11, (byte) -1,  ChatColor.BLUE, Color.BLUE, 45091),
    LIGHT_BLUE("Blue", 3, (byte) 1,  ChatColor.AQUA, Color.AQUA, 0),
    GOLD("Gold", 4, (byte) 1,  ChatColor.YELLOW, Color.YELLOW, 0),
    GREEN("Green", 5, (byte) 1,  ChatColor.GREEN, Color.LIME, 0),
    GRAPE("Grape", 2, (byte) 1,  ChatColor.DARK_PURPLE, Color.PURPLE, 0),
    ORANGE("Orange", 1, (byte) 1,  ChatColor.GOLD, Color.ORANGE, 0),
    VANILLA("Vanilla", 0, (byte) 0,  ChatColor.WHITE, Color.WHITE, 35),
    STRAWBERRY("Strawberry", 6, (byte) 0,  ChatColor.LIGHT_PURPLE, Color.FUCHSIA, 0),
    CHOCOLATE("Chocolate", 12, (byte) 0,  ChatColor.DARK_GRAY, Color.MAROON, 0),
    WHITE("White", 0, (byte) 0,  ChatColor.WHITE, Color.WHITE, 35),
    NULL(null, -1, (byte) 0,  ChatColor.GRAY, null, 0);

    private final String name;
    private final int data;
    private final byte byteData;
    private final int intData;
    private final ChatColor chatColor;
    private final Color color;
    private final int protocolData;

    TeamEnum(String name, int data, byte byteData, ChatColor chatColor, Color color, int protocolData) {
        this.name = name;
        this.data = data;
        this.byteData = byteData;
        this.intData = (int) byteData;
        this.chatColor = chatColor;
        this.color = color;
        this.protocolData = protocolData;
    }

    /**
     * @return The team name.
     */
    public String getName() {
        return name;
    }

    /**
     * @return The color data (for wool and glass).
     */
    public int getData() {
        return data;
    }

    /**
     * @return The byte data.
     */
    public byte getByteData() {
        return byteData;
    }

    /**
     * @return The byte data as an integer.
     */
    public int getIntData() {
        return intData;
    }

    /**
     * @return The ChatColor
     */
    public ChatColor getChatColor() {
        return chatColor;
    }

    /**
     * @return The Color
     */
    public Color getColor() {
        return color;
    }

    /**
     * @return Protocol Data
     */
    public int getProtocolData() {
        return protocolData;
    }

    /**
     * @return The red value in the RGB.
     */
    public float getRed() {
        return color.getRed();
    }

    /**
     * @return The blue value in the RGB.
     */
    public float getBlue() {
        return color.getBlue();
    }

    /**
     * @return The green value in the RGB.
     */
    public float getGreen() {
        return color.getGreen();
    }

    /**
     * Return a TeamEnum by teamColor
     * @param teamColor The team color
     * @return The TeamEnum.
     */
    public static TeamEnum getByColor(String teamColor) {
        if(teamColor == null) return NULL;
        for (TeamEnum teamEnum : TeamEnum.values()) {
            if (teamEnum.getName().equalsIgnoreCase(teamColor)) return teamEnum;
        }
        return NULL;
    }

    /**
     * Return a TeamEnum by data number.
     * @param id The data number.
     * @return The TeamEnum.
     */
    public static TeamEnum getByData(int id) {
        for (TeamEnum teamEnum : TeamEnum.values()) {
            if (teamEnum.getData() == id) return teamEnum;
        }
        return NULL;
    }

    /**
     * Check if a TeamEnum with a data number is valid/exists.
     * @param id The data number.
     * @return Whether it is valid/exists.
     */
    public static boolean isColorIDValid(int id) {
        for (TeamEnum teamEnum : TeamEnum.values()) {
            if (teamEnum.getData() == id) return true;
        }
        return false;
    }
}
