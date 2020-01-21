package com.podcrash.api.mc.mob;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.server.v1_8_R1.NBTTagCompound;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;


public class MobManager {

	public static Map<Integer, MobData> mobs = new HashMap<Integer, MobData>();

	private static void addMobMap(int id, MobData creature) {
		mobs.put(id, creature);
	}

	private static void removeMobMap(int id) {
		mobs.remove(id);
	}

	public static MobData getMobData(int id) {
		MobData data = mobs.get(id);
		return data;
		
	}
	
	public static Entity getMob(int id) {
		MobData mobClass = mobs.get(id);
		Entity entity = mobClass.getEntity();
		
		return entity;

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
	 * @param Entity e | Entity to freeze
	 */

	public static void freezeEntity(Entity en) {
		net.minecraft.server.v1_8_R1.Entity nmsEn = ((CraftEntity) en).getHandle();
	  NBTTagCompound compound = new NBTTagCompound();
	  ((net.minecraft.server.v1_8_R1.Entity) nmsEn).c(compound);
	  compound.setByte("NoAI", (byte) 1);
	  ((net.minecraft.server.v1_8_R1.Entity) nmsEn).f(compound);
	  MobData mob = mobs.get(en.getEntityId());
	  mob.toggleFreeze(true);
	}

    /*
     * Duplicated the entity provided and spawns a duplicate Entity with Ai enabled.
     * @param int id | id of the Entity to unFreeze
     */

    public static void unFreezeEntity(int id) {
    	Entity creature = getMob(id);
      World world = creature.getWorld();
      Location creatureLocation = creature.getLocation();
      EntityEquipment equiped = ((LivingEntity) creature).getEquipment();
      EntityType newEntityType = creature.getType();
      creature.remove();

      Entity entity = (Entity) world.spawnEntity(creatureLocation, newEntityType);
      EntityEquipment entityEquiped = ((LivingEntity) entity).getEquipment();
      entityEquiped.setBoots(equiped.getBoots());
      entityEquiped.setLeggings(equiped.getLeggings());
      entityEquiped.setChestplate(equiped.getChestplate());
      entityEquiped.setHelmet(equiped.getHelmet());
      entity.setCustomName(creature.getCustomName());
      entity.setCustomNameVisible(true);
      ((LivingEntity) entity).setCanPickupItems(false);
      ((LivingEntity) entity).setRemoveWhenFarAway(false);

			MobData mob = new MobData(entity, entity.getEntityId(), false, false);
			addMobMap(entity.getEntityId(), mob);
      if (mob.getDamageable()) {
        mob.toggleDamage(true);
      }
      if (entity instanceof Zombie) {
        ((Zombie) entity).setBaby(false);
      }

      removeMobMap(id);
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
	 ==================
	 NOTE: This system uses event listeners to toggle mob damage!
	 To allow the damage toggle to work paste the following code into your event handler


	 @EventHandler
     public void onMobDamage(EntityDamageEvent e) {

    	Iterator<?> mobsIterator = mobs.entrySet().iterator();

		while (mobsIterator.hasNext()) {
			Map.Entry mapElement = (Map.Entry)mobsIterator.next();
			MobData mob = mobs.get(mapElement.getKey());
			if (!mob.getDamageable()) {
				e.setCancelled(true);
			} else {
				return;
			}
		}
     }

	 ==================
	/*


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
	 * @param EntityType mobType | Type of mob to spawn
	 * @param Location spawnLoc | Location within the world to spawn the entity
	 */

	public static Entity createMob(EntityType mobType, Location spawnLoc) {

		World world = spawnLoc.getWorld();

		Entity entity = (Entity) world.spawnEntity(spawnLoc, mobType);
		((LivingEntity) entity).setCanPickupItems(false);
		((LivingEntity) entity).setRemoveWhenFarAway(false);

		MobData mob = new MobData(entity, entity.getEntityId(), false, true);
		addMobMap(entity.getEntityId(), mob);

		freezeEntity(entity);
		damageOff(entity.getEntityId());
		if (entity instanceof Zombie) {
			((Zombie) entity).setBaby(false);
		}

		return entity;

	}

}

