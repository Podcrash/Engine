package com.podcrash.api.sound;

//https://www.minecraftforum.net/forums/mapping-and-modding-java-edition/mapping-and-modding-tutorials/2213619-1-8-all-playsound-sound-arguments
public enum Audio {
    AMBIENT_CAVE("ambient.cave.cave"),
    AMBIENT_WEATHER_RAIN("ambient.weather.rain"),
    AMBIENT_WEATHER_THUNDER("ambient.weather.thunder");
    //more here....


    private final String code;

    Audio(String code) {
        this.code = code.toLowerCase();
    }

    @Override
    public String toString() {
        return code;
    }
}
