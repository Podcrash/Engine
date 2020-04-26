package com.podcrash.api.kits.iskilltypes.action;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketListener;
import com.podcrash.api.plugin.PodcrashSpigot;

public interface IInjector {
    default PacketListener inject() {
        PacketListener packetListener = new PacketAdapter(PodcrashSpigot.getInstance(), getListenerPriority(), getTypes()) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                recieve(event);
            }

            @Override
            public void onPacketSending(PacketEvent event) {
                send(event);
            }
        };
        ProtocolLibrary.getProtocolManager().addPacketListener(packetListener);
        return packetListener;
    }

    default ListenerPriority getListenerPriority() {
        return ListenerPriority.HIGHEST;
    }

    PacketType[] getTypes();

    void send(PacketEvent var1);
    void recieve(PacketEvent var1);
}
