package com.itsvollx.simple.utils;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.itsvollx.simple.Simple;

public class SitAndLay {
    
    private static HashMap<UUID, Entity> sittingPlayers = new HashMap<>();
    private static HashMap<UUID, Location> layingPlayers = new HashMap<>();
    
    // Make player sit
    public static void sitPlayer(Player player) {
        if(isSitting(player)) {
            player.sendMessage("§cYou are already sitting!");
            return;
        }
        
        if(isLaying(player)) {
            player.sendMessage("§cYou are already laying down! Use /lay to get up first.");
            return;
        }
        
        Location loc = player.getLocation();
        
        // Spawn invisible armor stand
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(loc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setCanPickupItems(false);
        stand.setCustomName("SIT_STAND");
        stand.setCustomNameVisible(false);
        stand.setMarker(true);
        
        // Make player sit on the armor stand
        stand.addPassenger(player);
        
        sittingPlayers.put(player.getUniqueId(), stand);
        player.sendMessage("§aYou are now sitting. Move to stand up.");
    }
    
    // Make player stand up from sitting
    public static void standPlayer(Player player) {
        if(!isSitting(player)) {
            player.sendMessage("§cYou are not sitting!");
            return;
        }
        
        Entity stand = sittingPlayers.get(player.getUniqueId());
        if(stand != null) {
            stand.removePassenger(player);
            stand.remove();
        }
        
        sittingPlayers.remove(player.getUniqueId());
        player.sendMessage("§aYou stood up.");
    }
    
    // Make player lay down
    public static void layPlayer(Player player) {
        if(isLaying(player)) {
            // Get up
            Location loc = layingPlayers.get(player.getUniqueId());
            if(loc != null) {
                player.teleport(loc);
            }
            layingPlayers.remove(player.getUniqueId());
            player.sendMessage("§aYou got up.");
            return;
        }
        
        if(isSitting(player)) {
            player.sendMessage("§cYou are sitting! Use /sit to stand up first.");
            return;
        }
        
        Location originalLoc = player.getLocation().clone();
        Location layLoc = originalLoc.clone();
        
        // Lower the player slightly to appear laying down
        layLoc.setY(layLoc.getY() - 1.5);
        
        // Create invisible armor stand to make player appear laying
        ArmorStand stand = (ArmorStand) player.getWorld().spawnEntity(layLoc, EntityType.ARMOR_STAND);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setInvulnerable(true);
        stand.setCanPickupItems(false);
        stand.setCustomName("LAY_STAND");
        stand.setCustomNameVisible(false);
        stand.setMarker(true);
        
        // Make player passenger
        stand.addPassenger(player);
        
        layingPlayers.put(player.getUniqueId(), originalLoc);
        sittingPlayers.put(player.getUniqueId(), stand); // Reuse sitting map for cleanup
        
        player.sendMessage("§aYou are now laying down. Use /lay again to get up.");
    }
    
    // Check if player is sitting
    public static boolean isSitting(Player player) {
        Entity entity = sittingPlayers.get(player.getUniqueId());
        if(entity != null && entity.isValid()) {
            return entity.getCustomName() != null && entity.getCustomName().equals("SIT_STAND");
        }
        return false;
    }
    
    // Check if player is laying
    public static boolean isLaying(Player player) {
        return layingPlayers.containsKey(player.getUniqueId());
    }
    
    // Remove player from sitting (called when player moves)
    public static void removeSitting(UUID uuid) {
        Entity stand = sittingPlayers.get(uuid);
        if(stand != null) {
            stand.eject();
            stand.remove();
            sittingPlayers.remove(uuid);
        }
        layingPlayers.remove(uuid);
    }
    
    // Cleanup all sitting/laying players
    public static void cleanupAll() {
        for(Entity stand : sittingPlayers.values()) {
            if(stand != null && stand.isValid()) {
                stand.eject();
                stand.remove();
            }
        }
        sittingPlayers.clear();
        layingPlayers.clear();
    }
    
    // Get sitting players map (for event handling)
    public static HashMap<UUID, Entity> getSittingPlayers() {
        return sittingPlayers;
    }
    
    // Get laying players map (for event handling)
    public static HashMap<UUID, Location> getLayingPlayers() {
        return layingPlayers;
    }
}


