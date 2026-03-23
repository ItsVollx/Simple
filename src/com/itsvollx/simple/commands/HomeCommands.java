package com.itsvollx.simple.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.itsvollx.simple.inventory.HomesMenu;
import com.itsvollx.simple.homes.Homes;

public class HomeCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        Homes homes = new Homes(player.getUniqueId());
        
        if(label.equalsIgnoreCase("sethome")) {
            if(args.length != 1) {
                player.sendMessage("§cUsage: /sethome <homename>");
                return true;
            }
            homes.setHome(args[0].replace(".", ""));
            player.sendMessage("§aHome '" + args[0] + "' has been set!");
            return true;
        }
        
        if(label.equalsIgnoreCase("delhome")) {
            if(args.length != 1) {
                player.sendMessage("§cUsage: /delhome <homename>");
                return true;
            }
            homes.delHome(args[0].replace(".", ""));
            player.sendMessage("§aHome '" + args[0] + "' has been deleted!");
            return true;
        }
        
        if(label.equalsIgnoreCase("home")) {
            if(args.length != 1) {
                player.sendMessage("§cUsage: /home <homename>");
                return true;
            }
            homes.goHome(args[0].replace(".", ""));
            return true;
        }
        
        if(label.equalsIgnoreCase("homes") || label.equalsIgnoreCase("homelist")) {
            HomesMenu menu = new HomesMenu();
            menu.open(player);
            return true;
        }
        
        return false;
    }
}

