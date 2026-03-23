package com.itsvollx.simple.sign;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;

public class Signs {

	public void Sign(Block block, PlayerInteractEvent event) {
		
        Sign sign = (Sign) block.getState();
        Player p = event.getPlayer();
        // Read lines from the sign
        String[] lines = sign.getLines();
        event.setCancelled(true);

        if(lines[0].startsWith("[") && lines[0].endsWith("]")) {
        String signcommand = lines[0].replace("[", "").replace("]", "");
        
        PluginCommand command = Bukkit.getPluginCommand(lines[0].replace("[", "").replace("]", ""));
        if(command == null) return;
        if(!p.hasPermission("sign." + signcommand) && !p.isOp())return;

        // Concatenate lines 2, 3, and 4 as arguments
        StringBuilder args = new StringBuilder();
        for(int i = 1; i < lines.length; i++) {
            if(!lines[i].isEmpty()) {
                if(args.length() > 0) args.append(" ");
                args.append(lines[i]);
            }
        }
        
        p.performCommand(signcommand + " " + args.toString());
        
        }
        
	}
	
	
}


