package com.podcrash.api.mc.hologram;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public class HologramMaker implements Runnable {
    //The below is used maybe if there are chunk errors
    private static List<Hologram> holograms = new ArrayList<>();
    private static List<HologramUpdateWrapper> hologramUpdaters = new ArrayList<>();
    private HologramMaker() {

    }

    public static Hologram createHologram(Location location, List<String> texts) {
        return new Hologram(location, texts);
    }
    public static Hologram createHologram(Location location, String... texts) {
        return new Hologram(location, texts);
    }

    public static void destroyHologram(Hologram hologram) {
        hologram.destroy();
        holograms.remove(hologram);
        hologramUpdaters.remove(hologram);
    }

    private static class HologramUpdateWrapper {
        private final Hologram hologram;
        private final int delay;
        private long lastTime;
        private HologramUpdateWrapper(Hologram hologram, int delayMillis) {
            this.hologram = hologram;
            this.delay = delayMillis;
            this.lastTime = System.currentTimeMillis();
        }

        private void update() {
            if(System.currentTimeMillis() - lastTime >= this.delay) {
                hologram.destroy();
                hologram.render();
                this.lastTime = System.currentTimeMillis();
            }
        }
    }

    public static void updater(Hologram hologram, int delayMillis) {
        hologramUpdaters.add(new HologramUpdateWrapper(hologram, delayMillis));
    }

    @Override
    public void run() {
        for(HologramUpdateWrapper updater : hologramUpdaters) {
            updater.update();
        }
    }
}
