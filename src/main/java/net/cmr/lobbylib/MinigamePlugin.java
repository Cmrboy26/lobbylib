package net.cmr.lobbylib;

import org.bukkit.entity.Player;

/**
 * Represents a LobbyLib compatible plugin, which allows players to /leave the minigame and prevents players from
 * being in multiple minigames at once. If you want your plugin to be joinable via /join, 
 * you should implement {@link LobbyJoinableMinigame} instead.
 */
public interface MinigamePlugin {
    
    public boolean isPlayerInMinigame(Player player);
    public void kickPlayerFromMinigame(Player player);
    public String getMinigameName();

}
