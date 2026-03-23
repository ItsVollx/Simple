package com.itsvollx.simple.warps;

import com.itsvollx.simple.utils.Message;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class Warps {

	private SimpleConfigs sc = new SimpleConfigs(Simple.pl);
	private UUID UUID;
	private Player p;
	private String WarpsFilePath = sc.getFilePath("Warps.yml");
	
	public Warps() {}
	
	public Warps(UUID UUID) {
	this.UUID = UUID;
	this.p = Bukkit.getPlayer(UUID);	
	}
	
	
	public void add(String warpname) {
	sc.saveLocation(WarpsFilePath, "Warps.", warpname, p.getLocation());
	
	}

	
	public void Teleport(String warpname) {

	p.teleport(sc.getLocation(WarpsFilePath, "Warps.", warpname));
	p.playSound(p, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
	p.sendMessage(Message.getMessage("Messages.warps.teleportToWarp").replace("{warp}", warpname));
		p.sendMessage(Message.getMessage("warps.teleportToWarp").replace("{warp}", warpname));
	}
	
	
	public void delete(String warpname) {
		YamlConfiguration config = SimpleConfigs.getConfig(WarpsFilePath);
		
		if(config.get("Warps." + warpname) == null) {
			p.sendMessage("§cWarp '" + warpname + "' does not exist.");
			return;
		}
		
		config.set("Warps." + warpname, null);
		sc.saveConfig(WarpsFilePath, config);
		p.sendMessage("§aWarp '" + warpname + "' has been deleted.");
	}
	
	public void list() {
	
	for(Object s : SimpleConfigs.getConfig(WarpsFilePath).getConfigurationSection("Warps.").getKeys(false)) {
		
	p.sendMessage("- " + s.toString());	
	
	}
	
	}
	
	public void openGUI() {
		YamlConfiguration config = SimpleConfigs.getConfig(WarpsFilePath);
		
		if(config.getConfigurationSection("Warps.") == null || config.getConfigurationSection("Warps.").getKeys(false).isEmpty()) {
			p.sendMessage("No warps available.");
			return;
		}
		
		int size = Math.min((config.getConfigurationSection("Warps.").getKeys(false).size() + 8) / 9 * 9, 54);
		Inventory inv = Bukkit.createInventory(null, size, "Warps");
		
		int slot = 0;
		for(String warpname : config.getConfigurationSection("Warps.").getKeys(false)) {
			ItemStack item = new ItemStack(Material.COMPASS);
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName("§a" + warpname);
			item.setItemMeta(meta);
			inv.setItem(slot++, item);
		}
		
		p.openInventory(inv);
	}
	
	
}


