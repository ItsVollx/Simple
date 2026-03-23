package com.itsvollx.simple.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class EnderChestInventory extends CustomInventory {
    
    public EnderChestInventory() {
        super("§5Ender Chest", 27);
    }
    
    @Override
    public void create() {
        // Ender chest inventory is per-player, so we don't pre-create it
    }
    
    @Override
    public void open(Player player) {
        // Open player's actual ender chest
        player.openInventory(player.getEnderChest());
    }
}

