package com.podcrash.api.mc.mob;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.minecraft.server.v1_8_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftEntity;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Zombie;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;


public class MobManager {

	public static String chatPrefix = ChatColor.GRAY + "[" + ChatColor.RED + "CustomMobs" + ChatColor.GRAY + "]" + ChatColor.YELLOW;
	public static HashMap<Integer, Entity> mobs = new HashMap<Integer, Entity>();
	public static HashMap<Integer, String> mobNames = new HashMap<Integer, String>();
	public static HashMap<Entity, Location> mobLocations = new HashMap<Entity, Location>();
	public static HashMap<Integer, Entity> denyDamage = new HashMap<Integer, Entity>();

	/*
	================================
	Please refrain from calling any methods between the two sets of '=' signs.

	Thank You
	================================
	*/

	public static void addMob(Integer id, Entity creature, Location location) {
		mobs.put(id, creature);
		mobLocations.put(creature, location);
	}

	public static void removeMob(Integer id, Entity creature) {
		mobs.remove(id);
		mobLocations.remove(creature);
	}

	public static Entity getMob(Integer id) {
		Entity mobData = mobs.get(id);

		return mobData;
	}

	public static Location getLocation(Entity creature) {

		Location mobLocation = mobLocations.get(creature);

		return mobLocation;
	}

	public static void setMobDamageable(Integer id) {
		denyDamage.remove(id);
	}

	public static void setMobUndamageable(Integer id, Entity en) {
		denyDamage.put(id, en);
	}

	public static void updateName(Integer id, String newName) {
		mobNames.remove(id);

		mobNames.put(id, newName);
	}

	/*
	================================
		Call methods below this line
	================================
	*/


	/*
	 * Create an Entity
	 * @param EntityType mobType | Type of mob to spawn
	 * @param String mobName | Name of the entity
	 * @param World world | World to spawn the entity in
	 * @param Location spawnLoc | Location within the world to spawn the entity
	 */

	 public static void createMob(EntityType mobType, String mobName, World world, Location spawnLoc) {

		 Entity entity = (Entity) world.spawnEntity(spawnLoc, mobType);
		 entity.setCustomName(mobName);
		 entity.setCustomNameVisible(true);
		 ((LivingEntity) entity).setCanPickupItems(false);
		 ((LivingEntity) entity).setRemoveWhenFarAway(false);
		 freezeEntity(entity);
		 addMob(entity.getEntityId(), entity, spawnLoc);
		 mobNames.put(entity.getEntityId(), mobName);
		 setMobUndamageable(entity.getEntityId(), entity);
		 if (entity instanceof Zombie) {
			 ((Zombie) entity).setBaby(false);
		 }
	 }

	 /*
	 	* Delete Entity
		* Integer id | Entity to be deleted
	  */

	public static void deleteMob(Integer id) {
		Entity en = getMob(id);

		removeMob(id, en);
		mobNames.remove(id);
		setMobDamageable(id);
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
	}

    /*
     * Duplicated the entity provided and spawns a duplicate Entity with Ai enabled.
     * @param Integer id | id of the Entity to unFreeze
     */

    public static void unFreezeEntity(Integer id) {
      Entity creature = getMob(id);
      World world = creature.getWorld();
      Location creatureLocation = creature.getLocation();
      EntityEquipment equiped = ((LivingEntity) creature).getEquipment();
      EntityType newEntityType = creature.getType();

      removeMob(id, creature);
      mobNames.remove(id);
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
      mobNames.put(entity.getEntityId(), creature.getCustomName());
      addMob(entity.getEntityId(), entity, creatureLocation);
      if (denyDamage.containsValue(creature)) {
        setMobUndamageable(entity.getEntityId(), entity);
      }
      if (entity instanceof Zombie) {
        ((Zombie) entity).setBaby(false);
      }
    }

  /*
   * Equip entity with a material provided by name. Example: diamond_sword
   * @param Integer id | id of the entity you want to equip
   * @param String useMaterial | the material you want the entity to hold.
   */

  public static void equipEntity(Integer id, String useMaterial) {
    Entity creature = getMob(id);
    EntityEquipment equiped = ((LivingEntity) creature).getEquipment();

    if (useMaterial.toLowerCase() == "none") {
      equiped.setItemInHand(null);
    } else {
      Material addMaterial = Material.valueOf(useMaterial.toUpperCase());
      ItemStack item = new ItemStack(addMaterial);
      equiped.setItemInHand(item);
    }
  }

  /*
   * Equip an entity with armor
   * @param Integer id | id of the entity to equip with armor
   * @param String armor | armor type to equip. If "none" removes armor
    */

