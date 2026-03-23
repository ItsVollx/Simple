package com.itsvollx.simple.commands;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.itsvollx.simple.utils.Economy;

public class EconomyCommands implements CommandExecutor {
    
    private Economy economy = new Economy();
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        
        // /balance or /bal or /money
        if(label.equalsIgnoreCase("balance") || label.equalsIgnoreCase("bal") || label.equalsIgnoreCase("money")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("Only players can check balance.");
                return true;
            }
            
            Player player = (Player) sender;
            
            // Check if using /money with admin subcommands (give/take/set)
            if(label.equalsIgnoreCase("money") && args.length > 0) {
                String subcommand = args[0].toLowerCase();
                if(subcommand.equals("give") || subcommand.equals("take") || subcommand.equals("set")) {
                    // Handle as admin command
                    if(!player.hasPermission("simple.eco") && !player.isOp()) {
                        player.sendMessage("§cYou don't have permission to use this command.");
                        return true;
                    }
                    
                    if(args.length < 3) {
                        player.sendMessage("§cUsage: /money <give|take|set> <player> <amount>");
                        return true;
                    }
                    
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target == null) {
                        player.sendMessage("§cPlayer not found.");
                        return true;
                    }
                    
                    double amount;
                    try {
                        amount = Double.parseDouble(args[2]);
                    } catch(NumberFormatException e) {
                        player.sendMessage("§cInvalid amount. Please enter a valid number.");
                        return true;
                    }
                    
                    if(amount < 0) {
                        player.sendMessage("§cAmount cannot be negative.");
                        return true;
                    }
                    
                    switch(subcommand) {
                        case "give":
                            economy.addBalance(target.getUniqueId(), amount);
                            player.sendMessage("§aGave §e" + Economy.format(amount) + "§a to §e" + target.getName());
                            target.sendMessage("§aYou received §e" + Economy.format(amount) + "§a from the server.");
                            break;
                            
                        case "take":
                            if(economy.removeBalance(target.getUniqueId(), amount)) {
                                player.sendMessage("§aTook §e" + Economy.format(amount) + "§a from §e" + target.getName());
                                target.sendMessage("§c" + Economy.format(amount) + "§c was taken from your account.");
                            } else {
                                player.sendMessage("§cPlayer doesn't have enough money.");
                            }
                            break;
                            
                        case "set":
                            economy.setBalance(target.getUniqueId(), amount);
                            player.sendMessage("§aSet §e" + target.getName() + "'s§a balance to §e" + Economy.format(amount));
                            target.sendMessage("§aYour balance was set to §e" + Economy.format(amount));
                            break;
                    }
                    
                    return true;
                }
            }
            
            if(args.length == 0) {
                // Check own balance
                double balance = economy.getBalance(player.getUniqueId());
                player.sendMessage("§e§lYour Balance: §a" + Economy.format(balance));
            } else if(args.length == 1) {
                // Check another player's balance (requires permission)
                if(!player.hasPermission("simple.balance.others") && !player.isOp()) {
                    player.sendMessage("§cYou don't have permission to check other players' balances.");
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[0]);
                if(target == null) {
                    player.sendMessage("§cPlayer not found.");
                    return true;
                }
                
                double balance = economy.getBalance(target.getUniqueId());
                player.sendMessage("§e" + target.getName() + "'s Balance: §a" + Economy.format(balance));
            }
            return true;
        }
        
        // /pay or /givemoney or /sendmoney
        if(label.equalsIgnoreCase("pay") || label.equalsIgnoreCase("givemoney") || label.equalsIgnoreCase("sendmoney")) {
            if(!(sender instanceof Player)) {
                sender.sendMessage("Only players can pay others.");
                return true;
            }
            
            Player player = (Player) sender;
            
            if(args.length != 2) {
                player.sendMessage("§cUsage: /pay <player> <amount>");
                return true;
            }
            
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            
            if(target.getUniqueId().equals(player.getUniqueId())) {
                player.sendMessage("§cYou cannot pay yourself!");
                return true;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(args[1]);
            } catch(NumberFormatException e) {
                player.sendMessage("§cInvalid amount. Please enter a valid number.");
                return true;
            }
            
            if(amount <= 0) {
                player.sendMessage("§cAmount must be greater than 0.");
                return true;
            }
            
            if(!economy.hasBalance(player.getUniqueId(), amount)) {
                player.sendMessage("§cYou don't have enough money! Your balance: §e" + 
                    Economy.format(economy.getBalance(player.getUniqueId())));
                return true;
            }
            
            if(economy.transfer(player.getUniqueId(), target.getUniqueId(), amount)) {
                player.sendMessage("§aYou sent §e" + Economy.format(amount) + "§a to §e" + target.getName());
                target.sendMessage("§aYou received §e" + Economy.format(amount) + "§a from §e" + player.getName());
            } else {
                player.sendMessage("§cTransaction failed.");
            }
            
            return true;
        }
        
        // /baltop or /moneytop or /richest or /balancetop
        if(label.equalsIgnoreCase("baltop") || label.equalsIgnoreCase("moneytop") || 
           label.equalsIgnoreCase("richest") || label.equalsIgnoreCase("balancetop")) {
            sender.sendMessage("§e§l===== Top Balances =====");
            
            Map<UUID, Double> topBalances = economy.getTopBalances(10);
            
            if(topBalances.isEmpty()) {
                sender.sendMessage("§7No balances found.");
                return true;
            }
            
            int rank = 1;
            for(Map.Entry<UUID, Double> entry : topBalances.entrySet()) {
                String playerName = Bukkit.getOfflinePlayer(entry.getKey()).getName();
                if(playerName == null) playerName = "Unknown";
                
                sender.sendMessage("§a" + rank + ". §e" + playerName + " §7- §a" + Economy.format(entry.getValue()));
                rank++;
            }
            
            return true;
        }
        
        // /eco or /economy
        if(label.equalsIgnoreCase("eco") || label.equalsIgnoreCase("economy")) {
            if(!sender.hasPermission("simple.eco") && !sender.isOp()) {
                sender.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            
            if(args.length < 3) {
                sender.sendMessage("§cUsage: /eco <give|take|set> <player> <amount>");
                return true;
            }
            
            String action = args[0].toLowerCase();
            Player target = Bukkit.getPlayer(args[1]);
            
            if(target == null) {
                sender.sendMessage("§cPlayer not found.");
                return true;
            }
            
            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch(NumberFormatException e) {
                sender.sendMessage("§cInvalid amount. Please enter a valid number.");
                return true;
            }
            
            if(amount < 0) {
                sender.sendMessage("§cAmount cannot be negative.");
                return true;
            }
            
            switch(action) {
                case "give":
                    economy.addBalance(target.getUniqueId(), amount);
                    sender.sendMessage("§aGave §e" + Economy.format(amount) + "§a to §e" + target.getName());
                    target.sendMessage("§aYou received §e" + Economy.format(amount) + "§a from the server.");
                    break;
                    
                case "take":
                    if(economy.removeBalance(target.getUniqueId(), amount)) {
                        sender.sendMessage("§aTook §e" + Economy.format(amount) + "§a from §e" + target.getName());
                        target.sendMessage("§c" + Economy.format(amount) + "§c was taken from your account.");
                    } else {
                        sender.sendMessage("§cPlayer doesn't have enough money.");
                    }
                    break;
                    
                case "set":
                    economy.setBalance(target.getUniqueId(), amount);
                    sender.sendMessage("§aSet §e" + target.getName() + "'s§a balance to §e" + Economy.format(amount));
                    target.sendMessage("§aYour balance was set to §e" + Economy.format(amount));
                    break;
                    
                default:
                    sender.sendMessage("§cUsage: /eco <give|take|set> <player> <amount>");
                    break;
            }
            
            return true;
        }
        
        return false;
    }
}

