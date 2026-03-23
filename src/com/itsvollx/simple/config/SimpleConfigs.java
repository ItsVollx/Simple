package com.itsvollx.simple.config;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.itsvollx.simple.Simple;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

public class SimpleConfigs {

    // Plugin reference and paths
    private static Simple plugin;
    public static String PluginFolder;
    public static String PluginName;

    // File path storage
    private final HashMap<String, String> UserFilePaths = new HashMap<>();
    public static final HashMap<String, String> ServerFilePaths = new HashMap<>();

    // Constructor: Initialize plugin and set up file paths
    public SimpleConfigs(Simple plugin) {
        SimpleConfigs.plugin = plugin;
        PluginFolder = plugin.getDataFolder().getPath();
        setupServerFiles(); // Initialize server files
        setupUserFiles();   // Initialize user files
    }

    // Default constructor (for flexibility if needed)
    public SimpleConfigs() {}

    // Factory method to return a new instance of SimpleConfigs
    public SimpleConfigs getSimpleConfigs() {
        return new SimpleConfigs(plugin);
    }

    // Setup user files by adding predefined file names
    private void setupUserFiles() {
        if (UserFilePaths.isEmpty()) {
            UserFilePaths.put("Homes", "Homes.yml");
            UserFilePaths.put("Teleport", "Teleport.yml");
            UserFilePaths.put("Balance", "Balance.yml");
        }
    }

    // Return the user-specific file path
    public String getUserFilePath(UUID userUUID, String filename) {
        return PluginFolder + File.separator + "Users" + File.separator + userUUID + File.separator + filename;
    }

    // Utility to create a new file instance
    public static File getFile(String path) {
        return new File(path);
    }

    // Create user files for a given UUID
    public void createUserFiles(UUID userUUID) {
        for (String fileName : UserFilePaths.values()) {
            createFile(getUserFilePath(userUUID, fileName));
        }
    }

    // Setup server files by adding predefined file names
    private void setupServerFiles() {
        if (ServerFilePaths.isEmpty()) {
            ServerFilePaths.put("Messages", "Messages.yml");
            ServerFilePaths.put("Warps", "Warps.yml");
            ServerFilePaths.put("Config", "config.yml");

            // Create all server files
            for (String fileName : ServerFilePaths.values()) {
                createFile(PluginFolder + File.separator + fileName);
                if(!fileName.equals("config.yml")) {
                    writeResourceToConfigFile(fileName, fileName);
                }
            }
            
            // Set default config values if not exists
            String configPath = getConfigPath("config.yml");
            YamlConfiguration config = getConfig(configPath);
            if(!config.contains("bed-set-home")) {
                config.set("bed-set-home", true);
                saveConfig(configPath, config);
            }
            
        }
    }

    public String getConfigPath(String fileName) {
    
    return PluginFolder + File.separator + fileName;
    }
    
    // Load and return YAML configuration from a file path
    public static YamlConfiguration getConfig(String path) {
        return YamlConfiguration.loadConfiguration(getFile(path));
    }

    // Save YAML configuration to a specified file path
    public void saveConfig(String filepath, YamlConfiguration config) {
        try {
            config.save(getFile(filepath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Set a list in the YAML configuration
    public void setList(String filepath, String path, Object value) {
        YamlConfiguration config = getConfig(filepath);
        config.set(path, value);
        saveConfig(filepath, config);
    }

    // Set a value in the YAML configuration
    public void set(String filepath, String path, Object value) {
        YamlConfiguration config = getConfig(filepath);
        config.set(path, value);
        saveConfig(filepath, config);
    }

    // Get a list from the YAML configuration
    public static List<?> getList(String filepath, String path) {
        YamlConfiguration config = getConfig(filepath);
        return config.getStringList(path);
    }

    // Save a location to the YAML configuration
    public void saveLocation(String filepath, String path, String vaultName, Location loc) {
        YamlConfiguration config = getConfig(filepath);
        config.set(path + "." + vaultName + ".world", loc.getWorld().getName());
        config.set(path + "." + vaultName + ".x", loc.getX());
        config.set(path + "." + vaultName + ".y", loc.getY());
        config.set(path + "." + vaultName + ".z", loc.getZ());
        config.set(path + "." + vaultName + ".yaw", loc.getYaw());
        config.set(path + "." + vaultName + ".pitch", loc.getPitch());
        saveConfig(filepath, config);
    }

    // Retrieve a location from the YAML configuration
    public Location getLocation(String filepath, String path, String vaultName) {
        YamlConfiguration config = getConfig(filepath);
        World world = Bukkit.getWorld(config.getString(path + "." + vaultName + ".world"));
        double x = config.getDouble(path + "." + vaultName + ".x");
        double y = config.getDouble(path + "." + vaultName + ".y");
        double z = config.getDouble(path + "." + vaultName + ".z");
        float yaw = (float) config.getDouble(path + "." + vaultName + ".yaw");
        float pitch = (float) config.getDouble(path + "." + vaultName + ".pitch");
        return new Location(world, x, y, z, yaw, pitch);
    }

    // Return the complete file path for a given file
    public String getFilePath(String path) {
        return PluginFolder + File.separator + path;
    }

    // Create a file and its directories if they don't exist
    public static void createFile(String path) {
        File file = getFile(path);

        // Create parent directories if they don't exist
        File parentDir = file.getParentFile();
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }

        // Try to create the file
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public HashMap<String, String> Section(String ConfigPath,String sectionPath) {
        HashMap<String, String> map = new HashMap<>();

        ConfigurationSection section = getConfig(ConfigPath).getConfigurationSection(sectionPath);
        if (section != null) {
            Set<String> keys = section.getKeys(true); // true to include nested keys

            for (String key : keys) {
                String fullPath = sectionPath + "." + key;
                String value = section.getString(key);
                if (value != null) {
                    map.put(fullPath, value); // Store with full path as key
                }
            }
        }

        return map;
    }
    
    public boolean writeResourceToConfigFile(String resourcePath, String fileName) {
        // Construct the output file path
        String pluginFolder = plugin.getDataFolder().getPath();
        File outputFile = new File(pluginFolder + File.separator + fileName);

        // Ensure the directory structure exists
        outputFile.getParentFile().mkdirs();

        // Check if the file already exists and is not empty
        if (outputFile.exists() && outputFile.length() > 0) {
            plugin.getLogger().info("File already exists and is not empty: " + outputFile.getPath());
            return true;
        }

        // Read resource and write to file
        try (InputStream inputStream = plugin.getResource(resourcePath)) {
            if (inputStream == null) {
                plugin.getLogger().warning("Resource file not found: " + resourcePath);
                return false; // Resource not found
            }

            Files.copy(inputStream, outputFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            plugin.getLogger().info("Resource file written: " + outputFile.getPath());
            return true; // Resource found and written successfully
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to write resource file: " + resourcePath);
            e.printStackTrace();
            return false; // Failed to write the file
        }
    }
    
    
}


