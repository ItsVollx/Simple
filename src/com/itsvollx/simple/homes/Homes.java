package com.itsvollx.simple.homes;

import java.util.Set;
import java.util.UUID;

import com.itsvollx.simple.utils.Message;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;


public class Homes {

	private static SimpleConfigs sc = new SimpleConfigs(Simple.pl);
    private static YamlConfiguration HomeFile;

    
	private Message Message = new Message();
	private static UUID UUID;
	
    private static String HomeFilePath;
    
	public Homes(UUID UUID)	{
	Homes.UUID = UUID;	
	HomeFilePath = sc.getUserFilePath(UUID, "Homes.yml"); 
    HomeFile = SimpleConfigs.getConfig(HomeFilePath);

	}	
			
			
	public void delHome(String homename) {
		

	HomeFile.set("Homes." + homename, null);

	}
	
	
	
	public void setHome(String homename) {
	
	    // Set home details such as location
	    Player p = Bukkit.getPlayer(UUID);
	    Location loc = p.getLocation();
	    
	    sc.saveLocation(HomeFilePath, "Homes", homename.toUpperCase(), loc);
	
	}	
	
	
	public void goHome(String homename) {
	
	Player p = Bukkit.getPlayer(UUID);	

	if(HomeFile.get("Homes." + homename.toUpperCase()) == null) {
	p.sendMessage(Message.getMessage("goHome.noHomeSet").replace("{home}", homename.toUpperCase()));
	return;
	}
	
	
	p.sendMessage(Message.getMessage("Messages.goHome.success").replace("{home}", homename.toUpperCase()));
		p.sendMessage(Message.getMessage("goHome.success").replace("{home}", homename.toUpperCase()));
	
    Location home = sc.getLocation(HomeFilePath, "Homes", homename.toUpperCase());

    p.teleport(home);
    p.playSound(p.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 0);
	}	
	
	
	
	public void listHomes() {
	Player p = Bukkit.getPlayer(UUID);	
	
	Set<String> homes = HomeFile.getConfigurationSection("Homes.").getKeys(false);

    // Loop through all the home names
    for (String homename : homes) {
    	
    p.sendMessage("- " + homename);
    
    }	
    
	}
	
	
	
}


