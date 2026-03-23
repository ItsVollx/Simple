package com.itsvollx.simple;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Phantom;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

public class PhantomForceField {
    private final JavaPlugin plugin;
    private BukkitTask task;
    
    // Configuration
    private double forceFieldRadius = 8.0;
    private double minimumPushForce = 2.0;
    private double forceMultiplier = 0.8;
    private double upwardForce = 0.5;
    private double damageAmount = 2.0;
    private int updateInterval = 2; // ticks
    
    public PhantomForceField(JavaPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void start() {
        task = Bukkit.getScheduler().runTaskTimer(plugin, this::updateForceField, 0L, updateInterval);
    }
    
    public void stop() {
        if(task != null) {
            task.cancel();
        }
    }
    
    private void updateForceField() {
        for(Player player : Bukkit.getOnlinePlayers()) {
            repelPhantoms(player);
        }
    }
    
    private void repelPhantoms(Player player) {
        player.getNearbyEntities(forceFieldRadius, forceFieldRadius, forceFieldRadius)
            .stream()
            .filter(entity -> entity.getType() == EntityType.PHANTOM)
            .forEach(phantom -> {
                // Calculate direction away from player
                Location playerLoc = player.getLocation();
                Location phantomLoc = phantom.getLocation();
                
                Vector direction = phantomLoc.toVector().subtract(playerLoc.toVector()).normalize();
                
                // Calculate distance-based force (closer = stronger push)
                double distance = playerLoc.distance(phantomLoc);
                double force = Math.max(minimumPushForce, (forceFieldRadius - distance) * forceMultiplier);
                
                // Apply force with upward component
                Vector pushVector = direction.multiply(force).setY(upwardForce);
                phantom.setVelocity(pushVector);
                
                // Deal damage
                if(phantom instanceof Phantom) {
                    ((Phantom) phantom).damage(damageAmount);
                }
                
                // Spawn particles
                spawnForceFieldParticles(player, phantom);
            });
    }
    
    private void spawnForceFieldParticles(Player player, Entity phantom) {
        World world = player.getWorld();
        world.spawnParticle(Particle.ELECTRIC_SPARK, phantom.getLocation(), 3);
    }
    
    // Getters and setters for configuration
    public void setForceFieldRadius(double forceFieldRadius) {
        this.forceFieldRadius = forceFieldRadius;
    }
    
    public void setMinimumPushForce(double minimumPushForce) {
        this.minimumPushForce = minimumPushForce;
    }
    
    public void setForceMultiplier(double forceMultiplier) {
        this.forceMultiplier = forceMultiplier;
    }
    
    public void setUpwardForce(double upwardForce) {
        this.upwardForce = upwardForce;
    }
    
    public void setDamageAmount(double damageAmount) {
        this.damageAmount = damageAmount;
    }
    
    public void setUpdateInterval(int updateInterval) {
        this.updateInterval = updateInterval;
    }
    
    public double getForceFieldRadius() {
        return forceFieldRadius;
    }
    
    public double getMinimumPushForce() {
        return minimumPushForce;
    }
    
    public double getForceMultiplier() {
        return forceMultiplier;
    }
    
    public double getUpwardForce() {
        return upwardForce;
    }
    
    public double getDamageAmount() {
        return damageAmount;
    }
    
    public int getUpdateInterval() {
        return updateInterval;
    }
}

