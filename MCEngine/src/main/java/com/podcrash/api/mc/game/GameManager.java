package com.podcrash.api.mc.game;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.mc.events.game.GameEndEvent;
import com.podcrash.api.mc.events.game.GameStartEvent;
import com.podcrash.api.mc.game.resources.GameResource;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.plugin.PodcrashSpigot;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Scoreboard;

import java.util.*;

/**
 * Singleton - Handles games
 */
public class GameManager {
    private static int gameID = 0;
    private static Game currentGame;

    /*
    public static Game createGame(String name, GameType type) {
        if(currentGame != null) return currentGame;
        Game game = null;
        switch (type){
            case DOM:
                game = new DomGame(gameID++, name);
                break;
            case CTF:
                break;
            case TDM:
                break;
        }
        if(game == null) throw new IllegalArgumentException("only the Dom GameType works for now");
        currentGame = game;
        game.createScoreboard();
        game.setGameWorld("GulleyRevamp");
        return game;
    }*/

    public static int getCurrentID() {
        return gameID;
    }

    public static void createGame(Game game) {
        if(currentGame != null) throw new RuntimeException("Making more than 1 game is ill-advised");
        Validate.isTrue(game.getId() == gameID);
        gameID++;
        currentGame = game;

        game.createScoreboard();

        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        Set<String> validMaps = new HashSet<>(table.getWorlds(game.getMode()));

        int size = validMaps.size();
        int item = new Random().nextInt(size);
        int i = 0;
        for(String map : validMaps) {
            if (i == item) {
                if(map == null) {
                    item = new Random().nextInt(size);
                } else {
                    setGameMap(map);
                    break;
                }
            }
            i++;
        }
    }

    public static void destroyCurrentGame() {
        if(currentGame == null) return;
        if(currentGame.getGameWorld() != null) Bukkit.unloadWorld(currentGame.getGameWorld(), false);
        currentGame = null;
    }
    public static void setGameMap(String worldName) {
        currentGame.setGameWorld(worldName);
    }

    public static void addSpectator(Player p) {
        Game game = currentGame;
        if(GameManager.isSpectating(p)) {
            game.removeSpectator(p);
            game.add(p);
            p.sendMessage(String.format(
                    "%sInvicta> %sYou are no longer spectating this game!",
                    ChatColor.BLUE,
                    ChatColor.GRAY));
            if(!p.getWorld().getName().equals("world")) {
                p.teleport(Bukkit.getWorld("world").getSpawnLocation());
            }
            return;
        }
        if(!game.contains(p)) {
            game.addSpectator(p);
            p.sendMessage(
                    String.format(
                            "%sInvicta> %sYou are now spectating %sGame %s%s.",
                            ChatColor.BLUE,
                            ChatColor.GRAY,
                            ChatColor.GREEN,
                            game.getId(),
                            ChatColor.GRAY));
        } else {
            p.sendMessage(
                    String.format(
                            "%sInvicta> %sYou are already in this game.",
                            ChatColor.BLUE,
                            ChatColor.GRAY));
        }
    }

    public static boolean isSpectating(Player player){
        return currentGame != null && currentGame.isSpectating(player);
    }

