package com.podcrash.api.mc.effect.status.custom;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import org.bukkit.entity.Player;

/**
 * Part of the respawn for games
 * @see me.raindance.champions.listeners.maintainers.GameListener#onDeath(com.podcrash.api.mc.events.DeathApplyEvent)
 */
public class IneptStatus extends CustomStatus {
    private final Game game;
    public IneptStatus(Player player) {
        super(player, Status.INEPTITUDE);
        game = GameManager.getGame();
    }

    @Override
    protected void doWhileAffected() {
        for(Player player : game.getBukkitPlayers()){
            if(player != getPlayer() && player.canSee(getPlayer())) player.hidePlayer(getPlayer());
        }
    }

    @Override
    protected boolean isInflicted() {
        return getApplier().isInept();
    }

    @Override
    protected void removeEffect() {
        getApplier().removeInept();
        for(Player player : game.getBukkitPlayers()){
            if(player != getPlayer()) player.showPlayer(getPlayer());
        }
    }
}
