package com.pets.plugin.util;

import com.pets.plugin.SoulPetsPlugin;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

public class PetUtil {
    private PetUtil() {}

    public static double distance2D(Location a, Location b) {
        if (a.getWorld() != b.getWorld()) return Double.MAX_VALUE;
        double dx = a.getX() - b.getX();
        double dz = a.getZ() - b.getZ();
        return Math.sqrt(dx * dx + dz * dz);
    }

    public static boolean isFlyingMob(EntityType type) {
        return switch (type) {
            case BAT, BEE, BLAZE, GHAST, PARROT, PHANTOM, VEX, WITHER, ALLAY -> true;
            default -> false;
        };
    }

    public static boolean isWaterMob(EntityType type) {
        return switch (type) {
            case AXOLOTL, COD, DOLPHIN, ELDER_GUARDIAN, GUARDIAN, PUFFERFISH, SALMON, SQUID, GLOW_SQUID, TROPICAL_FISH, TURTLE, FROG, TADPOLE -> true;
            default -> false;
        };
    }

    public static Material getPreferredFood(EntityType type) {
        SoulPetsPlugin plugin = SoulPetsPlugin.getInstance();
        String configFood = plugin.getConfig().getString("preferred-foods." + type.name());
        if (configFood != null) {
            Material mat = Material.matchMaterial(configFood);
            if (mat != null) return mat;
        }

        String name = type.name();
        if (name.contains("ZOMBIE") || name.contains("SKELETON") || name.contains("WITHER") || name.contains("DROWNED") || name.contains("HUSK") || name.contains("STRAY")) {
            return Material.ROTTEN_FLESH;
        }
        if (name.contains("COW") || name.contains("SHEEP") || name.contains("PIG") || name.contains("CHICKEN") || name.contains("RABBIT") || name.contains("HORSE") || name.contains("DONKEY") || name.contains("MULE") || name.contains("LLAMA") || name.contains("GOAT") || name.contains("MOOSHROOM")) {
            return Material.BREAD;
        }
        return Material.BEEF;
    }
}
