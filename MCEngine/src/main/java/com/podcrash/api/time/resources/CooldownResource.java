package com.podcrash.api.time.resources;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.podcrash.api.plugin.PodcrashSpigot;
import com.podcrash.api.time.resources.TimeResource;
import com.podcrash.api.events.skill.SkillCooldownEvent;
import com.podcrash.api.events.skill.SkillRechargeEvent;
import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.KitPlayerManager;
import com.podcrash.api.kits.Skill;
import com.podcrash.api.sound.SoundPlayer;
import com.podcrash.api.util.TitleSender;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import com.podcrash.api.kits.skilltypes.Passive;
import com.podcrash.api.util.SkillTitleSender;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Class used to show cooldowns for skills
 * {@link com.podcrash.api.listeners.SkillMaintainListener#toCooldown(SkillCooldownEvent)}
 */
public class CooldownResource implements TimeResource {
    private ICooldown skill;
    private Player player;
    private boolean switcher;
    public CooldownResource(ICooldown skill) {
        this.skill = skill;
        this.player = skill.getPlayer();
    }

    public CooldownResource(SkillCooldownEvent event) {
        this((ICooldown) event.getSkill());
    }

    public ICooldown getSkill() {
        return skill;
    }

    @Override
    public void task() {
        KitPlayer cPlayer = KitPlayerManager.getInstance().getKitPlayer(player);

        if(cPlayer == null) return;
        Skill skill = cPlayer.getCurrentSkillInHand();
        if(skill != null && skill == this.skill && !(skill instanceof Passive)) {
            WrappedChatComponent component = SkillTitleSender.coolDownBar(this.skill);
            TitleSender.sendTitle(player, component);
            if(!switcher) switcher = true;
        } else if (switcher) {
            TitleSender.sendTitle(player, TitleSender.emptyTitle());
            switcher = false;
        }
    }

    @Override
    public boolean cancel() {
        return skill == null || !skill.onCooldown();
    }

    @Override
    public void cleanup() {
        if(!skill.hasCooldown()) return;
        SkillRechargeEvent recharge = new SkillRechargeEvent(skill, player);
        Bukkit.getPluginManager().callEvent(recharge);

        if(!recharge.isCancelled()) {
            player.sendMessage(skill.getCanUseMessage());
            SoundPlayer.sendSound(player, "note.harp", 0.2f, 160);
        }

        TitleSender.sendTitle(player, TitleSender.emptyTitle());
    }

    @Override
    public String toString(){
        return String.format("CooldownResource{%s:%s}", player.getName(), skill.getName());
    }
}
