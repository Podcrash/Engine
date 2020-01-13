package com.podcrash.api.mc.item;

import com.podcrash.api.mc.callback.InterceptCallback;
import com.podcrash.api.mc.callback.sources.DelayItemIntercept;
import com.podcrash.api.mc.callback.sources.ItemIntercept;
import net.minecraft.server.v1_8_R3.EntityItem;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftItem;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * A utility class based around spawning items as of right now.
 * Uses the itemintercept lambda + CallbackAction to act on intercepted entities
 */
public class ItemManipulationManager {
    private static final char[] subset = "0123456789abcdefghijklmnopqrstuvwxyz".toCharArray();
    private final Random random = new Random();

    private ItemManipulationManager() {
        throw new RuntimeException("Please use static methods!");
    }

    public static Item spawnItem(Material material, Location location) {
        return spawnItem(material, location, null);
    }

    private static Item spawnItem(Material material, Location location, Vector vector) {
        World world = location.getWorld();
        CraftWorld craftWorld = (CraftWorld) world;
        EntityItem entity = new EntityItem(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(new ItemStack(material)));
        entity.pickupDelay = 5;
        craftWorld.getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if(vector != null) {
            entity.motX = vector.getX();
            entity.motY = vector.getY();
            entity.motZ = vector.getZ();
        }

        return new CraftItem(craftWorld.getHandle().getServer(), entity);
    }

    public static Item spawnItem(ItemStack itemStack, Location location, Vector vector) {
        World world = location.getWorld();
        CraftWorld craftWorld = (CraftWorld) world;
        //rename
        ItemMeta meta = itemStack.getItemMeta();
        meta.setDisplayName(itemStack.getType().name() + System.currentTimeMillis());
        itemStack.setItemMeta(meta);

        //spawn
        EntityItem entity = new EntityItem(craftWorld.getHandle(), location.getX(), location.getY(), location.getZ(), CraftItemStack.asNMSCopy(itemStack));
        entity.pickupDelay = 5;
        craftWorld.getHandle().addEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        if(vector != null) {
            entity.motX = vector.getX();
            entity.motY = vector.getY();
            entity.motZ = vector.getZ();
        }

        return new CraftItem(craftWorld.getHandle().getServer(), entity);
    }

    public static Item regular(Material material, Location location, Vector vector){
        return spawnItem(material,  location, vector);
    }

    public static Item intercept(Player owner, Material material, Location location, Vector vector, InterceptCallback callback) {
        Item item = spawnItem(material, location, vector);
        ItemIntercept intercept = new ItemIntercept(owner, item);
        if(callback != null) intercept.then(() -> callback.dorun(item, intercept.getIntercepted()));
        intercept.run();
        return item;
    }

    public static Item interceptWithCooldown(Player owner, Material material, Location location, Vector vector, float delay, InterceptCallback callback) {
        Item item = spawnItem(material, location, vector);
        ItemIntercept intercept = new DelayItemIntercept(owner, item, delay);
        if(callback != null) intercept.then(() -> callback.dorun(item, intercept.getIntercepted()));
        intercept.run();
        return item;
    }

    private static String getRandomChar(){
        return String.valueOf(subset[(int) (Math.random() * subset.length)]);
    }
}
