package com.podcrash.api.mc.listeners;

import me.raindance.champions.Main;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.effect.status.StatusWrapper;
import me.raindance.champions.events.DamageApplyEvent;
import me.raindance.champions.events.DeathApplyEvent;
import com.podcrash.api.mc.events.StatusApplyEvent;
import com.podcrash.api.mc.events.game.*;
import me.raindance.champions.events.skill.SkillUseEvent;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.IObjective;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.objectives.CapturePoint;
import com.podcrash.api.mc.game.objects.objectives.Emerald;
import com.podcrash.api.mc.game.objects.objectives.Restock;
import me.raindance.champions.game.resource.CapturePointDetector;
import me.raindance.champions.game.resource.CapturePointScorer;
import com.podcrash.api.mc.game.resources.ItemObjectiveSpawner;
import com.podcrash.api.mc.game.resources.ScoreboardRepeater;
import com.podcrash.api.mc.game.scoreboard.DomScoreboard;
import com.podcrash.api.mc.game.scoreboard.GameScoreboard;
import com.podcrash.api.mc.item.ItemManipulationManager;
import me.raindance.champions.game.DomGame;
import me.raindance.champions.kits.ChampionsPlayer;
import me.raindance.champions.kits.ChampionsPlayerManager;
import me.raindance.champions.kits.Skill;
import me.raindance.champions.kits.classes.Mage;
import me.raindance.champions.kits.skilltypes.TogglePassive;
import me.raindance.champions.listeners.ListenerBase;
import com.podcrash.api.redis.Communicator;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.SimpleTimeResource;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.PrefixUtil;
import com.podcrash.api.mc.util.VectorUtil;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * @see GameManager
 */
public class GameListener extends ListenerBase {
    private List<Player> deadPeople = new ArrayList<>();

    public GameListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler
    public void mapChange(GameMapChangeEvent e) {
        Communicator.putLobbyMap("map", e.getMap());
    }
    @EventHandler
    public void onJoin(GameJoinEvent e) {
        Communicator.putLobbyMap("size", e.getGame().size());
        //Communicator.publish(e.getGame().getGameCount());
    }

    @EventHandler
    public void onLeave(GameLeaveEvent e) {
        Communicator.putLobbyMap("size", e.getGame().size());
        //Communicator.publish(e.getGame().getGameCount());
    }

