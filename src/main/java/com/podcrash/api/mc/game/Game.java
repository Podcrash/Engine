package com.podcrash.api.mc.game;

import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.GameMap;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.mc.events.game.GameJoinEvent;
import com.podcrash.api.mc.events.game.GameLeaveEvent;
import com.podcrash.api.mc.events.game.GameMapChangeEvent;
import com.podcrash.api.mc.events.game.GameMapLoadEvent;
import com.podcrash.api.mc.game.resources.GameResource;
import com.podcrash.api.mc.game.scoreboard.GameLobbyScoreboard;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import com.podcrash.api.mc.ui.TeamSelectGUI;
import com.podcrash.api.mc.util.ChatUtil;
import com.podcrash.api.mc.util.ItemStackUtil;
import com.podcrash.api.mc.util.PrefixUtil;
import com.podcrash.api.plugin.Pluginizer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;

/**
 * Abstract Game Class
 * @author RainDance & JJCunningCreeper.
 *
 * TODO: createTeams()
 * TODO: Add kits?
 * TODO: Deal with modes.
 * TODO: Scoreboards and timers.
 */

public abstract class Game implements IGame {

    private int id;
    private String name;
    private List<GTeam> teams;
    private volatile boolean isLoadedMap;
    private GameState state;                            // TODO: May replace this with an Enum with a more specific state for the game.
    protected String gameWorldName;
    private List<GameResource> gameResources;

    // TODO: Everything in this section is related to the mode or type. To be modified later.
    private GameType type;                              // TODO: Mode - this may get replaced/removed later.
    private String primary_color;
    private String secondary_color;

    private Set<UUID> participants;                     // Participating players only.
    private Set<UUID> spectators;                       // Spectating players only.
    private Set<UUID> optIn;                            // Spectators to participate next game.
    private Set<UUID> respawning;                       // Respawning players.

    private GameLobbyScoreboard lobby_board;
    private GameLobbyTimer lobby_timer;

    private Map<Player, Double> playerRewards = new HashMap<>();

    private GameMap map;

    private Set<Player> isLobbyPVPing;
    /**
     * Constructor for the game.
     * @param id The ID of the game.
     * @param name The name of the game.
     * @param type The type of the game.
     */
    public Game(int id, String name, GameType type) {
        this.id = id;
        this.name = name;
        this.teams = new ArrayList<>();
        this.isLoadedMap = false;
        this.state = GameState.LOBBY;
        this.type = type;
        this.gameResources = new ArrayList<>();
        this.lobby_board = new GameLobbyScoreboard(this);
        lobby_board.run();
        this.lobby_timer = new GameLobbyTimer();

        this.participants = new HashSet<>();
        this.spectators = new HashSet<>();
        this.optIn = new HashSet<>();
        this.respawning = new HashSet<>();
        this.isLobbyPVPing = new HashSet<>();
//        this.lobby_board = new GameLobbyScoreboard(15, 0, this);
//        this.lobby_timer = new GameLobbyTimer();
    }

    public abstract GameScoreboard getGameScoreboard();
    public abstract int getAbsoluteMinPlayers();
    public abstract Location spectatorSpawn();
    public abstract void leaveCheck();

    public abstract Class<? extends GameMap> getMapClass();
    public abstract TeamSettings getTeamSettings();
    public abstract String getMode();
    public abstract String getPresentableResult();

    @Override
    public void increment(TeamEnum team, int score) {
        GTeam team1 = getTeam(team);
        team1.setScore(team1.getScore() + score);
        //getGameScoreboard().update();
    }

    /**
     * Load the map method, uses a callback of a bukkit event for custom loading
     */
    public void loadMap()  {
        System.out.println("Loading map!");
        MapTable mapTable = TableOrganizer.getTable(DataTableType.MAPS);

        //TODO: this solution is not clever.... rework
        CompletableFuture<? extends GameMap> futureMap = mapTable.downloadWorld(gameWorldName, getMode().toLowerCase(), getMapClass());
        futureMap.thenAcceptAsync(map -> {
            if(map == null) return;
            this.map = map;
            Bukkit.getPluginManager().callEvent(new GameMapLoadEvent(this, this.map, Bukkit.getWorld(gameWorldName)));
        });
    }

