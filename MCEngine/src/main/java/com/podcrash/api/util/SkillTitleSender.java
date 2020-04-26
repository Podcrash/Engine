package com.podcrash.api.util;

import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.podcrash.api.kits.Skill;
import com.podcrash.api.kits.iskilltypes.action.ICooldown;
import net.jafama.FastMath;
import org.bukkit.ChatColor;

public final class SkillTitleSender {

    /*
        Progress must be from 0 to 1
     */
    public static WrappedChatComponent chargeUpProgressBar(Skill skill, double progress) {
        if(progress > 1) progress = 1;
        String bar = TitleSender.generateBars("||");
        int size = bar.length() - 1;
        int currentProgress = (int) (size * progress);
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        /*
        String builder = String.format("%s %d:%s%s %s %s%s %d%%",
                skill.getName(), skill.getLevel(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, (int) (100f * progress));
        */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(skill.getName());
        stringBuilder.append(": ");
        stringBuilder.append(ChatColor.BOLD);
        stringBuilder.append(ChatColor.GREEN);
        stringBuilder.append(sprogress);
        stringBuilder.append(ChatColor.RESET);
        stringBuilder.append(ChatColor.BOLD);
        stringBuilder.append(' ');
        stringBuilder.append((int) (100f * progress));
        stringBuilder.append('%');
        return TitleSender.writeTitle(stringBuilder.toString());
    }


    public static WrappedChatComponent coolDownBar(ICooldown skill) {
        String bar = TitleSender.generateBars();
        int size = bar.length() - 1;
        float temp = skill.getCooldown();
        double cooldown = skill.cooldown();
        double product = 1F - cooldown/temp;
        int currentProgress = (int) (size * product);
        currentProgress = FastMath.min(currentProgress, size);
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        String builder = (!skill.onCooldown()) ? String.format("%s%s%s fully recharged!", ChatColor.GREEN, ChatColor.BOLD, skill.getName())
                : String.format("%s:%s%s %s%s%s %.2f s",
                skill.getName(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, cooldown);
        //String variant = skill.getName() + " " + skill.getLevel() + ChatColor.BOLD + ChatColor.GREEN + " " + sprogress + ChatColor.RESET + ChatColor.BOLD + cooldown

        return TitleSender.writeTitle(builder);
    }

    public static String coolDownString(ICooldown skill) {
        String bar = TitleSender.generateBars();
        int size = bar.length() - 1;
        float temp = skill.getCooldown();
        double cooldown = skill.cooldown();
        double product = 1F - cooldown/temp;
        int currentProgress = (int) (size * product);
        currentProgress = (currentProgress > size) ? size : currentProgress;
        String sprogress = bar.substring(0, currentProgress) + ChatColor.RED + bar.substring(currentProgress, size);
        return (!skill.onCooldown()) ? String.format("%s%s%s fully recharged!", ChatColor.GREEN, ChatColor.BOLD, skill.getName())
                : String.format("%s:%s%s %s%s%s %.2f s",
                skill.getName(), ChatColor.BOLD, ChatColor.GREEN, sprogress, ChatColor.RESET, ChatColor.BOLD, cooldown);
    }
}