  public static void armorEntity(Integer id, String armor) {
    Entity creature = getMob(id);
    EntityEquipment equiped = ((LivingEntity) creature).getEquipment();

    switch (armor) {

    case "none":
      equiped.setArmorContents(null);
      break;

    case "diamond":
      ItemStack diamond_helmet = new ItemStack(Material.DIAMOND_HELMET);
      ItemStack diamond_chestplate = new ItemStack(Material.DIAMOND_CHESTPLATE);
      ItemStack diamond_leggings = new ItemStack(Material.DIAMOND_LEGGINGS);
      ItemStack diamond_boots = new ItemStack(Material.DIAMOND_BOOTS);

      equiped.setHelmet(diamond_helmet);
      equiped.setChestplate(diamond_chestplate);
      equiped.setLeggings(diamond_leggings);
      equiped.setBoots(diamond_boots);

      break;

    case "iron":
      ItemStack iron_helmet = new ItemStack(Material.IRON_HELMET);
      ItemStack iron_chestplate = new ItemStack(Material.IRON_CHESTPLATE);
      ItemStack iron_leggings = new ItemStack(Material.IRON_LEGGINGS);
      ItemStack iron_boots = new ItemStack(Material.IRON_BOOTS);

      equiped.setHelmet(iron_helmet);
      equiped.setChestplate(iron_chestplate);
      equiped.setLeggings(iron_leggings);
      equiped.setBoots(iron_boots);

      break;

    case "chain":
      ItemStack chain_helmet = new ItemStack(Material.CHAINMAIL_HELMET);
      ItemStack chain_chestplate = new ItemStack(Material.CHAINMAIL_CHESTPLATE);
      ItemStack chain_leggings = new ItemStack(Material.CHAINMAIL_LEGGINGS);
      ItemStack chain_boots = new ItemStack(Material.CHAINMAIL_BOOTS);

      equiped.setHelmet(chain_helmet);
      equiped.setChestplate(chain_chestplate);
      equiped.setLeggings(chain_leggings);
      equiped.setBoots(chain_boots);

      break;

    case "gold":
      ItemStack gold_helmet = new ItemStack(Material.GOLD_HELMET);
      ItemStack gold_chestplate = new ItemStack(Material.GOLD_CHESTPLATE);
      ItemStack gold_leggings = new ItemStack(Material.GOLD_LEGGINGS);
      ItemStack gold_boots = new ItemStack(Material.GOLD_BOOTS);

      equiped.setHelmet(gold_helmet);
      equiped.setChestplate(gold_chestplate);
      equiped.setLeggings(gold_leggings);
      equiped.setBoots(gold_boots);

      break;

    case "leather":
      ItemStack leather_helmet = new ItemStack(Material.LEATHER_HELMET);
      ItemStack leather_chestplate = new ItemStack(Material.LEATHER_CHESTPLATE);
      ItemStack leather_leggings = new ItemStack(Material.LEATHER_LEGGINGS);
      ItemStack leather_boots = new ItemStack(Material.LEATHER_BOOTS);

      equiped.setHelmet(leather_helmet);
      equiped.setChestplate(leather_chestplate);
      equiped.setLeggings(leather_leggings);
      equiped.setBoots(leather_boots);

      break;
    }
  }

	/*
	 * Toggle name on for Entity
	 * @param Integer id | Id of the Entity
	 */

	public static void nameOn(Integer id) {
		Entity creature = getMob(id);

		creature.setCustomName(mobNames.get(id));
		creature.setCustomNameVisible(true);
	}

	 /*
	  * Toggle name off for Entity
	  * @param Integer id | Id of the Entity
	  */

	 public static void nameOff(Integer id) {
		 Entity creature = getMob(id);

		 creature.setCustomName("");
		 creature.setCustomNameVisible(false);
	 }

	 public static void updateEntityName(Integer id, String newName) {
		 Entity creature = getMob(id);

		 updateName(id, newName);
		 creature.setCustomName(newName);
	 }

	 /*
	 ==================
	 NOTE: This system uses event listeners to toggle mob damage!
	 To allow the damage toggle to work paste the following code into your event handler


	 @EventHandler
    public void onMobDamage(EntityDamageEvent e) {
    	Entity en = e.getEntity();

    	if (MobManager.denyDamage.containsValue(en)) {
        e.setCancelled(true);
    	} else {
        return;
    	}
    }

	 ==================
	 */


	 /*
	  * Make a mob damageable
		* @param Integer id | id of the entity to allow damage on
	  */

	 public static void damageOn(Integer id) {
		 setMobDamageable(id);
	 }

	 /*
	  * Make a mob undamageable
	  * @param Integer id | id of the entity to not allow damage on
	  * @param Entity en | Entity to make undamageable
	  */

	 public static void damageOff(Integer id, Entity en) {
		 setMobUndamageable(id, en);
	 }

	 /*
	  * Returns all the entities in a nice list
	  */

	 public static String getEntities() {
		 String message = "\n";

		 Iterator<?> mobsIterator = mobs.entrySet().iterator();

		 while (mobsIterator.hasNext()) {
			 Map.Entry mapElement = (Map.Entry)mobsIterator.next();
			 message = message + mapElement.getKey() + " " + mapElement.getValue() + " | " + mobNames.get(mapElement.getKey()) + "\n";
		 }

		 return message;
	 }



}
