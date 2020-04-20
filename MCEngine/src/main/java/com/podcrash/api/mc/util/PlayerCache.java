package com.podcrash.api.mc.util;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class PlayerCache {
    private static final HashMap<String, PlayerCache> cache = new HashMap<>();
    private WrappedChatComponent component;
    private final Player player;
    private String displayName;
    private PlayerCache(String displayName) {
        this.displayName = displayName;
        this.player = Bukkit.getPlayer(displayName);
        this.component = WrappedChatComponent.fromText(displayName);
    }

    public static PlayerCache getPlayerCache(Player player) {
        PlayerCache playerCache = cache.getOrDefault(player.getName(), null);
        if(playerCache == null) {
            addPlayerCache(player);
            playerCache = cache.get(player.getName());
        }
        return playerCache;
    }
    public static PlayerCache getPlayerCache(String name) {
        PlayerCache playerCache = cache.getOrDefault(name, null);
        if(playerCache == null) {
            cache.put(name, new PlayerCache(name));
            playerCache = cache.get(name);
        }
        return playerCache;
    }

    public static void addPlayerCache(Player player) {
        addPlayerCache(player, player.getName());
    }
    public static void addPlayerCache(Player player, String displayName) {
        cache.put(player.getName(), new PlayerCache(displayName));
    }
    public static void packetUpdater() {
        //TODO: make this better
        /*
        ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Main.instance, ListenerPriority.HIGHEST, PacketType.Play.Server.CHAT, PacketType.Play.Server.PLAYER_INFO) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                //TODO: We are going to need this later down the line

                if(event.getPacketType() == PacketType.Play.Server.CHAT) {
                    WrapperPlayServerChat chat = new WrapperPlayServerChat(event.getPacket());
                    WrappedChatComponent component = chat.getMessage();
                    String json = component.getJson();
                    JsonObject jsonObject = new JsonParser().parse(json).getAsJsonObject();
                    jsonObject.add

                    Bukkit.broadcastMessage(component.getJson());
                }else if(event.getPacketType() == PacketType.Play.Server.PLAYER_INFO) {
                    WrapperPlayServerPlayerInfo info = new WrapperPlayServerPlayerInfo(event.getPacket());
                    List<PlayerInfoData> newPlayerInfoDataList = new ArrayList<>();

                    for (PlayerInfoData data : info.getData()) {
                        Player player;
                        if (data == null || data.getProfile() == null || (player = Bukkit.getEntity(data.getProfile().getUUID())) == null || !player.isOnline()) {
                            newPlayerInfoDataList.add(data);
                            continue;
                        }
                        WrappedGameProfile profile = data.getProfile();

                        WrappedGameProfile newProfile = profile.withName(PlayerCache.getPlayerCache(profile.getName()).getDisplayName());
                        newProfile.getProperties().putAll(profile.getProperties());

                        PlayerInfoData newPlayerInfoData = new PlayerInfoData(newProfile, data.getPing(), data.getGameMode(), data.getDisplayName());
                        newPlayerInfoDataList.add(newPlayerInfoData);
                    }

                    info.setData(newPlayerInfoDataList);
                }

            }
        });
        */
    }

    public WrappedChatComponent getComponent() {
        return component;
    }

    private void setComponent(WrappedChatComponent component) {
        this.component = component;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        setComponent(WrappedChatComponent.fromText(displayName));
    }
}
