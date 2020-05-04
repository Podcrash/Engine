package com.podcrash.api.kits.skilltypes;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import com.podcrash.api.util.SkillTitleSender;
import com.podcrash.api.util.TitleSender;
import com.podcrash.api.events.skill.SkillRechargeEvent;
import com.podcrash.api.events.skill.SkillUseEvent;
import com.podcrash.api.kits.Skill;
import com.podcrash.api.kits.enums.ItemType;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerInteractEvent;

/*
    This is with swords most likely
 */
public abstract class ChargeUp extends Skill implements TimeResource, ICooldown {
    protected boolean isUsing = false;
    private double power = 0;

    @Override
    public ItemType getItemType() {
        return ItemType.SWORD;
    }

    public abstract float getRate();
    public ChargeUp() {
        super();
    }

    @EventHandler
    public void recharge(SkillRechargeEvent e) {
        if(e.getSkillName().equalsIgnoreCase(this.getName()) && getPlayer().isBlocking()) {
            if(isInWater()) {
                getPlayer().sendMessage(getWaterMessage());
                return;
            }
            if(!onCooldown()) {
                SkillUseEvent useEvent = new SkillUseEvent(this);
                Bukkit.getPluginManager().callEvent(useEvent);
                if(useEvent.isCancelled()) return;
                TimeHandler.repeatedTime(1, 0, this);
            }
        }
    }

    @EventHandler(
            priority = EventPriority.HIGH
    )
    public void block(PlayerInteractEvent e){
        if(e.getPlayer() == this.getPlayer()){
            if(rightClickCheck(e.getAction()) && isHolding()){
                if(isInWater()) {
                    getPlayer().sendMessage(getWaterMessage());
                    return;
                }
                if(!onCooldown()) {
                    SkillUseEvent useEvent = new SkillUseEvent(this);
                    Bukkit.getPluginManager().callEvent(useEvent);
                    if(useEvent.isCancelled()) return;
                    TimeHandler.repeatedTime(1, 0, this);
                }
            }
        }
    }

    public abstract void release();
    @Override
    public void task() {
        charge();
        isUsing = true;
        WrappedChatComponent progress = SkillTitleSender.chargeUpProgressBar(this, this.getCharge());
        if(getCharge() < 1f) SoundPlayer.sendSound(this.getPlayer(), "note.harp", 0.75f, (int)(130 * getCharge()) );
        TitleSender.sendTitle(this.getPlayer(), progress);
    }

    @Override
    public boolean cancel() {
        return !getPlayer().isBlocking();
    }

    @Override
    public void cleanup() {
        TimeHandler.unregister(this);
        if(power >= 1D) power = 1D;
        Bukkit.getScheduler().runTask(PodcrashSpigot.getInstance(), () -> {
            release();
            resetCharge();
        });
        setLastUsed(System.currentTimeMillis());
        isUsing = false;
    }

    protected void charge(){
        power += getRate();
    }
    protected void charge(double boost){
        power += boost;
    }

    protected double getCharge() {
        return power;
    }

    protected void resetCharge(){
        power = 0;
    }
}