    /**
     *
     * @param player who received the money
     * @param reward how much money they earned
     * @return whether the player was successfully given the money
     */
    public boolean addReward(Player player, double reward) {
        if (playerRewards.containsKey(player)) {
            playerRewards.put(player, playerRewards.get(player) + reward);
            return true;
        }
        return false;
    }

    public double getReward(Player player) {
        if(playerRewards.get(player) == null) return 0;
        return playerRewards.get(player);
    }

    /**
     * @return The lobby timer attached to this game.
     */
    public GameLobbyTimer getTimer() {
        return lobby_timer;
    }

    /**
     * @return The ID of the game.
     */
    public int getId() { return id; }

    /**
     * @return The name of the game.
     */
    public String getName() { return name; }

    /**
     * @return A list of teams.
     */
    public List<GTeam> getTeams() { return teams; }

    /**
     * @return The number of teams.
     */
    public int numberOfTeams() { return teams.size(); }

    /**
     * @return If the map is loaded.
     */
    public boolean isLoadedMap() {
        return isLoadedMap;
    }

    public boolean hasChosenMap() {
        return gameWorldName != null && !gameWorldName.isEmpty();
    }
    /**
     * Precondition: if loadMap has been called.
     * @return the map
     */
    public BaseMap getMap() {
        return map;
    }

    /**
     * @return Whether the game is ongoing.
     */
    public GameState getGameState() { return state; }

    /**
     * Set whether the game is ongoing.
     * @param state The ongoing boolean.
     */
    public void setState(GameState state) { this.state = state; }

    /**
     * @return The Game World.
     */
    public World getGameWorld() { return Bukkit.getWorld(gameWorldName); }

    /**
     *
     * @return The set of players enabled pvp in the lobby
     */
    public Set<Player> getPlayersLobbyPVPing() {
        return isLobbyPVPing;
    }

    /**
     * Removes the player from the set of lobby pvpers if able
     * @param p - The player
     */
    public void removePlayerLobbyPVPing(Player p) {
        if (isLobbyPVPing.contains(p)) {
            isLobbyPVPing.remove(p);
        }
    }

    /**
     * Adds the player to the set of lobby pvpers if able
     * @param p - The player
     */
    public void addPlayerLobbyPVPing(Player p) {
        if (!isLobbyPVPing.contains(p)) {
            isLobbyPVPing.add(p);
        }
    }

    /**
     * Set the Game World.
     * @param world The Game World name.
     */
    public void setGameWorld(String world){
        //unload the last game world
        if(this.gameWorldName != null) Bukkit.unloadWorld(this.gameWorldName, false);
        if(world.equalsIgnoreCase(gameWorldName)) return;
        //unload the current world just in case
        Bukkit.unloadWorld(world, false);
        this.gameWorldName = world;
        Bukkit.getPluginManager().callEvent(new GameMapChangeEvent(this, world));
        this.loadMap();
    }

    /**
     * Get the map name.
     * TODO: This is suggesting that the world name is the map name. This will most likely change when the maps system is established.
     * @return The map name.
     */
    public String getMapName(){
        if(gameWorldName == null) {
            return "None";
        } else {
            String[] splitName = gameWorldName.split("(?=\\p{Upper})");
            StringBuilder result = new StringBuilder();
            for(String word: splitName) {
                result.append(word);
                result.append(" ");
            }
            return result.toString();
        }
    }

    /**
     * @return The Game Type.
     */
    public GameType getType() {
        return type;
    }

    /**
     * @return The capacity for the game (sum of all team capacities).
     */
    public int getCapacity() {
        int capacity = 0;
        for (GTeam team : teams) {
            capacity = capacity + team.getCapacity();
        }
        return capacity;
    }

