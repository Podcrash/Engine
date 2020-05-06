package com.podcrash.api.listeners;

import com.podcrash.api.effect.status.Status;
import com.podcrash.api.effect.status.StatusApplier;
import com.podcrash.api.events.skill.SkillCooldownEvent;
import com.podcrash.api.events.skill.SkillInteractEvent;
import com.podcrash.api.events.skill.SkillUseEvent;
import com.podcrash.api.game.Game;
import com.podcrash.api.game.GameManager;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.time.TimeHandler;
import com.podcrash.api.time.resources.CooldownResource;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.java.JavaPlugin;

public class SkillMaintainListener extends ListenerBase {
    public SkillMaintainListener(JavaPlugin plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void toCooldown(SkillCooldownEvent event){
        if(event.getSkill() instanceof ICooldown){
            TimeHandler.repeatedTime(1, 0, new CooldownResource(event));
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void useSkill(SkillUseEvent e){
        if(StatusApplier.getOrNew(e.getPlayer()).isInept()) {
            e.setCancelled(true);
            return;
        }
        KitPlayer kitPlayer = e.getSkill().getChampionsPlayer();
        if(kitPlayer.isSilenced()){
            e.setCancelled(true);
            e.getPlayer().sendMessage(String.format("%sCondition> %sYou are silenced for %s%.2f %sseconds",
                    ChatColor.BLUE,
                    ChatColor.GRAY,
                    ChatColor.GREEN,
                    StatusApplier.getOrNew(e.getPlayer()).getRemainingDuration(Status.SILENCE) / 1000L,
                    ChatColor.GRAY));
        }

       // if(!e.isCancelled()) e.getPlayer().sendMessage(e.getSkill().getUsedMessage());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void useInteractSkill(SkillInteractEvent e) {
        Player user = e.getPlayer();
        LivingEntity interacto = e.getInteracted();
        if(!(interacto instanceof Player)) return;
        Player interactor = (Player) interacto;

        Game game = GameManager.getGame();
        if(game == null) return;

        if(game.isRespawning(user) || game.isRespawning(interactor))
            e.setCancelled(true);
        else if (game.isSpectating(interactor))
            e.setCancelled(true);
    }
}
