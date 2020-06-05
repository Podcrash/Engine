package com.podcrash.api.plugin;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.db.redis.Communicator;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.commands.*;
import com.podcrash.api.damage.DamageQueue;
import com.podcrash.api.economy.EconomyHandler;
import com.podcrash.api.effect.particle.ParticleRunnable;
import com.podcrash.api.kits.KitPlayerManager;
import com.podcrash.api.listeners.*;
import com.podcrash.api.tracker.CoordinateTracker;
import com.podcrash.api.tracker.Tracker;
import com.podcrash.api.tracker.VectorTracker;
import com.podcrash.api.util.PlayerCache;
import com.podcrash.api.util.ReflectionUtil;
import com.podcrash.api.world.SpawnWorldSetter;
import com.podcrash.api.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PodcrashSpigot extends PodcrashPlugin {

    // TODO make all bukkit calls synchronous

    private static PodcrashSpigot INSTANCE;
    public static final String CHANNEL_NAME = "INVICTASERVER";
    private final Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    private final ExecutorService service = Executors.newCachedThreadPool();
    private int dQInt;

    private List<Tracker> trackers;
    private CoordinateTracker coordinateTracker;
    private VectorTracker vectorTracker;

    private final Map<String, Configurator> configurators = new HashMap<>();

    private EconomyHandler economyHandler;
    private SpawnWorldSetter worldSetter;

    private CommandMap commandMap;


    public static PodcrashSpigot getInstance() {
        return INSTANCE;
    }

    @Override
    public ExecutorService getExecutorService() {
        return service;
    }

    private void addTracker(Tracker tracker) {
        trackers.add(tracker);
        tracker.enable();
    }

    public void registerConfigurator(String identifier, Reader reader) {
        getLogger().info("Registering configurator: " + identifier);
        configurators.put(identifier, new Configurator(this, identifier, reader));
    }
    public void registerConfigurator(String identifier) {
        getLogger().info("Registering configurator: " + identifier);
        configurators.put(identifier, new Configurator(this, identifier));
    }

    /**
     * This method is used to start setting up the game servers
     */
    public void gameStart() {
        getLogger().info("This Server is a game lobby with code" + Communicator.getCode());
        setKnockback();
        registerGameListeners();

        DamageQueue.active = true;
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DamageQueue(), 0, 0);
        dQInt = task.getTaskId();
        trackers = new ArrayList<>();
        addTracker(coordinateTracker = new CoordinateTracker(this));
        addTracker(vectorTracker = new VectorTracker(this));


    }

    public void gameDisable() {
        for(World world : Bukkit.getWorlds()) {
            if (world.getName().equalsIgnoreCase("world")) continue;
            Bukkit.unloadWorld(world, false);
        }
        DamageQueue.active = false;
        Bukkit.getScheduler().cancelTask(dQInt);
        HandlerList.unregisterAll(this);
        for(Tracker tracker : trackers)
            tracker.disable();

    }
    @Override
    public void onEnable() {
        INSTANCE = this;
        ReflectionUtil.initiate();
        getLogger().info("Starting PodcrashSpigot!");

        Future<Void> dbFuture = enableWrap();

        //WorldManager.getInstance().loadWorlds();

        worldSetter = new SpawnWorldSetter(); // this is a special cookie

        registerMessengers();
        // Fetch private bukkit commandmap by reflections
        try {
            Field commandMapField = getServer().getClass().getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = (CommandMap) commandMapField.get(getServer());

            dbFuture.get();
        } catch (NoSuchFieldException | IllegalAccessException e) {
            getLogger().severe("Failed to load Bukkit commandmap. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        } catch (InterruptedException | ExecutionException e) {
            getLogger().severe("Failed to load databases. Disabling plugin.");
            getServer().getPluginManager().disablePlugin(this);
        }

        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        table.setPlugin(this); //this is required

        registerCommands();
        registerListeners();
        economyHandler = new EconomyHandler();
        ParticleRunnable.start();
        PlayerCache.packetUpdater();


        Communicator.readyGameLobby();
        if (Communicator.isGameLobby())
            gameStart();
    }

    @Override
    public void onDisable() {
        if (Communicator.isGameLobby()) {
            DamageQueue.active = false;
            gameDisable();
            KitPlayerManager.getInstance().clear();
        }
        disable();
        WorldManager.getInstance().unloadWorlds();
    }

    @Override
    public void onLoad() {
    }

    public <K extends Tracker> K getTracker(Class<? extends K> trackerClasz) {
        for(Tracker tracker : trackers)
            if (tracker.getClass().equals(trackerClasz))
                return trackerClasz.cast(tracker);
        throw new RuntimeException("tracker is null, shouldn't happen");
    }
    public CoordinateTracker getCoordinateTracker() {
        return coordinateTracker;
    }
    public VectorTracker getVectorTracker() {
        return vectorTracker;
    }


    /**
     * Customized Knockback
     */
    private void setKnockback() {
        Logger log = this.getLogger();
        log.info("Kb Numbers: ");
        /*

        getDouble("settings.knockback.friction", knockbackFriction);
        getDouble("settings.knockback.horizontal", knockbackHorizontal);
        getDouble("settings.knockback.vertical", knockbackVertical);
        getDouble("settings.knockback.verticallimit", knockbackVerticalLimit);
        getDouble("settings.knockback.extrahorizontal", knockbackExtraHorizontal);
        getDouble("settings.knockback.extravertical", knockbackExtraVertical);
         */

        SpigotConfig.knockbackFriction = SpigotConfig.config.getDouble("settings.knockback.friction");
        SpigotConfig.knockbackHorizontal = SpigotConfig.config.getDouble("settings.knockback.horizontal");
        SpigotConfig.knockbackVertical = SpigotConfig.config.getDouble("settings.knockback.vertical");
        SpigotConfig.knockbackVerticalLimit = SpigotConfig.config.getDouble("settings.knockback.verticallimit");
        SpigotConfig.knockbackExtraHorizontal = SpigotConfig.config.getDouble("settings.knockback.extrahorizontal");
        SpigotConfig.knockbackExtraVertical = SpigotConfig.config.getDouble("settings.knockback.extravertical");

        debugLog("Friction: " + SpigotConfig.knockbackFriction);
        debugLog("Horizontal: " + SpigotConfig.knockbackHorizontal);
        debugLog("Veritcal: " + SpigotConfig.knockbackVertical);
        debugLog("Vertical Limit: " + SpigotConfig.knockbackVerticalLimit);
        debugLog("Extra Horizontal: " + SpigotConfig.knockbackExtraHorizontal);
        debugLog("Extra Vertical: " + SpigotConfig.knockbackExtraVertical);
    }
    private void registerListeners() {
        new ActionBlockListener(this);
        new BaseChatListener(this);
        new CmdPreprocessHandler(this);
        new SpigotJoinListener(this);
        new StatusListener(this);
        new MobListeners(this);
        new FallDamageHandler(this);
        new MOTDHandler(this);
        new PlayerInventoryListener(this);
        new GeneralLobbyListener(this);
        new GenEconListener(this);
        new KickListener(this);
        new SkillMaintainListener(this);
        new ApplyKitListener(this);
        new FriendsMenuListener(this);
    }


    private void registerGameListeners() {
        new GameListener(this);
        new GameDamagerConverterListener(this);
        new TrapListener(this);
        new BackfillListener(this);
        new MapMaintainListener(this);
        new DeathHandler(this);
        new SpecDisallowListener(this);
        new GameLobbyListener(this);
    }

    private void registerCommands() {
        //todo: use more reflection
        registerCommand(new SetRoleCommand());
        registerCommand(new AddRoleCommand());
        registerCommand(new BalanceCommand());
        registerCommand(new BuyCommand());
        registerCommand(new ConfirmCommand());
        registerCommand(new TellCommand());
        registerCommand(new EndCommand());
        registerCommand(new PingCommand());
        registerCommand(new StartCommand());
        registerCommand(new ViewCommand());
        registerCommand(new SpecCommand());
        registerCommand(new SetMapCommand());
        registerCommand(new TeamCommand());
        registerCommand(new KillCommand());
        registerCommand(new KnockbackCommand());
        registerCommand(new HitRegCommand());
        registerCommand(new MuteCommand());
        registerCommand(new AcceptCommand());
        registerCommand(new IncreaseMaxPlayersCommand());
        registerCommand(new DecreaseMaxPlayersCommand());
        registerCommand(new WhitelistCommand());
        registerCommand(new FriendCommand());
    }

    public Configurator getConfigurator(String identifier) {
        return configurators.get(identifier);
    }
    public void reloadConfigurators() {
        configurators.values().forEach(Configurator::reloadConfig);
    }

    public EconomyHandler getEconomyHandler() {
        return economyHandler;
    }
    public SpawnWorldSetter getWorldSetter() {
        return worldSetter;
    }

    public void setupPermissions(Player player) {
        //todo make this not slow
        PermissionAttachment attachment = player.addAttachment(this);
        this.playerPermissions.put(player.getUniqueId(), attachment);
        permissionsSetter(player);
    }

    private void permissionsSetter(Player player) {
        PermissionAttachment attachment = this.playerPermissions.get(player.getUniqueId());
        String[] disallowedPerms = new String[] {
                "bukkit.command.reload",
                "bukkit.command.timings",
                "bukkit.command.plugins",
                "bukkit.command.help",
                "bukkit.command.ban-ip",
                "bukkit.command.stop",
                "invicta.map",
                "invicta.host",
                "invicta.developer",
                "invicta.testing",
                "invicta.mute",
                "invicta.mod"
        };

        getInstance().getLogger().info("Disabling bad permissions");
        for(String disallowed : disallowedPerms)
            attachment.setPermission(disallowed, false);

        RanksTable table = TableOrganizer.getTable(DataTableType.PERMISSIONS);

        table.getRanksAsync(player.getUniqueId()).thenAccept(ranks -> {
            for(Rank r : ranks) {
                player.sendMessage(String.format("%s%sYou have been assigned the %s role!", ChatColor.GREEN, ChatColor.BOLD, r.getName()));
                for(String permission : r.getPermissions())
                    attachment.setPermission(permission, true);
            }
        });
    }

    public Map<UUID, PermissionAttachment> getPlayerPermissions() {
        return playerPermissions;
    }

    public UUID getPPLOwner() {
        String prop = System.getProperty("mps.owner");
        if (prop == null) return null;
        return UUID.fromString(prop);
    }

    public boolean hasPPLOwner() {
        return System.getProperty("mps.owner") != null;
    }

    private void registerMessengers() {
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        //getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL_NAME, new MessageListener(CHANNEL_NAME));
    }

    public void registerCommand(BukkitCommand command) {
        commandMap.register(command.getLabel(), command);
    }

    public static void debugLog(String message) {
        if(DEBUG)
            getInstance().getLogger().info(message);
    }

    public static void debugErr(String message) {
        if(DEBUG)
            getInstance().getLogger().log(Level.SEVERE, message);
    }

}
