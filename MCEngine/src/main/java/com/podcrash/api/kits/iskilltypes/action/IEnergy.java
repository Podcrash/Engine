package com.podcrash.api.kits.iskilltypes.action;

import com.podcrash.api.kits.KitPlayer;
import com.podcrash.api.kits.EnergyBar;
import net.md_5.bungee.api.ChatColor;

public interface IEnergy {
    String getName();
    KitPlayer getChampionsPlayer();

    /**
     * Get energy usage in seconds
     */
    int getEnergyUsage();

    default double getEnergyUsageTicks() {
        return getEnergyUsage() / 20D;
    }

    default void useEnergy() {
        useEnergy(getEnergyUsage());
    }
    /**
     * decrease the champions energy
     * @param energy
     */
    default void useEnergy(double energy){
        EnergyBar ebar = getChampionsPlayer().getEnergyBar();
        ebar.incrementEnergy(-energy);
    }

    /**
     * if energy - current energy of champion > 0
     * @param energy
     * @return
     */
    default boolean hasEnergy(double energy) {
        return (getEnergyBar().getEnergy() - energy  >= 0);
    }

    /**
     * Specific method to see if champion has enough energy for the energy usage
     * @return
     */
    default boolean hasEnergy() {
        return hasEnergy(getEnergyUsage());
    }

    default String getNoEnergyMessage() {
        return String.format(
                "%sEnergy> %sYou are too exhausted to use %s%s%s.",
                ChatColor.BLUE,
                ChatColor.GRAY,
                ChatColor.GREEN,
                getName(),
                ChatColor.GRAY
        );
    }

    default EnergyBar getEnergyBar() {
        return getChampionsPlayer().getEnergyBar();
    }
}
