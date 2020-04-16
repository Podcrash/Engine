package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import org.bukkit.entity.Player;

public class GameLeaveEvent extends GamePlayerEvent {
    public GameLeaveEvent(Game game, Player who) {
        super(game, who, "message");
    }
}
