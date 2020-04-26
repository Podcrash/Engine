package com.podcrash.api.mob;

import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;


public class MobManager {
    public static Map<Integer, MobData> mobs = new HashMap<>();

    private static void addMobMap(int id, MobData creature) {
        mobs.put(id, creature);
    }

    private static void removeMobMap(int id) {
        mobs.remove(id);
    }

    public static MobData getMobData(int id) {
        return mobs.get(id);
    }
    
    public static Entity getMob(int id) {
        return mobs.get(id).getEntity();
    }


    private static void setMobDamageable(int id) {
        MobData mobData = mobs.get(id);
        mobData.toggleDamage(true);
    }

    private static void setMobUndamageable(int id) {
        MobData mobData = mobs.get(id);
        mobData.toggleDamage(false);
    }

    /*
     * Delete Entity
     * int id | Entity to be deleted
     */

    public static void deleteMob(int id) {
        Entity en = getMob(id);

        removeMobMap(id);
        en.remove();
    }

    /*
   * Removes the ai from an Entity
     * @param int id | Id of entity to freeze
     */
    public static void freezeEntity(Entity en) {
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) en).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 1);
        nmsEn.f(compound);
    }

    /*
     * Duplicated the entity provided and spawns a duplicate Entity with Ai enabled.
     * @param int id | id of the Entity to unFreeze
     */

    public static void unFreezeEntity(int id) {
        
        Entity en = getMob(id);
        
        net.minecraft.server.v1_8_R3.Entity nmsEn = ((CraftEntity) en).getHandle();
        NBTTagCompound compound = new NBTTagCompound();
        nmsEn.c(compound);
        compound.setByte("NoAI", (byte) 0);
        nmsEn.f(compound);
        MobData mob = mobs.get(id);
        mob.toggleFreeze(false);
    }

    /*
     * Equip entity with a material provided by name. Example: diamond_sword
     * @param int id | id of the entity you want to equip
     * @param String useMaterial | the material you want the entity to hold.
     */

    public static void equipEntity(int id, ItemStack item) {
        Entity creature = getMob(id);
        EntityEquipment equiped = ((LivingEntity) creature).getEquipment();
        equiped.setItemInHand(item);
    }

      /*
        * Equip an entity with armor
        * @param int id | id of the entity to equip with armor
     * @param ItemStack helmet | Helmet to place on entity
     * @param ItemStack chestplate | Chestplate to place on entity
     * @param ItemStack leggings | Leggings to place on entity
     * @param ItemStack boots | Boots to place on entity
     */

    public static void armorEntity(int id, ItemStack helmet, ItemStack chestplate, ItemStack leggings, ItemStack boots) {
        Entity creature = getMob(id);
        EntityEquipment equiped = ((LivingEntity) creature).getEquipment();

        equiped.setHelmet(helmet);
        equiped.setChestplate(chestplate);
        equiped.setLeggings(leggings);
        equiped.setBoots(boots);
    }


     /*
      * Make a mob damageable
        * @param int id | id of the entity to allow damage on
      */

     public static void damageOn(int id) {
         setMobDamageable(id);
     }

     /*
      * Make a mob undamageable
      * @param int id | id of the entity to not allow damage on
      */

     public static void damageOff(int id) {
         setMobUndamageable(id);
     }

    /*
     * Create an Entity
     * Defaults: Does not take damage, is frozen, will NOT burn in day light or fire, and will NOT be effected by potions.
     * @param EntityType mobType | Type of mob to spawn
     * @param Location spawnLoc | Location within the world to spawn the entity
     */

    public static Entity createMob(EntityType mobType, Location spawnLoc) {

        World world = spawnLoc.getWorld();

        Entity entity = world.spawnEntity(spawnLoc, mobType);
        ((LivingEntity) entity).setCanPickupItems(false);
        ((LivingEntity) entity).setRemoveWhenFarAway(false);

        MobData mob = new MobData(entity, entity.getEntityId(), false, true, false, false);
        addMobMap(entity.getEntityId(), mob);

        freezeEntity(entity);
        damageOff(entity.getEntityId());
        if (entity instanceof Zombie)
            ((Zombie) entity).setBaby(false);

        return entity;

    }

}
