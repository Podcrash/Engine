package com.podcrash.api.effect.particle;

import com.podcrash.api.time.resources.TimeResource;
import com.podcrash.api.plugin.PodcrashSpigot;

import java.util.ArrayList;
import java.util.List;

public class ParticleRunnable implements TimeResource {
    static ParticleRunnable particleRunnable;
    private final List<EntityParticleWrapper> wrappers = new ArrayList<>();
    private boolean active = false;
    private ParticleRunnable() {
        particleRunnable = this;
        active = true;
        PodcrashSpigot.getInstance().getLogger().info("ParticleRunnable Starting!");
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
        PodcrashSpigot.getInstance().getLogger().info("[ParticleRunnable]: Shutting off!");
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<EntityParticleWrapper> getWrappers() {
        return wrappers;
    }
}
