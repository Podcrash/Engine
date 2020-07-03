package com.podcrash.api.game;

import com.podcrash.api.annotations.GamePlugin;
import com.podcrash.api.plugin.IGamePlugin;
import com.podcrash.api.util.ReflectionUtil;
import org.bukkit.plugin.java.JavaPlugin;

//add more to this later if you want
public final class GameContainer {
    private final Class<? extends Game> gameClass;
    private final JavaPlugin plugin;

    public Class<? extends Game> getGameClass() {
        return gameClass;
    }

    public JavaPlugin getPlugin() { return plugin; }

    public GameContainer(Class<? extends Game> gameClass, JavaPlugin plugin) {
        this.gameClass = gameClass;
        this.plugin = plugin;
    }

    public void registerListeners() {
        ((IGamePlugin) plugin).registerListeners();
    }
}
