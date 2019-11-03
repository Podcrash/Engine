package com.podcrash.api.mc.util;

import org.bukkit.ChatColor;

/**
 * Translating color codes
 *
 * @author JJCunningCreeper
 */

public final class ChatUtil {
    private static final char COLOR_CHAR = 167;

    /**
     * Converts a string to its color coded format
     * @param s The string to convert
     * @return
     */
    public static String chat(String s) {
        char[] b = s.toCharArray();
        String a = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
        int size = b.length - 1;
        for(int i = 0; i < size; ++i) {
            int next = i + 1;
            if (b[i] == '&' && a.indexOf(b[next]) > -1) {
                b[i] = COLOR_CHAR;
                b[next] = Character.toLowerCase(b[next]);
            }
        }
        return new String(b);
    }
    public static String purge(String s) {
        char[] charAR = s.toCharArray();
        int size = charAR.length;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++) {
            if(charAR[i] == ChatColor.COLOR_CHAR) {
                i += 1;
                continue;
            }
            builder.append(charAR[i]);
        }
        return builder.toString();
    }
}