    //--------------------------------------
    //GameEvents
    //--------------------------------------
    /**
     * @see com.podcrash.api.mc.game.GameManager#startGame
     * @param e event callback
     */
    @EventHandler
    public void onStart(GameStartEvent e) {
        Game game = e.getGame();
        game.broadcast(game.toString());
        Main.getInstance().getLogger().info("game is " + game);
        if (e.getGame().getPlayerCount() < 1) {
            this.plugin.getLogger().info(String.format("Can't start game %d, not enough players!", game.getId()));
        }
        String startingMsg = String.format("Game %d is starting up with map %s", e.getGame().getId(), e.getGame().getMapName());
        for(Player p : e.getGame().getPlayers()) p.sendMessage(startingMsg);

        game.loadMap();

        GameScoreboard gamescoreboard = game.getGameScoreboard();
        gamescoreboard.makeObjective();
        gamescoreboard.setupScoreboard();

        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            Location red = game.getRedSpawn().get(0);
            Location blue = game.getBlueSpawn().get(0);

            for (int i = 0; i < game.getBlueTeam().size(); i++) {
                Location spawn = game.getBlueSpawn().get(i);
                Player player = game.getBlueTeam().get(i);
                ChampionsPlayer championsPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
                Vector vector = VectorUtil.fromAtoB(spawn, red);
                spawn.setDirection(vector);
                championsPlayer.setSpawnLocation(spawn);
                player.teleport(spawn);
                championsPlayer.restockInventory();
            }
            for (int i = 0; i < game.getRedTeam().size(); i++) {
                Location spawn = game.getRedSpawn().get(i);
                Player player = game.getRedTeam().get(i);
                Vector vector = VectorUtil.fromAtoB(spawn, blue);
                spawn.setDirection(vector);
                ChampionsPlayer championsPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
                championsPlayer.setSpawnLocation(spawn);
                player.teleport(spawn);
                championsPlayer.restockInventory();
            }

            Location specSpawn = game.getGameWorld().getSpawnLocation();
            for(int i = 0; i < game.getSpectators().size(); i++) {
                Player player = game.getSpectators().get(i);
                player.teleport(specSpawn);
                player.setGameMode(GameMode.SPECTATOR);
            }
        }, 0L);

        game.sendColorTab(false);
        CapturePointDetector capture = new CapturePointDetector(game.getId());
        game.registerResources(
                new ScoreboardRepeater(game.getId()),
                new ItemObjectiveSpawner(game.getId()),
                capture,
                new CapturePointScorer(capture)
        );
        game.broadcast(e.getMessage());
    }

    @EventHandler
    public void onEnd(GameEndEvent e) {
        Game game = e.getGame();
        game.sendColorTab(true);
        for (Player player : game.getPlayers()) {
            player.teleport(e.getSpawnlocation());
            player.sendMessage(e.getMessage());
            if(game.isSpectating(player)){
                player.setGameMode(GameMode.ADVENTURE);
            }
            deadPeople.remove(player);
        }

        Communicator.publishLobby(Communicator.getCode() + " close");
        DomGame game1 = new DomGame(GameManager.getCurrentID(), Long.toString(System.currentTimeMillis()));
        GameManager.createGame(game1);
        //WorldManager.getInstance().deleteWorld(e.getGame().getGameWorld(), true);

    }

    @EventHandler
    public void onGameDeath(GameDeathEvent e) {

        TeamEnum victimTeam = TeamEnum.getByColor(e.getGame().getTeamColor(e.getWho()));
        TeamEnum enemyTeam = null;
        switch(victimTeam) {
            case RED:
                e.getGame().increment("blue", 50);
                enemyTeam = TeamEnum.BLUE;
                break;
            case BLUE:
                e.getGame().increment("red", 50);
                enemyTeam = TeamEnum.RED;
                break;
        }
        Player victim = e.getWho();
        ChampionsPlayer victimPlayer;
        if((victimPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(victim)) != null) {
            victimPlayer.getSkills().forEach(skill -> {
                if(skill instanceof TogglePassive)
                    if(((TogglePassive) skill).isToggled())
                        ((TogglePassive) skill).toggle();
            });
        }
        LivingEntity killer = e.getKiller();
        String msg = e.getMessage();
        if(enemyTeam != null && killer != null) {
            msg = msg.replace(victim.getName(), victimTeam.getChatColor() + victim.getName())
                    .replace(killer.getName(), enemyTeam.getChatColor() + killer.getName());
        }
        e.getGame().broadcast(msg);

        deathAnimation(e.getWho().getLocation());

        e.getWho().sendMessage(String.format("%sRespawn>%s You will respawn in 9 seconds.",ChatColor.BLUE, ChatColor.GRAY));
        //e.getWho().
        //StatusApplier.getOrNew(e.getWho()).applyStatus(Status.INEPTITUDE, 9, 1);
        Bukkit.getScheduler().runTaskLater(Main.instance, () -> {
            for(Player player : e.getGame().getPlayers()){
                if(player != e.getWho() && player.canSee(e.getWho())) player.hidePlayer(e.getWho());
            }
        }, 1L);
        Vector vector = e.getWho().getVelocity();
        e.getWho().setVelocity(vector.add(new Vector(0, 0.75D, 0)));
        e.getWho().setAllowFlight(true);
        e.getWho().setFlying(true);
        deadPeople.add(e.getWho());
        StatusApplier.getOrNew(e.getWho()).removeStatus(Status.values());
        TimeHandler.delayTime(180L, new SimpleTimeResource() {
            @Override
            public void task() {
                Bukkit.getPluginManager().callEvent(new GameResurrectEvent(e.getGame(), e.getWho()));
            }
        });
    }

    private void deathAnimation(Location loc){
        List<Item> blood = new ArrayList<>();

        for(double x = -0.1; x <= 0.1; x += 0.1) {
            for(double z = -0.1; z <= 0.1; z += 0.1){
                System.out.println(x + " 0.5 " + z);
                Item singleBlood = ItemManipulationManager.spawnItem(new ItemStack(Material.INK_SACK, 1, (short)1), loc, new Vector(x, 0.5, z));
                singleBlood.setCustomName("RITB");
                ItemMeta meta = singleBlood.getItemStack().getItemMeta();
                meta.setDisplayName("blood" + Long.toString(System.currentTimeMillis()));
                singleBlood.getItemStack().setItemMeta(meta);
                blood.add(singleBlood);
            }
        }
        TimeHandler.delayTime(17, new SimpleTimeResource() {
            @Override
            public void task() {
                for(Item dye : blood){
                    dye.remove();
                }
            }
        });
    }

    @EventHandler
    public void onResurrect(GameResurrectEvent e) {
        deadPeople.remove(e.getWho());
        ChampionsPlayerManager.getInstance().getChampionsPlayer(e.getWho()).respawn();
        e.getWho().sendMessage(e.getMessage());

        ChampionsPlayerManager.getInstance().getChampionsPlayer(e.getWho()).getGame().getRespawning().remove(e.getWho());
        StatusWrapper regen = new StatusWrapper(Status.REGENERATION, 4, 3, false);
        StatusWrapper resist = new StatusWrapper(Status.RESISTANCE, 4, 3, false);
        StatusApplier.getOrNew(e.getWho()).applyStatus(regen, resist);
    }

    @EventHandler
    public void onCapture(GameCaptureEvent e){
        Game game = e.getGame();
        IObjective objective = e.getObjective();
        if(objective instanceof CapturePoint){
            String teamColor = ((CapturePoint) objective).getColor();
            TeamEnum team = TeamEnum.getByColor(teamColor);
            StringBuilder builder = new StringBuilder();
            builder.append(team.getChatColor());
            builder.append(ChatColor.BOLD);
            builder.append(team.getName());
            builder.append(" has captured ");
            builder.append(objective.getName());
            builder.append("!");
            e.getGame().broadcast(builder.toString());
            DomScoreboard scoreboard = (DomScoreboard) e.getGame().getGameScoreboard();
            scoreboard.updateCapturePoint(teamColor, objective.getName());
            objective.spawnFirework();
        }else if(objective instanceof Emerald) {

        }else e.getWho().sendMessage(e.getMessage());
    }

    @EventHandler
    public void onPickup(GamePickUpEvent event){
        ItemObjective itemObjective = event.getItem();
        itemObjective.die();
        Player player = event.getWho();
        itemObjective.setAcquiredByPlayer(player);
        Game game = event.getGame();
        String teamColor = game.getTeamColor(player);
        TeamEnum team = TeamEnum.getByColor(teamColor);
        if(itemObjective instanceof Emerald) {
            game.increment(teamColor, 300);
            StringBuilder builder = new StringBuilder();
            //builder.append(team.getChatColor());
            builder.append(ChatColor.DARK_GREEN);
            builder.append(ChatColor.BOLD);
            builder.append(team.getName());
            //builder.append(ChatColor.GREEN);
            builder.append(" has gained 300 points!");
            game.broadcast(builder.toString());
        }else if(itemObjective instanceof Restock) {
            ChampionsPlayer cPlayer = ChampionsPlayerManager.getInstance().getChampionsPlayer(player);
            cPlayer.restockInventory();
            if(cPlayer instanceof Mage) {
                cPlayer.getEnergyBar().setEnergy(cPlayer.getEnergyBar().getMaxEnergy());
                player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "You restored energy!");
            }
            player.sendMessage(ChatColor.YELLOW + ChatColor.BOLD.toString() + "You recieved supplies!");
        }
        itemObjective.spawnFirework();
        game.getGameResources().forEach(resource -> {
            if(resource instanceof ItemObjectiveSpawner){
                ((ItemObjectiveSpawner) resource).setItemTime(itemObjective, System.currentTimeMillis());
            }
        });
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void gameDamage(GameDamageEvent event) {
        Game game = event.getGame();
        if(!game.isOngoing()) return;
        String color = game.getTeamColor(event.getWho());
        if("spec".equalsIgnoreCase(color) || color.equalsIgnoreCase(game.getTeamColor(event.getVictim()))) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void damage(EntityDamageEvent e) {
        if(e.getEntity() instanceof Player) {
            if (deadPeople.contains(e.getEntity()) || GameManager.isSpectating((Player) e.getEntity()))
                e.setCancelled(true);
        }
    }
    //--------------------------------------
    //GameEvents (Converters)
    //--------------------------------------

    /**
     * GameDeathEvent(Game game, Player who)
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void onDeath(final DeathApplyEvent e) {
        Player p = e.getPlayer();
        Game game = GameManager.getGame();
        LivingEntity killer = e.getAttacker();
        if (game == null || !game.isOngoing()) return;

        List<Skill> skills = ChampionsPlayerManager.getInstance().getChampionsPlayer(p).getSkills();
        for(Skill skill : skills) {
            if(!(skill instanceof TogglePassive)) continue;
            if (((TogglePassive) skill).isToggled())
                ((TogglePassive) skill).toggle();
        }
        p.setHealth(p.getMaxHealth()); //heal right away
        if(e.wasUnsafe())
            p.teleport(game.getGameWorld().getSpawnLocation());
        game.getRespawning().add(p);
        plugin.getServer().getPluginManager().callEvent(new GameDeathEvent(game, p, killer, e.getDeathMessage()));
    }

    /**
     * GameDamageEvent(Game game, Player who, Player victim)
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHit(DamageApplyEvent e) {
        if(!(e.getVictim() instanceof Player) || !(e.getAttacker() instanceof Player)) return;
        Player victim = (Player) e.getVictim();
        if(deadPeople.contains(victim) || deadPeople.contains(e.getAttacker())|| GameManager.isSpectating(victim)) {
            e.setCancelled(true);
        }else if (e.getAttacker() != null) {
            Player attacker = (Player) e.getAttacker();
            Game game = GameManager.getGame();
            if (game == null) return;
            GameDamageEvent event = new GameDamageEvent(game, attacker, victim);
            plugin.getServer().getPluginManager().callEvent(event);
            e.setCancelled(event.isCancelled());
        }
    }

    @EventHandler
    public void status(StatusApplyEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if(GameManager.isSpectating(player) || deadPeople.contains(player))
            event.setCancelled(true);
    }

    @EventHandler
    public void velocity(PlayerVelocityEvent event) {
        Player player = event.getPlayer();
        if(GameManager.isSpectating(player) || deadPeople.contains(player))
            event.setCancelled(true);
    }

    /**
     * GamePickUpEvent(Game game, Player player, Item item, int remaining)
     */
    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        if(!EntityUtil.onGround(e.getItem()) || deadPeople.contains(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        Player who = e.getPlayer();
        Game game = GameManager.getGame();
        if (game == null) return;
        e.setCancelled(true);
        org.bukkit.entity.Item item = e.getItem();
        ItemObjective itemObj = null;
        for(ItemObjective itemObjective : game.getItemObjectives()) {
            if(itemObjective.getItem().getEntityId() == item.getEntityId()) {
                itemObj = itemObjective;
            }
        }
        if(itemObj == null) return;
        int remaining = e.getRemaining(); // most likely not too important

        plugin.getServer().getPluginManager().callEvent(new GamePickUpEvent(game, who, itemObj, remaining));
    }

    @EventHandler
    public void chat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        if(player.hasPermission("Champions.mute")){
            event.setCancelled(true);
            player.sendMessage(String.format("%sChampions> %sYou are muted.", ChatColor.BLUE, ChatColor.GRAY));
            return;
        }
        if(GameManager.hasPlayer(player)) {
            event.setCancelled(true);
            Game game = GameManager.getGame();
            game.broadcast(String.format("%s%s%s" + ChatColor.RESET + " %s",
                    PrefixUtil.getPrefix(PrefixUtil.getPlayerRole(player)),
                    TeamEnum.getByColor(game.getTeamColor(player)).getChatColor(),
                    player.getName(),
                    event.getMessage())
            );
        }else {
            event.getRecipients().removeIf(GameManager::hasPlayer);
            event.setFormat(PrefixUtil.getPrefix(PrefixUtil.getPlayerRole(player)) + ChatColor.RESET + "%s " + ChatColor.GRAY + "%s");

        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void skill(SkillUseEvent e){
        if(deadPeople.contains(e.getPlayer()) || GameManager.isSpectating(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockBreak(BlockBreakEvent e){
        if(GameManager.hasPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockPlace(BlockPlaceEvent e){
        if(GameManager.hasPlayer(e.getPlayer())) {
            e.setCancelled(true);
        }
    }


}