    public static void addPlayer(Player p) {
        Game game = currentGame;
        if(GameManager.hasPlayer(p)) {
                p.sendMessage(String.format(
                        "%sInvicta> %sYou are already in a game!",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
                return;
        }
        if(!game.contains(p)) {
            game.add(p);
        }else p.sendMessage(
                String.format(
                        "%sInvicta> %sYou are already in the game.",
                        ChatColor.BLUE,
                        ChatColor.GRAY));
        if (game.getMaxPlayers() == game.size()) {
            game.getTimer().start();
        }
    }
    public static void removePlayer(Player p) {
        Game game = currentGame;
        game.removePlayer(p);

        Inventory inventory = p.getInventory();
        inventory.setItem(1, null);
        inventory.setItem(2, null);
    }
    public static boolean hasPlayer(Player p) {
        return currentGame != null && currentGame.contains(p);
    }

    // TODO: Probably replace with a queue system.
    // TODO: This is assuming blue and red team enums, which are subject to change.
    public static void randomTeam(Player player) {
        Game game = currentGame;
        int red = game.getTeam(0).teamSize();
        int blue = game.getTeam(1).teamSize();
        if(blue > red)
            joinTeam(player, TeamEnum.RED);
        else if(red > blue)
            joinTeam(player, TeamEnum.BLUE);
        else //they are equal, good-ol RNG!
            joinTeam(player, new TeamEnum[]{TeamEnum.RED, TeamEnum.BLUE}[(int) (Math.random() + 0.5D)]);

    }
    public static void joinTeam(Player player, TeamEnum teamEnum) {
        Game game = currentGame;

        // If the player is not actually in the yet game, do not allow them to join a team.
        if (!hasPlayer(player)) return;

        // Make sure the player is actually on a team (so anyone who isn't spectating), and that the player
        // is not already on the team them are trying to join.
        if(game.getTeam(player) != null && game.getTeamEnum(player) == teamEnum) {
            player.sendMessage(String.format(
                    "%sInvicta> %sYou are already on this team%s!",
                    ChatColor.BLUE,
                    ChatColor.GRAY,
                    ChatColor.GRAY));
            return;
        }

        // Iterate through all of the teams that currently exist in the game and remove the player from them.
        // This guarantees that people cannot accidentally be on multiple teams at the same time.
        for (GTeam team : game.getTeams()) {
            if (team.isPlayerOnTeam(player)) {
                team.removeFromTeam(player.getUniqueId());
            }
        }

        // Now we try to send the player into the team; if the player successfully joins, then send the success message.
        // Reasons for failure include: player is not online, game is ongoing, the player is not participating,
        // there is no GTeam associated with the requested teamEnum, and if the team size is greater than or equal
        // to the maximum amount of players per team.
        if(game.joinTeam(player, teamEnum, false)) {
            player.sendMessage(
                    String.format(
                            "%sInvicta> %sYou joined the %s%s Team %sin %sGame %s%s.",
                            ChatColor.BLUE,
                            ChatColor.GRAY,
                            teamEnum.getChatColor(),
                            teamEnum.getName(),
                            ChatColor.GRAY,
                            ChatColor.GREEN,
                            game.getId(),
                            ChatColor.GRAY));
        } else {
            // Now we know that for one of the above reasons, the player couldn't join the team they wanted. We now
            // want to catch a couple of these failures and send a helpful chat message explaining what happened.
            if(game.getTeam(teamEnum).teamSize() >= game.getTeam(teamEnum).getMaxPlayers()) {
                player.sendMessage(
                        String.format(
                                "%sInvicta> %sThe team you are trying to join is full.",
                                ChatColor.BLUE,
                                ChatColor.GRAY));
            }
        }

    }

    public static void startGame() {
        if (currentGame == null) return;
        Game game = currentGame;
        if (game.getGameState() == GameState.STARTED) {
            return;
        }
        Pluginizer.getSpigotPlugin().getLogger().info("Attempting to start game " + game.getId());
        if (!game.hasChosenMap()) {
            game.broadcast("There is no map selected for this game.");
        }

        // Close all inventories before the game starts to prevent weird onClose event bugs
        for (Player p : game.getBukkitPlayers()) {
            p.closeInventory();
        }
        // Sort every participant who hasn't chosen a team into a random team.
        for (UUID uuid: game.getParticipantsNoTeam()) {
            Player player = Bukkit.getPlayer(uuid);
            randomTeam(player);
        }

        game.setState(GameState.STARTED);
        GameStartEvent gamestart = new GameStartEvent(game);
        Pluginizer.getSpigotPlugin().getServer().getPluginManager().callEvent(gamestart);
    }

    public static void endGame(Game game) {
        //use the default world if it doesn't exist
        //otherwise, use the set spawn
        String name = "world";
        if(Pluginizer.getSpigotPlugin().getWorldSetter().getCurrentWorldName() != null) {
            name = Pluginizer.getSpigotPlugin().getWorldSetter().getCurrentWorldName();
        }
        Location spawnLoc = Bukkit.getWorld(name).getSpawnLocation();
        game.setState(GameState.LOBBY);
        GameEndEvent gameend = new GameEndEvent(game, spawnLoc);
        //currentGame = null;
        Pluginizer.getSpigotPlugin().getServer().getPluginManager().callEvent(gameend);
    }

    public static Game getGame() {
        return currentGame;
    }
    /**
     *
     * @param game The game in which you want the GameResource from.
     * @return the GameResources
     */
    public static List<GameResource> getGameResources(Game game){
        return game.getGameResources();
    }
    public static Scoreboard getScoreboard(Game game) {
        return game.getGameScoreboard().getBoard();
    }

    private GameManager() {

    }
}
