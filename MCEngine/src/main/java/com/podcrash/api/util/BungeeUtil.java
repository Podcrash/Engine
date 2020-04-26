package com.podcrash.api.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.entity.Player;

public final class BungeeUtil {
    public static void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);
        PodcrashSpigot.debugLog("Sending " + player.getName() + " to " + server);
        player.sendPluginMessage(PodcrashSpigot.getInstance(), "BungeeCord", out.toByteArray());
    }
}
