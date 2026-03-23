package com.itsvollx.simple.teleport;

import com.itsvollx.simple.utils.Message;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class TeleportRequest {
    
    // Store pending teleport requests
    // Key: Target player UUID, Value: Array [Requester UUID, Request Type (0=tpa, 1=tpahere)]
    private static HashMap<UUID, Object[]> pendingRequests = new HashMap<>();
    
    private Message message = new Message();
    
    /**
     * Send a teleport request
     * @param requester The player requesting teleport
     * @param target The player receiving the request
     * @param isTpaHere true if tpahere, false if tpa
     */
    public void sendRequest(Player requester, Player target, boolean isTpaHere) {
        if(requester.getUniqueId().equals(target.getUniqueId())) {
            requester.sendMessage("§cYou cannot send a teleport request to yourself.");
            return;
        }
        
        pendingRequests.put(target.getUniqueId(), new Object[]{requester.getUniqueId(), isTpaHere});
        
        if(isTpaHere) {
            requester.sendMessage("§aTeleport request sent to §e" + target.getName() + "§a. They will teleport to you if they accept.");
            target.sendMessage("§e" + requester.getName() + "§a has requested that you teleport to them.");
        } else {
            requester.sendMessage("§aTeleport request sent to §e" + target.getName() + "§a.");
            target.sendMessage("§e" + requester.getName() + "§a has requested to teleport to you.");
        }
        target.sendMessage("§aType §e/tpaccept§a to accept or §e/tpdeny§a to deny.");
        
        // Auto-expire request after 60 seconds
        Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("Simple"), () -> {
            if(pendingRequests.containsKey(target.getUniqueId())) {
                Object[] request = pendingRequests.get(target.getUniqueId());
                if(request[0].equals(requester.getUniqueId())) {
                    pendingRequests.remove(target.getUniqueId());
                    if(requester.isOnline()) {
                        requester.sendMessage("§cTeleport request to " + target.getName() + " has expired.");
                    }
                    if(target.isOnline()) {
                        target.sendMessage("§cTeleport request from " + requester.getName() + " has expired.");
                    }
                }
            }
        }, 1200L); // 60 seconds = 1200 ticks
    }
    
    /**
     * Accept a pending teleport request
     */
    public void acceptRequest(Player target) {
        if(!pendingRequests.containsKey(target.getUniqueId())) {
            target.sendMessage("§cYou have no pending teleport requests.");
            return;
        }
        
        Object[] request = pendingRequests.remove(target.getUniqueId());
        UUID requesterUUID = (UUID) request[0];
        boolean isTpaHere = (boolean) request[1];
        
        Player requester = Bukkit.getPlayer(requesterUUID);
        if(requester == null || !requester.isOnline()) {
            target.sendMessage("§cThat player is no longer online.");
            return;
        }
        
        target.sendMessage("§aTeleport request accepted.");
        requester.sendMessage("§e" + target.getName() + "§a accepted your teleport request.");
        
        // Perform teleport
        if(isTpaHere) {
            // Target teleports to requester
            target.teleport(requester.getLocation());
            target.playSound(target.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        } else {
            // Requester teleports to target
            requester.teleport(target.getLocation());
            requester.playSound(requester.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
        }
    }
    
    /**
     * Deny a pending teleport request
     */
    public void denyRequest(Player target) {
        if(!pendingRequests.containsKey(target.getUniqueId())) {
            target.sendMessage("§cYou have no pending teleport requests.");
            return;
        }
        
        Object[] request = pendingRequests.remove(target.getUniqueId());
        UUID requesterUUID = (UUID) request[0];
        
        Player requester = Bukkit.getPlayer(requesterUUID);
        
        target.sendMessage("§cTeleport request denied.");
        if(requester != null && requester.isOnline()) {
            requester.sendMessage("§e" + target.getName() + "§c denied your teleport request.");
        }
    }
}


