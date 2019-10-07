package com.podcrash.api.mc.util;

import org.bukkit.Color;

import java.util.Objects;

public class ColorMaker {
    private static final float[] saturationArray = new float[]{0.35F, 0.5F, 0.65F};
    private static final float[] lightnessArray = new float[]{0.35F, 0.5F, 0.65F};

    public static Color findColorViaString(String string) {
        int hash = Objects.hash(string);
        float hue = hash & 359;
        float sat = saturationArray[hash/360 % saturationArray.length];
        float light = lightnessArray[hash/360 / saturationArray.length & lightnessArray.length];
        java.awt.Color color = java.awt.Color.getHSBColor(hue, sat, light);

        return Color.fromRGB(color.getRed(), color.getGreen(), color.getBlue());
    }
}
