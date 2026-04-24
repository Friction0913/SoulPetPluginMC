package com.pets.plugin.combat;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.pet.Pet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.List;

public class CombatListener implements Listener {
    private final SoulPetsPlugin plugin;

    public CombatListener(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity target = event.getEntity();
        Player owner = null;
        LivingEntity enemy = null;

        if (damager instanceof Player player) {
            owner = player;
            if (target instanceof LivingEntity le) enemy = le;
        } else if (target instanceof Player player) {
            owner = player;
            if (damager instanceof Projectile proj && proj.getShooter() instanceof LivingEntity le) {
                enemy = le;
            } else if (damager instanceof LivingEntity le) {
                enemy = le;
            }
        }

        if (owner == null || enemy == null) return;

        List<Pet> activePets = plugin.getPetManager().getActivePets(owner.getUniqueId());
        for (Pet pet : activePets) {
            if (!pet.isActive()) continue;
            if (pet.getEntity() == null) continue;
            if (pet.getEntity().getLocation().distanceSquared(owner.getLocation()) > 256) continue;
            pet.setTarget(enemy);
        }
    }

    @EventHandler
    public void onDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        double huntingXp = plugin.getConfig().getDouble("xp.hunting-per-kill", 10);
        List<Pet> nearby = plugin.getPetManager().getActivePetsMap().values().stream()
            .filter(Pet::isActive)
            .filter(p -> p.getEntity() != null && p.getEntity().getWorld().equals(dead.getWorld()) && p.getEntity().getLocation().distanceSquared(dead.getLocation()) <= 256)
            .toList();
        for (Pet pet : nearby) {
            pet.getStats().addHuntingXp(huntingXp);
        }
    }
}
