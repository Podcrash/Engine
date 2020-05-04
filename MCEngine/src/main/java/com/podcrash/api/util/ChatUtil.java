package com.podcrash.api.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

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
        if (message.length() <= 256) {
            sender.sendMessage(message);
            return;
        }
        sender.sendMessage(message.substring(0, 255));
        sendMessage(sender, message.substring(256));
    }
    /**
     * Converts a string to its color coded format
     * @param message The string to convert
     * @return the string properly formatted with minecraft color codes
     */
    public static String chat(String message) {
        char[] characters = message.toCharArray();
        String validCodes = "0123456789AaBbCcDdEeFfKkLlMmNnOoRr";
        int size = characters.length - 1;
        for(int i = 0; i < size; ++i) {
            int next = i + 1;
            if (characters[i] == '&' && validCodes.indexOf(characters[next]) > -1) {
                characters[i] = COLOR_CHAR;
                characters[next] = Character.toLowerCase(characters[next]);
            }
        }
        return new String(characters);
    }
    public static String purge(String s) {
        char[] charAR = s.toCharArray();
        int size = charAR.length;
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < size; i++) {
            if (charAR[i] == ChatColor.COLOR_CHAR) {
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
