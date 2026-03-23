package com.itsvollx.simple.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.itsvollx.simple.warps.Warps;

public class WarpCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        Warps warps = new Warps(player.getUniqueId());
        
        if(label.equalsIgnoreCase("warps")) {
            warps.openGUI();
            return true;
        }
        
        if(label.equalsIgnoreCase("warp")) {
            if(args.length < 1) {
                player.sendMessage("§cUsage: /warp <warpname>");
                return true;
            }
            warps.Teleport(args[0].replace(".", ""));
            return true;
        }
        
        if(label.equalsIgnoreCase("setwarp")) {
            if(args.length < 1) {
                player.sendMessage("§cUsage: /setwarp <warpname>");
                return true;
            }
            warps.add(args[0].replace(".", ""));
            player.sendMessage("§aWarp '" + args[0] + "' has been created.");
            return true;
        }
        
        if(label.equalsIgnoreCase("delwarp")) {
            if(!player.hasPermission("simple.delwarp")) {
                player.sendMessage("§cYou don't have permission to delete warps.");
                return true;
            }
            if(args.length < 1) {
                player.sendMessage("§cUsage: /delwarp <warpname>");
                return true;
            }
            warps.delete(args[0].replace(".", ""));
            return true;
        }
        
        return false;
    }
}

