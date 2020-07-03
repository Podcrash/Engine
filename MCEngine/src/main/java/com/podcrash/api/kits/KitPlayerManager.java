package com.podcrash.api.kits;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketListener;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.skill.ApplyKitEvent;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.kits.iskilltypes.action.ICharge;
import com.podcrash.api.kits.iskilltypes.action.IConstruct;
import com.podcrash.api.kits.iskilltypes.action.IInjector;
import com.podcrash.api.kits.iskilltypes.action.IPassiveTimer;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class KitPlayerManager {
    private static volatile KitPlayerManager cpm;
    private final JavaPlugin plugin = PodcrashSpigot.getInstance();
    private final HashMap<String, KitPlayer> kitPlayer = new HashMap<>();
    private final Map<KitPlayer, List<PacketListener>> injectors = new HashMap<>();

    private void register(Skill skill) {
        plugin.getServer().getPluginManager().registerEvents(skill, plugin);
        skill.init();
        if (skill instanceof IPassiveTimer) ((IPassiveTimer) skill).start();
        if (skill instanceof IConstruct) ((IConstruct) skill).doConstruct();
        if (skill instanceof IInjector) addPacketListener(getKitPlayer(skill.getPlayer()), ((IInjector) skill).inject());
        if (skill instanceof ICharge) skill.getPlayer().sendMessage(String.format("%s%s> %sMaximum Charges: %d", ChatColor.BLUE, skill.getName(), ChatColor.GOLD, ((ICharge) skill).getMaxCharges()));
    }

    public void addKitPlayer(KitPlayer cplayer) {
        if (cplayer == null) return;
        ApplyKitEvent apply = new ApplyKitEvent(cplayer);
        Bukkit.getPluginManager().callEvent(apply);
        if (apply.isCancelled())
            return;
        KitPlayer oldPlayer = getKitPlayer(cplayer.getPlayer());
        removeKitPlayer(oldPlayer);

        kitPlayer.putIfAbsent(cplayer.getPlayer().getName(), cplayer);

        KitPlayer cp = getKitPlayer(cplayer.getPlayer());
        StatusApplier.getOrNew(cp.getPlayer()).removeStatus(Status.values());

        cp.equip();
        cp.heal(20);
        cp.getPlayer().setFoodLevel(20);
        cp.effects();
        for(Skill skill : cp.getSkills()) {
            skill.setPlayer(cplayer.getPlayer());
            register(skill);
        }

        if(!apply.isKeepInventory())
            cp.restockInventory();
        //cp.getPlayer().sendMessage(cp.skillsRead());
        //cp.skillsRead();
    }
    public void removeKitPlayer(KitPlayer cplayer) {
        if (cplayer == null ||
                !kitPlayer.containsKey(cplayer.getPlayer().getName())) return;
        Set<Skill> skills = cplayer.getSkills();
        Iterator<Skill> skillIterator = skills.iterator();
        while (skillIterator.hasNext()) {
            final Skill skill = skillIterator.next();
            HandlerList.unregisterAll(skill);
            PodcrashSpigot.debugLog(String.format("%s unregistered from %s", skill.getName(), skill.getPlayer()));
            if (skill instanceof TimeResource) TimeHandler.unregister((TimeResource) skill);
            if (skill instanceof IPassiveTimer) ((IPassiveTimer) skill).stop();
        }
        clearPacketListeners(cplayer);
        cplayer.setUsesEnergy(false);
        kitPlayer.remove(cplayer.getPlayer().getName());
    }
    public void removeKitPlayer(Player player) {
        KitPlayer kitPlayer = this.kitPlayer.getOrDefault(player.getName(), null);
        if(kitPlayer != null)
            removeKitPlayer(kitPlayer);
    }

    public KitPlayer getKitPlayer(Player player) {
        return kitPlayer.getOrDefault(player.getName(), null);
    }

    private void addPacketListener(KitPlayer cPlayer, PacketListener listener) {
        List<PacketListener> packetListeners = injectors.getOrDefault(cPlayer, new ArrayList<>());
        packetListeners.add(listener);
    }
    private void clearPacketListeners(KitPlayer cPlayer) {
        List<PacketListener> packetListeners = injectors.getOrDefault(cPlayer, new ArrayList<>());
        if(packetListeners.size() == 0) return;
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        for(PacketListener listener : packetListeners) {
            manager.removePacketListener(listener);
        }
    }
    public JsonObject deserialize(String jsonStr) {
        return new JsonParser().parse(jsonStr).getAsJsonObject();
    }

    public void clear(){
        Iterator iterator = kitPlayer.keySet().iterator();
        while (iterator.hasNext()) {
            iterator.next();
            iterator.remove();
        }
    }


    public static KitPlayerManager getInstance() {
        if (cpm == null) {
            synchronized (KitPlayerManager.class) {
                if (cpm == null) {
                    cpm = new KitPlayerManager();
                }
            }

        }
        return cpm;
    }


    private KitPlayerManager() {

    }
}
