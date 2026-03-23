package com.itsvollx.simple.inventory;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TrashInventory extends CustomInventory {
    
    public TrashInventory() {
        super("§cTrash", 27); // 3 rows
    }
    
    @Override
    public void create() {
        inventory = Bukkit.createInventory(null, size, title);
        
        // Add decorative border
        ItemStack border = new ItemStack(Material.RED_STAINED_GLASS_PANE);
        ItemMeta meta = border.getItemMeta();
        if(meta != null) {
            meta.setDisplayName(" ");
            border.setItemMeta(meta);
        }
        
        // Top row border
        for(int i = 0; i < 9; i++) {
            inventory.setItem(i, border);
        }
        
        // Bottom row border
        for(int i = 18; i < 27; i++) {
            inventory.setItem(i, border);
        }
        
        // Middle row is empty for items
    }
}

