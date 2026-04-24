package com.pets.plugin.pet;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;

import java.util.UUID;

public class Pet {
    private final UUID id;
    private final UUID owner;
    private final EntityType type;
    private String name;
    private int level;
    private double xp;
    private double hunger;
    private double health;
    private double maxHealth;
    private final PetStats stats;

    private transient Entity entity;
    private transient LivingEntity target;

    public Pet(UUID owner, EntityType type, String name) {
        this.id = UUID.randomUUID();
        this.owner = owner;
        this.type = type;
        this.name = name;
        this.level = 1;
        this.xp = 0;
        this.hunger = 100;
        this.health = 20;
        this.maxHealth = 20;
        this.stats = new PetStats();
    }

    public Pet(UUID id, UUID owner, EntityType type, String name, int level, double xp, double hunger, double health, double maxHealth, PetStats stats) {
        this.id = id;
        this.owner = owner;
        this.type = type;
        this.name = name;
        this.level = level;
        this.xp = xp;
        this.hunger = hunger;
        this.health = health;
        this.maxHealth = maxHealth;
        this.stats = stats;
    }

    public UUID getId() { return id; }
    public UUID getOwner() { return owner; }
    public EntityType getType() { return type; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }
    public double getXp() { return xp; }
    public void setXp(double xp) { this.xp = xp; }
    public double getHunger() { return hunger; }
    public void setHunger(double hunger) { this.hunger = Math.max(0, Math.min(100, hunger)); }
    public double getHealth() { return health; }
    public void setHealth(double health) { this.health = Math.max(0, Math.min(maxHealth, health)); }
    public double getMaxHealth() { return maxHealth; }
    public void setMaxHealth(double maxHealth) { this.maxHealth = maxHealth; }
    public PetStats getStats() { return stats; }

    public Entity getEntity() { return entity; }
    public void setEntity(Entity entity) { this.entity = entity; }
    public LivingEntity getTarget() { return target; }
    public void setTarget(LivingEntity target) { this.target = target; }

    public boolean isActive() {
        return entity != null && !entity.isDead();
    }

    public void addXp(double amount) {
        this.xp += amount;
        double needed = 100 * Math.pow(level, 1.5);
        while (xp >= needed) {
            xp -= needed;
            level++;
            maxHealth += 2;
            health = maxHealth;
            needed = 100 * Math.pow(level, 1.5);
        }
    }
}
