package com.pets.plugin.lifecycle;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.pet.Pet;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

public class DeathListener implements Listener {
    private final SoulPetsPlugin plugin;

    public DeathListener(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        LivingEntity dead = event.getEntity();
        for (Pet pet : plugin.getPetManager().getActivePetsMap().values()) {
            if (pet.isActive() && pet.getEntity().getUniqueId().equals(dead.getUniqueId())) {
                event.getDrops().clear();
                event.setDroppedExp(0);
                plugin.getPetManager().deletePet(pet);
                Player owner = Bukkit.getPlayer(pet.getOwner());
                if (owner != null) {
                    plugin.msg(owner, plugin.getConfig().getString("messages.pet-died", "&cYour pet %name% has died. All progress is lost.").replace("%name%", pet.getName()));
                }
                return;
            }
        }
    }
}
