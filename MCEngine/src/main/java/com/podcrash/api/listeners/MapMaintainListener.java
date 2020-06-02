package com.podcrash.api.listeners;

import com.packetwrapper.abstractpackets.WrapperPlayServerEntityStatus;
import com.podcrash.api.db.TableOrganizer;
import com.podcrash.api.db.tables.DataTableType;
import com.podcrash.api.db.tables.WorldLoader;
import com.podcrash.api.damage.Cause;
import com.podcrash.api.damage.DamageApplier;
import com.podcrash.api.damage.DamageQueue;
import com.podcrash.api.effect.status.Status;
import com.podcrash.api.events.*;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.game.GameState;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.PacketUtil;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.util.ReflectionUtil;
import net.minecraft.server.v1_8_R3.EntityLiving;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.painting.PaintingBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

/**
 * Prevent dumb things from happening
 */
public class MapMaintainListener extends ListenerBase {
    private final Map<String, Long> lastHit;
    private final Map<String, Boolean> checkCache;


    public MapMaintainListener(JavaPlugin plugin) {
        super(plugin);
        this.lastHit = new HashMap<>();
        this.checkCache = new HashMap<>();

    }

    @EventHandler
    public void verifyCloak(StatusRemoveEvent e) {
        Game game = GameManager.getGame();
        if (game == null) return;
        if (!(e.getLivingEntity() instanceof Player)) return;
        Player p = (Player) e.getLivingEntity();
        if (e.getStatus() == Status.CLOAK && game.isRespawning(p))
            e.setCancelled(true);

    }
    @EventHandler
    // stops crops from being destroyed if players jump on it
    public void onPlayerInteract(PlayerInteractEvent e) {
        // Physical means jump on it
        if (e.getAction() == Action.PHYSICAL) {
            Block block = e.getClickedBlock();
            if (block == null)
                return;
            // If the block is farmland (soil)
            if (block.getType() == Material.SOIL)
                e.setCancelled(true);
                //block.setTypeIdAndData(block.getType().getId(), block.getData(), true);
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) e.setCancelled(true);
    }

