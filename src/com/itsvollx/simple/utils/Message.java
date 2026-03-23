package com.itsvollx.simple.utils;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class Message {

	SimpleConfigs sc = new SimpleConfigs(Simple.pl).getSimpleConfigs();
			
	private static HashMap<String, String> Messages = new HashMap<String, String>();
	
	
	
	public void Register() {

    Messages = sc.Section(sc.getConfigPath("Messages.yml"), "Messages");
        
	}

	public static String getMessage(String str) {

    return ChatColor.translateAlternateColorCodes('&', Messages.get(str));
	}
	
	
}


