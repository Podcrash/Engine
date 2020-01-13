package com.podcrash.api.mc.util;

import com.abstractpackets.packetwrapper.WrapperPlayServerExperience;
import org.bukkit.entity.Player;

public final class ExpUtil {
    public static void updateExp(Player player, float xp) {
        if(xp < 0 || xp > 1) throw new IllegalArgumentException("xp must be between 0 and 1");
        WrapperPlayServerExperience experiencePacket = new WrapperPlayServerExperience();
        experiencePacket.setExperienceBar(xp);
        experiencePacket.setLevel(player.getLevel());
        experiencePacket.setTotalExperience(player.getTotalExperience());
        experiencePacket.sendPacket(player);
        //update sync
        player.setExp(xp);
    }
}
