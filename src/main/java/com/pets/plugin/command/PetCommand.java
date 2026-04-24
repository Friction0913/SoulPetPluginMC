package com.pets.plugin.command;

import com.pets.plugin.SoulPetsPlugin;
import com.pets.plugin.gui.PetGUI;
import com.pets.plugin.item.SoulPearl;
import com.pets.plugin.pet.Pet;
import com.pets.plugin.util.PetUtil;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class PetCommand implements CommandExecutor, TabCompleter {
    private final SoulPetsPlugin plugin;

    public PetCommand(SoulPetsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("petadmin")) {
            if (!sender.hasPermission("SoulPetsPlugin.admin")) {
                sender.sendMessage(plugin.color("&cNo permission."));
                return true;
            }
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                plugin.reloadConfig();
                sender.sendMessage(plugin.color("&aConfig reloaded."));
                return true;
            }
            sender.sendMessage(plugin.color("&cUsage: /petadmin reload"));
            return true;
        }

        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.color("&cPlayers only."));
            return true;
        }

        if (args.length == 0) {
            PetGUI.open(player, plugin);
            return true;
        }

        String sub = args[0].toLowerCase();
        switch (sub) {
            case "spawn" -> {
                if (args.length < 2) {
                    player.sendMessage(plugin.color("&cUsage: /pet spawn <name>"));
                    return true;
                }
                String name = args[1];
                Pet pet = findPetByName(player, name);
                if (pet == null) {
                    player.sendMessage(plugin.color("&cPet not found."));
                    return true;
                }
                plugin.getPetManager().spawnPet(player, pet);
            }
            case "despawn" -> {
                if (args.length < 2) {
                    player.sendMessage(plugin.color("&cUsage: /pet despawn <name>"));
                    return true;
                }
                String name = args[1];
                Pet pet = findPetByName(player, name);
                if (pet == null) {
                    player.sendMessage(plugin.color("&cPet not found."));
                    return true;
                }
                if (pet.isActive()) {
                    plugin.getPetManager().despawnPet(pet);
                } else {
                    player.sendMessage(plugin.color("&cPet is not active."));
                }
            }
            case "rename" -> {
                if (args.length < 3) {
                    player.sendMessage(plugin.color("&cUsage: /pet rename <name> <newName>"));
                    return true;
                }
                String name = args[1];
                String newName = args[2];
                Pet pet = findPetByName(player, name);
                if (pet == null) {
                    player.sendMessage(plugin.color("&cPet not found."));
                    return true;
                }
                pet.setName(newName);
                plugin.getPetManager().savePlayer(player.getUniqueId());
                player.sendMessage(plugin.color(plugin.getConfig().getString("messages.pet-renamed", "&aRenamed to %name%.").replace("%name%", newName)));
            }
            case "feed" -> {
                if (args.length < 2) {
                    player.sendMessage(plugin.color("&cUsage: /pet feed <name>"));
                    return true;
                }
                String name = args[1];
                Pet pet = findPetByName(player, name);
                if (pet == null) {
                    player.sendMessage(plugin.color("&cPet not found."));
                    return true;
                }
                Material food = PetUtil.getPreferredFood(pet.getType());
                if (!player.getInventory().contains(food)) {
                    player.sendMessage(plugin.color("&cYou need " + food.name().toLowerCase().replace("_", " ") + " to feed this pet."));
                    return true;
                }
                player.getInventory().removeItem(new ItemStack(food, 1));
                pet.setHunger(pet.getHunger() + 30);
                pet.setHealth(pet.getHealth() + 5);
                player.sendMessage(plugin.color(plugin.getConfig().getString("messages.pet-fed", "&a%name% enjoyed the meal.").replace("%name%", pet.getName())));
            }
            case "givepearl" -> {
                if (!player.hasPermission("SoulPetsPlugin.admin")) {
                    player.sendMessage(plugin.color("&cNo permission."));
                    return true;
                }
                ItemStack pearl = SoulPearl.create(plugin);
                player.getInventory().addItem(pearl);
                player.sendMessage(plugin.color("&aGiven Soul Pearl."));
            }
            default -> player.sendMessage(plugin.color("&cUnknown subcommand."));
        }
        return true;
    }

    private Pet findPetByName(Player player, String name) {
        for (Pet pet : plugin.getPetManager().getPets(player.getUniqueId())) {
            if (pet.getName().equalsIgnoreCase(name)) return pet;
        }
        return null;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player player)) return List.of();
        String cmd = command.getName().toLowerCase();
        if (cmd.equals("petadmin")) {
            if (args.length == 1) return List.of("reload");
            return List.of();
        }
        if (args.length == 1) {
            return List.of("spawn", "despawn", "rename", "feed", "givepearl");
        }
        if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (sub.equals("spawn") || sub.equals("despawn") || sub.equals("rename") || sub.equals("feed")) {
                List<String> names = new ArrayList<>();
                for (Pet pet : plugin.getPetManager().getPets(player.getUniqueId())) {
                    names.add(pet.getName());
                }
                return names;
            }
        }
        return List.of();
    }
}
