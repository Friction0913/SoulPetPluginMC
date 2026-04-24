package com.pets.plugin.gui;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.pet.Pet;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class PetGUI {
    private PetGUI() {}

    public static void open(Player player, SoulPetsPlugin plugin) {
        List<Pet> pets = plugin.getPetManager().getPets(player.getUniqueId());
        int size = ((pets.size() / 9) + 1) * 9;
        if (size < 9) size = 9;
        if (size > 54) size = 54;

        Inventory inv = Bukkit.createInventory(new PetHolder(null), size, plugin.color("&8Your Pets"));
        for (int i = 0; i < Math.min(pets.size(), size); i++) {
            Pet pet = pets.get(i);
            Material mat = getSpawnEgg(pet.getType());
            ItemStack item = new ItemStack(mat);
            ItemMeta meta = item.getItemMeta();
            if (meta != null) {
                meta.setDisplayName(plugin.color("&e" + pet.getName()));
                List<String> lore = Arrays.asList(
                    plugin.color("&7Type: &f" + pet.getType()),
                    plugin.color("&7Level: &f" + pet.getLevel()),
                    plugin.color("&7Health: &f" + String.format("%.1f", pet.getHealth()) + "/" + String.format("%.1f", pet.getMaxHealth())),
                    plugin.color("&7Hunger: &f" + String.format("%.1f", pet.getHunger())),
                    plugin.color(""),
                    plugin.color("&aLeft-Click to spawn/despawn"),
                    plugin.color("&bRight-Click for stats")
                );
                meta.setLore(lore);
                item.setItemMeta(meta);
            }
            inv.setItem(i, item);
        }
        player.openInventory(inv);
    }

    public static void openStats(Player player, SoulPetsPlugin plugin, Pet pet) {
        Inventory inv = Bukkit.createInventory(new PetHolder(pet), 27, plugin.color("&8Stats: " + pet.getName()));
        ItemStack combat = createItem(plugin, Material.IRON_SWORD, "&cCombat", "&7Level: &f" + pet.getStats().getCombatLevel(), "&7XP: &f" + String.format("%.1f", pet.getStats().getCombatXp()));
        ItemStack mining = createItem(plugin, Material.IRON_PICKAXE, "&9Mining", "&7Level: &f" + pet.getStats().getMiningLevel(), "&7XP: &f" + String.format("%.1f", pet.getStats().getMiningXp()));
        ItemStack wood = createItem(plugin, Material.IRON_AXE, "&6Woodcutting", "&7Level: &f" + pet.getStats().getWoodcuttingLevel(), "&7XP: &f" + String.format("%.1f", pet.getStats().getWoodcuttingXp()));
        ItemStack hunting = createItem(plugin, Material.BOW, "&aHunting", "&7Level: &f" + pet.getStats().getHuntingLevel(), "&7XP: &f" + String.format("%.1f", pet.getStats().getHuntingXp()));
        ItemStack endurance = createItem(plugin, Material.SHIELD, "&eEndurance", "&7Level: &f" + pet.getStats().getEnduranceLevel(), "&7XP: &f" + String.format("%.1f", pet.getStats().getEnduranceXp()));

        inv.setItem(11, combat);
        inv.setItem(12, mining);
        inv.setItem(13, wood);
        inv.setItem(14, hunting);
        inv.setItem(15, endurance);
        player.openInventory(inv);
    }

    private static ItemStack createItem(SoulPetsPlugin plugin, Material mat, String name, String... loreArr) {
        ItemStack item = new ItemStack(mat);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.color(name));
            meta.setLore(Arrays.stream(loreArr).map(plugin::color).toList());
            item.setItemMeta(meta);
        }
        return item;
    }

    private static Material getSpawnEgg(EntityType type) {
        String name = type.name() + "_SPAWN_EGG";
        try {
            return Material.valueOf(name);
        } catch (IllegalArgumentException e) {
            return Material.BAT_SPAWN_EGG;
        }
    }

    public static class PetHolder implements InventoryHolder {
        private final Pet pet;

        public PetHolder(Pet pet) {
            this.pet = pet;
        }

        public Pet getPet() {
            return pet;
        }

        @Override
        public Inventory getInventory() {
            return null;
        }
    }
}
