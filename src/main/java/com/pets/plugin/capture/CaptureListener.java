package com.pets.plugin.capture;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.item.SoulPearl;
import com.pets.plugin.pet.Pet;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;

public class CaptureListener implements Listener {
    private final SoulPetsPlugin plugin;

    public CaptureListener(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEntityEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        if (!SoulPearl.isSoulPearl(item)) return;

        Entity entity = event.getRightClicked();
        if (!(entity instanceof LivingEntity living)) return;
        if (entity instanceof Player) return;

        event.setCancelled(true);

        int maxStored = plugin.getConfig().getInt("max-stored-pets", 10);
        if (plugin.getPetManager().getPets(player.getUniqueId()).size() >= maxStored) {
            plugin.msg(player, plugin.getConfig().getString("messages.capture-failed-storage-full", "&cYou can't store more pets (max %max%).").replace("%max%", String.valueOf(maxStored)));
            return;
        }

        if (isBoss(living) && !player.hasPermission("SoulPetsPlugin.capture.boss")) {
            plugin.msg(player, "&cYou don't have permission to capture boss mobs.");
            return;
        }

        String name = living.getCustomName() != null ? living.getCustomName() : living.getType().toString();
        Pet pet = new Pet(player.getUniqueId(), living.getType(), name);
        plugin.getPetManager().getPets(player.getUniqueId()).add(pet);
        plugin.getPetManager().savePlayer(player.getUniqueId());

        living.remove();
        item.setAmount(item.getAmount() - 1);
        player.getInventory().setItemInMainHand(item.getAmount() <= 0 ? null : item);

        plugin.msg(player, plugin.getConfig().getString("messages.captured", "&aYou captured a %mob%!").replace("%mob%", name));
    }

    private boolean isBoss(LivingEntity entity) {
        return entity.getType().name().contains("DRAGON") ||
               entity.getType().name().contains("WITHER") ||
               entity.getType().name().contains("WARDEN");
    }
}
