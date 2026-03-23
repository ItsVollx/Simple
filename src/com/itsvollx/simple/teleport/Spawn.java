package com.itsvollx.simple.teleport;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class Spawn {
    
    private SimpleConfigs sc = new SimpleConfigs(Simple.pl);
    private String spawnFilePath = sc.getFilePath("Spawn.yml");
    
    /**
     * Set the spawn location
     */
    public void setSpawn(Location location) {
        sc.saveLocation(spawnFilePath, "Spawn", "location", location);
    }
    
    /**
     * Get the spawn location
     */
    public Location getSpawn() {
        YamlConfiguration config = SimpleConfigs.getConfig(spawnFilePath);
        
        if(config.get("Spawn.location.world") == null) {
            // Return default world spawn if not set
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        }
        
        return sc.getLocation(spawnFilePath, "Spawn", "location");
    }
    
    /**
     * Check if spawn is set
     */
    public boolean isSpawnSet() {
        YamlConfiguration config = SimpleConfigs.getConfig(spawnFilePath);
        return config.get("Spawn.location.world") != null;
    }
    
    /**
     * Teleport player to spawn
     */
    public void teleportToSpawn(Player player) {
        Location spawn = getSpawn();
        player.teleport(spawn);
        player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        player.sendMessage("§aTeleported to spawn.");
    }
}


