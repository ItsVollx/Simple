package com.itsvollx.simple.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.itsvollx.simple.Simple;

public class Hologram {
    
    private String id;
    private Location location;
    private List<Object> lines; // Can be String or ItemStack
    private List<Entity> entities; // ArmorStands or Items
    private boolean persistent;
    private HashMap<UUID, Boolean> playerVisibility; // Per-player visibility
    private Consumer<Player> clickAction; // Click handler
    private boolean following;
    private UUID followingPlayer;
    private int taskId = -1;
    
    // Constants
    private static final double LINE_HEIGHT = 0.25;
    
    public Hologram(String id, Location location, boolean persistent) {
        this.id = id;
        this.location = location.clone();
        this.lines = new ArrayList<>();
        this.entities = new ArrayList<>();
        this.persistent = persistent;
        this.playerVisibility = new HashMap<>();
        this.following = false;
    }
    
    /**
     * Add a text line to the hologram
     */
    public Hologram addLine(String text) {
        lines.add(text);
        return this;
    }
    
    /**
     * Add an item line to the hologram
     */
    public Hologram addLine(ItemStack item) {
        lines.add(item);
        return this;
    }
    
    /**
     * Set all lines at once
     */
    public Hologram setLines(List<String> textLines) {
        lines.clear();
        lines.addAll(textLines);
        if(!entities.isEmpty()) {
            respawn();
        }
        return this;
    }
    
    /**
     * Update a specific line
     */
    public Hologram updateLine(int index, String text) {
        if(index >= 0 && index < lines.size()) {
            lines.set(index, text);
            if(index < entities.size()) {
                Entity entity = entities.get(index);
                if(entity instanceof ArmorStand) {
                    ((ArmorStand) entity).setCustomName(text);
                }
            }
        }
        return this;
    }
    
    /**
     * Spawn the hologram
     */
    public void spawn() {
        if(!entities.isEmpty()) {
            despawn();
        }
        
        Location currentLoc = location.clone().add(0, (lines.size() - 1) * LINE_HEIGHT, 0);
        
        for(Object line : lines) {
            if(line instanceof String) {
                // Create armor stand for text
                ArmorStand stand = (ArmorStand) location.getWorld().spawnEntity(currentLoc, EntityType.ARMOR_STAND);
                stand.setVisible(false);
                stand.setGravity(false);
                stand.setCustomNameVisible(true);
                stand.setCustomName((String) line);
                stand.setMarker(true);
                stand.setInvulnerable(true);
                stand.setCollidable(false);
                stand.setPersistent(persistent);
                entities.add(stand);
            } else if(line instanceof ItemStack) {
                // Create dropped item for item hologram
                Item item = location.getWorld().dropItem(currentLoc, (ItemStack) line);
                item.setGravity(false);
                item.setPickupDelay(Integer.MAX_VALUE);
                item.setInvulnerable(true);
                item.setVelocity(item.getVelocity().zero());
                item.setPersistent(persistent);
                entities.add(item);
            }
            currentLoc.subtract(0, LINE_HEIGHT, 0);
        }
        
        // Apply per-player visibility
        updateVisibility();
    }
    
    /**
     * Despawn the hologram
     */
    public void despawn() {
        for(Entity entity : entities) {
            entity.remove();
        }
        entities.clear();
        stopFollowing();
    }
    
    /**
     * Respawn the hologram (useful after updates)
     */
    public void respawn() {
        despawn();
        spawn();
    }
    
    /**
     * Teleport hologram to new location
     */
    public void teleport(Location newLocation) {
        this.location = newLocation.clone();
        respawn();
    }
    
    /**
     * Set visibility for a specific player
     */
    public void setVisibility(Player player, boolean visible) {
        playerVisibility.put(player.getUniqueId(), visible);
        updateVisibilityForPlayer(player);
    }
    
    /**
     * Update visibility for all players
     */
    private void updateVisibility() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            updateVisibilityForPlayer(player);
        }
    }
    
    /**
     * Update visibility for a specific player
     */
    private void updateVisibilityForPlayer(Player player) {
        Boolean visible = playerVisibility.get(player.getUniqueId());
        if(visible != null && !visible) {
            // Hide from player
            for(Entity entity : entities) {
                player.hideEntity(Simple.pl, entity);
            }
        } else {
            // Show to player
            for(Entity entity : entities) {
                player.showEntity(Simple.pl, entity);
            }
        }
    }
    
    /**
     * Set click action handler
     */
    public Hologram setClickAction(Consumer<Player> action) {
        this.clickAction = action;
        return this;
    }
    
    /**
     * Handle click event
     */
    public void onClick(Player player) {
        if(clickAction != null) {
            clickAction.accept(player);
        }
    }
    
    /**
     * Check if entity belongs to this hologram
     */
    public boolean isPartOfHologram(Entity entity) {
        return entities.contains(entity);
    }
    
    /**
     * Make hologram follow a player
     */
    public void followPlayer(Player player) {
        this.following = true;
        this.followingPlayer = player.getUniqueId();
        
        taskId = Bukkit.getScheduler().runTaskTimer(Simple.pl, () -> {
            Player p = Bukkit.getPlayer(followingPlayer);
            if(p == null || !p.isOnline()) {
                stopFollowing();
                return;
            }
            
            Location newLoc = p.getLocation().add(0, 2, 0);
            if(!location.getWorld().equals(newLoc.getWorld()) || location.distance(newLoc) > 0.5) {
                teleport(newLoc);
            }
        }, 0L, 2L).getTaskId();
    }
    
    /**
     * Stop following player
     */
    public void stopFollowing() {
        if(taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
        this.following = false;
        this.followingPlayer = null;
    }
    
    /**
     * Start an animation (cycling through different texts)
     */
    public void startAnimation(int lineIndex, List<String> frames, long tickDelay) {
        if(lineIndex < 0 || lineIndex >= lines.size()) return;
        
        final int[] currentFrame = {0};
        taskId = Bukkit.getScheduler().runTaskTimer(Simple.pl, () -> {
            updateLine(lineIndex, frames.get(currentFrame[0]));
            currentFrame[0] = (currentFrame[0] + 1) % frames.size();
        }, 0L, tickDelay).getTaskId();
    }
    
    /**
     * Stop any running animation
     */
    public void stopAnimation() {
        if(taskId != -1) {
            Bukkit.getScheduler().cancelTask(taskId);
            taskId = -1;
        }
    }
    
    // Getters
    public String getId() { return id; }
    public Location getLocation() { return location.clone(); }
    public List<Object> getLines() { return new ArrayList<>(lines); }
    public boolean isPersistent() { return persistent; }
    public boolean isFollowing() { return following; }
    public UUID getFollowingPlayer() { return followingPlayer; }
}


