package net.cmr.lobbylib;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface MinigamePlugin {
    
    public boolean isPlayerInMinigame(Player player);
    public void kickPlayerFromMinigame(Player player);
    public String getMinigameName();
    
}
