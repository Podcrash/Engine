package import com.podcrash.api.mc.mob;

import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;

public class MobData {
	private Entity creature;
	private int id;
	private boolean damage;
	private boolean frozen;

	public MobData(Entity entity, int creatureId, boolean takesDamage, boolean isFrozen) {
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

