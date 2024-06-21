package net.cmr.lobbylib;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class LobbyLib extends JavaPlugin {

    @Override
    public void onEnable() {
        // Get every plugin on the server
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            // Check if the plugin is a MinigamePlugin
            if (plugin instanceof MinigamePlugin) {
                MinigamePlugin minigamePlugin = (MinigamePlugin) plugin;
                // Do something with the MinigamePlugin
                getLogger().info("Found MinigamePlugin: " + minigamePlugin.getMinigameName());
            }
        }   
    }

    @Override
    public void onDisable() {
        
    }

}