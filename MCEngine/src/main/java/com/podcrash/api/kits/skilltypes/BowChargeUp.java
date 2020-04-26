package com.podcrash.api.kits.skilltypes;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.podcrash.api.damage.Cause;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.TimeResource;
import com.podcrash.api.util.SkillTitleSender;
import com.podcrash.api.util.TitleSender;
import com.podcrash.api.events.skill.SkillUseEvent;
import com.podcrash.api.kits.Skill;
import com.podcrash.api.kits.enums.ItemType;
import net.minecraft.server.v1_8_R3.EntityPlayer;
import net.minecraft.server.v1_8_R3.EnumAnimation;
import net.minecraft.server.v1_8_R3.ItemStack;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.inventory.CraftItemStack;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Map;

public abstract class BowChargeUp extends Skill implements TimeResource {
    private Map<String, Long> times = new HashMap<>();
    protected boolean isUsing = false;
    private float power = 0;
    private HashMap<Arrow, Float> charges;

    @Override
    public ItemType getItemType() {
        return ItemType.BOW;
    }

    public abstract float getRate();
    public BowChargeUp() {
        super();
        charges = new HashMap<>();

    }

    @EventHandler(priority = EventPriority.HIGH)
    public void block(PlayerInteractEvent e){
        if(e.getPlayer() != this.getPlayer()) return;
        if(!rightClickCheck(e.getAction()) || !isHolding()) return;
        if(isInWater()) return;

        times.put(getPlayer().getName(), System.currentTimeMillis());
        unregister();
        TimeHandler.repeatedTime(1, 0, this);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void shoot(EntityShootBowEvent e){
        if(e.getEntity() != getPlayer() || !(e.getProjectile() instanceof Arrow)) return;
        Arrow a = (Arrow) e.getProjectile();
        charges.put(a, getCharge());

        SkillUseEvent useEvent = new SkillUseEvent(this);
        Bukkit.getPluginManager().callEvent(useEvent);
        doShoot(a, charges.get(a));
        resetCharge();
    }


    public abstract void doShoot(Arrow arrow, float charge);

    @EventHandler(priority = EventPriority.HIGHEST)
    public void shoot(DamageApplyEvent e){
        if(e.getAttacker() == getPlayer() && charges.containsKey(e.getArrow()) && e.getCause() == Cause.PROJECTILE){
            shootPlayer(e.getArrow(), charges.get(e.getArrow()), e);
        }
    }

    public abstract void shootPlayer(Arrow arrow, float charge, DamageApplyEvent e);


    @EventHandler(priority = EventPriority.HIGHEST)
    public void ground(ProjectileHitEvent e){
        Projectile proj = e.getEntity();
        if(proj instanceof Arrow){
            Arrow arrow = (Arrow) e.getEntity();
            if(proj.getShooter() == getPlayer() && charges.containsKey(arrow)){
                shootGround(arrow, charges.get(arrow));
            }
        }
    }
    public abstract void shootGround(Arrow arrow, float charge);

    @Override
    public void task() {
        if(System.currentTimeMillis() - times.get(getPlayer().getName()) >= 1500L) {
            charge();
            isUsing = true;
            WrappedChatComponent progress = SkillTitleSender.chargeUpProgressBar(instance, getCharge());
            if (getCharge() < 1f)
                SoundPlayer.sendSound(getPlayer(), "note.harp", 0.75f, (int) (130 * getCharge()));
            TitleSender.sendTitle(getPlayer(), progress);
        }
    }

    @Override
    public boolean cancel() {
        boolean truth = true;
        if(getPlayer().getItemInHand() != null) {
            EntityPlayer ep = ((CraftPlayer) getPlayer()).getHandle();
            ItemStack itemStack = CraftItemStack.asNMSCopy(getPlayer().getItemInHand());
            if(itemStack == null) return true;
            truth = !(ep.bS() && itemStack.getItem().e(itemStack) == EnumAnimation.BOW);
        }
        return truth;
    }

    @Override
    public void cleanup() {
        TimeHandler.unregister(this);
        times.remove(getPlayer().getName());
        isUsing = false;
        this.resetCharge();
    }


    protected void charge(){
        power += getRate();
        power = (power >= 1f) ? 1f : power;
    }
    protected void charge(double boost){
        power += boost;
    }

    protected float getCharge() {
        return power;
    }

    protected void resetCharge(){
        power = 0;
    }
}
