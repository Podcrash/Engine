package com.podcrash.api.mc.util;

import org.bukkit.ChatColor;

/**
 * Translating color codes
 *
 * @author JJCunningCreeper
 */

public final class ChatUtil {

    /**
     * Converts a string to its color coded format
     * @param s The string to convert
     * @return
     */
    public static String chat (String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

}
