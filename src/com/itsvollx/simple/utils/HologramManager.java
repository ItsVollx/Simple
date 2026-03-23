package com.itsvollx.simple.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class HologramManager {
    
    private static HologramManager instance;
    private HashMap<String, Hologram> holograms;
    private SimpleConfigs sc;
    private String hologramFilePath;
    
    private HologramManager() {
        this.holograms = new HashMap<>();
        this.sc = new SimpleConfigs(Simple.pl);
        this.hologramFilePath = sc.getFilePath("Holograms.yml");
        loadHolograms();
    }
    
    public static HologramManager getInstance() {
        if(instance == null) {
            instance = new HologramManager();
        }
        return instance;
    }
    
    /**
     * Create a new hologram
     */
    public Hologram createHologram(String id, Location location, List<String> lines, boolean persistent) {
        if(holograms.containsKey(id)) {
            deleteHologram(id);
        }
        
        Hologram hologram = new Hologram(id, location, persistent);
        for(String line : lines) {
            hologram.addLine(line);
        }
        hologram.spawn();
        
        holograms.put(id, hologram);
        
        if(persistent) {
            saveHologram(hologram);
        }
        
        return hologram;
    }
    
    /**
     * Get a hologram by ID
     */
    public Hologram getHologram(String id) {
        return holograms.get(id);
    }
    
    /**
     * Get hologram by entity (for click detection)
     */
    public Hologram getHologramByEntity(Entity entity) {
        for(Hologram hologram : holograms.values()) {
            if(hologram.isPartOfHologram(entity)) {
                return hologram;
            }
        }
        return null;
    }
    
    /**
     * Delete a hologram
     */
    public boolean deleteHologram(String id) {
        Hologram hologram = holograms.remove(id);
        if(hologram != null) {
            hologram.despawn();
            
            if(hologram.isPersistent()) {
                YamlConfiguration config = SimpleConfigs.getConfig(hologramFilePath);
                config.set("Holograms." + id, null);
                sc.saveConfig(hologramFilePath, config);
            }
            return true;
        }
        return false;
    }
    
    /**
     * Get all hologram IDs
     */
    public List<String> getHologramIds() {
        return new ArrayList<>(holograms.keySet());
    }
    
    /**
     * Reload all holograms
     */
    public void reloadHolograms() {
        // Despawn all current holograms
        for(Hologram hologram : holograms.values()) {
            hologram.despawn();
        }
        holograms.clear();
        
        // Load from file
        loadHolograms();
    }
    
    /**
     * Save hologram to file
     */
    private void saveHologram(Hologram hologram) {
        YamlConfiguration config = SimpleConfigs.getConfig(hologramFilePath);
        
        Location loc = hologram.getLocation();
        String path = "Holograms." + hologram.getId();
        
        config.set(path + ".world", loc.getWorld().getName());
        config.set(path + ".x", loc.getX());
        config.set(path + ".y", loc.getY());
        config.set(path + ".z", loc.getZ());
        config.set(path + ".yaw", loc.getYaw());
        config.set(path + ".pitch", loc.getPitch());
        
        List<String> lines = new ArrayList<>();
        for(Object line : hologram.getLines()) {
            if(line instanceof String) {
                lines.add((String) line);
            }
        }
        config.set(path + ".lines", lines);
        
        sc.saveConfig(hologramFilePath, config);
    }
    
    /**
     * Load holograms from file
     */
    private void loadHolograms() {
        YamlConfiguration config = SimpleConfigs.getConfig(hologramFilePath);
        
        ConfigurationSection hologramsSection = config.getConfigurationSection("Holograms");
        if(hologramsSection == null) return;
        
        for(String id : hologramsSection.getKeys(false)) {
            try {
                String path = "Holograms." + id;
                
                String worldName = config.getString(path + ".world");
                if(Bukkit.getWorld(worldName) == null) continue;
                
                Location location = new Location(
                    Bukkit.getWorld(worldName),
                    config.getDouble(path + ".x"),
                    config.getDouble(path + ".y"),
                    config.getDouble(path + ".z"),
                    (float) config.getDouble(path + ".yaw"),
                    (float) config.getDouble(path + ".pitch")
                );
                
                List<String> lines = config.getStringList(path + ".lines");
                
                Hologram hologram = new Hologram(id, location, true);
                for(String line : lines) {
                    hologram.addLine(line);
                }
                hologram.spawn();
                
                holograms.put(id, hologram);
            } catch(Exception e) {
                Bukkit.getLogger().warning("Failed to load hologram: " + id);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Despawn all holograms (for plugin disable)
     */
    public void despawnAll() {
        for(Hologram hologram : holograms.values()) {
            hologram.despawn();
        }
    }
}


