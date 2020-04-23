package com.podcrash.api.plugin;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

/**
 * this class will be currently unused
 */
public class MessageListener implements PluginMessageListener {
    private final String CHANNEL_NAME;

    public MessageListener(String CHANNEL_NAME) {
        this.CHANNEL_NAME = CHANNEL_NAME;
    }
    /**'
     * Strictly for games
     * @param channel
     * @param player
     * @param data
     */
    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data) {
        if(!channel.equals(CHANNEL_NAME)) return;
        ByteArrayDataInput input = ByteStreams.newDataInput(data);
        String subChannel = input.readUTF();

        if(subChannel.equals("gamequery"))
            gameQuery(player, input);
    }
    private void gameQuery(Player player, ByteArrayDataInput input) {
        /*
        String query = input.readUTF();
        Game game = GameManager.getGame();

        ByteArrayDataOutput output = ByteStreams.newDataOutput();
        output.writeUTF("gameresponse-" + query);
        if(query.equalsIgnoreCase("playercount"))
            output.writeUTF(String.format("%d/%d", game.size(), game.getMaxPlayers()));
        else if(query.equalsIgnoreCase("gamestate"))
            output.writeUTF(game.isOngoing() ? "INGAME" : "POLLING");

        player.sendPluginMessage(Main.instance, CHANNEL_NAME, output.toByteArray());\

         */
    }
}