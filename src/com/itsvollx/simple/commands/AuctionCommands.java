package com.itsvollx.simple.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.itsvollx.simple.inventory.AuctionInventory;
import com.itsvollx.simple.shop.AuctionHouse;

public class AuctionCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use auction commands.");
            return true;
        }
        
        Player player = (Player) sender;
        AuctionHouse ah = new AuctionHouse(player);
        
        // /auction or /ah - open GUI
        if(label.equalsIgnoreCase("auction") || label.equalsIgnoreCase("ah")) {
            if(args.length == 0) {
                AuctionInventory auctionInv = new AuctionInventory();
                auctionInv.open(player);
                return true;
            }
            
            // /ah sell <price>
            if(args[0].equalsIgnoreCase("sell")) {
                if(args.length != 2) {
                    player.sendMessage("§cUsage: /ah sell <price>");
                    return true;
                }
                
                ItemStack itemInHand = player.getInventory().getItemInMainHand();
                if(itemInHand == null || itemInHand.getType().isAir()) {
                    player.sendMessage("§cYou must hold an item to list on the auction house!");
                    return true;
                }
                
                double price;
                try {
                    price = Double.parseDouble(args[1]);
                } catch(NumberFormatException e) {
                    player.sendMessage("§cInvalid price! Please enter a valid number.");
                    return true;
                }
                
                if(price <= 0) {
                    player.sendMessage("§cPrice must be greater than 0!");
                    return true;
                }
                
                // Check listing limit
                int activeListings = ah.getActiveListingsCount(player.getUniqueId());
                int maxListings = 5; // Default limit
                
                if(player.hasPermission("simple.ah.limit.10")) {
                    maxListings = 10;
                } else if(player.hasPermission("simple.ah.limit.unlimited")) {
                    maxListings = Integer.MAX_VALUE;
                }
                
                if(activeListings >= maxListings) {
                    player.sendMessage("§cYou have reached your listing limit of " + maxListings + "!");
                    player.sendMessage("§7Cancel some listings to list more items.");
                    return true;
                }
                
                ah.listItem(itemInHand, price);
                return true;
            }
            
            // /ah info
            if(args[0].equalsIgnoreCase("info")) {
                int activeListings = ah.getActiveListingsCount(player.getUniqueId());
                int maxListings = 5;
                
                if(player.hasPermission("simple.ah.limit.10")) {
                    maxListings = 10;
                } else if(player.hasPermission("simple.ah.limit.unlimited")) {
                    maxListings = Integer.MAX_VALUE;
                }
                
                player.sendMessage("§e§l=== Auction House Info ===");
                player.sendMessage("§7Your active listings: §a" + activeListings + "§7/§a" + 
                    (maxListings == Integer.MAX_VALUE ? "∞" : maxListings));
                player.sendMessage("§7To list an item: §e/ah sell <price>");
                player.sendMessage("§7To view auction: §e/ah");
                return true;
            }
            
            player.sendMessage("§cUsage: /ah [sell <price>|info]");
            return true;
        }
        
        return false;
    }
}

