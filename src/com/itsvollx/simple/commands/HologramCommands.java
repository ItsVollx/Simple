package com.itsvollx.simple.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.itsvollx.simple.utils.Hologram;
import com.itsvollx.simple.utils.HologramManager;

public class HologramCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        
        Player player = (Player) sender;
        
        if(!player.hasPermission("simple.hologram")) {
            player.sendMessage("§cYou don't have permission to use this command.");
            return true;
        }
        
        if(args.length == 0) {
            player.sendMessage("§e§lHologram Commands:");
            player.sendMessage("§a/holo create <id> <text> §7- Create hologram (use | for multiple lines)");
            player.sendMessage("§a/holo delete <id> §7- Delete hologram");
            player.sendMessage("§a/holo list §7- List all holograms");
            player.sendMessage("§a/holo teleport <id> §7- Move hologram to your location");
            player.sendMessage("§a/holo addline <id> <text> §7- Add a line");
            player.sendMessage("§a/holo setline <id> <lineNum> <text> §7- Update a line");
            return true;
        }
        
        String subCmd = args[0].toLowerCase();
        HologramManager hm = HologramManager.getInstance();
        
        switch(subCmd) {
            case "create":
                if(args.length < 3) {
                    player.sendMessage("§cUsage: /holo create <id> <text> (use | for multiple lines)");
                    return true;
                }
                
                String id = args[1];
                java.util.List<String> lines = new java.util.ArrayList<>();
                StringBuilder fullText = new StringBuilder();
                for(int i = 2; i < args.length; i++) {
                    if(i > 2) fullText.append(" ");
                    fullText.append(args[i]);
                }
                String[] splitLines = fullText.toString().split("\\|");
                for(String line : splitLines) {
                    lines.add(line.trim().replace("&", "§"));
                }
                hm.createHologram(id, player.getLocation(), lines, true);
                player.sendMessage("§aHologram '§e" + id + "§a' created!");
                break;
                
            case "delete":
            case "remove":
                if(args.length < 2) {
                    player.sendMessage("§cUsage: /holo delete <id>");
                    return true;
                }
                if(hm.deleteHologram(args[1])) {
                    player.sendMessage("§aHologram deleted.");
                } else {
                    player.sendMessage("§cHologram not found.");
                }
                break;
                
            case "list":
                java.util.List<String> ids = hm.getHologramIds();
                if(ids.isEmpty()) {
                    player.sendMessage("§cNo holograms exist.");
                } else {
                    player.sendMessage("§e§lHolograms (" + ids.size() + "):");
                    for(String holoId : ids) {
                        player.sendMessage("§a- §e" + holoId);
                    }
                }
                break;
                
            case "teleport":
            case "tp":
                if(args.length < 2) {
                    player.sendMessage("§cUsage: /holo teleport <id>");
                    return true;
                }
                Hologram holo = hm.getHologram(args[1]);
                if(holo == null) {
                    player.sendMessage("§cHologram not found.");
                    return true;
                }
                holo.teleport(player.getLocation());
                player.sendMessage("§aHologram moved!");
                break;
                
            case "addline":
                if(args.length < 3) {
                    player.sendMessage("§cUsage: /holo addline <id> <text>");
                    return true;
                }
                Hologram holoAdd = hm.getHologram(args[1]);
                if(holoAdd == null) {
                    player.sendMessage("§cHologram not found.");
                    return true;
                }
                StringBuilder text = new StringBuilder();
                for(int i = 2; i < args.length; i++) {
                    if(i > 2) text.append(" ");
                    text.append(args[i]);
                }
                holoAdd.addLine(text.toString().replace("&", "§"));
                holoAdd.respawn();
                player.sendMessage("§aLine added!");
                break;
                
            case "setline":
                if(args.length < 4) {
                    player.sendMessage("§cUsage: /holo setline <id> <lineNum> <text>");
                    return true;
                }
                Hologram holoSet = hm.getHologram(args[1]);
                if(holoSet == null) {
                    player.sendMessage("§cHologram not found.");
                    return true;
                }
                try {
                    int lineNum = Integer.parseInt(args[2]) - 1;
                    StringBuilder setText = new StringBuilder();
                    for(int i = 3; i < args.length; i++) {
                        if(i > 3) setText.append(" ");
                        setText.append(args[i]);
                    }
                    holoSet.updateLine(lineNum, setText.toString().replace("&", "§"));
                    player.sendMessage("§aLine updated!");
                } catch(NumberFormatException e) {
                    player.sendMessage("§cInvalid line number.");
                }
                break;
                
            default:
                player.sendMessage("§cUnknown subcommand. Use /holo for help.");
                break;
        }
        
        return true;
    }
}

