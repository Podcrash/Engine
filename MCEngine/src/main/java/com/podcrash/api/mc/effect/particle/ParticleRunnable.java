package com.podcrash.api.mc.effect.particle;

import com.podcrash.api.mc.time.resources.TimeResource;
import com.podcrash.api.plugin.Pluginizer;

import java.util.ArrayList;
import java.util.List;

public class ParticleRunnable implements TimeResource {
    static ParticleRunnable particleRunnable;
    private final List<EntityParticleWrapper> wrappers = new ArrayList<>();
    private boolean active = false;
    private ParticleRunnable() {
        particleRunnable = this;
        active = true;
        Pluginizer.getSpigotPlugin().getLogger().info("ParticleRunnable Starting!");
    }

    public static void start(){
        new ParticleRunnable().run(1,0);
    }

    public static void stop(){
        particleRunnable.setActive(false);
    }

    @Override
    public void task() {
        if (wrappers.size() > 0) {
            for (EntityParticleWrapper wrapper : wrappers) {
                if (!wrapper.cancel()) {
                    wrapper.send();
                }
            }
            wrappers.removeIf(entityParticleWrapper -> !entityParticleWrapper.getEntity().isValid());
        }
    }

    @Override
    public boolean cancel() {
        return !active;
    }

    @Override
    public void cleanup() {
        Pluginizer.getSpigotPlugin().getLogger().info("[ParticleRunnable]: Shutting off!");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<EntityParticleWrapper> getWrappers() {
        return wrappers;
    }
}
