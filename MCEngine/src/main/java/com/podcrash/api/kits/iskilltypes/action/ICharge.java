package com.podcrash.api.kits.iskilltypes.action;

import com.podcrash.api.time.resources.TimeResource;
import net.md_5.bungee.api.ChatColor;

public interface ICharge extends TimeResource {
    String getName();

    void addCharge();

    int getCurrentCharges();

    int getMaxCharges();

    default String getCurrentChargeMessage() {
        return String.format("%sSkill> %s%s Charges: %s%d",
                ChatColor.BLUE, ChatColor.GRAY, getName(), ChatColor.GREEN, getCurrentCharges());
    }

    default String getNoChargeMessage() {
        return String.format("%sSkill> %sNo %s Charges.",
                ChatColor.BLUE, ChatColor.GRAY, getName());
    }

    default boolean isMaxAtStart() {
        return true;
    }
}
