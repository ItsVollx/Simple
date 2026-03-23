package com.itsvollx.simple.listeners;

import java.util.Arrays;
import java.util.List;

import com.itsvollx.simple.Simple;
import com.itsvollx.simple.config.SimpleConfigs;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.attribute.Attributable;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.block.Sign;
import org.bukkit.block.Block;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Tameable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.Location;
import com.itsvollx.simple.inventory.PagedMenu;
import com.itsvollx.simple.inventory.HomesMenu;
import com.itsvollx.simple.inventory.AuctionInventory;
import com.itsvollx.simple.inventory.WarpsMenu;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import com.itsvollx.simple.utils.Hologram;
import com.itsvollx.simple.utils.HologramManager;
import com.itsvollx.simple.utils.Message;
import com.itsvollx.simple.sign.Signs;
import com.itsvollx.simple.warps.Warps;
import com.itsvollx.simple.shop.AuctionHouse;
import com.itsvollx.simple.utils.SitAndLay;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class SimpleEvents implements Listener {
	//private final CustomZombie customZombie;
	private static Simple plugin;
    private Message Message = new Message();
	
	public SimpleEvents(Simple plugin) {

		SimpleEvents.plugin = plugin;
		//this.customZombie = new CustomZombie(plugin);
	}

	@EventHandler
    public void onEntityExplode(EntityExplodeEvent event) {
        // Check if the exploding entity is a creeper
        if (event.getEntityType() == EntityType.CREEPER) {
            event.setCancelled(true);  // Cancel the explosion
        }
        
    }
	
	@EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        new SimpleConfigs(plugin).createUserFiles(player.getUniqueId());
        player.sendMessage(Message.getMessage("Messages.general.welcome") + " ");
    }
    
	 @EventHandler
	    public void onSignClick(PlayerInteractEvent event) {
	        // Check if the action is a right-click
	        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
	            Block block = event.getClickedBlock();

	            // Check if the block is not null and is a sign
	            if (block != null && (Tag.SIGNS.isTagged(block.getType()))) {
	            new Signs().Sign(block, event);
	                
	            }
	            
	        }
	        
	    }
	 
	 @EventHandler
	 public void onSignChange(SignChangeEvent event) {
	     Player player = event.getPlayer();
	     String[] lines = event.getLines();
	     Block block = event.getBlock();
	     
	     // Check if this is an existing sign being edited
	     if(block.getState() instanceof Sign) {
	         Sign sign = (Sign) block.getState();
	         NamespacedKey ownerKey = new NamespacedKey(plugin, "sign_owner");
	         
	         // If sign already has an owner (being edited, not newly placed)
	         if(sign.getPersistentDataContainer().has(ownerKey, PersistentDataType.STRING)) {
	             String ownerUUID = sign.getPersistentDataContainer().get(ownerKey, PersistentDataType.STRING);
	             
	             // Allow edit if: player is owner, has permission, or is op
	             if(!player.getUniqueId().toString().equals(ownerUUID) 
	                 && !player.hasPermission("simple.sign.edit") 
	                 && !player.isOp()) {
	                 event.setCancelled(true);
	                 player.sendMessage("§cYou don't have permission to edit this sign.");
	                 return;
	             }
	         } else {
	             // New sign - store the owner after event completes
	             org.bukkit.Bukkit.getScheduler().runTask(plugin, () -> {
	                 if(block.getState() instanceof Sign) {
	                     Sign newSign = (Sign) block.getState();
	                     newSign.getPersistentDataContainer().set(ownerKey, PersistentDataType.STRING, player.getUniqueId().toString());
	                     newSign.update();
	                 }
	             });
	         }
	     }
	     
	     // Check if first line has command syntax [command]
	     if(lines[0].startsWith("[") && lines[0].endsWith("]")) {
	         // Require permission to create command signs
	         if(!player.hasPermission("simple.sign.create") && !player.isOp()) {
	             event.setCancelled(true);
	             player.sendMessage("§cYou don't have permission to create command signs.");
	             return;
	         }
	         player.sendMessage("§aCommand sign created: " + lines[0]);
	     }
	 }
	 
	 
	 @EventHandler
	    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
	        // Ensure the event is only triggered for the main hand
	        if (event.getHand() != EquipmentSlot.HAND) {
	            return;
	        }

	        Entity entity = event.getRightClicked();
	        Player player = event.getPlayer();

	        ItemStack itemInHand = player.getInventory().getItemInMainHand();
	        if (itemInHand != null && itemInHand.hasItemMeta()) {
	            String itemName = itemInHand.getItemMeta().getDisplayName();

	            if (itemName.equals("info")) {
	                printMinecraftTags(player, entity);
	            }
	        }
	    }
    	    
	 private void printMinecraftTags(Player player, Entity entity) {
	        player.sendMessage("Data for entity: " + entity.getType().name());

	        // Print Persistent Data
	        PersistentDataContainer dataContainer = entity.getPersistentDataContainer();
	        dataContainer.getKeys().forEach(namespacedKey -> {
	            String value = dataContainer.get(namespacedKey, PersistentDataType.STRING);
	            if (value != null) {
	                player.sendMessage(namespacedKey.getNamespace() + ":" + namespacedKey.getKey() + " = " + value);
	            } else {
	                player.sendMessage(namespacedKey.getNamespace() + ":" + namespacedKey.getKey() + " has no value.");
	            }
	        });

	        // Print Metadata
	        List<MetadataValue> metadataValues = entity.getMetadata("");
	        for (MetadataValue metadataValue : metadataValues) {
	            player.sendMessage("Metadata - " + metadataValue.getOwningPlugin().getName() + ": " + metadataValue.value());
	        }

	        
	        // Print Attributes (if applicable)
	        if (entity instanceof Attributable) {
	            Attributable attributable = (Attributable) entity;
	            for (Attribute attribute : Attribute.values()) {
	                AttributeInstance attributeInstance = attributable.getAttribute(attribute);
	                if (attributeInstance != null) {
	                    player.sendMessage("Attribute - " + attribute.name() + ": " + attributeInstance.getValue());
	                }
	            }
	        }

	        // Print Entity Type-Specific Data (if applicable)
	        if (entity instanceof org.bukkit.entity.Ageable) {
	            org.bukkit.entity.Ageable ageable = (org.bukkit.entity.Ageable) entity;
	            player.sendMessage("Ageable - isAdult: " + ageable.isAdult());
	        }

	        if (entity instanceof Tameable) {
	            Tameable tameable = (Tameable) entity;
	            player.sendMessage("Tameable - isTamed: " + tameable.isTamed());
	            if (tameable.getOwner() != null) {
	                player.sendMessage("Tameable - Owner: " + tameable.getOwner().getName());
	            }
	        }
	        
	        if (entity instanceof Animals) {
	            Animals animal = (Animals) entity;

	            // Check if the animal is capable of breeding
	            boolean isBreedable = animal.getAge() == 0 && animal.getLoveModeTicks() == 0;

	            player.sendMessage("Breedable - isBreedable: " + isBreedable);
	        }
	        
	        if (entity instanceof InventoryHolder) {
	            Inventory inventory = ((InventoryHolder) entity).getInventory();

	            // Filter non-null items from storage contents
	            List<ItemStack> items = Arrays.stream(inventory.getStorageContents())
	                                          .filter(item -> item != null) // Exclude null items
	                                          .toList();

	            player.sendMessage("Inventory Items: " + items);
	        }
	        
	    }
	    
	 @EventHandler
	 public void onInventoryClick(InventoryClickEvent event) {
	     if(!(event.getWhoClicked() instanceof Player)) return;
	     
	     Player player = (Player) event.getWhoClicked();
	     String title = event.getView().getTitle();
	     int slot = event.getSlot();
	     ItemStack clickedItem = event.getCurrentItem();
	     
	     // Handle paginated warps menu
	     if(title.contains("Warps")) {
	         event.setCancelled(true);
	         
	         WarpsMenu menu = new WarpsMenu();
	         
	         // Check for navigation clicks
	         if(PagedMenu.handleClick(player, title, slot, menu)) {
	             return;
	         }
	         
	         // Handle warp item clicks
	         if(clickedItem != null && clickedItem.hasItemMeta()) {
	             player.closeInventory();
	             menu.handleWarpClick(player, clickedItem);
	         }
	         return;
	     }
	     
	     // Handle paginated homes menu
	     if(title.contains("Homes")) {
	         event.setCancelled(true);
	         
	         HomesMenu menu = new HomesMenu();
	         
	         // Check for navigation clicks
	         if(PagedMenu.handleClick(player, title, slot, menu)) {
	             return;
	         }
	         
	         // Handle home item clicks
	         if(clickedItem != null && clickedItem.hasItemMeta()) {
	             boolean isShiftRightClick = event.getClick() == ClickType.SHIFT_RIGHT;
	             menu.handleHomeClick(player, clickedItem, isShiftRightClick);
	         }
	         return;
	     }
	     
	     // Handle paginated auction house
	     if(title.contains("Auction House")) {
	         event.setCancelled(true);
	         
	         AuctionInventory menu = new AuctionInventory();
	         
	         // Check for navigation clicks
	         if(PagedMenu.handleClick(player, title, slot, menu)) {
	             return;
	         }
	         
	         // Handle item purchase/cancel
	         if(clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasLore()) {
	             List<String> lore = clickedItem.getItemMeta().getLore();
	             
	             // Extract listing ID from config
	             String auctionPath = SimpleConfigs.PluginFolder + File.separator + "AuctionHouse.yml";
	             YamlConfiguration config = SimpleConfigs.getConfig(auctionPath);
	             
	             if(config.getConfigurationSection("listings") == null) return;
	             
	             // Find matching listing by comparing items
	             for(String listingId : config.getConfigurationSection("listings").getKeys(false)) {
	                 ItemStack listedItem = config.getItemStack("listings." + listingId + ".item");
	                 String sellerUUID = config.getString("listings." + listingId + ".seller");
	                 
	                 if(listedItem != null && listedItem.getType() == clickedItem.getType()) {
	                     AuctionHouse ah = new AuctionHouse(player);
	                     
	                     // Right-click to cancel own listing
	                     if(event.getClick() == ClickType.RIGHT && sellerUUID.equals(player.getUniqueId().toString())) {
	                         ah.cancelListing(listingId);
	                         player.closeInventory();
	                         // Reopen menu
	                         Bukkit.getScheduler().runTaskLater(plugin, () -> menu.open(player), 1L);
	                         return;
	                     }
	                     
	                     // Left-click to buy
	                     if(event.getClick() == ClickType.LEFT && !sellerUUID.equals(player.getUniqueId().toString())) {
	                         if(ah.buyItem(listingId)) {
	                             player.closeInventory();
	                             // Reopen menu
	                             Bukkit.getScheduler().runTaskLater(plugin, () -> menu.open(player), 1L);
	                         }
	                         return;
	                     }
	                     
	                     break;
	                 }
	             }
	         }
	         return;
	     }
	     
	     // Handle trash inventory - allow middle row only
	     if(title.equals("§cTrash")) {
	         // Prevent clicking border items (rows 1 and 3)
	         if(slot < 9 || slot >= 18) {
	             event.setCancelled(true);
	         }
	         return;
	     }
	 }
 
 @EventHandler
 public void onBedEnter(PlayerBedEnterEvent event) {
	     // Check if bed-set-home is disabled in config
	     String configPath = new SimpleConfigs(plugin).getConfigPath("config.yml");
	     boolean bedSetHome = SimpleConfigs.getConfig(configPath).getBoolean("bed-set-home", true);
	     
	     if(!bedSetHome) {
	         // Cancel setting respawn point but allow sleeping
	         if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
	             Player player = event.getPlayer();
	             player.sendMessage("§7Bed homes are disabled on this server.");
	         }
	     }
	 }
	 
	 @EventHandler
	 public void onPlayerMove(PlayerMoveEvent event) {
	     Player player = event.getPlayer();
	     
	     // Check if player moved significantly (not just head movement)
	     Location from = event.getFrom();
	     Location to = event.getTo();
	     
	     if(to == null) return;
	     
	     // Only unsit if player actually moved position (not just looked around)
	     if(from.getBlockX() != to.getBlockX() || 
	        from.getBlockY() != to.getBlockY() || 
	        from.getBlockZ() != to.getBlockZ()) {
	         
	         if(SitAndLay.isSitting(player) || SitAndLay.isLaying(player)) {
	             SitAndLay.removeSitting(player.getUniqueId());
	             player.sendMessage("§7You stood up.");
	         }
	     }
	 }
	 
	 @EventHandler
	 public void onPlayerQuit(PlayerQuitEvent event) {
	     // Clean up sitting/laying when player leaves
	     SitAndLay.removeSitting(event.getPlayer().getUniqueId());
	 }
	 
	 
}

