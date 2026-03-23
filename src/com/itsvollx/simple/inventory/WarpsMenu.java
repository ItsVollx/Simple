package com.itsvollx.simple.inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

public class WarpsMenu extends PagedMenu {

    private SimpleConfigs sc = new SimpleConfigs(Simple.pl);

    @Override
    protected String getMenuKey() {
        return "warps";
    }

    @Override
    protected String getTitle(int page, int totalPages) {
        if (totalPages <= 1) {
            return "§6Warps";
        }
        return "§6Warps §7(Page " + (page + 1) + "/" + totalPages + ")";
    }

    @Override
    protected List<ItemStack> getItems(Player player) {
        List<ItemStack> items = new ArrayList<>();
        
        String warpsFilePath = sc.getFilePath("Warps.yml");
        YamlConfiguration config = SimpleConfigs.getConfig(warpsFilePath);
        
        if (config.getConfigurationSection("Warps.") != null) {
            Set<String> warpNames = config.getConfigurationSection("Warps.").getKeys(false);
            
            for (String warpName : warpNames) {
                ItemStack item = new ItemStack(Material.COMPASS);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName("§a" + warpName);
                
                List<String> lore = new ArrayList<>();
                lore.add("§7Click to teleport");
                meta.setLore(lore);
                
                item.setItemMeta(meta);
                items.add(item);
            }
        }
        
        return items;
    }

    @Override
    protected String getEmptyMessage() {
        return "§cNo warps available.";
    }

    /**
     * Handle warp click - teleport to the selected warp
     */
    public boolean handleWarpClick(Player player, ItemStack clicked) {
        if (clicked == null || clicked.getType() != Material.COMPASS) {
            return false;
        }
        
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || meta.getDisplayName() == null) {
            return false;
        }
        
        // Extract warp name (remove color code)
        String warpName = meta.getDisplayName().replace("§a", "");
        
        // Execute warp command
        Bukkit.getScheduler().runTask(Simple.pl, () -> {
            player.performCommand("warp " + warpName);
        });
        
        return true;
    }
}
