package com.itsvollx.simple.inventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class PagedMenu {
    // Track what page each player is on for each menu
    private static final Map<String, Map<UUID, Integer>> MENU_PAGES = new HashMap<>();
    
    protected int size = 54; // 6 rows
    protected int itemsPerPage = 36; // 4 rows of items (rows 2-5)
    protected int contentStartSlot = 9; // Start after first row
    
    /**
     * Unique key for this menu type (for page tracking)
     */
    protected abstract String getMenuKey();
    
    /**
     * Get the title for this page
     * @param page Current page (0-indexed)
     * @param totalPages Total number of pages
     */
    protected abstract String getTitle(int page, int totalPages);
    
    /**
     * Get all items to display (will be paginated automatically)
     */
    protected abstract List<ItemStack> getItems(Player player);
    
    /**
     * Message to show when there are no items
     */
    protected String getEmptyMessage() {
        return ChatColor.RED + "No items to display!";
    }
    
    /**
     * Hook called before opening (for custom decorations)
     */
    protected void onBeforeOpen(Player player, Inventory inventory) {
        // Override if needed
    }
    
    /**
     * Open the menu for a player on a specific page
     */
    public void open(Player player, int page) {
        List<ItemStack> items = getItems(player);
        
        // Check if empty
        if(items == null || items.isEmpty()) {
            player.sendMessage(getEmptyMessage());
            return;
        }
        
        // Calculate pages
        int totalPages = (int) Math.ceil((double) items.size() / itemsPerPage);
        
        // Clamp page to valid range
        page = Math.max(0, Math.min(page, totalPages - 1));
        
        // Track player's page
        setPlayerPage(getMenuKey(), player.getUniqueId(), page);
        
        // Create inventory
        Inventory inv = Bukkit.createInventory(null, size, getTitle(page, totalPages));
        
        // Apply layout
        applyDefaultLayout(inv, page, totalPages);
        
        // Fill with page items
        fillPageItems(inv, items, page);
        
        // Custom hook
        onBeforeOpen(player, inv);
        
        // Open
        player.openInventory(inv);
    }
    
    /**
     * Open menu on current/default page
     */
    public void open(Player player) {
        int page = getPlayerPage(getMenuKey(), player.getUniqueId());
        open(player, page);
    }
    
    private void applyDefaultLayout(Inventory inv, int page, int totalPages) {
        // Gray glass pane border on first row
        ItemStack border = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta borderMeta = border.getItemMeta();
        if(borderMeta != null) {
            borderMeta.setDisplayName(" ");
            border.setItemMeta(borderMeta);
        }
        
        for(int i = 0; i < 9; i++) {
            inv.setItem(i, border);
        }
        
        // Navigation buttons in last row
        ItemStack prevButton = new ItemStack(Material.ARROW);
        ItemMeta prevMeta = prevButton.getItemMeta();
        if(prevMeta != null) {
            prevMeta.setDisplayName(ChatColor.YELLOW + "← Previous Page");
            prevButton.setItemMeta(prevMeta);
        }
        
        ItemStack nextButton = new ItemStack(Material.ARROW);
        ItemMeta nextMeta = nextButton.getItemMeta();
        if(nextMeta != null) {
            nextMeta.setDisplayName(ChatColor.YELLOW + "Next Page →");
            nextButton.setItemMeta(nextMeta);
        }
        
        ItemStack closeButton = new ItemStack(Material.BARRIER);
        ItemMeta closeMeta = closeButton.getItemMeta();
        if(closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            closeButton.setItemMeta(closeMeta);
        }
        
        // Bottom row layout: [Border][Prev][Border][...][Close][...][Border][Next][Border]
        // Fill bottom row with border
        for(int i = 45; i < 54; i++) {
            inv.setItem(i, border);
        }
        
        // Add buttons
        if(page > 0) {
            inv.setItem(45, prevButton); // Left side
        }
        if(page < totalPages - 1) {
            inv.setItem(53, nextButton); // Right side
        }
        inv.setItem(49, closeButton); // Center
    }
    
    private void fillPageItems(Inventory inv, List<ItemStack> items, int page) {
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, items.size());
        
        int slot = contentStartSlot;
        for(int i = startIndex; i < endIndex && slot < 45; i++) {
            ItemStack item = items.get(i);
            if(item != null) {
                inv.setItem(slot, item);
            }
            slot++;
        }
    }
    
    // Page tracking methods
    private static void setPlayerPage(String menuKey, UUID playerUUID, int page) {
        MENU_PAGES.computeIfAbsent(menuKey, k -> new HashMap<>()).put(playerUUID, page);
    }
    
    private static int getPlayerPage(String menuKey, UUID playerUUID) {
        return MENU_PAGES.getOrDefault(menuKey, new HashMap<>()).getOrDefault(playerUUID, 0);
    }
    
    /**
     * Handle navigation clicks
     * @return true if this was a navigation click
     */
    public static boolean handleClick(Player player, String title, int slot, PagedMenu menu) {
        // Previous page
        if(slot == 45) {
            int currentPage = getPlayerPage(menu.getMenuKey(), player.getUniqueId());
            if(currentPage > 0) {
                menu.open(player, currentPage - 1);
            }
            return true;
        }
        
        // Next page
        if(slot == 53) {
            int currentPage = getPlayerPage(menu.getMenuKey(), player.getUniqueId());
            menu.open(player, currentPage + 1);
            return true;
        }
        
        // Close button
        if(slot == 49) {
            player.closeInventory();
            return true;
        }
        
        // Border clicks (first row and last row except buttons)
        if(slot < 9 || (slot >= 45 && slot != 45 && slot != 49 && slot != 53)) {
            return true;
        }
        
        return false;
    }
}