    @EventHandler
    public void onPaintingBreak(PaintingBreakEvent e){
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onWeather(WeatherChangeEvent event){
        if (evaluate(event.getWorld())) {
            event.getWorld().setWeatherDuration(0);
            event.setCancelled(true);
        }
    }
    @EventHandler
    public void onSpawn(CreatureSpawnEvent event) {
        if (evaluate(event.getEntity().getWorld())) {
            if (event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM))
                return;
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlace(BlockPlaceEvent e){
        if (!evaluate(e.getBlock().getWorld()))
            return;
        Game game = GameManager.getGame();
        if (game == null)
            return;
        if (game.getGameState() == GameState.STARTED)
            return;
        if (!e.getPlayer().hasPermission("invicta.map"))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBreak(BlockBreakEvent e) {
        if (!evaluate(e.getBlock().getWorld()))
            return;
        Game game = GameManager.getGame();
        if (game == null)
            return;
        if (game.getGameState() == GameState.STARTED)
            return;
        if (!e.getPlayer().hasPermission("invicta.map"))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDrop(PlayerDropItemEvent e) {
        if (isSpawnWorld(e.getPlayer().getWorld()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onFood(FoodLevelChangeEvent e) {
        if (isSpawnWorld(e.getEntity().getWorld()))
            e.setFoodLevel(20);
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onPickUp(PlayerPickupItemEvent event){
        String name = event.getItem().getCustomName();
        if (name != null && name.startsWith("RITB"))
            event.setCancelled(true);
    }

    @EventHandler
    public void despawn(ItemDespawnEvent e) {
        if (evaluate(e.getEntity().getWorld()))
            e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void damage(EntityDamageByEntityEvent e) {
        if (e.getDamager() instanceof Player)
            e.setCancelled(true);
    }
    /**
     * Remove the default damage tick which makes alters your velocity
     * Remove default death mechanics.
     */
    @EventHandler(priority = EventPriority.HIGH)
    public void statusDamage(EntityDamageEvent event) {
        if (event.isCancelled())
            return;
        //if the event was cancelled, or the entity isn't living, or if it's in the og world,
        //or if the cause is null or custom
        // then cancel it
        if ((!(event.getEntity() instanceof LivingEntity)) ||
                DamageApplier.getInvincibleEntities().contains(event.getEntity()) ||
                (event.getCause() == null || event.getCause() == EntityDamageEvent.DamageCause.CUSTOM)) {
            event.setCancelled(true);
            return;
        }
        LivingEntity p = (LivingEntity) event.getEntity();
        double damage = event.getDamage();
        //if the damage is 0, don't go through with the event
        if (damage <= 0) {
            event.setCancelled(true);
            return;
        }
        double afterHealth = p.getHealth() - damage;

        final List<EntityDamageEvent.DamageCause> poss = Arrays.asList(
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FALL,
                EntityDamageEvent.DamageCause.POISON,
                EntityDamageEvent.DamageCause.WITHER,
                EntityDamageEvent.DamageCause.VOID,
                EntityDamageEvent.DamageCause.SUFFOCATION,
                EntityDamageEvent.DamageCause.DROWNING,
                EntityDamageEvent.DamageCause.LAVA,
                EntityDamageEvent.DamageCause.CONTACT
                );
        //if the damage is one of the causes above
        //cancel it and set our own damage.
        //This is done because the types of damage above affects velocity
        if (!poss.contains(event.getCause()))
            return;
        event.setCancelled(true);
        Long last = lastHit.get(p.getName());
        long curr = System.currentTimeMillis();
        if (last != null) {
            long ticksMilles = 600L;
            if (curr - last <= ticksMilles)
                return;
        }
        lastHit.put(p.getName(), curr);
        Cause cause = Cause.findByEntityDamageCause(event.getCause());
        DamageEvent event2 = new DamageEvent(p, damage, cause);
        Bukkit.getPluginManager().callEvent(event2);
        if (event2.isCancelled())
            return;
        damage(p, damage);

        DamageQueue.artificialAddHistory(p, damage, cause);
        //if the player is about to die, cancel it
        //Then call our own death event.
        if (afterHealth <= 0) {
            if (p instanceof Player) {
                DamageQueue.artificialDie((Player) p);
                DropDeathLootEvent e = new DropDeathLootEvent(p);
                Bukkit.getPluginManager().callEvent(e);
                if (e.isCancelled())
                    return;
                PlayerInventory inventory = ((Player) p).getInventory();
                List<ItemStack> drops = new ArrayList<>(Arrays.asList(inventory.getContents()));
                drops.addAll(Arrays.asList(inventory.getArmorContents()));
                World world = p.getWorld();
                Location location = p.getLocation();
                Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
                    for (ItemStack stack : drops) {
                        if (stack == null || stack.getType() == Material.AIR) continue;
                        world.dropItemNaturally(location, stack);
                    }
                });

            }else {
                Bukkit.getPluginManager().callEvent(new DeadEntityEvent(p, cause));
                EntityLiving living = ((CraftLivingEntity) p).getHandle();
                ReflectionUtil.runMethod(living, living.getClass().getName(), "dropDeathLoot", Void.class, new Class[]{boolean.class, int.class}, true, 1);
                ReflectionUtil.runMethod(living, living.getClass().getName(),"dropEquipment", Void.class, new Class[] {boolean.class, int.class}, true, 1);

            }
        }
    }

    /**
     * Send animation packets as well as sound
     * (For some reason, the animation packet is also supposed to send sound as well
     * but it doesn't?)
     * @param victim The player that is taking the damage.
     * @param damage How much damage the player must take
     */
    private void damage(LivingEntity victim, double damage) {

        double health = victim.getHealth() - damage;
        victim.setHealth((health < 0) ? 0 : health);

        WrapperPlayServerEntityStatus packet = new WrapperPlayServerEntityStatus();
        packet.setEntityId(victim.getEntityId());
        packet.setEntityStatus(WrapperPlayServerEntityStatus.Status.ENTITY_HURT);

        PacketUtil.syncSend(packet, victim.getWorld().getPlayers());

        EntityLiving craftLiving = ((CraftLivingEntity) victim).getHandle();
        String hurtSound = ReflectionUtil.runMethod(craftLiving, craftLiving.getClass().getName(),"bo", String.class);
        SoundPlayer.sendSound(victim.getLocation(), hurtSound, 1, 75);

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void die(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.getDrops().clear();
        PodcrashSpigot.getInstance().getLogger().info("from MapMaintainListener#184: If you ever see this message, it's a bug");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void blockTransform(BlockFromToEvent e) {
        Block block = e.getBlock();
        Block to = e.getToBlock();
        boolean cancel = false;
        if(block.getType() == Material.ICE && to.getType() == Material.WATER)
            cancel = true;
        else if (block.getType() == Material.AIR && to.getType() == Material.VINE)
            cancel = true;
        if (cancel)
            e.setCancelled(true);
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void damage(DamageApplyEvent event) {
        Game game = GameManager.getGame();

        if (game == null || game.getGameState() != GameState.STARTED)
            if (event.getAttacker() instanceof Player && DamageApplier.getInvincibleEntities().contains(event.getAttacker()))
                event.setCancelled(true);
        //if (event.getVictim() instanceof Player) && isSpawnWorld(event.getVictim().getWorld()))
            //event.setCancelled(true);
    }

    /**
     *
     * @param world World to evaluate if restrictive or not
     * @return true if the world is restrictive, false if not
     */
    private boolean evaluate(World world) {
        WorldLoader loader = TableOrganizer.getTable(DataTableType.WORLDS);
        if (isSpawnWorld(world)) return true;
        Boolean contains;
        if ((contains = checkCache.get(world.getName())) != null) {
            return contains;
        }
        boolean containsDB = loader.listWorlds().contains(world.getName());
        checkCache.put(world.getName(), true);
        return containsDB;
    }

    private boolean isSpawnWorld(World world) {
        //if this is a world loaded by the spawn setter
        String name = PodcrashSpigot.getInstance().getWorldSetter().getCurrentWorldName();
        return world.getName().equalsIgnoreCase(name);
    }
}
