package com.itsvollx.simple.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class HomesMenu extends PagedMenu {
    private static final String MENU_KEY = "homes";
    private static final String OTHER_MENU_KEY = "other_homes";
    
    private final UUID targetUuid;
    private final String targetName;
    private final boolean isOtherHomes;
    
    /**
     * Create menu for own homes
     */
    public HomesMenu() {
        this.targetUuid = null;
        this.targetName = null;
        this.isOtherHomes = false;
    }
    
    /**
     * Create menu for another player's homes
     */
    public HomesMenu(UUID targetUuid, String targetName) {
        this.targetUuid = targetUuid;
        this.targetName = targetName;
        this.isOtherHomes = true;
    }
    
    @Override
    protected String getMenuKey() {
        return isOtherHomes ? OTHER_MENU_KEY : MENU_KEY;
    }
    
    @Override
    protected String getTitle(int page, int totalPages) {
        if(isOtherHomes && targetName != null) {
            return ChatColor.DARK_GREEN + targetName + "'s Homes " + ChatColor.GRAY + "(" + (page + 1) + "/" + totalPages + ")";
        }
        return ChatColor.DARK_GREEN + "My Homes " + ChatColor.GRAY + "(" + (page + 1) + "/" + totalPages + ")";
    }
    
    @Override
    protected List<ItemStack> getItems(Player player) {
        List<ItemStack> items = new ArrayList<>();
        
        UUID uuid = isOtherHomes ? targetUuid : player.getUniqueId();
        
        SimpleConfigs sc = new SimpleConfigs(Simple.pl);
        String homeFilePath = sc.getUserFilePath(uuid, "Homes.yml");
        YamlConfiguration homeFile = SimpleConfigs.getConfig(homeFilePath);
        
        if(homeFile.getConfigurationSection("Homes") == null) {
            return items;
        }
        
        Set<String> homes = homeFile.getConfigurationSection("Homes").getKeys(false);
        
        for(String homeName : homes) {
            ItemStack item = new ItemStack(Material.RED_BED);
            ItemMeta meta = item.getItemMeta();
            
            if(meta != null) {
                meta.setDisplayName(ChatColor.YELLOW + homeName);
                
                List<String> lore = new ArrayList<>();
                lore.add(ChatColor.GRAY + "Click to teleport");
                if(!isOtherHomes) {
                    lore.add(ChatColor.RED + "Shift+Right-Click to delete");
                }
                meta.setLore(lore);
                
                item.setItemMeta(meta);
            }
            
            items.add(item);
        }
        
        return items;
    }
    
    @Override
    protected String getEmptyMessage() {
        if(isOtherHomes) {
            return ChatColor.RED + "This player has no homes!";
        }
        return ChatColor.RED + "You have no homes set! Use /sethome <name> to create one.";
    }
    
    /**
     * Handle click on a home item
     * @return true if handled
     */
    public boolean handleHomeClick(Player player, ItemStack clicked, boolean isShiftRightClick) {
        if(clicked == null || !clicked.hasItemMeta()) return false;
        
        String homeName = ChatColor.stripColor(clicked.getItemMeta().getDisplayName());
        
        if(isShiftRightClick && !isOtherHomes) {
            // Delete home
            player.performCommand("delhome " + homeName);
            player.closeInventory();
            // Reopen menu
            Bukkit.getScheduler().runTaskLater(Simple.pl, () -> this.open(player), 1L);
            return true;
        } else {
            // Teleport to home
            if(isOtherHomes && targetName != null) {
                player.performCommand("home " + targetName + " " + homeName);
            } else {
                player.performCommand("home " + homeName);
            }
            player.closeInventory();
            return true;
        }
    }
}

