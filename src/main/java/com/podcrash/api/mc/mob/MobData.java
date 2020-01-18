package import com.podcrash.api.mc.mob;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public class MobData {
	public static Entity creature;
	public static Integer id;
	public static boolean damage;
	public static boolean frozen;

	public MobData(Entity entity, Integer creatureId, boolean takesDamage, boolean isFrozen) {
		this.creature = entity;
		this.id = creatureId;
		this.damage = takesDamage;
		this.frozen = isFrozen;
	}

	public Entity getEntity() {
		return this.creature;
	}

	public Integer getId() {
		return this.id;
	}


	public boolean getDamageable() {
		return this.damage;
	}

	public boolean getFrozen() {
		return this.frozen;
	}

	public EntityEquipment getArmor() {
		return ((LivingEntity) creature).getEquipment();
	}

}
