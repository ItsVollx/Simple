package com.itsvollx.simple;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.itsvollx.simple.commands.HomeCommands;
import com.itsvollx.simple.commands.WarpCommands;
import com.itsvollx.simple.commands.TeleportCommands;
import com.itsvollx.simple.commands.HologramCommands;
import com.itsvollx.simple.commands.SpawnCommands;
import com.itsvollx.simple.commands.UtilityCommands;
import com.itsvollx.simple.commands.EconomyCommands;
import com.itsvollx.simple.commands.AuctionCommands;
import com.itsvollx.simple.config.SimpleConfigs;
import com.itsvollx.simple.listeners.SimpleEvents;
import com.itsvollx.simple.utils.HologramManager;
import com.itsvollx.simple.utils.Message;
import com.itsvollx.simple.utils.SitAndLay;


public class Simple extends JavaPlugin {

	public static Simple pl;
	public static SimpleConfigs ConfigManager;
	private static PhantomForceField phantomForceField;

    @Override
    public void onEnable() {
        // Plugin startup logic
    	pl = this;
        getLogger().info("Simple plugin has been enabled.");
        // Configs
        loadConfigs();
        // Load Messages
        // Initialize HologramManager
        HologramManager.getInstance();
        new Message().Register();
        
        // Register events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new SimpleEvents(this), this); 
        registercommands();
        
        // Start phantom force field
        phantomForceField = new PhantomForceField(this);
        phantomForceField.start();
        getLogger().info("Phantom force field activated!");
        
    }

    public void loadConfigs() {
    	ConfigManager = new SimpleConfigs(this);
    	
    	for(Player p : Bukkit.getOnlinePlayers()) {
    	ConfigManager.createUserFiles(p.getUniqueId());
    	}
    	
    }
    
    
    public void registercommands() {
    	// Home Commands
    	HomeCommands homeCommands = new HomeCommands();
    	getCommand("sethome").setExecutor(homeCommands);
    	getCommand("delhome").setExecutor(homeCommands);
    	getCommand("home").setExecutor(homeCommands);
    	getCommand("homes").setExecutor(homeCommands);
    	getCommand("homelist").setExecutor(homeCommands);
    	
    	// Warp Commands
    	WarpCommands warpCommands = new WarpCommands();
    	getCommand("warp").setExecutor(warpCommands);
    	getCommand("warps").setExecutor(warpCommands);
    	getCommand("setwarp").setExecutor(warpCommands);
    	getCommand("delwarp").setExecutor(warpCommands);
    	
    	// Teleport Commands
    	TeleportCommands teleportCommands = new TeleportCommands();
    	getCommand("back").setExecutor(teleportCommands);
    	getCommand("tpa").setExecutor(teleportCommands);
    	getCommand("tpahere").setExecutor(teleportCommands);
    	getCommand("tpaccept").setExecutor(teleportCommands);
    	getCommand("tpdeny").setExecutor(teleportCommands);
    	getCommand("tp").setExecutor(teleportCommands);
    	getCommand("tphere").setExecutor(teleportCommands);
    	
    	// Hologram Commands
    	HologramCommands hologramCommands = new HologramCommands();
    	getCommand("hologram").setExecutor(hologramCommands);
    	getCommand("holo").setExecutor(hologramCommands);
    	
    	// Spawn Commands
    	SpawnCommands spawnCommands = new SpawnCommands();
    	getCommand("spawn").setExecutor(spawnCommands);
    	getCommand("setspawn").setExecutor(spawnCommands);
    	
    	// Utility Commands
    	UtilityCommands utilityCommands = new UtilityCommands();
    	getCommand("echest").setExecutor(utilityCommands);
    	getCommand("trash").setExecutor(utilityCommands);
    	getCommand("ping").setExecutor(utilityCommands);
    	getCommand("simple").setExecutor(utilityCommands);
    	getCommand("top").setExecutor(utilityCommands);
    	getCommand("time").setExecutor(utilityCommands);
    	getCommand("bedhome").setExecutor(utilityCommands);
    	getCommand("workbench").setExecutor(utilityCommands);
    	getCommand("anvil").setExecutor(utilityCommands);
    	getCommand("endme").setExecutor(utilityCommands);
    	getCommand("hat").setExecutor(utilityCommands);
    	getCommand("sit").setExecutor(utilityCommands);
    	getCommand("lay").setExecutor(utilityCommands);
    	
    	// Economy Commands
    	EconomyCommands economyCommands = new EconomyCommands();
    	getCommand("balance").setExecutor(economyCommands);
    	getCommand("bal").setExecutor(economyCommands);
    	getCommand("money").setExecutor(economyCommands);
    	getCommand("pay").setExecutor(economyCommands);
    	getCommand("givemoney").setExecutor(economyCommands);
    	getCommand("sendmoney").setExecutor(economyCommands);
    	getCommand("baltop").setExecutor(economyCommands);
    	getCommand("moneytop").setExecutor(economyCommands);
    	getCommand("richest").setExecutor(economyCommands);
    	getCommand("balancetop").setExecutor(economyCommands);
    	getCommand("eco").setExecutor(economyCommands);
    	getCommand("economy").setExecutor(economyCommands);
    	
    	// Auction House Commands
    	AuctionCommands auctionCommands = new AuctionCommands();
    	getCommand("auction").setExecutor(auctionCommands);
    	getCommand("ah").setExecutor(auctionCommands);
    }
    
    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if(phantomForceField != null) {
            phantomForceField.stop();
        }
        HologramManager.getInstance().despawnAll();
        SitAndLay.cleanupAll();
        getLogger().info("Simple plugin has been disabled.");
    }
    
}

