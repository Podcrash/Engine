package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class CloakStatus extends CustomStatus {
    public CloakStatus(LivingEntity player) {
        super(player, Status.CLOAK);
    }

    @Override
    protected void doWhileAffected() {
        if (!instancePlayer) return;
        Player player = (Player) getEntity();
        for (Player p : getEntity().getWorld().getPlayers()) {
            p.hidePlayer(player);
        }
    }

    @Override
    protected void removeEffect() {
        if (!instancePlayer) return;
        Player player = (Player) getEntity();
        getApplier().removeCloak();
        for (Player p : getEntity().getWorld().getPlayers()) {
            p.showPlayer(player);
        }
        getEntity().sendMessage(String.format("%sCondition> %sYou are no longer invisible.", ChatColor.BLUE, ChatColor.GRAY));
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isCloaked();
    }
}
