package com.podcrash.api.mc.sound;

public final class SoundWrapper {
    private final String soundName;
    private final float volume;
    private final int pitch;

    public SoundWrapper(String soundName, float volume, int pitch) {
        this.soundName = soundName;
        this.volume = volume;
        this.pitch = pitch;
    }

    public String getSoundName() {
        return soundName;
    }
    public float getVolume() {
        return volume;
    }
    public int getPitch() {
        return pitch;
    }

    public boolean isValid() {
        return !soundName.isEmpty();
    }

}
