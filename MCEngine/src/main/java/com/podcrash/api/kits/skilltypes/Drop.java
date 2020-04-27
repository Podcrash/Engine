package com.podcrash.api.kits.skilltypes;

import com.podcrash.api.events.skill.SkillUseEvent;
import com.podcrash.api.kits.Skill;
import com.podcrash.api.kits.enums.ItemType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;

import javax.annotation.Nonnull;

public abstract class Drop extends Skill {

    @EventHandler
    public final void dropSkill(PlayerDropItemEvent e) {
        if(e.getPlayer() != getPlayer()) return;
        if(!isHolding(e.getItemDrop().getItemStack())) return;
        if(isInWater()) {
            e.getPlayer().sendMessage(getWaterMessage());
            return;
        }
        //check for cooldown
        if(this instanceof ICooldown) {
            ICooldown cooldownSkill = (ICooldown) this;
            if(cooldownSkill.onCooldown()) {
                getPlayer().sendMessage(cooldownSkill.getCooldownMessage());
                return;
            }
        }

        SkillUseEvent useEvent = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(useEvent);
        if (useEvent.isCancelled()) return;
        drop(e);
        e.setCancelled(true);
    }

    /**
     * true if the skill is used.
     * @param e
     * @return
     */
    public abstract boolean drop(PlayerDropItemEvent e);

    protected boolean isHolding(@Nonnull ItemStack dropped) {
        ItemType[] weapons = ItemType.details();

        String name = dropped.getType().name().toUpperCase();

        if(getItemType() != ItemType.NULL) {
            return name.contains(getItemType().getName());
        }
        for(ItemType w : weapons) {
            if (w.getName() == null) continue;
            if(name.contains(w.getName())) return true;
        }
        return false;
    }
}
