package com.podcrash.api.mc.events.game;

import com.podcrash.api.mc.game.Game;
import org.bukkit.entity.Player;

public class GameJoinEvent extends GamePlayerEvent {
    public GameJoinEvent(Game game, Player who) {
        super(game, who, "message");
    }
}
