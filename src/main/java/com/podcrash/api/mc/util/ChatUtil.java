package com.podcrash.api.mc.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Translating color codes
 *
 * @author JJCunningCreeper
 */

public final class ChatUtil {

    private static final char COLOR_CHAR = 167;

    /**
     * If the message overfills over 256 characters (the word limit on mc),
     * then overflow it to the next sent chat message.
     * //TODO: make a loop for this instead of using recursion as java doesn't optimized tail calls.
     * @param sender
     * @param message
     */
    public static void sendMessage(CommandSender sender, String message) {
        if(message.length() <= 256) {
            sender.sendMessage(message);
            return;
        }
        sender.sendMessage(message.substring(0, 255));
        sendMessage(sender, message.substring(256));
    }
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

    /**
     * Strip the parametric string into only its alphabetical characters
     * and lowercased.
     */
    public static String strip(String str) {
        str = ChatUtil.purge(str);
        return str.replaceAll("/([^A-z]*)/g", str).replaceAll(" ", "").toLowerCase();
    }
}