    /**
     * @return The max number of players for the game (sum of amm team maxes).
     */
    public int getMaxPlayers() {
        int max = 0;
        for (GTeam team : teams) {
            max = max + team.getMaxPlayers();
        }
        return max;
    }

    public int getMinPlayers() {
        int min = 0;
        for (GTeam team : teams) {
            min = min + team.getMinPlayers();
        }
        return min;
    }

    /**
     * @return The game resources.
     */
    public List<GameResource> getGameResources() {
        return gameResources;
    }

    /**
     * Register a game resource.
     * @param resource The game resource.
     */
    public void registerResource(GameResource resource){
        if(resource.getGameID() != this.id) throw new IllegalArgumentException("resource does not correspond with its game id" + "gameid: " + id + " resourceid: " + resource.getGameID());
        gameResources.add(resource);
        resource.run(resource.getTicks(), resource.getDelayTicks());
    }

    /**
     * Register game resources.
     * @param resources The game resources.
     */
    public void registerResources(GameResource... resources){
        for (GameResource resource: resources) {
            registerResource(resource);
        }
    }

    /**
     * Unregister a game resource.
     * @param resource The game resource to unregister.
     */
    public void unregisterGameResource(GameResource resource){
        resource.unregister();
        gameResources.remove(resource);
    }

    /**
     * @return A set of the participating players.
     */
    public Set<UUID> getParticipants() { return participants; }

    /**
     * @return A set of spectating players.
     */
    public Set<UUID> getSpectators() { return spectators; }

    /**
     * @return A set of spectators to opt in next game.
     */
    public Set<UUID> getOptIn() { return optIn; }

    /**
     * @return The set of respawning players.
     */
    public Set<UUID> getRespawning() { return respawning; }

    /**
     * @return A set of all players (participating and spectating).
     */
    public Set<UUID> getPlayers() {
        Set<UUID> players = new HashSet<>();
        players.addAll(participants);
        players.addAll(spectators);
        return players;
    }

    public Set<UUID> getParticipantsNoTeam() {
        Set<UUID> result = new HashSet<UUID>();
        for (UUID uuid : participants) {
            if (!isOnTeam(uuid)) { result.add(uuid); }
        }
        return result;
    }

