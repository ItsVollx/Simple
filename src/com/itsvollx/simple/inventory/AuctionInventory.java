package com.itsvollx.simple.inventory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.itsvollx.simple.config.SimpleConfigs;
import com.itsvollx.simple.utils.Economy;

public class AuctionInventory extends PagedMenu {
    private static final String MENU_KEY = "auction";
    
    @Override
    protected String getMenuKey() {
        return MENU_KEY;
    }
    
    @Override
    protected String getTitle(int page, int totalPages) {
        return ChatColor.GOLD + "Auction House " + ChatColor.GRAY + "(" + (page + 1) + "/" + totalPages + ")";
    }
    
    @Override
    protected List<ItemStack> getItems(Player player) {
        List<ItemStack> items = new ArrayList<>();
        
        String auctionPath = SimpleConfigs.PluginFolder + File.separator + "AuctionHouse.yml";
        YamlConfiguration config = SimpleConfigs.getConfig(auctionPath);
        
        if(config.getConfigurationSection("listings") == null) {
            return items;
        }
        
        for(String listingId : config.getConfigurationSection("listings").getKeys(false)) {
            ItemStack item = config.getItemStack("listings." + listingId + ".item");
            String sellerName = config.getString("listings." + listingId + ".seller-name");
            String sellerUUID = config.getString("listings." + listingId + ".seller");
            double price = config.getDouble("listings." + listingId + ".price");
            
            if(item != null) {
                ItemStack displayItem = item.clone();
                ItemMeta meta = displayItem.getItemMeta();
                
                if(meta != null) {
                    List<String> lore = meta.hasLore() ? meta.getLore() : new ArrayList<>();
                    lore.add("");
                    lore.add(ChatColor.GRAY + "Seller: " + ChatColor.YELLOW + sellerName);
                    lore.add(ChatColor.GRAY + "Price: " + ChatColor.GREEN + Economy.format(price));
                    lore.add("");
                   if(sellerUUID.equals(player.getUniqueId().toString())) {
                        lore.add(ChatColor.YELLOW + "Right-click to cancel listing");
                    } else {
                        lore.add(ChatColor.GREEN + "Left-click to purchase");
                    }
                    
                    meta.setLore(lore);
                    displayItem.setItemMeta(meta);
                }
                
                items.add(displayItem);
            }
        }
        
        return items;
    }
    
    @Override
    protected String getEmptyMessage() {
        return ChatColor.RED + "No items are currently listed on the auction house.";
    }
}

