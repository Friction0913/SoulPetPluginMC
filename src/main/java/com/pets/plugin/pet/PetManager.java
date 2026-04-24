package com.pets.plugin.pet;

import com.pets.plugin.SoulPetsPlugin;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Mob;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PetManager {
    private final SoulPetsPlugin plugin;
    private final File dataFolder;
    private final Map<UUID, List<Pet>> playerPets = new HashMap<>();
    private final Map<UUID, Pet> activePets = new HashMap<>();

    public PetManager(SoulPetsPlugin plugin) {
        this.plugin = plugin;
        this.dataFolder = new File(plugin.getDataFolder(), "players");
        if (!dataFolder.exists()) {
            dataFolder.mkdirs();
        }
    }

    public List<Pet> getPets(UUID owner) {
        return playerPets.computeIfAbsent(owner, k -> {
            loadPlayer(k);
            return playerPets.getOrDefault(k, new ArrayList<>());
        });
    }

    public List<Pet> getActivePets(UUID owner) {
        List<Pet> active = new ArrayList<>();
        for (Pet pet : activePets.values()) {
            if (pet.getOwner().equals(owner)) {
                active.add(pet);
            }
        }
        return active;
    }

    public Pet getPetById(UUID id) {
        for (List<Pet> pets : playerPets.values()) {
            for (Pet pet : pets) {
                if (pet.getId().equals(id)) return pet;
            }
        }
        return null;
    }

    public boolean spawnPet(Player player, Pet pet) {
        int maxActive = plugin.getConfig().getInt("max-active-pets", 2);
        if (getActivePets(player.getUniqueId()).size() >= maxActive) {
            plugin.msg(player, plugin.getConfig().getString("messages.capture-failed-active-full", "&cYou already have %max% active pets.").replace("%max%", String.valueOf(maxActive)));
            return false;
        }
        Location loc = player.getLocation();
        World world = loc.getWorld();
        if (world == null) return false;
        Entity entity = world.spawnEntity(loc, pet.getType());
        if (entity instanceof LivingEntity living) {
            if (living.getAttribute(Attribute.MAX_HEALTH) != null) {
                living.getAttribute(Attribute.MAX_HEALTH).setBaseValue(pet.getMaxHealth());
            }
            living.setHealth(Math.min(pet.getHealth(), pet.getMaxHealth()));
            living.setCustomName(plugin.color(pet.getName()));
            living.setCustomNameVisible(true);
            if (living instanceof Mob mob) {
                mob.setRemoveWhenFarAway(false);
            }
        }
        pet.setEntity(entity);
        activePets.put(pet.getId(), pet);
        plugin.msg(player, plugin.getConfig().getString("messages.pet-spawned", "&a%name% spawned!").replace("%name%", pet.getName()));
        return true;
    }

    public void despawnPet(Pet pet) {
        if (pet.getEntity() != null) {
            if (pet.getEntity() instanceof LivingEntity living) {
                pet.setHealth(living.getHealth());
            }
            if (!pet.getEntity().isDead()) {
                pet.getEntity().remove();
            }
            pet.setEntity(null);
        }
        pet.setTarget(null);
        activePets.remove(pet.getId());
        Player owner = Bukkit.getPlayer(pet.getOwner());
        if (owner != null) {
            plugin.msg(owner, plugin.getConfig().getString("messages.pet-despawned", "&7%name% returned to storage.").replace("%name%", pet.getName()));
        }
    }

    public void deletePet(Pet pet) {
        despawnPet(pet);
        for (List<Pet> pets : playerPets.values()) {
            pets.remove(pet);
        }
    }

    public void loadPlayer(UUID owner) {
        File file = new File(dataFolder, owner.toString() + ".yml");
        if (!file.exists()) {
            playerPets.put(owner, new ArrayList<>());
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(file);
        List<Map<?, ?>> list = config.getMapList("pets");
        List<Pet> pets = new ArrayList<>();
        for (Map<?, ?> map : list) {
            try {
                UUID id = UUID.fromString((String) map.get("id"));
                EntityType type = EntityType.valueOf((String) map.get("type"));
                String name = (String) map.get("name");
                int level = ((Number) map.get("level")).intValue();
                double xp = ((Number) map.get("xp")).doubleValue();
                double hunger = ((Number) map.get("hunger")).doubleValue();
                double health = ((Number) map.get("health")).doubleValue();
                double maxHealth = ((Number) map.get("maxHealth")).doubleValue();

                PetStats stats = new PetStats();
                Map<String, Object> statsMap = (Map<String, Object>) map.get("stats");
                if (statsMap != null) {
                    stats.setCombatLevel(((Number) statsMap.getOrDefault("combatLevel", 1)).intValue());
                    stats.setCombatXp(((Number) statsMap.getOrDefault("combatXp", 0)).doubleValue());
                    stats.setMiningLevel(((Number) statsMap.getOrDefault("miningLevel", 1)).intValue());
                    stats.setMiningXp(((Number) statsMap.getOrDefault("miningXp", 0)).doubleValue());
                    stats.setWoodcuttingLevel(((Number) statsMap.getOrDefault("woodcuttingLevel", 1)).intValue());
                    stats.setWoodcuttingXp(((Number) statsMap.getOrDefault("woodcuttingXp", 0)).doubleValue());
                    stats.setHuntingLevel(((Number) statsMap.getOrDefault("huntingLevel", 1)).intValue());
                    stats.setHuntingXp(((Number) statsMap.getOrDefault("huntingXp", 0)).doubleValue());
                    stats.setEnduranceLevel(((Number) statsMap.getOrDefault("enduranceLevel", 1)).intValue());
                    stats.setEnduranceXp(((Number) statsMap.getOrDefault("enduranceXp", 0)).doubleValue());
                }

                Pet pet = new Pet(id, owner, type, name, level, xp, hunger, health, maxHealth, stats);
                pets.add(pet);
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load pet for " + owner + ": " + e.getMessage());
            }
        }
        playerPets.put(owner, pets);
    }

    public void savePlayer(UUID owner) {
        List<Pet> pets = playerPets.get(owner);
        if (pets == null) return;
        File file = new File(dataFolder, owner.toString() + ".yml");
        FileConfiguration config = new YamlConfiguration();
        List<Map<String, Object>> list = new ArrayList<>();
        for (Pet pet : pets) {
            if (pet.isActive() && pet.getEntity() instanceof LivingEntity living) {
                pet.setHealth(living.getHealth());
            }
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("id", pet.getId().toString());
            map.put("type", pet.getType().name());
            map.put("name", pet.getName());
            map.put("level", pet.getLevel());
            map.put("xp", pet.getXp());
            map.put("hunger", pet.getHunger());
            map.put("health", pet.getHealth());
            map.put("maxHealth", pet.getMaxHealth());
            Map<String, Object> statsMap = new LinkedHashMap<>();
            PetStats stats = pet.getStats();
            statsMap.put("combatLevel", stats.getCombatLevel());
            statsMap.put("combatXp", stats.getCombatXp());
            statsMap.put("miningLevel", stats.getMiningLevel());
            statsMap.put("miningXp", stats.getMiningXp());
            statsMap.put("woodcuttingLevel", stats.getWoodcuttingLevel());
            statsMap.put("woodcuttingXp", stats.getWoodcuttingXp());
            statsMap.put("huntingLevel", stats.getHuntingLevel());
            statsMap.put("huntingXp", stats.getHuntingXp());
            statsMap.put("enduranceLevel", stats.getEnduranceLevel());
            statsMap.put("enduranceXp", stats.getEnduranceXp());
            map.put("stats", statsMap);
            list.add(map);
        }
        config.set("pets", list);
        try {
            config.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Failed to save pets for " + owner + ": " + e.getMessage());
        }
    }

    public void saveAll() {
        for (UUID owner : new ArrayList<>(playerPets.keySet())) {
            savePlayer(owner);
        }
    }

    public Map<UUID, Pet> getActivePetsMap() {
        return new HashMap<>(activePets);
    }
}
