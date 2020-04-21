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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/**
 * Just a feeling that the async stuff is uneeded as FileConfiguration just caches it....
 *
 */
public class Configurator {
    private static final int MAX_THREADS = 5;
    private static final ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);
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

    public CompletableFuture<Void> read(String path, Consumer<Object> consumer) {
        return CompletableFuture.supplyAsync(() -> this.config.get(path), executor).thenAcceptAsync(consumer);
    }
    public CompletableFuture<Void>  readInt(String path, Consumer<Integer> consumer) {
        return CompletableFuture.supplyAsync(() -> (int) this.config.get(path), executor).thenAcceptAsync(consumer);
    }
    public CompletableFuture<Void>  readDouble(String path, Consumer<Double> consumer) {
        return CompletableFuture.supplyAsync(() -> (double) this.config.get(path), executor).thenAcceptAsync(consumer);
    }
    public CompletableFuture<Void> readString(String path, Consumer<String> consumer) {
        return CompletableFuture.supplyAsync(() -> (String) this.config.get(path), executor).thenAcceptAsync(consumer);
    }
    public CompletableFuture<Void> readList(String path, Consumer<List> consumer) {
        return CompletableFuture.supplyAsync(() -> this.config.getList(path, new ArrayList<>()), executor).thenAcceptAsync(consumer);
    }

    public void set(String path, Object value) {
        Runnable run = () -> config.set(path, value);
        executor.submit(run);
    }

    public boolean hasPath(String path) {
        return config.isSet(path);
    }

    public void deletePath(String path) {
        Runnable deleteCall = () -> {
            if (config.isSet(path) ) {
                config.set(path, null);
                saveConfig();
            }
        };
        executor.submit(deleteCall);
    }
    public void saveConfig(){
        executor.submit(() -> {
            synchronized (config) {
                try {
                    config.save(configFile);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
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
