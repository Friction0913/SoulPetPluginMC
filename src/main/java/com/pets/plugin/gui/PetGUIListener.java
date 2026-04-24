package com.pets.plugin.gui;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.pet.Pet;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class PetGUIListener implements Listener {
    private final SoulPetsPlugin plugin;

    public PetGUIListener(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getInventory().getHolder() instanceof PetGUI.PetHolder holder)) return;
        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player player)) return;
        ItemStack clicked = event.getCurrentItem();
        if (clicked == null || !clicked.hasItemMeta()) return;
        ItemMeta meta = clicked.getItemMeta();
        if (meta == null || !meta.hasDisplayName()) return;

        // Stats GUI - no actions
        if (holder.getPet() != null) {
            return;
        }

        String display = meta.getDisplayName();
        List<Pet> pets = plugin.getPetManager().getPets(player.getUniqueId());

        for (Pet pet : pets) {
            if (display.equals(plugin.color("&e" + pet.getName()))) {
                if (event.isLeftClick()) {
                    player.closeInventory();
                    if (pet.isActive()) {
                        plugin.getPetManager().despawnPet(pet);
                    } else {
                        plugin.getPetManager().spawnPet(player, pet);
                    }
                    plugin.getServer().getScheduler().runTaskLater(plugin, () -> PetGUI.open(player, plugin), 1L);
                } else if (event.isRightClick()) {
                    PetGUI.openStats(player, plugin, pet);
                }
                break;
            }
        }
    }
}
