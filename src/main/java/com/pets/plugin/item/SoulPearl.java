package com.pets.plugin.item;

import com.pets.plugin.SoulPetsPlugin;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class SoulPearl {
    private SoulPearl() {}

    public static ItemStack create(SoulPetsPlugin plugin) {
        ItemStack item = new ItemStack(Material.ENDER_PEARL);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(plugin.color(plugin.getConfig().getString("soul-pearl.name", "&dSoul Pearl")));
            List<String> lore = plugin.getConfig().getStringList("soul-pearl.lore");
            if (lore.isEmpty()) {
                lore = List.of("&7Right-click a mob to capture it.", "&7Soul bound to you.");
            }
            meta.setLore(lore.stream().map(plugin::color).toList());
            meta.setCustomModelData(1001);
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "soul_pearl"), PersistentDataType.STRING, "true");
            item.setItemMeta(meta);
        }
        return item;
    }

    public static boolean isSoulPearl(ItemStack item) {
        if (item == null || item.getType() != Material.ENDER_PEARL || !item.hasItemMeta()) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(new NamespacedKey(SoulPetsPlugin.getInstance(), "soul_pearl"), PersistentDataType.STRING);
    }

    public static void registerRecipe(SoulPetsPlugin plugin) {
        List<String> ingredients = plugin.getConfig().getStringList("soul-pearl.recipe");
        if (ingredients.size() < 3) {
            ingredients = List.of("ENDER_PEARL", "DIAMOND", "GHAST_TEAR");
        }

        Material matA = Material.matchMaterial(ingredients.get(0));
        Material matB = Material.matchMaterial(ingredients.get(1));
        Material matC = Material.matchMaterial(ingredients.get(2));
        if (matA == null) matA = Material.ENDER_PEARL;
        if (matB == null) matB = Material.DIAMOND;
        if (matC == null) matC = Material.GHAST_TEAR;

        NamespacedKey key = new NamespacedKey(plugin, "soul_pearl");
        ShapedRecipe recipe = new ShapedRecipe(key, create(plugin));
        recipe.shape("ABC");
        recipe.setIngredient('A', matA);
        recipe.setIngredient('B', matB);
        recipe.setIngredient('C', matC);
        plugin.getServer().addRecipe(recipe);
    }
}
