package com.itsvollx.simple.utils;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.bukkit.configuration.file.YamlConfiguration;

import com.itsvollx.simple.config.SimpleConfigs;

public class Economy {
    
    private static final String ECONOMY_FILE = "Economy.yml";
    private static final double STARTING_BALANCE = 100.0;
    
    // Get the economy file path
    private static String getEconomyPath() {
        return SimpleConfigs.PluginFolder + File.separator + ECONOMY_FILE;
    }
    
    // Get player's balance file path
    private String getPlayerBalancePath(UUID uuid) {
        SimpleConfigs configs = new SimpleConfigs();
        return configs.getUserFilePath(uuid, "Balance.yml");
    }
    
    // Get player's balance
    public double getBalance(UUID uuid) {
        String path = getPlayerBalancePath(uuid);
        File file = SimpleConfigs.getFile(path);
        
        if(!file.exists()) {
            setBalance(uuid, STARTING_BALANCE);
            return STARTING_BALANCE;
        }
        
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        return config.getDouble("balance", STARTING_BALANCE);
    }
    
    // Set player's balance
    public void setBalance(UUID uuid, double amount) {
        String path = getPlayerBalancePath(uuid);
        SimpleConfigs.createFile(path);
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        config.set("balance", amount);
        
        SimpleConfigs configs = new SimpleConfigs();
        configs.saveConfig(path, config);
    }
    
    // Add money to player's balance
    public void addBalance(UUID uuid, double amount) {
        double currentBalance = getBalance(uuid);
        setBalance(uuid, currentBalance + amount);
    }
    
    // Remove money from player's balance
    public boolean removeBalance(UUID uuid, double amount) {
        double currentBalance = getBalance(uuid);
        if(currentBalance >= amount) {
            setBalance(uuid, currentBalance - amount);
            return true;
        }
        return false;
    }
    
    // Check if player has enough money
    public boolean hasBalance(UUID uuid, double amount) {
        return getBalance(uuid) >= amount;
    }
    
    // Transfer money from one player to another
    public boolean transfer(UUID from, UUID to, double amount) {
        if(hasBalance(from, amount)) {
            removeBalance(from, amount);
            addBalance(to, amount);
            return true;
        }
        return false;
    }
    
    // Get top balances (for /baltop)
    public Map<UUID, Double> getTopBalances(int limit) {
        Map<UUID, Double> balances = new HashMap<>();
        
        File usersFolder = new File(SimpleConfigs.PluginFolder + File.separator + "Users");
        if(!usersFolder.exists()) {
            return balances;
        }
        
        File[] userFolders = usersFolder.listFiles();
        if(userFolders == null) {
            return balances;
        }
        
        // Load all player balances
        for(File userFolder : userFolders) {
            if(userFolder.isDirectory()) {
                try {
                    UUID uuid = UUID.fromString(userFolder.getName());
                    double balance = getBalance(uuid);
                    balances.put(uuid, balance);
                } catch(IllegalArgumentException e) {
                    // Skip invalid UUID folders
                }
            }
        }
        
        // Sort by balance descending and limit results
        return balances.entrySet()
                .stream()
                .sorted(Map.Entry.<UUID, Double>comparingByValue().reversed())
                .limit(limit)
                .collect(Collectors.toMap(
                    Map.Entry::getKey,
                    Map.Entry::getValue,
                    (e1, e2) -> e1,
                    LinkedHashMap::new
                ));
    }
    
    // Format money with currency symbol
    public static String format(double amount) {
        return String.format("$%.2f", amount);
    }
    
    // Get starting balance
    public static double getStartingBalance() {
        return STARTING_BALANCE;
    }
}


