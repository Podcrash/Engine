package com.podcrash.api.mc.sound;

import com.abstractpackets.packetwrapper.WrapperPlayServerNamedSoundEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.List;

public class SoundPlayer {
    //TODO: this needs to get refactored a bit

    /**
     * Create a sound packet with a location, sound, volume, and pitch
     * @param loc
     * @param sound the actual sound, see playsound arguments
     * @param volume [0, 1]
     * @param pitch [0, 126] "1" is 63
     * @return
     */
    public static WrapperPlayServerNamedSoundEffect createSound(Location loc, String sound, float volume, int pitch) {
        WrapperPlayServerNamedSoundEffect soundPacket = new WrapperPlayServerNamedSoundEffect();
        soundPacket.setSoundName(sound);
        int x = (int) loc.getX() * 8;
        int y = (int) loc.getY() * 8;
        int z = (int) loc.getZ() * 8;
        soundPacket.setVolume(volume);
        soundPacket.setEffectPositionX(x);
        soundPacket.setEffectPositionY(y);
        soundPacket.setEffectPositionZ(z);
        soundPacket.setPitch((byte) pitch);

        return soundPacket;
    }

    public static void sendSound(Location loc, String sound, float volume, int pitch, List<Player> players) {
        WrapperPlayServerNamedSoundEffect soundPacket = createSound(loc, sound, volume, pitch);

        if(players == null) players = loc.getWorld().getPlayers();
        players.forEach(soundPacket::sendPacket);
    }

    public static void sendSound(Location loc, String sound, float volume, int pitch) {
        sendSound(loc, sound, volume, pitch,null);
    }

    public static void sendSound(Player p, String sound, float volume, int pitch) {
        sendSound(p.getLocation(), sound, volume, pitch, Collections.singletonList(p));
    }
    public static void sendSound(List<Player> players, String sound, float volume, int pitch){
        for (Player player : players) {
            sendSound(player, sound, volume, pitch);
        }
    }

    public static void sendSound(Player player, Audio sound, float volume, int pitch) {
        sendSound(player, sound.toString(), volume, pitch);
    }

    public static void sendSound(World world, String sound, float volume, int pitch) {
        for (Player player : world.getPlayers()) {
            sendSound(player, sound, volume, pitch);
        }
    }

    public static void sendSound(World world, Audio sound, float volume, int pitch) {
        sendSound(world, sound.toString(), volume, pitch);
    }

    public static void sendSound(Location location, SoundWrapper soundWrapper) {
        sendSound(location, soundWrapper.getSoundName(), soundWrapper.getVolume(), soundWrapper.getPitch());
    }
}
