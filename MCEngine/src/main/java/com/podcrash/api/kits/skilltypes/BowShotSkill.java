package com.podcrash.api.kits.skilltypes;

import com.podcrash.api.damage.Cause;
import com.podcrash.api.events.DamageApplyEvent;
import com.podcrash.api.kits.enums.ItemType;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEvent;

import java.util.HashMap;

/*
    For arrow hits
 */
public abstract class BowShotSkill extends Instant implements ICooldown {
    protected boolean isPrepared;
    private HashMap<Arrow, Float> arrowForceMap;

    @Override
    public ItemType getItemType() {
        return ItemType.BOW;
    }

    public BowShotSkill() {
        super();
        arrowForceMap = new HashMap<>();
    }
    /*
    Preparing an arrow (left click)
     */
    @Override
    protected void doSkill(PlayerEvent event, Action action) {
        if (rightClickCheck(action)) return;
        if(onCooldown()) return;
        isPrepared = true;// sound goes here
        SoundPlayer.sendSound(getPlayer().getLocation(), "mob.blaze.breathe", 0.75f, 200);
        this.getPlayer().sendMessage(String.format("%s%s> %s%s %sprepared.", ChatColor.BLUE, getChampionsPlayer().getName(), ChatColor.GREEN, this.getName(), ChatColor.GRAY ));

        this.setLastUsed(System.currentTimeMillis());
        //arrowForceMap.keySet().removeIf(arr -> (arr.isDead() || !arr.isValid()));
    }

    /*
    Shooting the arrow
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void shootBow(EntityShootBowEvent event){
        if(!isPrepared) return;
        if(event.isCancelled()) return;
        if(event.getEntity() instanceof Player && event.getProjectile() instanceof Arrow){
            Player player = (Player) event.getEntity();
            if(player == getPlayer()){
                isPrepared = false;
                Arrow arrow = (Arrow) event.getProjectile();
                float currentForce = event.getForce();
                arrowForceMap.put(arrow, currentForce);
                shotArrow(arrow, arrowForceMap.get(arrow));
                /*
                getEntity().sendMessage(currentForce + "< -- currentforce");
                getEntity().sendMessage(arrowForceMap.get(arrow) + "<-- me.raindance.champions.map");
                getEntity().sendMessage(arrowForceMap.toString());
                */
                StringBuilder builder = new StringBuilder();
                builder.append(ChatColor.BLUE);
                builder.append("Bow> ");
                builder.append(ChatColor.GRAY);
                builder.append(" You shot ");
                builder.append(ChatColor.BOLD);
                builder.append(ChatColor.YELLOW);
                builder.append(getName());
                builder.append(ChatColor.GRAY);
                builder.append(".");
            }
        }
    }

    protected abstract void shotArrow(Arrow arrow, float force);


    /*
                Shooting a player
             */
    @EventHandler(priority = EventPriority.NORMAL)
    public void arrowShotPlayer(DamageApplyEvent event){
        if(event.isCancelled() || event.getCause() != Cause.PROJECTILE) return;
        LivingEntity livingEntity = event.getAttacker();
        Arrow proj = event.getArrow();
        if(livingEntity != getPlayer()) return;
        if(!arrowForceMap.containsKey(proj)) return;
        getPlayer().sendMessage(getUsedMessage(event.getVictim()).replace("used", "shot"));
        shotEntity(event, (Player) proj.getShooter(), event.getVictim(), proj, arrowForceMap.get(proj));

        //proj.remove();
    }
    protected abstract void shotEntity(DamageApplyEvent event, Player shooter, LivingEntity victim, Arrow arrow, float force);

    /*
    Shooting the ground
     */
    @EventHandler(priority = EventPriority.MONITOR)
    public void arrowShotGround(ProjectileHitEvent event){
        if(event.getEntity() instanceof Arrow ){

            Arrow arr = (Arrow) event.getEntity();
            if(arr.getShooter() instanceof Player){
                Player shooter = (Player) arr.getShooter();
                if(shooter == getPlayer() && arrowForceMap.containsKey(arr)){
                    shotGround(shooter, event.getEntity().getLocation(), arr, arrowForceMap.get(arr));
                    //arrowForceMap.remove(arr);
                }

            }
        }
    }

    protected abstract void shotGround(Player shooter, Location location, Arrow arrow, float force);

}
