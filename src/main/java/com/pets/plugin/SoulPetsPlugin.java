package com.pets.plugin;

import com.pets.plugin.capture.CaptureListener;
import com.pets.plugin.combat.CombatListener;
import com.pets.plugin.combat.SkillListener;
import com.pets.plugin.command.PetCommand;
import com.pets.plugin.gui.PetGUIListener;
import com.pets.plugin.item.SoulPearl;
import com.pets.plugin.lifecycle.DeathListener;
import com.pets.plugin.pet.ActivePetTask;
import com.pets.plugin.pet.PetManager;
import org.bukkit.ChatColor;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SoulPetsPlugin extends JavaPlugin {

    private static SoulPetsPlugin instance;
    private PetManager petManager;
    private ActivePetTask activePetTask;
    private String prefix;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        prefix = getConfig().getString("messages.prefix", "&6[SoulPetsPlugin] &r");

        petManager = new PetManager(this);

        getServer().getPluginManager().registerEvents(new CaptureListener(this), this);
        getServer().getPluginManager().registerEvents(new CombatListener(this), this);
        getServer().getPluginManager().registerEvents(new SkillListener(this), this);
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
        getServer().getPluginManager().registerEvents(new PetGUIListener(this), this);

        PetCommand petCommand = new PetCommand(this);
        PluginCommand petCmd = getCommand("pet");
        if (petCmd != null) {
            petCmd.setExecutor(petCommand);
            petCmd.setTabCompleter(petCommand);
        }
        PluginCommand petAdminCmd = getCommand("petadmin");
        if (petAdminCmd != null) {
            petAdminCmd.setExecutor(petCommand);
            petAdminCmd.setTabCompleter(petCommand);
        }

        SoulPearl.registerRecipe(this);

        activePetTask = new ActivePetTask(this);
        activePetTask.runTaskTimer(this, 10L, 10L);
    }

    @Override
    public void onDisable() {
        if (petManager != null) {
            petManager.saveAll();
        }
        if (activePetTask != null) {
            activePetTask.cancel();
        }
    }

    public static SoulPetsPlugin getInstance() {
        return instance;
    }

    public PetManager getPetManager() {
        return petManager;
    }

    public String getPrefix() {
        return prefix;
    }

    public String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    public void msg(Player player, String message) {
        if (player != null && player.isOnline()) {
            player.sendMessage(color(prefix + message));
        }
    }
}
