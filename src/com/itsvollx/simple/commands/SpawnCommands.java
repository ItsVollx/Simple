package com.itsvollx.simple.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.itsvollx.simple.teleport.Spawn;

public class SpawnCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        Spawn spawn = new Spawn();
        
        if(label.equalsIgnoreCase("setspawn")) {
            if(!player.hasPermission("simple.setspawn")) {
                player.sendMessage("§cYou don't have permission to set spawn.");
                return true;
            }
            spawn.setSpawn(player.getLocation());
            player.sendMessage("§aSpawn location set to your current position.");
            return true;
        }
        
        if(label.equalsIgnoreCase("spawn")) {
            spawn.teleportToSpawn(player);
            return true;
        }
        
        return false;
    }
}

