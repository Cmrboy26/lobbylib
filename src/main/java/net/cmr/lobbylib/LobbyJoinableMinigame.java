package net.cmr.lobbylib;

import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface LobbyJoinableMinigame extends MinigamePlugin {
    
    public void joinMinigame(Player player);
    public Material getMinigameIcon();
    public String getMinigameDescription();

}
