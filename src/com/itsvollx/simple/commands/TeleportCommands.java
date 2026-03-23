package com.itsvollx.simple.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.itsvollx.simple.teleport.PlayerTeleport;
import com.itsvollx.simple.teleport.TeleportRequest;

public class TeleportCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(label.equalsIgnoreCase("back")) {
            new PlayerTeleport(player.getUniqueId()).goBackToLocation();
            return true;
        }
        
        if(label.equalsIgnoreCase("tpa")) {
            if(args.length < 1) {
                player.sendMessage("§cUsage: /tpa <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            new TeleportRequest().sendRequest(player, target, false);
            return true;
        }
        
        if(label.equalsIgnoreCase("tpahere")) {
            if(args.length < 1) {
                player.sendMessage("§cUsage: /tpahere <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            new TeleportRequest().sendRequest(player, target, true);
            return true;
        }
        
        if(label.equalsIgnoreCase("tpaccept")) {
            new TeleportRequest().acceptRequest(player);
            return true;
        }
        
        if(label.equalsIgnoreCase("tpdeny")) {
            new TeleportRequest().denyRequest(player);
            return true;
        }
        
        if(label.equalsIgnoreCase("tp")) {
            if(!player.hasPermission("simple.tp")) {
                player.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            if(args.length < 1) {
                player.sendMessage("§cUsage: /tp <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            player.teleport(target.getLocation());
            player.sendMessage("§aTeleported to " + target.getName());
            return true;
        }
        
        if(label.equalsIgnoreCase("tphere")) {
            if(!player.hasPermission("simple.tphere")) {
                player.sendMessage("§cYou don't have permission to use this command.");
                return true;
            }
            if(args.length < 1) {
                player.sendMessage("§cUsage: /tphere <player>");
                return true;
            }
            Player target = Bukkit.getPlayer(args[0]);
            if(target == null) {
                player.sendMessage("§cPlayer not found.");
                return true;
            }
            target.teleport(player.getLocation());
            player.sendMessage("§aTeleported " + target.getName() + " to you.");
            target.sendMessage("§aYou have been teleported to " + player.getName());
            return true;
        }
        
        return false;
    }
}

