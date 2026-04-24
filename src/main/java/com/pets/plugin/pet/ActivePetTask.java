package com.pets.plugin.pet;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.util.PetUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ActivePetTask extends BukkitRunnable {
    private final SoulPetsPlugin plugin;
    private final Map<UUID, Location> lastLocations = new HashMap<>();
    private final Map<UUID, Long> stuckTimers = new HashMap<>();

    public ActivePetTask(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        double hungerDecay = plugin.getConfig().getDouble("hunger-decay", 0.1);
        double healthRegen = plugin.getConfig().getDouble("health-regen", 0.05);

        for (Pet pet : plugin.getPetManager().getActivePetsMap().values()) {
            if (!pet.isActive()) continue;
            Entity entity = pet.getEntity();
            if (!(entity instanceof LivingEntity living)) continue;

            Player owner = Bukkit.getPlayer(pet.getOwner());
            if (owner == null || !owner.isOnline()) {
                plugin.getPetManager().despawnPet(pet);
                continue;
            }

            // Hunger decay
            pet.setHunger(pet.getHunger() - hungerDecay);
            if (pet.getHunger() <= 0) {
                pet.setHealth(pet.getHealth() - 1);
                if (pet.getHealth() <= 0) {
                    living.setHealth(0);
                    continue;
                }
            } else if (pet.getHunger() > 50) {
                pet.setHealth(pet.getHealth() + healthRegen);
            }

            // Sync entity health back
            if (living.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH) != null) {
                living.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).setBaseValue(pet.getMaxHealth());
            }
            living.setHealth(Math.min(pet.getHealth(), living.getMaxHealth()));
            pet.setHealth(living.getHealth());

            // Combat logic & pathfinding
            Location petLoc = living.getLocation();
            LivingEntity target = pet.getTarget();
            if (target != null && !target.isDead() && target.getWorld().equals(petLoc.getWorld())) {
                double dist = petLoc.distance(target.getLocation());
                if (dist <= 2.5) {
                    double damage = 1.0 + (pet.getStats().getCombatLevel() * 0.5);
                    target.damage(damage, living);
                }
                if (living instanceof Mob mob) {
                    mob.getPathfinder().moveTo(target.getLocation());
                }
            } else {
                if (living instanceof Mob mob && owner.getWorld().equals(petLoc.getWorld())) {
                    mob.getPathfinder().moveTo(owner.getLocation());
                }
            }

            // Teleport if too far or flying/water mob
            double distOwner = PetUtil.distance2D(petLoc, owner.getLocation());
            boolean needsTeleport = distOwner > 20 || PetUtil.isFlyingMob(pet.getType()) || PetUtil.isWaterMob(pet.getType());
            if (needsTeleport) {
                living.teleport(owner.getLocation());
                lastLocations.put(pet.getId(), owner.getLocation().clone());
                stuckTimers.remove(pet.getId());
                continue;
            }

            // Stuck detection
            Location last = lastLocations.get(pet.getId());
            if (last != null && last.getWorld().equals(petLoc.getWorld()) && last.distanceSquared(petLoc) < 0.1) {
                Long stuck = stuckTimers.get(pet.getId());
                if (stuck == null) {
                    stuckTimers.put(pet.getId(), System.currentTimeMillis());
                } else if (System.currentTimeMillis() - stuck > 3000) {
                    living.teleport(owner.getLocation());
                    lastLocations.put(pet.getId(), owner.getLocation().clone());
                    stuckTimers.remove(pet.getId());
                    continue;
                }
            } else {
                stuckTimers.remove(pet.getId());
            }
            lastLocations.put(pet.getId(), petLoc.clone());
        }
    }
}
