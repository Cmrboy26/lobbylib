package net.cmr.lobbylib;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public interface LobbyJoinableMinigame extends MinigamePlugin {
    
    /**
     * When the player selects the minigame on the lobby compass or uses /join, this will be called.
     * The minigame should handle how the player joins the game. Typically, a map will be randomly
     * selected for the player to join. Running this method does not have to guarantee that 
     * {@link #isPlayerInMinigame(Player)} will return true.
     */
    public void joinMinigame(Player player);

    /**
     * @return true if the player is in the minigame, false otherwise.
     */
    public Material getMinigameIcon();

    /**
     * @return the color of the minigame title to be used in the lobby compass.
     */
    public default ChatColor[] getMinigameTitleColor() {
        return new ChatColor[] {};
    }

    /**
     * @return a description that will be displayed in the lobby compass (if enabled).
     */
    public String getMinigameDescription();

    /**
     * If this returns true, the player will not be able to join the minigame, as it is either disabled, undergoing maintenance, etc.
     */
    public default boolean disableJoining() {
        return false;
    }

    /**
     * @return if the player has permission to join the minigame. If this returns false, the player will not be able to join.
     */
    public default boolean hasPermissionToJoin(Player player) {
        return true;
    }

}
