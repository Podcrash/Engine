package com.podcrash.api.effect.status.custom;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

/**
 * Part of the respawn for games
 */
public class IneptStatus extends CustomStatus {
    private final Game game;
    public IneptStatus(LivingEntity player) {
        super(player, Status.INEPTITUDE);
        game = GameManager.getGame();
    }

    @Override
    protected void doWhileAffected() {
        if (!instancePlayer) return;
        Player p = (Player) getEntity();
        for(Player player : game.getBukkitPlayers()){
            if (player != getEntity() && player.canSee(p)) player.hidePlayer(p);
        }
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isInept();
    }

    @Override
    protected void removeEffect() {
        if (!instancePlayer) return;
        Player p = (Player) getEntity();
        getApplier().removeInept();
        for(Player player : game.getBukkitPlayers()){
            if (player != getEntity()) player.showPlayer(p);
        }
    }
}
