package com.itsvollx.simple.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public abstract class CustomInventory {
    protected Inventory inventory;
    protected String title;
    protected int size;
    
    public CustomInventory(String title, int size) {
        this.title = title;
        this.size = size;
    }
    
    /**
     * Create the inventory - must be implemented by subclasses
     */
    public abstract void create();
    
    /**
     * Open the inventory for a player
     */
    public void open(Player player) {
        if(inventory == null) {
            create();
        }
        player.openInventory(inventory);
    }
    
    public Inventory getInventory() {
        return inventory;
    }
    
    public String getTitle() {
        return title;
    }
    
    public int getSize() {
        return size;
    }
}

