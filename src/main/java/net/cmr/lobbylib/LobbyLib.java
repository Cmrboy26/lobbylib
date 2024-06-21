package net.cmr.lobbylib;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyLib extends JavaPlugin implements MinigamePlugin {

    List<MinigamePlugin> minigamePlugins;

    @Override
    public void onEnable() {
        getCommand("leave").setExecutor(this);
        getLogger().info("LobbyLib is searching for compatible minigames...");

        // Get every plugin on the server
        minigamePlugins = new ArrayList<MinigamePlugin>();
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        int count = 0;
        for (Plugin plugin : plugins) {
            // Check if the plugin is a MinigamePlugin
            count++;
            getLogger().info("Checking plugin: " + plugin.getName()+"... ("+count+"/"+plugins.length+")");

            if (plugin instanceof MinigamePlugin && !(plugin instanceof LobbyLib)) {
                MinigamePlugin minigamePlugin = (MinigamePlugin) plugin;
                minigamePlugins.add(minigamePlugin);
                // Do something with the MinigamePlugin
                getLogger().info("Found MinigamePlugin: " + minigamePlugin.getMinigameName());
            }
        }   
        getLogger().info("LobbyLib has found "+minigamePlugins.size()+" compatible minigames.");
    }

    @Override
    public void onDisable() {
        minigamePlugins.clear();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("leave")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // Check if the player is in a minigame
                if (isPlayerInMinigame(player)) {
                    // Kick the player from the minigame
                    kickPlayerFromMinigame(player);
                    player.sendMessage("You have left the minigame.");
                } else {
                    player.sendMessage(ChatColor.RED+"You are not in a minigame.");
                }
            } else {
                sender.sendMessage(ChatColor.RED+"You must be a player to use this command.");
            }
            return true;
        }
        if (command.getName().equalsIgnoreCase("join")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                if (args.length == 1) {
                    String minigameName = args[0];
                    for (MinigamePlugin minigamePlugin : minigamePlugins) {
                        String minimizedString = minigamePlugin.getMinigameName().toLowerCase().replaceAll(" ", "_");
                        if (minigamePlugin instanceof LobbyJoinableMinigame) {
                            LobbyJoinableMinigame lobbyJoinableMinigame = (LobbyJoinableMinigame) minigamePlugin;
                            if (minimizedString.equals(minigameName)) {
                                if (isPlayerInMinigame(player)) {
                                    kickPlayerFromMinigame(player);
                                }
                                lobbyJoinableMinigame.joinMinigame(player);
                                return true;
                            }
                        }
                    }
                    player.sendMessage(ChatColor.RED+"Minigame not found.");
                } else {
                    player.sendMessage(ChatColor.RED+"Usage: /join <minigame>");
                }
            } else {
                sender.sendMessage(ChatColor.RED+"You must be a player to use this command.");
            }
            return true;
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("leave")) {
            return null;
        }
        if (command.getName().equalsIgnoreCase("join")) {
            if (args.length == 1) {
                List<String> completions = new ArrayList<String>();
                for (MinigamePlugin minigamePlugin : minigamePlugins) {
                    if (minigamePlugin instanceof LobbyJoinableMinigame) {
                        completions.add(minigamePlugin.getMinigameName().toLowerCase().replaceAll(" ", "_"));
                    }
                }
                return completions;
            }
        }
        return null;
    }

    @Override
    public boolean isPlayerInMinigame(Player player) {
        for (MinigamePlugin minigamePlugin : minigamePlugins) {
            if (minigamePlugin.isPlayerInMinigame(player)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void kickPlayerFromMinigame(Player player) {
        for (MinigamePlugin minigamePlugin : minigamePlugins) {
            if (minigamePlugin.isPlayerInMinigame(player)) {
                minigamePlugin.kickPlayerFromMinigame(player);
                return;
            }
        }
    }

    @Override
    public String getMinigameName() {
        return "Lobby";
    }

}