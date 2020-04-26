package com.podcrash.api.mc.util;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public final class PacketUtil {
    public static void syncSend(AbstractPacket packet, List<Player> players) {
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> players.forEach(packet::sendPacket));
    }

    public static void syncSend(AbstractPacket packet, Player... players) {
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            for (Player player : players) {
                packet.sendPacket(player);
            }
        });
    }

    public static void syncSend(List<AbstractPacket> packets, List<Player> players) {
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void syncSend(AbstractPacket[] packets, List<Player> players) {
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void syncSend(AbstractPacket[] packets, Player... players) {
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void syncSend(List<AbstractPacket> packets, Player... players) {
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void asyncSend(AbstractPacket packet, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(PodcrashSpigot.getInstance(), () -> players.forEach(packet::sendPacket));
    }

    public static void asyncSend(AbstractPacket packet, Player... players) {
        Bukkit.getScheduler().runTaskAsynchronously(PodcrashSpigot.getInstance(), () -> {
            for (Player player : players) {
                packet.sendPacket(player);
            }
        });
    }

    public static void asyncSend(List<AbstractPacket> packets, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void asyncSend(AbstractPacket[] packets, List<Player> players) {
        Bukkit.getScheduler().runTaskAsynchronously(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void asyncSend(AbstractPacket[] packets, Player... players) {
        Bukkit.getScheduler().runTaskAsynchronously(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }

    public static void asyncSend(List<AbstractPacket> packets, Player... players) {
        Bukkit.getScheduler().runTaskAsynchronously(PodcrashSpigot.getInstance(), () -> {
            for (AbstractPacket packet : packets) {
                if (packet == null)
                    continue;
                for (Player player : players) {
                    packet.sendPacket(player);
                }
            }
        });
    }
}
