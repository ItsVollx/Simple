package com.itsvollx.simple.shop;

import com.itsvollx.simple.utils.Economy;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class AuctionHouse {
    
    private static final String AUCTION_FILE = "AuctionHouse.yml";
    private Player player;
    private Economy economy = new Economy();
    
    public AuctionHouse(Player player) {
        this.player = player;
    }
    
    // Get auction file path
    private static String getAuctionPath() {
        return SimpleConfigs.PluginFolder + File.separator + AUCTION_FILE;
    }
    
    // List an item for sale
    public void listItem(ItemStack item, double price) {
        if(item == null || item.getType() == Material.AIR) {
            player.sendMessage("§cYou must hold an item to list!");
            return;
        }
        
        String path = getAuctionPath();
        SimpleConfigs.createFile(path);
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        
        // Generate unique listing ID
        String listingId = UUID.randomUUID().toString();
        
        // Save listing
        config.set("listings." + listingId + ".seller", player.getUniqueId().toString());
        config.set("listings." + listingId + ".seller-name", player.getName());
        config.set("listings." + listingId + ".price", price);
        config.set("listings." + listingId + ".item", item);
        
        new SimpleConfigs().saveConfig(path, config);
        
        // Remove item from player's hand
        player.getInventory().setItemInMainHand(new ItemStack(Material.AIR));
        player.sendMessage("§aListed " + item.getType().name() + " §ax" + item.getAmount() + " for §e" + Economy.format(price));
    }
    
    // Buy an item from auction
    public boolean buyItem(String listingId) {
        String path = getAuctionPath();
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        
        if(!config.contains("listings." + listingId)) {
            player.sendMessage("§cThis listing no longer exists.");
            return false;
        }
        
        String sellerUUID = config.getString("listings." + listingId + ".seller");
        double price = config.getDouble("listings." + listingId + ".price");
        ItemStack item = config.getItemStack("listings." + listingId + ".item");
        
        if(sellerUUID.equals(player.getUniqueId().toString())) {
            player.sendMessage("§cYou cannot buy your own listing!");
            return false;
        }
        
        if(!economy.hasBalance(player.getUniqueId(), price)) {
            player.sendMessage("§cYou don't have enough money! You need §e" + Economy.format(price));
            return false;
        }
        
        // Check if player has inventory space
        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cYour inventory is full!");
            return false;
        }
        
        // Process transaction
        economy.removeBalance(player.getUniqueId(), price);
        economy.addBalance(UUID.fromString(sellerUUID), price);
        
        // Give item to buyer
        player.getInventory().addItem(item);
        
        // Remove listing
        config.set("listings." + listingId, null);
        new SimpleConfigs().saveConfig(path, config);
        
        // Notify players
        player.sendMessage("§aPurchased " + item.getType().name() + " §ax" + item.getAmount() + " for §e" + Economy.format(price));
        
        Player seller = Bukkit.getPlayer(UUID.fromString(sellerUUID));
        if(seller != null && seller.isOnline()) {
            seller.sendMessage("§aYour " + item.getType().name() + " §awas sold for §e" + Economy.format(price));
        }
        
        return true;
    }
    
    // Cancel own listing
    public boolean cancelListing(String listingId) {
        String path = getAuctionPath();
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        
        if(!config.contains("listings." + listingId)) {
            player.sendMessage("§cThis listing no longer exists.");
            return false;
        }
        
        String sellerUUID = config.getString("listings." + listingId + ".seller");
        ItemStack item = config.getItemStack("listings." + listingId + ".item");
        
        if(!sellerUUID.equals(player.getUniqueId().toString())) {
            player.sendMessage("§cYou can only cancel your own listings!");
            return false;
        }
        
        // Check if player has inventory space
        if(player.getInventory().firstEmpty() == -1) {
            player.sendMessage("§cYour inventory is full!");
            return false;
        }
        
        // Return item to player
        player.getInventory().addItem(item);
        
        // Remove listing
        config.set("listings." + listingId, null);
        new SimpleConfigs().saveConfig(path, config);
        
        player.sendMessage("§aListing cancelled. Item returned to your inventory.");
        return true;
    }
    
    // Open auction GUI
    public void openGUI() {
        openGUI(0);
    }
    
    // Open auction GUI with page number
    public void openGUI(int page) {
        String path = getAuctionPath();
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        
        Inventory inv = Bukkit.createInventory(null, 54, "Auction House");
        
        ConfigurationSection listings = config.getConfigurationSection("listings");
        
        if(listings == null || listings.getKeys(false).isEmpty()) {
            ItemStack noItems = new ItemStack(Material.BARRIER);
            ItemMeta meta = noItems.getItemMeta();
            meta.setDisplayName("§cNo items listed");
            noItems.setItemMeta(meta);
            inv.setItem(22, noItems);
            player.openInventory(inv);
            return;
        }
        
        List<String> listingIds = new ArrayList<>(listings.getKeys(false));
        int itemsPerPage = 45;
        int maxPage = (listingIds.size() - 1) / itemsPerPage;
        
        if(page > maxPage) page = maxPage;
        if(page < 0) page = 0;
        
        int startIndex = page * itemsPerPage;
        int endIndex = Math.min(startIndex + itemsPerPage, listingIds.size());
        
        // Add listings to GUI
        int slot = 0;
        for(int i = startIndex; i < endIndex; i++) {
            String listingId = listingIds.get(i);
            ItemStack item = config.getItemStack("listings." + listingId + ".item").clone();
            String sellerName = config.getString("listings." + listingId + ".seller-name");
            double price = config.getDouble("listings." + listingId + ".price");
            String sellerUUID = config.getString("listings." + listingId + ".seller");
            
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();
            lore.add("§7Seller: §e" + sellerName);
            lore.add("§7Price: §a" + Economy.format(price));
            lore.add("");
            
            if(sellerUUID.equals(player.getUniqueId().toString())) {
                lore.add("§eRight-click to cancel listing");
            } else {
                lore.add("§aLeft-click to buy");
            }
            
            meta.setLore(lore);
            item.setItemMeta(meta);
            
            inv.setItem(slot++, item);
        }
        
        // Navigation buttons
        if(page > 0) {
            ItemStack prevPage = new ItemStack(Material.ARROW);
            ItemMeta meta = prevPage.getItemMeta();
            meta.setDisplayName("§7← Previous Page");
            prevPage.setItemMeta(meta);
            inv.setItem(45, prevPage);
        }
        
        if(page < maxPage) {
            ItemStack nextPage = new ItemStack(Material.ARROW);
            ItemMeta meta = nextPage.getItemMeta();
            meta.setDisplayName("§7Next Page →");
            nextPage.setItemMeta(meta);
            inv.setItem(53, nextPage);
        }
        
        // List item button
        ItemStack listButton = new ItemStack(Material.GOLD_INGOT);
        ItemMeta listMeta = listButton.getItemMeta();
        listMeta.setDisplayName("§6List Item");
        List<String> listLore = new ArrayList<>();
        listLore.add("§7Use: §e/ah sell <price>");
        listMeta.setLore(listLore);
        listButton.setItemMeta(listMeta);
        inv.setItem(49, listButton);
        
        player.openInventory(inv);
    }
    
    // Get player's active listings count
    public int getActiveListingsCount(UUID playerUUID) {
        String path = getAuctionPath();
        YamlConfiguration config = SimpleConfigs.getConfig(path);
        
        ConfigurationSection listings = config.getConfigurationSection("listings");
        if(listings == null) return 0;
        
        int count = 0;
        for(String listingId : listings.getKeys(false)) {
            String sellerUUID = config.getString("listings." + listingId + ".seller");
            if(sellerUUID.equals(playerUUID.toString())) {
                count++;
            }
        }
        return count;
    }
}


