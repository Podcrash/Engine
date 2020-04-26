package com.podcrash.api.util;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public final class TitleSender {
    /**
     *
     * @param a must be smaller than b
     * @param b must be larger than a
     * @return
     */
    public static WrappedChatComponent simpleTime(String header, String footer, double a, double b) {
        String bar = generateBars("||");
        int size = bar.length() - 1;
        double percentage = 1D - a/b;
        int currentProgress = (int) (size * percentage);
        currentProgress = Math.min(currentProgress, size);

        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        String builder = ChatColor.BOLD + header + ChatColor.GREEN + sprogress + ChatColor.RESET + ChatColor.BOLD + ' ' + footer;
        return writeTitle(builder);
    }

    public static void sendTitle(Player p, WrappedChatComponent iChatBaseComponent) {
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        PacketContainer packet = manager.createPacket(PacketType.Play.Server.CHAT);
        packet.getChatComponents().write(0, iChatBaseComponent);
        packet.getBytes().write(0, (byte) 2);
        try {
            manager.sendServerPacket(p, packet);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static WrappedChatComponent emptyTitle() {
        return writeTitle("");
    }

    public static WrappedChatComponent writeTitle(String string) {
        return WrappedChatComponent.fromJson("{\"text\":\"" + string + "\"}");
    }

    public static String generateBars(String a) {
        return generateBars().replace("|", a);
    }

    public static String generateBars() {
        return "||||||||||||||||||||";
    }
}
