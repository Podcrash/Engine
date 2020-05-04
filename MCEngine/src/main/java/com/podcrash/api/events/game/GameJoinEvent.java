package com.podcrash.api.events.game;

import com.podcrash.api.game.Game;
import org.bukkit.entity.Player;

public class GameJoinEvent extends GamePlayerEvent {
    public GameJoinEvent(Game game, Player who) {
        super(game, who, "message");
    }
}