    /**
     * @return A list of all players.
     */
    public List<Player> getBukkitPlayers() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : getPlayers()) {
            Player p;
            if ((p = Bukkit.getPlayer(uuid)) != null) players.add(p);
        }
        return players;
    }
    public void consumeBukkitPlayer(Consumer<Player> playerConsumer) {
        for(UUID uuid : getPlayers()) {
            Player p = Bukkit.getPlayer(uuid);
            if(p != null) playerConsumer.accept(p);
        }
    }

    /**
     * @return List of spectators.
     */
    public List<Player> getBukkitSpectators() {
        List<Player> players = new ArrayList<>();
        for(UUID uuid : getSpectators()) {
            Player p;
            if ((p = Bukkit.getPlayer(uuid)) != null) players.add(p);
        }
        return players;
    }

    /**
     * The entire team of players that the specified player is on.
     * @param player The player
     * @return The list of team members.
     */
    public List<UUID> getTeamPlayers(Player player) {
        for (GTeam team : teams) {
            if (team.getPlayers().contains(player.getUniqueId())) {
                return team.getPlayers();
            }
        }
        return null;
    }

    /**
     * @return The total player count (sum of players on each team).
     */
    public int getPlayerCount() {
        int sum = 0;
        for (GTeam team : teams) {
            sum = sum + team.teamSize();
        }
        return sum;
    }

    /**
     * @return The number of participating players in the game.
     */
    public int size() {
        return participants.size();
    }

    /**
     * @return If the game is full.
     */
    public boolean isFull() {
        return getPlayerCount() >= getMaxPlayers();
    }

    /**
     * @param player The player.
     * @return If the player is respawning.
     */
    public boolean isRespawning(Player player) {
        return respawning.contains(player.getUniqueId());
    }

    public void makeTeams() {
        teams = new ArrayList<>();
        TeamSettings settings = getTeamSettings();
        for(TeamEnum team : settings.getTeamColors()) {
            //better would be new GTeam(team, settings);
            GTeam gTeam = new GTeam(team, settings.getCapacity(), settings.getMin(), settings.getMax(), null);
            teams.add(gTeam);
        }
    }
    /**
     * Create a new scoreboard for the teams.
     * TODO: This should only be done when the game starts because team enums can change prior to that.
     */
    public void createScoreboard(){
        Scoreboard colorBoard = getGameScoreboard().getBoard();
        for (GTeam team : teams) {
            TeamEnum gTeam = team.getTeamEnum();
            String teamString = getTeamString(gTeam);
            if (colorBoard.getTeam(teamString) != null)
                colorBoard.getTeam(teamString).unregister();

            Team newTeam = colorBoard.registerNewTeam(teamString);
            newTeam.setPrefix(gTeam.getChatColor().toString());
            newTeam.setCanSeeFriendlyInvisibles(true);
            newTeam.setAllowFriendlyFire(true);
            newTeam.setNameTagVisibility(NameTagVisibility.ALWAYS);

        }
    }
    /**
     * Get the team unique string for putting player colors
     * @param team the team enum that correlates with the color
     * @return the unique string
     */
    protected final String getTeamString(TeamEnum team) {
        return id + team.getByteData() + team.getName() + "Team";
    }

    /**
     * Toggles the spectating status of a player.
     * @param player The player.
     */
    public void toggleSpec(Player player) {
        if (state == GameState.STARTED) {
            optIn.add(player.getUniqueId());
            return;
        }
        if (isSpectating(player)) {
            removeSpectator(player);
        } else {
            addSpectator(player);
        }
    }

    /**
     * Add a player to a scoreboard team with a team enum.
     * This is used for display team colors in player display names (above player's head).
     * This should only happen upon the start of a game as team enums can change prior to that.
     * TODO: Perhaps a misleading method name that could be changed later?
     * @param player The player to add.
     * @param teamEnum The team enum.
     */
    public void addPlayerToTeam(Player player, TeamEnum teamEnum) {
         spectators.remove(player.getUniqueId());
         getTeam(teamEnum).addToTeam(player.getUniqueId());
         Team team = getGameScoreboard().getBoard().getTeam(getTeamString(teamEnum));
         player.setScoreboard(getGameScoreboard().getBoard());
         team.addEntry(player.getName());
//         add(player);
    }

    /**
     * Player joining a team.
     * @param player The player.
     * @param teamEnum The Team Enum of the team to join.
     * @return If the join was successful.
     */
    public boolean joinTeam(Player player, TeamEnum teamEnum) {
        if (!player.isOnline() || state == GameState.STARTED || !isParticipating(player)) return false;
        leaveTeam(player);

        GTeam team = getTeam(teamEnum);
        if (team == null || team.teamSize() >= team.getMaxPlayers()) return false;

        Scoreboard scoreboard =  getGameScoreboard().getBoard();
        Team bukkitTeam = scoreboard.getTeam(getTeamString(teamEnum));
        player.setScoreboard(scoreboard);
        bukkitTeam.addEntry(player.getName());
        team.addToTeam(player);

        refreshTabColor(player, teamEnum.getChatColor().toString());

        if (player.getOpenInventory().getTitle().equals(TeamSelectGUI.inventory_name)) { player.openInventory(TeamSelectGUI.selectTeam(this, player)); }
        return true;
    }

    public void refreshTabColor(Player player, String color) {
        Rank rank = PrefixUtil.getPlayerRole(player);
        if(rank != null) {
            player.setPlayerListName(String.format("%s %s%s",
                    PrefixUtil.getPrefix(rank),
                    color,
                    player.getName()));
        } else {
            player.setPlayerListName(String.format("%s%s",
                    color,
                    player.getName()));
        }
    }

    public boolean leaveTeam(Player player) {
        if (!contains(player) || !isOnTeam(player)) { return false; }
        getTeam(player).removeFromTeam(player);
        player.setPlayerListName(ChatUtil.chat("&7" + player.getName()));
        if (player.getOpenInventory().getTitle().equals(TeamSelectGUI.inventory_name)) { player.openInventory(TeamSelectGUI.selectTeam(this, player)); }
        leaveCheck();
        return true;
    }

    /**
     * If the player wants to spectate, add them to the list.
     * If the game is ongoing, teleport them to the game.
     * TODO: Check if the added player is a participant/on a team and remove them accordingly?
     * @param player The player.
     */
    public void addSpectator(Player player) {
        // Add to spectators and remove from participants.
        spectators.add(player.getUniqueId());
        participants.remove(player.getUniqueId());
        leaveTeam(player);
        refreshTabColor(player, ChatUtil.chat("&7&o"));
        // If is ongoing, set them to spectator mode and send them to the spectator spawn.
        if (state == GameState.STARTED) {
            player.teleport(spectatorSpawn());
            player.setGameMode(GameMode.SPECTATOR);
            player.setScoreboard(getGameScoreboard().getBoard());
            player.setSpectator(true);
        } else {
            updateLobbyInventory(player);
        }
        player.sendMessage(String.format(
                "%sInvicta> %sYou joined the %sSpectators %sin %sGame %s%s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.YELLOW,
                ChatColor.GRAY,
                ChatColor.GREEN,
                GameManager.getGame().getId(),
                ChatColor.GRAY));
    }

    /**
     * Remove a spectator.
     * @param player The spectator player.
     */
    public void removeSpectator(Player player) {
        spectators.remove(player.getUniqueId());
        participants.add(player.getUniqueId());
        if (state == GameState.LOBBY) { updateLobbyInventory(player); }
        GameManager.randomTeam(player);
    }

    public void addParticipant(Player player) {
        removeSpectator(player);
        // TODO: Set the lobby scoreboard, etc...
    }

    public void removeParticipant(Player player) {
        addSpectator(player);
    }

    /**
     * Add a player to the game.
     * @param player The player.
     */
    public void add(Player player) {
        // If ongoing, add to spectators. Else, add to participants.
        if (state == GameState.STARTED) {
            addSpectator(player);
            optIn.add(player.getUniqueId());
            resetPlayer(player, GameMode.ADVENTURE, false, true, true);
        } else {
            addParticipant(player);
        }

        playerRewards.put(player, 0.0);

        // Call event.
        Bukkit.getServer().getPluginManager().callEvent(new GameJoinEvent(this, player));
    }

    /**
     * Remove a player from the game.
     * @param player The player.
     */
    public void remove(Player player) {
        // Remove from participants and spectators.
        participants.remove(player.getUniqueId());
        spectators.remove(player.getUniqueId());
        optIn.remove(player.getUniqueId());
        // If on a team, remove from team.
        if (isOnTeam(player)) {
            leaveTeam(player);
        }
        // Reset scoreboard.
        //player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        // Call event.
        Bukkit.getServer().getPluginManager().callEvent(new GameLeaveEvent(this, player));
    }

    /**
     * @param player The player.
     * @return Whether the game contains the player.
     */
    public boolean contains(Player player) {
        return isOnTeam(player) || isSpectating(player) || participants.contains(player.getUniqueId()) || spectators.contains(player.getUniqueId());
    }

    /**
     * Return a list of player UUIDs on a team, given the team name (color).
     * @param color The team name (color).
     * @return The list of player UUIDs on the team.
     */
    public List<UUID> getTeamStr(String color) {
        for (GTeam team : teams) {
            if (team.getTeamEnum().getName().equalsIgnoreCase(color)) {
                return team.getPlayers();
            }
        }
        throw new IllegalArgumentException(String.format("%s is not a valid team color!", color));
    }

    /**
     * The team that the specified player is on.
     * @param player The player.
     * @return The team.
     */
    public GTeam getTeam(Player player) {
        for (GTeam team : teams) {
            if (team.getPlayers().contains(player.getUniqueId())) {
                return team;
            }
        }
        return null;
    }

    /**
     * Get team with team enum.
     * @param teamEnum The Team Enum.
     * @return The team.
     */
    public GTeam getTeam(TeamEnum teamEnum) {
        for (GTeam team : teams) {
            if (team.getTeamEnum() == teamEnum) {
                return team;
            }
        }
        return null;
    }

    /**
     * Get team with index in the teams list.
     * @param index The index.
     * @return The team.
     */
    public GTeam getTeam(int index) {
        return teams.get(index);
    }

    /**
     * Get the team ID of a player.
     * @param player The player.
     * @return The team ID of a player.
     */
    public TeamEnum getTeamEnum(Player player) {
        return getTeam(player).getTeamEnum();
    }


    /**
     * Get the players on a team with a team enum.
     * @param teamEnum The team enum.
     * @return The team players.
     */
    public List<UUID> getTeamPlayers(TeamEnum teamEnum) {
        return getTeam(teamEnum).getPlayers();
    }

    /**
     * If a player is on a team.
     * @param player The player.
     * @return Whether the player is on a team.
     */
    public boolean isOnTeam(Player player) {
        return (isOnTeam(player.getUniqueId()));
    }

    /**
     * If a player is on a team.
     * @param uuid The UUID of the player.
     * @return Whether the player is on a team.
     */
    public boolean isOnTeam(UUID uuid) {
        for (GTeam team : teams) {
            if (team.getPlayers().contains(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a player is on a team with a team enum.
     * @param player The player.
     * @param teamEnum The team enum.
     * @return Whether the player is on the team.
     */
    public boolean isOnTeam(Player player, TeamEnum teamEnum) {
        return getTeam(teamEnum).getPlayers().contains(player.getUniqueId());
    }

    /**
     * Set the new teamEnum of a team.
     * @param teamEnum The team enum of the team to change.
     * @param id The new teamEnum ID.
     * @return Whether it was successful.
     */
    public boolean setTeamEnum(TeamEnum teamEnum, int id) {
        return (setTeamEnum(teamEnum, TeamEnum.getByData(id)));
    }

    /**
     * Set the newteamEnum of a team.
     * @param teamEnum The team enum of the team to change.
     * @param newTeamEnum The new team enum.
     * @return Whether it was successful.
     */
    public boolean setTeamEnum(TeamEnum teamEnum, TeamEnum newTeamEnum) {
        if (TeamEnum.isColorIDValid(id) && !isTeamEnumTaken(TeamEnum.getByData(id))) {
            getTeam(teamEnum).setTeamEnum(newTeamEnum);
            for (UUID uuid : getTeam(newTeamEnum).getPlayers()) {
                Player p = Bukkit.getPlayer(uuid);
                p.setPlayerListName(newTeamEnum.getChatColor() + p.getName());
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks if a team enum is already taken by a team.
     * @param teamEnum The team enum.
     * @return Whether the team enum has been taken.
     */
    public boolean isTeamEnumTaken(TeamEnum teamEnum) {
        for (GTeam team : teams) {
            if (team.getTeamEnum() == teamEnum) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if the game has an empty team.
     * @return If the game has an empty team.
     */
    public boolean hasEmptyTeam() {
        for (GTeam team : teams) {
            if (team.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if two players are on the same team.
     * @param p1 Player 1.
     * @param p2 Player 2.
     * @return If they are on the same team.
     */
    public boolean isOnSameTeam(Player p1, Player p2) {
        if (!(isOnTeam(p1) && isOnTeam(p2)))  return false;
        return getTeam(p1).isPlayerOnTeam(p2);
    }

    /**
     * If a player is participating.
     * @param player The player.
     * @return If the player is participating.
     */
    public boolean isParticipating(Player player) {
        return isParticipating(player.getUniqueId());
    }

    /**
     * Whether the player with a name is participating.
     * @param name The name of the player.
     * @return Whether the player with the name is participating.
     */
    public boolean isParticipating(String name) {
        return isParticipating(Bukkit.getPlayer(name).getUniqueId());
    }

    /**
     * Whether the player with a UUID is participating.
     * @param uuid The UUID of the player.
     * @return Whether the player with the UUID is participating.
     */
    public boolean isParticipating(UUID uuid) {
        return participants.contains(uuid);
    }

    /**
     * If a player is spectating.
     * @param player The player.
     * @return If the player is spectating.
     */
    public boolean isSpectating(Player player) {
        return isSpectating(player.getUniqueId());
    }

    /**
     * Whether the player with a name is spectating.
     * @param name The name of the player.
     * @return Whether the player with the name is spectating.
     */
    public boolean isSpectating(String name) {
        return isSpectating(Bukkit.getPlayer(name).getUniqueId());
    }

    /**
     * Whether the player with a UUID is spectating.
     * @param uuid The UUID of the player.
     * @return Whether the player with the UUID is spectating.
     */
    public boolean isSpectating(UUID uuid) {
        return spectators.contains(uuid);
    }

    /**
     * Opt in all spectators who have decided to opt in for the next game.
     * This should only happen at the end of a game.
     */
    public void optIn() {
        for (UUID uuid : optIn) {
            removeSpectator(Bukkit.getPlayer(uuid));
            addParticipant(Bukkit.getPlayer(uuid));
        }
    }

    /**
     * Reset a player.
     * @param p The player.
     * @param gm The Gamemode.
     * @param flight If the player should be flying.
     * @param resetExp If the player's experience/level should be reset to 0.
     */
    public void resetPlayer(Player p, GameMode gm, boolean visible, boolean flight, boolean resetExp) {
        for (PotionEffect effect : p.getActivePotionEffects()) { p.removePotionEffect(effect.getType()); }
        p.setHealth(20);
        p.closeInventory();
        p.setGameMode(gm);
        p.setAllowFlight(flight);
        p.setFlying(flight);
        p.getInventory().clear();
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (visible) {
                player.showPlayer(p);
            } else {
                player.hidePlayer(p);
            }
        }
        if (resetExp) {
            p.setLevel(0);
            p.setExp(0);
        }
    }

    public void updateLobbyInventory(Player p) {
        ItemStack[] hotbarSave = p.getInventory().getContents();
        p.getInventory().clear();
        Inventory inv = p.getInventory();

        if (isLobbyPVPing.contains(p)) {
            for (int i = 0; i < 9; i++) {
                inv.setItem(i, hotbarSave[i]);
            }
        } else {
            ItemStackUtil.createItem(inv, 388, 1, 1, "&a&lEnable Lobby PVP");
        }
        // Setting items in the player's inventory
        // TODO: Remove the Force Start Item.
        //ItemStackUtil.createItem(inv, 388, 1, 1, "&a&lForce-Start Game &7(Temporary for testing)");
        ItemStackUtil.createItem(inv, 355, 1, 9, "&d&lReturn to Lobby");

        ItemStackUtil.createItem(inv, 145, 1, 21, ChatUtil.chat("&6&lSelect Kit"));
        ItemStackUtil.createItem(inv, 421, 1, 23, ChatUtil.chat("&6&lSelect Team"));
        //ItemStackUtil.createItem(inv, 95, 7, 1, 24, ChatUtil.chat("&7&lLeave Team Queue"));
        ItemStack spectate;
        if (isSpectating(p)) {
            spectate = ItemStackUtil.createItem(373, 8270, 1, ChatUtil.chat("&7&lToggle Spectator Mode"));
            spectate.getItemMeta().removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        } else {
            spectate = ItemStackUtil.createItem(374, 1, ChatUtil.chat("&7&lToggle Spectator Mode"));
        }
        inv.setItem(25 - 1, spectate); //-1 because here we are using inventory's method (begins at 0)
    }

    /*
    Not sure what these methods below do as much... Will probably focus on them later...
     */

    public Location getSpawnLocation() {
        return getGameWorld().getSpawnLocation();
    }

    /**
     * TODO: Honestly not sure what the hell this does.
     * @param reset Reset boolean.
     */
    public void sendColorTab(boolean reset) {
        if(!reset) {
            /*
            for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                if(team.getName().equals(id + "redTeam") || team.getName().equals(id + "blueTeam")) {
                    WrapperPlayServerScoreboardTeam teamPacket = new WrapperPlayServerScoreboardTeam();
                    teamPacket.setMode(WrapperPlayServerScoreboardTeam.Mode.TEAM_UPDATED);
                    teamPacket.setPrefix(team.getPrefix());
                    teamPacket.setSuffix(team.getSuffix());
                    teamPacket.setPlayers(new ArrayList<>(team.getEntries()));
                    teamPacket.setDisplayName(team.getDisplayName());
                    teamPacket.setName(team.getName());
                    for(Player player : getBukkitPlayers()) teamPacket.sendPacket(player);
                    for(Player player : getSpectators()) teamPacket.sendPacket(player);
                }
            }
            */
        }else {
            for(Team team : Bukkit.getScoreboardManager().getMainScoreboard().getTeams()) {
                if(team.getName().equals(id + "redTeam") || team.getName().equals(id + "blueTeam")) {
                    Pluginizer.getSpigotPlugin().getLogger().info("cleared a team! from " + id);
                    team.unregister();
                }
            }
        }
    }

    /**
     * Remove a player from the game.
     * TODO: Instead of passing in the team ID, find the team ID from the player?
     * TODO: Fix this
     * @param player The player.
     */
    public void removePlayer(Player player) {
        //getTeam(player).removeFromTeam(player.getUniqueId());
        //remove(player);
        if (participants.contains(player.getUniqueId())) {
            remove(player);
            GTeam gteam = getTeam(player);
            if (gteam != null) {
                gteam.removeFromTeam(player.getUniqueId());
                Team team = getGameScoreboard().getBoard().getTeam(id + gteam.getTeamEnum().getName() + "Team");
                if (team != null) {
                    team.removeEntry(name);
                }
            }
            player.sendMessage(
                    String.format(
                            "%sGame> %sYou were removed from Game #" + id + '!',
                            ChatColor.BLUE,
                            ChatColor.GRAY));
            //player.setScoreboard(Bukkit.getScoreboardManager().getMainScoreboard());
        } else if(isSpectating(player)) {
            spectators.remove(player.getUniqueId());
        }
    }

    // TODO: Change this to work with more than 2 teams.
    @Override
    public String toString() {
        return String.format("%s{Game %d}%s[%d/%d]%s:\n %s\n %s\n %s\n %s",ChatColor.GREEN, id, ChatColor.WHITE, getPlayerCount(), getMaxPlayers(), ChatColor.GRAY, niceLookingTeam(TeamEnum.RED), niceLookingTeam(TeamEnum.BLUE), niceLookingSpec(), optIn);
    }

    /**
     * Get the nice looking string of a team with an ID.
     * @param teamEnum The Team Enum.
     * @return The team's nice looking string.
     */
    private String niceLookingTeam(TeamEnum teamEnum) {
        return getTeam(teamEnum).niceLooking();
    }

    /**
     * @return The nice looking string of spectators.
     */
    private String niceLookingSpec() {
        StringBuilder result = new StringBuilder(ChatColor.YELLOW + "" + ChatColor.BOLD + "Spectators: ");
        result.append(ChatColor.RESET);
        for(UUID uuid : spectators) {
            Player p = Bukkit.getPlayer(uuid);
            result.append(p.getName());
            result.append(' ');
        }
        return result.toString();
    }

    /**
     * Broadcast a message to all Bukkit Players involved in the game.
     * @param msg
     */
    public void broadcast(String msg) {
        for (Player player : getBukkitPlayers()){
            player.sendMessage(msg);
        }
    }
    protected final void log(String msg){
        Pluginizer.getSpigotPlugin().getLogger().info(String.format("%s: %s", toString(), msg));
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Game)) return false;
        Game game = (Game) o;
        return id == game.id &&
                Objects.equals(name, game.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}