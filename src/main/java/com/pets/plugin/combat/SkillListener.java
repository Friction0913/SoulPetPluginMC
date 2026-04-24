package com.pets.plugin.combat;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.pet.Pet;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.List;

public class SkillListener implements Listener {
    private final SoulPetsPlugin plugin;

    public SkillListener(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Material type = block.getType();
        boolean isMining = isOre(type) || type.name().contains("DEEPSLATE") || type == Material.STONE || type == Material.NETHERRACK || type == Material.BASALT || type == Material.BLACKSTONE;
        boolean isWood = type.name().contains("LOG") || type.name().contains("WOOD") || type.name().contains("STEM") || type.name().contains("HYPHAE");

        if (!isMining && !isWood) return;

        double miningXp = plugin.getConfig().getDouble("xp.mining-per-block", 1);
        double woodXp = plugin.getConfig().getDouble("xp.woodcutting-per-block", 1);

        List<Pet> activePets = plugin.getPetManager().getActivePets(event.getPlayer().getUniqueId());
        for (Pet pet : activePets) {
            if (!pet.isActive()) continue;
            if (pet.getEntity() == null) continue;
            if (!pet.getEntity().getWorld().equals(block.getWorld())) continue;
            if (pet.getEntity().getLocation().distanceSquared(block.getLocation()) > 256) continue;
            if (isMining) {
                pet.getStats().addMiningXp(miningXp);
            }
            if (isWood) {
                pet.getStats().addWoodcuttingXp(woodXp);
            }
        }
    }

    private boolean isOre(Material type) {
        return type.name().contains("ORE") || type == Material.ANCIENT_DEBRIS || type == Material.NETHER_GOLD_ORE;
    }
}
