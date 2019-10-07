package com.podcrash.api.plugin;

import net.md_5.bungee.api.plugin.Plugin;
import org.redisson.api.RedissonClient;

public class PodcrashBungee extends Plugin implements PodcrashPlugin {
    private static PodcrashBungee INSTANCE;

    public static PodcrashBungee getInstance() {
        return INSTANCE;
    }

    @Override
    public void redis(RedissonClient client) {

    }

    @Override
    public void onEnable() {
        INSTANCE = this;
        Pluginizer.setInstance(this);
        enable();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public void onDisable() {
        disable();
    }
}
