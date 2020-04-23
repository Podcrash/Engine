package com.podcrash.api.mc.util;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public final class BungeeUtil {
    public static void sendToServer(Player player, String server) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();

        out.writeUTF("Connect");
        out.writeUTF(server);
        Pluginizer.getLogger().info("Sending " + player.getName() + " to " + server);
        player.sendPluginMessage(Pluginizer.getSpigotPlugin(), "BungeeCord", out.toByteArray());
    }
}
