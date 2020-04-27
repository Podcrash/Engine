package com.podcrash.api.kits.iskilltypes.action;

import org.bukkit.entity.Player;

/**
 * This class will be used for making changes after the championsPlayer is made (as skills are made before them)
 * Good example: mana pool
 *
 * This is also an easy way to bypass exceptions during tests. Contain all bukkit/api methods there.
 */
public interface IConstruct {
    Player getPlayer();
    default void doConstruct() {
        if(getPlayer() != null) afterConstruction();
    }
    void afterConstruction();

    default void afterRespawn() {}
}
