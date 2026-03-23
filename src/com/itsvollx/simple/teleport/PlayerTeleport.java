package com.itsvollx.simple.teleport;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class PlayerTeleport {

	private SimpleConfigs sc = new SimpleConfigs(Simple.pl);
    private static YamlConfiguration Teleport;	
    private static String PlayerTeleportFilePath;
	private UUID UUID;
	
	public PlayerTeleport(UUID UUID) {
		
		this.UUID = UUID;	
		PlayerTeleportFilePath = sc.getUserFilePath(UUID, "Teleport.yml"); 
	    Teleport = SimpleConfigs.getConfig(PlayerTeleportFilePath);	
		
	}
	
	
	public void goBackToLocation() {
	
	Player p = Bukkit.getPlayer(UUID);
	Location loc = p.getLocation();
	
	sc.saveLocation(PlayerTeleportFilePath, "Teleport", "Back.1", loc);
	sc.saveLocation(PlayerTeleportFilePath, "Teleport", "Back.2", loc);
	Location loc2 = sc.getLocation(PlayerTeleportFilePath, "Teleport", "Back");
	
	p.teleport(loc2);
	
	}

		
}


