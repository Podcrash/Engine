package com.podcrash.api.plugin;

import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.pojos.Rank;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.MapTable;
import com.podcrash.api.db.tables.RanksTable;
import com.podcrash.api.mc.Configurator;
import com.podcrash.api.mc.commands.*;
import com.podcrash.api.mc.damage.DamageQueue;
import com.podcrash.api.mc.economy.EconomyHandler;
import com.podcrash.api.mc.economy.IEconomyHandler;
import com.podcrash.api.mc.listeners.*;
import com.podcrash.api.mc.tracker.CoordinateTracker;
import com.podcrash.api.mc.tracker.Tracker;
import com.podcrash.api.mc.tracker.VectorTracker;
import com.podcrash.api.mc.world.SpawnWorldSetter;
import com.podcrash.api.mc.world.WorldManager;
import com.podcrash.api.db.redis.Communicator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.SpigotConfig;

import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Logger;

public class PodcrashSpigot extends JavaPlugin implements PodcrashPlugin {
    private static PodcrashSpigot INSTANCE;
    public static PodcrashSpigot getInstance() {
        return INSTANCE;
    }
    public static final String CHANNEL_NAME = "INVICTASERVER";
    private final Map<UUID, PermissionAttachment> playerPermissions = new HashMap<>();

    private ExecutorService service = Executors.newCachedThreadPool();
    private int dQInt;

    private List<Tracker> trackers;
    private CoordinateTracker coordinateTracker;
    private VectorTracker vectorTracker;

    private final Map<String, Configurator> configurators = new HashMap<>();

    private IEconomyHandler economyHandler;
    private SpawnWorldSetter worldSetter;

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
     * This method is used to start setting up for the game servers
     */
    public void gameStart() {
        getLogger().info("This Server is a game lobby with code" + Communicator.getCode());
        Future future = CompletableFuture.allOf(
                setKnockback(),
                registerGameListeners()
        );

        DamageQueue.active = true;
        BukkitTask task = Bukkit.getScheduler().runTaskTimerAsynchronously(this, new DamageQueue(), 0, 0);
        dQInt = task.getTaskId();
        trackers = new ArrayList<>();
        addTracker(coordinateTracker = new CoordinateTracker(this));
        addTracker(vectorTracker = new VectorTracker(this));

        try {
            getLogger().info("Awaiting....");
            future.get();
            getLogger().info("Awaited!");
        }catch (InterruptedException|ExecutionException e) {
            e.printStackTrace();
        }

    }

    public void gameDisable() {
        for(World world : Bukkit.getWorlds()) {
            if(world.getName().equalsIgnoreCase("world")) continue;
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
        getLogger().info("Starting PodcrashSpigot!");
        Pluginizer.setInstance(this);

        Future future = CompletableFuture.allOf(
                enableWrap(),
                registerCommands(),
                registerListeners());
        //WorldManager.getInstance().loadWorlds();
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        MapTable table = TableOrganizer.getTable(DataTableType.MAPS);
        table.setPlugin(this); //this is required

        economyHandler = new EconomyHandler();
        worldSetter = new SpawnWorldSetter(); // this is a special cookie
        registerMessengers();
        Communicator.readyGameLobby();
        if(Communicator.isGameLobby())
            gameStart();
    }

    @Override
    public void onDisable() {
        if(Communicator.isGameLobby())
            gameDisable();
        disable();
        WorldManager.getInstance().unloadWorlds();
    }

    @Override
    public void onLoad() {
    }

    public <K extends Tracker> K getTracker(Class<? extends K> trackerClasz) {
        for(Tracker tracker : trackers)
            if(tracker.getClass().equals(trackerClasz))
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
     * @return
     */
    private CompletableFuture<Void> setKnockback() {
        return CompletableFuture.runAsync(() -> {
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


            log.info("Friction: " + SpigotConfig.knockbackFriction);
            log.info("Horizontal: " + SpigotConfig.knockbackHorizontal);
            log.info("Veritcal: " + SpigotConfig.knockbackVertical);
            log.info("Vertical Limit: " + SpigotConfig.knockbackVerticalLimit);
            log.info("Extra Horizontal: " + SpigotConfig.knockbackExtraHorizontal);
            log.info("Extra Vertical: " + SpigotConfig.knockbackExtraVertical);

        }, getExecutorService());
    }
    private CompletableFuture<Void> registerListeners() {
        new BaseChatListener(this);
        return CompletableFuture.runAsync(() -> {
            new MapMaintainListener(this);
            new PlayerInventoryListener(this);
            new SpigotJoinListener(this);
            new StatusListener(this);
            new MobListeners(this);
            new ActionBlockListener(this);
            new FallDamageHandler(this);
            new MOTDHandler(this);
            new CmdPreprocessHandler(this);
            new GeneralLobbyListener(this);
            new BackfillListener(this);
            new GenEconListener(this);
            new KickListener(this);
            // TODO: Add more listeners here..
        });
    }

    private CompletableFuture<Void> registerGameListeners() {
        return CompletableFuture.runAsync(() -> {
            new GameListener(this);
            new GameDamagerConverterListener(this);
            new TrapListener(this);

            // TODO: Add more listeners here..
        });
    }
    private CompletableFuture<Void> registerCommands() {
        return CompletableFuture.runAsync(() -> {
            getCommand("setrole").setExecutor(new SetRoleCommand());
            getCommand("addrole").setExecutor(new AddRoleCommand());
            getCommand("bal").setExecutor(new BalanceCommand());
            getCommand("buy").setExecutor(new BuyCommand());
            getCommand("confirm").setExecutor(new ConfirmCommand());
            getCommand("tell").setExecutor(new TellCommand());
            getCommand("endgame").setExecutor(new EndCommand());
            getCommand("ping").setExecutor(new PingCommand());
            getCommand("start").setExecutor(new StartCommand());
            getCommand("view").setExecutor(new ViewCommand());
            getCommand("spec").setExecutor(new SpecCommand());
            getCommand("setmap").setExecutor(new SetMapCommand());
            getCommand("team").setExecutor(new TeamCommand());
            getCommand("kill").setExecutor(new KillCommand());
            getCommand("kb").setExecutor(new KnockbackCommand());
            getCommand("hitreg").setExecutor(new HitRegCommand());
            getCommand("mute").setExecutor(new MuteCommand());
            getCommand("accept").setExecutor(new AcceptCommand());
            getCommand("increase").setExecutor(new IncreaseMaxPlayersCommand());
            getCommand("decrease").setExecutor(new DecreaseMaxPlayersCommand());
            getCommand("whitelist").setExecutor(new WhitelistCommand());
        });
}

    public Configurator getConfigurator(String identifier) {
        return configurators.get(identifier);
    }
    public void reloadConfigurators() {
        CompletableFuture.runAsync(() ->
                configurators.values().forEach(Configurator::reloadConfig));
    }

    public IEconomyHandler getEconomyHandler() {
        return economyHandler;
    }
    public SpawnWorldSetter getWorldSetter() {
        return worldSetter;
    }

    public void setupPermissions(Player player) {
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
                for(String permission : r.getPermissions()) {
                    attachment.setPermission(permission, true);
                }
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
}
