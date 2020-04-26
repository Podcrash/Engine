package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;

public class CloakStatus extends CustomStatus {
    public CloakStatus(Player player) {
        super(player, Status.CLOAK);
    }

    @Override
    protected void doWhileAffected() {
        for (Player p : getPlayer().getWorld().getPlayers()) {
            p.hidePlayer(getPlayer());
        }
    }

    @Override
    protected void removeEffect() {
        getApplier().removeCloak();
        for (Player p : getPlayer().getWorld().getPlayers()) {
            p.showPlayer(getPlayer());
        }
        getPlayer().sendMessage(String.format("%sCondition> %sYou are no longer invisible.", ChatColor.BLUE, ChatColor.GRAY));
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isCloaked();
    }
}
