package com.podcrash.api.mc;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Configurator {
    private final JavaPlugin plugin;

    private File configFile;
    private FileConfiguration config;
    private final String fileName;

    private File getFileFromFolder(File folder, String fileName) {
        if (folder.isDirectory()) {
            for(File file1 : folder.listFiles()) {
                if (fileName.equals(file1.getName())) return file1;
            }
        }
        return null;
    }

    private boolean mkdirFile(File file) {
        try {
            return file.mkdir();
        } catch (SecurityException e) {
            e.printStackTrace();
            return false;
        }
    }
    public Configurator(JavaPlugin plugin, File file, String fileName, boolean hasDefaults) {
        this.plugin = plugin;
        this.fileName = fileName + ".yml";
        plugin.getLogger().info("[Configurator] Loading " + this.fileName);
        if (!file.exists()) {
            plugin.getLogger().info("[Configurator] Creating folder " + file.getAbsolutePath());
            boolean created = mkdirFile(file);
            if (!created)
                throw new IllegalStateException("The file must be made!");
        }
        if ((this.configFile = getFileFromFolder(file, this.fileName)) == null) {
            plugin.getLogger().info("[Configurator] " + this.fileName + " did not exist! Creating!");
            this.configFile = new File(file, this.fileName);
            if (hasDefaults) {
                plugin.getLogger().info("[Configurator] " + this.fileName + " saving default values!");
                plugin.saveResource(this.fileName, false);
            }
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        saveConfig();
    }

    public Configurator(JavaPlugin plugin, String fileName, Reader reader) {
        this.plugin = plugin;
        this.fileName = fileName + ".yml";
        this.configFile = new File(plugin.getDataFolder(), fileName);
        this.config = YamlConfiguration.loadConfiguration(reader);
        saveConfig();
    }
    /**
     * Default bukkit folder
     * @param plugin
     * @param fileName
     * @param hasDefaults
     */
    public Configurator(JavaPlugin plugin, String fileName, boolean hasDefaults) {
        this(plugin, plugin.getDataFolder(), fileName, hasDefaults);
    }

    public Configurator(JavaPlugin plugin, String fileName) {
        this(plugin, fileName, false);
    }

    public void read(String path, Consumer<Object> consumer) {
        consumer.accept(this.config.get(path));
    }
    public void  readInt(String path, Consumer<Integer> consumer) {
        consumer.accept((int) this.config.get(path));
    }
    public void  readDouble(String path, Consumer<Double> consumer) {
        consumer.accept((double) this.config.get(path));
    }
    public void readString(String path, Consumer<String> consumer) {
        consumer.accept((String) this.config.get(path));
    }

    public void readList(String path, Consumer<List<?>> consumer) {
        consumer.accept(this.config.getList(path, new ArrayList<>()));
    }

    public void set(String path, Object value) {
        config.set(path, value);
    }

    public boolean hasPath(String path) {
        return config.isSet(path);
    }

    public void deletePath(String path) {
        if (config.isSet(path)) {
            config.set(path, null);
            saveConfig();
        }
    }
    public void saveConfig(){
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public void reloadConfig() {
        config = YamlConfiguration.loadConfiguration(configFile);
        Reader defConfigStream = new InputStreamReader(plugin.getResource(fileName), StandardCharsets.UTF_8);
        if (defConfigStream != null) {
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            config.setDefaults(defConfig);
        }
    }
}
