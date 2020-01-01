package com.podcrash.api.mc.listeners;

import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.effect.status.StatusWrapper;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.events.StatusApplyEvent;
import com.podcrash.api.mc.events.game.*;
import com.podcrash.api.mc.game.GTeam;
import com.podcrash.api.mc.game.Game;
import com.podcrash.api.mc.game.GameManager;
import com.podcrash.api.mc.game.TeamEnum;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.map.BaseGameMap;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.time.resources.SimpleTimeResource;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.PrefixUtil;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.redis.Communicator;
import org.bukkit.*;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void mapChange(GameMapChangeEvent e) {
        Communicator.putLobbyMap("map", e.getMap());
    }
    @EventHandler
    public void onJoin(GameJoinEvent e) {
        Communicator.putLobbyMap("size", e.getGame().size());
        //Communicator.publish(e.getGame().getGameCount());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onLeave(GameLeaveEvent e) {
        Communicator.putLobbyMap("size", e.getGame().size());
        //Communicator.publish(e.getGame().getGameCount());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void mapLoad(GameMapLoadEvent event) {
        System.out.println("test");
        BaseGameMap map = event.getMap();
        World world = event.getWorld();
        Game game = event.getGame();

        List<GTeam> teams = game.getTeams();
        List<double[][]> spawnsArr = map.getSpawns();
        //set spawns
        for(int i = 0, gTeamSize = teams.size(); i < gTeamSize; i++) {
            GTeam team = teams.get(i);
            double[][] spawns = spawnsArr.get(i);
            List<Location> spawnLocs = new ArrayList<>();
            for(double[] s : spawns) {
                spawnLocs.add(new Location(world, s[0], s[1], s[2]));
            }
            team.setSpawns(spawnLocs);
        }
    }

    //--------------------------------------
    //GameEvents
    //--------------------------------------
    /**
     * @see com.podcrash.api.mc.game.GameManager#startGame
     * @param e event callback
     */
    @EventHandler(priority = EventPriority.LOWEST)
    public void onStart(GameStartEvent e) {
        Game game = e.getGame();
        game.getTeams().forEach(GTeam::allSpawn);
        game.getBukkitSpectators().forEach(player -> player.teleport(game.getSpawnLocation()));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnd(GameEndEvent e) {
        Game game = e.getGame();
        game.sendColorTab(true);
        for (Player player : game.getBukkitPlayers()) {
            player.teleport(e.getSpawnlocation());
            player.sendMessage(e.getMessage());
            if(game.isSpectating(player)){
                player.setGameMode(GameMode.ADVENTURE);
            }
            deadPeople.remove(player);
        }
        //WorldManager.getInstance().deleteWorld(e.getGame().getGameWorld(), true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGameDeath(GameDeathEvent e) {
        //if(!(e.getKiller() instanceof Player)) return;

        Player victim = e.getWho();
        LivingEntity killer = e.getKiller();
        Game game = e.getGame();

        String finalMsg = editMessage(e.getMessage(), game, victim, killer);


        e.getWho().sendMessage(String.format("%sRespawn>%s You will respawn in 9 seconds.",ChatColor.BLUE, ChatColor.GRAY));
        Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(), () -> {
            deathAnimation(victim.getLocation());
            victim.setAllowFlight(true);
            victim.setFlying(true);
            e.getGame().consumeBukkitPlayer(player -> {
                player.sendMessage(finalMsg);
                if(player != victim && player.canSee(victim)) player.hidePlayer(victim);
            });
        }, 1L);
        Vector vector = victim.getVelocity();
        victim.setVelocity(vector.add(new Vector(0, 0.75D, 0)));
        deadPeople.add(victim);
        StatusApplier.getOrNew(victim).removeStatus(Status.values());
        //StatusApplier.getOrNew(victim).applyStatus(Status.INEPTITUDE, 9, 1);
        TimeHandler.delayTime(180L, () -> {
            GTeam team = game.getTeam(victim);
            victim.teleport(team.getSpawn(victim));
            Bukkit.getPluginManager().callEvent(new GameResurrectEvent(game, victim));
        });
    }

    private String editMessage(String msg, Game game, Player victim, LivingEntity killer) {
        TeamEnum victimTeam = game.getTeamEnum(victim);
        if(killer != null) {
            TeamEnum enemyTeam = game.getTeamEnum((Player) killer);
            msg = msg.replace(victim.getName(), victimTeam.getChatColor() + victim.getName())
                    .replace(killer.getName(), enemyTeam.getChatColor() + killer.getName());
        }

        return msg;
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
        e.getGame().getRespawning().remove(e.getWho().getUniqueId()); //forgot what this does tbh
        e.getWho().sendMessage(e.getMessage());

        StatusWrapper regen = new StatusWrapper(Status.REGENERATION, 4, 3, false);
        StatusWrapper resist = new StatusWrapper(Status.RESISTANCE, 4, 3, false);
        StatusApplier.getOrNew(e.getWho()).applyStatus(regen, resist);
    }

    @EventHandler
    public void onCapture(GameCaptureEvent e){

    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPickup(GamePickUpEvent event){
        ItemObjective itemObjective = event.getItem();
        itemObjective.die();
        Player player = event.getWho();
        itemObjective.setAcquiredByPlayer(player);
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void gameDamage(GameDamageEvent event) {
        Game game = event.getGame();
        if(!game.isOngoing()) return;
        boolean cancel = false;

        if(game.getTeam(event.getWho()) == null) {
            event.setCancelled(true);
            return;
        }
        if(deadPeople.contains(event.getKiller())) {
            event.setCancelled(true);
            return;
        }
        cancel = game.isOnSameTeam(event.getKiller(), event.getWho());
        event.setCancelled(cancel);
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
    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(DeathApplyEvent e) {
        Player p = e.getPlayer();
        Game game = GameManager.getGame();
        LivingEntity killer = e.getAttacker();
        if (game == null || !game.isOngoing()) return;
        p.setHealth(p.getMaxHealth()); //heal right away
        if(e.wasUnsafe())
            p.teleport(game.getGameWorld().getSpawnLocation());
        game.getRespawning().add(p.getUniqueId());
        Bukkit.getServer().getPluginManager().callEvent(new GameDeathEvent(game, p, killer, e.getDeathMessage()));
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
            Bukkit.getServer().getPluginManager().callEvent(event);
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

        Bukkit.getServer().getPluginManager().callEvent(new GamePickUpEvent(game, who, itemObj, remaining));
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
                    game.getTeamEnum(player).getChatColor(),
                    player.getName(),
                    event.getMessage())
            );
        }else {
            event.getRecipients().removeIf(GameManager::hasPlayer);
            event.setFormat(PrefixUtil.getPrefix(PrefixUtil.getPlayerRole(player)) + ChatColor.RESET + "%s " + ChatColor.GRAY + "%s");

        }
    }

    // TODO: The following are invalid now? Turf Wars requires block placement.

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void blockBreak(BlockBreakEvent e){
//        if(GameManager.hasPlayer(e.getPlayer())) {
//            e.setCancelled(true);
//        }
//    }
//
//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void blockPlace(BlockPlaceEvent e){
//        if(GameManager.hasPlayer(e.getPlayer())) {
//            e.setCancelled(true);
//        }
//    }


}
