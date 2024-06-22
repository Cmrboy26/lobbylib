package net.cmr.lobbylib;

import org.bukkit.entity.Player;

/**
 * Represents a LobbyLib compatible plugin, which allows players to /leave the minigame and prevents players from
 * being in multiple minigames at once. If you want your plugin to be joinable via /join, 
 * you should implement {@link LobbyJoinableMinigame} instead.
 */
public interface MinigamePlugin {

    /**
     * Checks if the player is involved or participating in the minigame. Involvement can be defined as being in a queue,
     * in a game, or any other state where the plugin will affect the player's inventory, position, xp, etc.
     */
    public boolean isPlayerInMinigame(Player player);
    /**
     * Whenever LobbyLib wants to kick a player from the minigame at any time (i.e. with /leave, /join, etc.),
     * this method will be called. Thread safety is not guaranteed, so you should handle that yourself.
     * In addition, the call may be made after onDisable() has been called, so keep that in mind if you're setting
     * values to null in that method.
     */
    public void kickPlayerFromMinigame(Player player);
    /**
     * Returns the name of the minigame.
     * Will be used in the /join command (spaces will be replaced with "_" and it will be case-insensitive)
     */
    public String getMinigameName();

}
