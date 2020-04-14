package com.podcrash.api.mc.economy;

import org.bukkit.ChatColor;

public enum Currency {

    GOLD("gold", ChatColor.GOLD),
    //CRYSTAL("Crystals", ChatColor.LIGHT_PURPLE)
    ;

    private final String name;
    private final ChatColor color;


    Currency(String name, ChatColor color) {
        this.name = name;
        this.color = color;
    }

    /**
     *
     * @return The name of the currency LOWERCASE
     */
    public String getName() {return name;}

    /**
     *
     * @return The formatting for the number that accompanies name
     */
    public String getFormatting() {return "" + color + ChatColor.BOLD;}

}
