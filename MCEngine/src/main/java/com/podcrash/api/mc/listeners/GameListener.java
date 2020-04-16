package com.podcrash.api.mc.listeners;

import com.abstractpackets.packetwrapper.AbstractPacket;
import com.podcrash.api.db.pojos.map.BaseMap;
import com.podcrash.api.db.pojos.map.GameMap;
import com.podcrash.api.db.pojos.map.Point;
import com.podcrash.api.db.pojos.map.Point2Point;
import com.podcrash.api.mc.damage.DamageApplier;
import com.podcrash.api.mc.effect.particle.ParticleGenerator;
import com.podcrash.api.mc.effect.status.Status;
import com.podcrash.api.mc.effect.status.StatusApplier;
import com.podcrash.api.mc.effect.status.StatusWrapper;
import com.podcrash.api.mc.events.DamageApplyEvent;
import com.podcrash.api.mc.events.DeathApplyEvent;
import com.podcrash.api.mc.events.ItemCollideEvent;
import com.podcrash.api.mc.events.StatusApplyEvent;
import com.podcrash.api.mc.events.game.*;
import com.podcrash.api.mc.game.*;
import com.podcrash.api.mc.game.objects.ItemObjective;
import com.podcrash.api.mc.game.objects.action.ActionBlock;
import com.podcrash.api.mc.item.ItemManipulationManager;
import com.podcrash.api.mc.sound.SoundPlayer;
import com.podcrash.api.mc.time.TimeHandler;
import com.podcrash.api.mc.util.EntityUtil;
import com.podcrash.api.mc.util.ItemStackUtil;
import com.podcrash.api.mc.util.PacketUtil;
import com.podcrash.api.mc.world.WorldManager;
import com.podcrash.api.plugin.Pluginizer;
import com.podcrash.api.db.redis.Communicator;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    @EventHandler(priority = EventPriority.LOWEST) //ensures that this happens first
    public void mapLoad(GameMapLoadEvent event) {
        System.out.println("test MAP LOAD");
        GameMap map = event.getMap();
        World world = event.getWorld();
        Game game = event.getGame();

        System.out.println("test " + map);
        List<GTeam> teams = game.getTeams();
        Map<String, List<Point>> spawnsArr = map.getSpawns();
        //set spawns
        for(int i = 0, gTeamSize = teams.size(); i < gTeamSize; i++) {
            GTeam team = teams.get(i);
            String color = team.getTeamEnum().getColor().name();
            List<Point> spawns = spawnsArr.get(color);
            List<Location> spawnLocs = new ArrayList<>();
            for(Point s : spawns) {
                spawnLocs.add(new Location(world, s.getX(), s.getY(), s.getZ()));
            }
            team.setSpawns(spawnLocs);
        }

        List<Point2Point> launchPads = map.getLaunchPads();
        List<Point2Point> teleportPads = map.getTeleportPads();

        List<ActionBlock> blocks = new ArrayList<>();
        addActionBlocks(blocks, ActionBlock.Type.SLIME, launchPads);
        addActionBlocks(blocks, ActionBlock.Type.TELEPORT, teleportPads);

        ActionBlockListener.setBlocks(world, blocks);
    }

    private void addActionBlocks(List<ActionBlock> list, ActionBlock.Type type, List<Point2Point> points) {
        for(Point2Point p : points) {
            ActionBlock block = new ActionBlock(p);
            block.setType(type);
            list.add(block);
        }
    }

    /**
     * EventPriority of LOW will ensure it will run before most things
     * @param e
     */
    @EventHandler(priority = EventPriority.LOW)
    public void collideItem(ItemCollideEvent e) {
        Game game = GameManager.getGame();
        if(game == null) return;
        if(!(e.getCollisionVictim() instanceof Player)) return;
        Player p = (Player) e.getCollisionVictim();
        //if the player is not participating (spectator) or is respawning, then let the item pass through them.
        if(!game.isParticipating(p) || game.isRespawning(p))
            e.setCancelled(true);

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
        game.getTeams().forEach(team -> {
            team.allSpawn();
            team.getBukkitPlayers().forEach(player -> {
                DamageApplier.removeInvincibleEntity(player);
                game.removePlayerLobbyPVPing(player);
                player.setHealth(player.getMaxHealth());
                player.setSpectator(false);
                player.setGameMode(GameMode.SURVIVAL);
            });
        });
        game.getBukkitSpectators().forEach(player -> {
            DamageApplier.removeInvincibleEntity(player);
            game.removePlayerLobbyPVPing(player);
            player.setHealth(player.getMaxHealth());
            player.teleport(game.getSpawnLocation());
            player.setGameMode(GameMode.SPECTATOR);
        });
        BaseMap map = game.getMap();
        if(map == null) return;
        StringBuilder authorBuilder = new StringBuilder();
        map.getAuthors().forEach(authorBuilder::append);
        String message = ChatColor.GRAY + "\n ==================== \n \n " + ChatColor.RESET + "" +
                ChatColor.BOLD + "Map: " + ChatColor.RESET + "" + ChatColor.YELLOW + map.getName() + "\n" +
                ChatColor.RESET + "" + ChatColor.GRAY +  " Built by: " + ChatColor.RESET + "" + ChatColor.BOLD + ""  + ChatColor.GOLD + authorBuilder.toString() +
                ChatColor.GRAY + "\n \n ==================== \n ";
        game.consumeBukkitPlayer(player -> player.sendMessage(message));
        SoundPlayer.sendSound(game.getBukkitPlayers(), "fireworks.blast", 1F, 63);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEnd(GameEndEvent e) {
        Game game = e.getGame();
        game.sendColorTab(true);
        for (Player player : game.getBukkitPlayers()) {
            DamageApplier.addInvincibleEntity(player);
            player.setHealth(player.getMaxHealth());
            player.teleport(e.getSpawnlocation());
            player.sendMessage(e.getMessage());
            player.setGameMode(GameMode.ADVENTURE);
            deadPeople.remove(player);
            player.sendMessage(game.getPresentableResult());
            SoundPlayer.sendSound(player, "fireworks.launch", 1F, 63);
        }
        WorldManager.getInstance().unloadWorld(e.getGame().getGameWorld().getName());
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
            StatusApplier.getOrNew(victim).removeStatus(Status.values());
            e.getGame().consumeBukkitPlayer(player -> {
                player.sendMessage(finalMsg);
                if(player != victim && player.canSee(victim)) player.hidePlayer(victim);
            });

            String causes = e.getDeathCausesMessage();
            if (causes != null) {
                e.getWho().sendMessage(causes);
            }
        }, 1L);

        Vector vector = victim.getVelocity();
        victim.setVelocity(vector.add(new Vector(0, 0.75D, 0)));
        deadPeople.add(victim);


        //StatusApplier.getOrNew(victim).applyStatus(Status.INEPTITUDE, 9, 1);
        TimeHandler.delayTime(200L, () -> {
            if(game.getGameState() == GameState.STARTED) {
                GTeam team = game.getTeam(victim);
                victim.teleport(team.getSpawn(victim));
                Bukkit.getPluginManager().callEvent(new GameResurrectEvent(game, victim));
            }
        });
    }

    public static String editMessage(String msg, Game game, Player victim, LivingEntity killer) {
        TeamEnum victimTeam = game.getTeamEnum(victim);
        if(killer != null) {
            TeamEnum enemyTeam = game.getTeamEnum((Player) killer);
            msg = msg.replace(victim.getName(), victimTeam.getChatColor() + victim.getName())
                    .replace(killer.getName(), enemyTeam.getChatColor() + killer.getName());
        }

        return msg;
    }

    private void deathAnimation(Location loc){

        AbstractPacket boneShatter = ParticleGenerator.createBlockEffect(loc, Material.WEB.getId());
        PacketUtil.asyncSend(boneShatter, loc.getWorld().getPlayers());

        List<Item> blood = new ArrayList<>();
        int counter = 0;

        for(double x = -0.1; x <= 0.1; x += 0.1) {
            for(double z = -0.1; z <= 0.1; z += 0.1){
                Item singleBlood = ItemManipulationManager.spawnItem(new ItemStack(Material.INK_SACK, 1, (short)1), loc, new Vector(x, 0.5, z));
                singleBlood.setCustomName("RITB");
                ItemMeta meta = singleBlood.getItemStack().getItemMeta();
                meta.setDisplayName(counter + Long.toString(System.currentTimeMillis()));
                singleBlood.getItemStack().setItemMeta(meta);
                blood.add(singleBlood);
                counter++;
            }
        }
        TimeHandler.delayTime(17, () -> {
            for(Item dye : blood){
                dye.remove();
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

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void gameDamage(GameDamageEvent e) {
        Game game = e.getGame();
        //if(game.getGameState() == GameState.LOBBY) return;

        if(e.getWho() == null || e.getKiller() == null) return;
        Player victim = e.getWho();
        if(deadPeople.contains(victim) || deadPeople.contains(e.getKiller())|| GameManager.isSpectating(victim))
            e.setCancelled(true);
        else if(game.getTeam(e.getWho()) == null)
            e.setCancelled(true);
        else if(deadPeople.contains(e.getKiller()))
            e.setCancelled(true);
        else if(game.isSpectating(e.getKiller()) || game.isSpectating(e.getWho()))
            //latter part might be uneeded but just in case
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void hit(DamageApplyEvent e) {
        Game game = GameManager.getGame();
        if(game == null) return;
        //if(game.getGameState() == GameState.LOBBY) return;
        if(!(e.getAttacker() instanceof Player) && !(e.getVictim() instanceof Player)) return;
        boolean sameTeam = game.isOnSameTeam((Player) e.getAttacker(), (Player) e.getVictim());
        System.out.println(e.getCause() + " sameTeam clause: " + sameTeam + " attacker: " + e.getAttacker() + " victim: " + e.getVictim());
        if(sameTeam)
            e.setCancelled(true);
    }
    @EventHandler(priority = EventPriority.NORMAL)
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
        if (e.isCancelled()) return;
        Player p = e.getPlayer();
        Game game = GameManager.getGame();
        LivingEntity killer = e.getAttacker();
        if (game == null || game.getGameState() == GameState.LOBBY) return;
        p.setHealth(p.getMaxHealth()); //heal right away
        if(e.wasUnsafe())
            p.teleport(game.getGameWorld().getSpawnLocation());
        game.getRespawning().add(p.getUniqueId());
        Bukkit.getServer().getPluginManager().callEvent(new GameDeathEvent(game, p, killer, e.getDeathMessage(), e.getCausesMessage()));
    }

    @EventHandler(ignoreCancelled = true)
    public void onLobbyDeath(DeathApplyEvent e) {
        Game game = GameManager.getGame();
        Player player = e.getPlayer();

        if (game == null || game.getGameState() != GameState.STARTED) {
            // AKA if we are in a game lobby
            if(game != null ) {
                game.removePlayerLobbyPVPing(player);
                game.updateLobbyInventory(player);
            } else {    // AKA if we are in a general lobby
                ItemStackUtil.createItem(player.getInventory(), 388, 1, 2, "&a&lEnable Lobby PVP");
            }
            // For ALL lobbies, make the player invincible again
            DamageApplier.addInvincibleEntity(player);

            Bukkit.getScheduler().runTaskLater(Pluginizer.getSpigotPlugin(), () -> {
                deathAnimation(player.getLocation());
                StatusApplier.getOrNew(player).removeStatus(Status.values());
                player.setHealth(player.getMaxHealth());
                player.teleport(player.getWorld().getSpawnLocation());
            }, 1L);
        }
    }

    /**
     * GameDamageEvent(Game game, Player who, Player victim)
     */

    @EventHandler
    public void status(StatusApplyEvent e) {
        if(!(e.getEntity() instanceof Player)) return;
        Player player = (Player) e.getEntity();
        if(GameManager.isSpectating(player) || deadPeople.contains(player) || DamageApplier.getInvincibleEntities().contains(player))
            e.setCancelled(true);
    }

    @EventHandler
    public void velocity(PlayerVelocityEvent e) {
        Player player = e.getPlayer();
        if(GameManager.isSpectating(player) || deadPeople.contains(player) || DamageApplier.getInvincibleEntities().contains(player))
            e.setCancelled(true);
    }

    /**
     * GamePickUpEvent(Game game, Player player, Item item, int remaining)
     */
    @EventHandler
    public void onPickUp(PlayerPickupItemEvent e) {
        if(!EntityUtil.onGround(e.getItem()) || deadPeople.contains(e.getPlayer()) || DamageApplier.getInvincibleEntities().contains(e.getPlayer())) {
            e.setCancelled(true);
            return;
        }
        Player who = e.getPlayer();
        Game game = GameManager.getGame();
        if (game == null) return;
        e.setCancelled(true);
        org.bukkit.entity.Item item = e.getItem();
        ItemObjective itemObj = null;
        List<ItemObjective> objectives = game.getItemObjectives();
        if(objectives == null) return;
        for(ItemObjective itemObjective : objectives) {
            if(itemObjective.getItem().getEntityId() == item.getEntityId()) {
                itemObj = itemObjective;
            }
        }
        if(itemObj == null) return;
        int remaining = e.getRemaining(); // most likely not too important

        Bukkit.getServer().getPluginManager().callEvent(new GamePickUpEvent(game, who, itemObj, remaining));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void chat(AsyncPlayerChatEvent e) {
        if(e.isCancelled()) return;
        Player player = e.getPlayer();

        if(GameManager.getGame() != null && GameManager.getGame().isParticipating(player)) {
            e.setCancelled(true);
            Game game = GameManager.getGame();
            String color = "";
            if(!game.isSpectating(player)) color = game.getTeamEnum(player).getChatColor().toString();
            String name = player.getName();
            String replace = ChatColor.RESET + color + name;
            game.broadcast(e.getFormat().replace(name, replace));
        }
    }

    @EventHandler
    public void enableGeneralLobbyPVP(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        // Only run this code if there is no game going on; this will work even if engine is the only plugin present
        if(GameManager.getGame() != null || player.getItemInHand().getType().equals(Material.AIR)) { return;}

        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK ||
                event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)
                && (player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().getDisplayName().contains("Enable Lobby PVP"))) {

            SoundPlayer.sendSound(player, "random.pop", 1F, 63);
            DamageApplier.removeInvincibleEntity(player);
            applyGeneralPVPGear(player);
        }
    }

    private void applyGeneralPVPGear(Player player) {
        ItemStack sword = new ItemStack(Material.DIAMOND_SWORD);
        ItemMeta meta = sword.getItemMeta();
        meta.spigot().setUnbreakable(true);
        sword.setItemMeta(meta);
        player.setItemInHand(sword);

        Material[] armor = {Material.IRON_BOOTS, Material.IRON_LEGGINGS, Material.IRON_CHESTPLATE , Material.IRON_HELMET};
        ItemStack[] armors = new ItemStack[4];
        for(int i = 0; i < armor.length; i++){
            Material mat = armor[i];
            net.minecraft.server.v1_8_R3.ItemStack nmsStack = CraftItemStack.asNMSCopy(new ItemStack(mat));
            NBTTagCompound tag = new NBTTagCompound();
            tag.setBoolean("Unbreakable", true);
            nmsStack.setTag(tag);

            armors[i] = new ItemStack(CraftItemStack.asBukkitCopy(nmsStack));
        }
        player.getEquipment().setArmorContents(armors);
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
