package com.itsvollx.simple.commands;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.itsvollx.simple.config.SimpleConfigs;
import com.itsvollx.simple.inventory.EnderChestInventory;
import com.itsvollx.simple.inventory.TrashInventory;
import com.itsvollx.simple.utils.SitAndLay;

public class UtilityCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(label.equalsIgnoreCase("simple")) {
            player.sendMessage("§e§l=== Simple Plugin ===");
            player.sendMessage("§aA comprehensive plugin with homes, warps, teleport, and more!");
            player.sendMessage("§7Use /homes, /warps, /tpa, /spawn, and other commands.");
            return true;
        }
        
        if(label.equalsIgnoreCase("echest")) {
            EnderChestInventory echest = new EnderChestInventory();
            if(args.length == 0) {
                echest.open(player);
            } else if(args.length == 1) {
                if(!player.hasPermission("simple.echest.others")) {
                    player.sendMessage("§cYou don't have permission to open other players' ender chests.");
                    return true;
                }
                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    player.sendMessage("§cPlayer not found.");
                    return true;
                }
                player.openInventory(target.getEnderChest());
                player.sendMessage("§aOpening " + target.getName() + "'s ender chest.");
            }
            return true;
        }
        
        if(label.equalsIgnoreCase("trash")) {
            TrashInventory trash = new TrashInventory();
            trash.open(player);
            return true;
        }
        
        if(label.equalsIgnoreCase("ping")) {
            player.sendMessage("§aPong! :)");
            return true;
        }
        
        if(label.equalsIgnoreCase("top")) {
            Location loc = player.getLocation();
            int highestY = player.getWorld().getHighestBlockYAt(loc);
            
            // Find safe landing spot
            Location topLoc = new Location(player.getWorld(), loc.getX(), highestY, loc.getZ());
            while(topLoc.getBlock().getType() != Material.AIR && topLoc.getY() < player.getWorld().getMaxHeight()) {
                topLoc.add(0, 1, 0);
            }
            
            topLoc.setPitch(loc.getPitch());
            topLoc.setYaw(loc.getYaw());
            player.teleport(topLoc);
            player.sendMessage("§aTeleported to the top!");
            return true;
        }
        
        if(label.equalsIgnoreCase("time")) {
            long worldTime = player.getWorld().getTime();
            long hours = (worldTime / 1000 + 6) % 24;
            long minutes = (worldTime % 1000) * 60 / 1000;
            
            String period = hours < 12 ? "AM" : "PM";
            long displayHours = hours % 12;
            if(displayHours == 0) displayHours = 12;
            
            player.sendMessage("§e§lWorld Time:");
            player.sendMessage("§a" + String.format("%02d:%02d %s", displayHours, minutes, period));
            player.sendMessage("§7(Ticks: " + worldTime + ")");
            return true;
        }
        
        if(label.equalsIgnoreCase("bedhome")) {
            if(!player.hasPermission("simple.bedhome") && !player.isOp()) {
                player.sendMessage("§cYou don't have permission to toggle bed homes.");
                return true;
            }
            
            SimpleConfigs configs = new SimpleConfigs();
            String configPath = configs.getConfigPath("config.yml");
            YamlConfiguration config = SimpleConfigs.getConfig(configPath);
            
            boolean currentValue = config.getBoolean("bed-set-home", true);
            config.set("bed-set-home", !currentValue);
            configs.saveConfig(configPath, config);
            
            if(!currentValue) {
                player.sendMessage("§aBed homes are now §aENABLED§a. Players can set spawn points with beds.");
            } else {
                player.sendMessage("§cBed homes are now §cDISABLED§c. Players cannot set spawn points with beds.");
            }
            return true;
        }
        
        if(label.equalsIgnoreCase("workbench")) {
            player.openWorkbench(null, true);
            player.sendMessage("§aOpened virtual crafting table.");
            return true;
        }
        
        if(label.equalsIgnoreCase("anvil")) {
            Inventory anvil = Bukkit.createInventory(null, 9, "Anvil");
            player.openInventory(anvil);
            player.sendMessage("§aOpened virtual anvil.");
            return true;
        }
        
        if(label.equalsIgnoreCase("endme")) {
            player.setHealth(0);
            player.sendMessage("§cYou have ended yourself.");
            Bukkit.broadcastMessage("§7" + player.getName() + "§7 has ended themselves.");
            return true;
        }
        
        if(label.equalsIgnoreCase("hat")) {
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            
            if(itemInHand == null || itemInHand.getType() == Material.AIR) {
                player.sendMessage("§cYou need to hold an item to wear as a hat!");
                return true;
            }
            
            ItemStack currentHelmet = player.getInventory().getHelmet();
            
            // Set item in hand as helmet
            player.getInventory().setHelmet(itemInHand.clone());
            
            // Put old helmet in hand (or air if no helmet)
            if(currentHelmet != null && currentHelmet.getType() != Material.AIR) {
                player.getInventory().setItemInMainHand(currentHelmet);
                player.sendMessage("§aYou are now wearing " + itemInHand.getType().name() + "§a as a hat!");
            } else {
                player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                player.sendMessage("§aYou are now wearing " + itemInHand.getType().name() + "§a as a hat!");
            }
            
            return true;
        }
        
        if(label.equalsIgnoreCase("sit")) {
            SitAndLay.sitPlayer(player);
            return true;
        }
        
        if(label.equalsIgnoreCase("lay") || label.equalsIgnoreCase("laydown")) {
            SitAndLay.layPlayer(player);
            return true;
        }
        
        return false;
    }
}

