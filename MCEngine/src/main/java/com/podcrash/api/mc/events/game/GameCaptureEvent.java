package com.podcrash.api.mc.events.game;


import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.objects.IObjective;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class GameCaptureEvent extends GamePlayerEvent {
    private static final HandlerList handlers = new HandlerList();
    private final Game game;
    private final Player who;
    private final IObjective iObjective;

    public GameCaptureEvent(Game game, Player who, IObjective iObjective, String message) {
        super(game, who, message);
        this.iObjective = iObjective;
        this.who = who;
        this.game = game;
    }

    public GameCaptureEvent(Game game, Player who, IObjective iObjective){
        this(game, who, iObjective, String.format(ChatColor.BOLD + "You captured %s", iObjective.getName()));
    }

    public Player getWho() {return who;}

    public Game getGame() {return game;}

    public IObjective getObjective() {
        return iObjective;
    }

    public static HandlerList getHandlersList(){
        return handlers;
    }


}
