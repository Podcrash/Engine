package com.podcrash.api.mc.mob;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public class MobData {
	private Entity creature;
	private int id;
	private boolean damage;
	private boolean frozen;
	private boolean burns;
	private boolean potionEffects;

	public MobData(Entity entity, int creatureId, boolean takesDamage, boolean isFrozen, boolean canBurn, boolean takesPotionEffects) {
		this.creature = entity;
		this.id = creatureId;
		this.damage = takesDamage;
		this.frozen = isFrozen;
		this.burns = canBurn;
		this.potionEffects = takesPotionEffects;
	}

	public Entity getEntity() {
		return this.creature;
	}

	public Integer getId() {
		return this.id;
	}

	public boolean takesPotionEffects() {
		return this.potionEffects;
	}

	public void togglePotionEffects() {
		if (this.potionEffects) {
			this.potionEffects = false;
		} else {
			this.potionEffects = true;
		}
	}

	public void toggleBurn() {
		if (this.burns) {
			this.burns = false;
		} else {
			this.burns = true;
		}
	}

	public boolean canBurn() {
		return this.burns;
	}

	public boolean isDamageable() {
		return this.damage;
	}

	public boolean isFrozen() {
		return this.frozen;
	}

	public EntityEquipment getArmor() {
		return ((LivingEntity) creature).getEquipment();
	}
	
	public void toggleDamage(boolean dmg) {
		this.damage = dmg;
	}
	
	public void setEntity(Entity en) {
		this.creature = en;
	}
	
	public void toggleFreeze(boolean froze) {
		this.frozen = froze;
	}
	
	public void setId(int newId) {
		this.id = newId;
	}

}
