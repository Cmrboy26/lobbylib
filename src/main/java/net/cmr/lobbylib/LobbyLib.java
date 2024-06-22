package net.cmr.lobbylib;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import net.cmr.lobbylib.gui.Entry;
import net.cmr.lobbylib.gui.GUI;

public class LobbyLib extends JavaPlugin implements MinigamePlugin, Listener {

    List<MinigamePlugin> minigamePlugins;

    public static LobbyLib getLobbyManager() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("LobbyLib");
        if (plugin instanceof LobbyLib) {
            return (LobbyLib) plugin;
        }
        return null;
    }

    @Deprecated
    public LobbyLib() {
        super();
    }

    @Override
    public void onEnable() {
        getCommand("leave").setExecutor(this);
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("LobbyLib is searching for compatible minigames...");

        // Get every plugin on the server
        minigamePlugins = new ArrayList<MinigamePlugin>();
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        for (Plugin plugin : plugins) {
            // Check if the plugin is a MinigamePlugin
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
        getLogger().info("Removing all players from minigames...");
        List<Player> playersSnapshot = new ArrayList<Player>(Bukkit.getOnlinePlayers());
        for (Player player : playersSnapshot) {
            kickPlayerFromMinigame(player);
        }
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
                                joinMinigame(player, lobbyJoinableMinigame);
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
        if (command.getName().equalsIgnoreCase("selector")) {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                // Open up the minigame selector GUI
                return true;
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

    public void joinMinigame(Player player, LobbyJoinableMinigame minigamePlugin) {
        if (minigamePlugin.disableJoining()) {
            player.sendMessage(ChatColor.RED+"This minigame is currently disabled. Please try again later.");
            return;
        }
        if (!minigamePlugin.hasPermissionToJoin(player)) {
            player.sendMessage(ChatColor.RED+"You do not have permission to join this minigame.");
            return;
        }
        if (isPlayerInMinigame(player)) {
            kickPlayerFromMinigame(player);
        }
        minigamePlugin.joinMinigame(player);
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

    public void onPlayerLeaveMinigame(Player player, MinigamePlugin plugin) {
        // Give the player a compass
        getLogger().info("\""+player.getName()+"\" has left \""+plugin.getMinigameName()+"\".");
        ItemStack compass = new ItemStack(LOBBY_SELECTOR_MATERIAL);
        ItemMeta meta = compass.getItemMeta();
        meta.setDisplayName(LOBBY_SELECTOR_NAME);
        compass.setItemMeta(meta);
        player.getInventory().setItem(4, compass);
    }

    public void onPlayerJoinMinigame(Player player, MinigamePlugin plugin) {
        // Remove the compass from the player
        getLogger().info("\""+player.getName()+"\" joined \""+plugin.getMinigameName()+"\".");
        player.getInventory().setItem(4, null);
    }

    // Minigame Selector

    public static final String LOBBY_SELECTOR_NAME = ChatColor.GRAY+""+ChatColor.BOLD+"Minigame Menu";
    public static final Material LOBBY_SELECTOR_MATERIAL = Material.RECOVERY_COMPASS;

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (isPlayerInMinigame(player)) {
            kickPlayerFromMinigame(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Give the player a compass
        onPlayerLeaveMinigame(player, this);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        // Don't allow the player to move the compass if they are not in creative mode and opped
        boolean isLobbyCompass = isLobbySelector(event.getCurrentItem());
        boolean canMoveCompass = canMoveLobbySelector((Player) event.getWhoClicked());
        if (isLobbyCompass && !canMoveCompass) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemInteractEvent(PlayerInteractEvent event) {
        boolean isLobbyCompass = isLobbySelector(event.getItem());
        boolean canMoveCompass = canMoveLobbySelector(event.getPlayer());
        if (isLobbyCompass && !canMoveCompass) {
            event.setCancelled(true);
            // Open the minigame selector GUI
            openSelectorInventory(event.getPlayer());
        }
    }

    @EventHandler
    public void onDropEvent(PlayerDropItemEvent event) {
        if (isLobbySelector(event.getItemDrop().getItemStack()) && !canMoveLobbySelector(event.getPlayer())) {
            event.setCancelled(true);
        }
    }

    private boolean isLobbySelector(ItemStack item) {
        if (item == null) {
            getLogger().warning("Item is null");
            return false;
        }
        if (item.getType() != LOBBY_SELECTOR_MATERIAL) {
            getLogger().warning("Item is not a compass");
            return false;
        }
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            getLogger().warning("Item has no meta data");
            return false;
        }

        String itemName = meta.getDisplayName();
        if (itemName == null) {
            getLogger().warning("Item has no display name");
            return false;
        }

        boolean equals = meta.getDisplayName().equals(LOBBY_SELECTOR_NAME);
        getLogger().info("Item is a compass with display name \""+itemName+"\". Is it a lobby selector? "+equals);
        if (!equals) {
            getLogger().warning("Item is not a lobby selector:"+meta.getDisplayName()+"|"+LOBBY_SELECTOR_NAME);
        }
        return equals;
    }

    private boolean canMoveLobbySelector(Player player) {
        return player.isOp() && player.getGameMode() == org.bukkit.GameMode.CREATIVE;
    }

    public void openSelectorInventory(Player player) {
        // Open up the minigame selector GUI
        // NOTE: when programming, ensure to kick the player from their current minigame before sending them to their selected minigame (use joinMinigame(player, minigame))

        List<Entry> entries = new ArrayList<Entry>();

        for (MinigamePlugin minigamePlugin : minigamePlugins) {
            if (minigamePlugin instanceof LobbyJoinableMinigame) {
                LobbyJoinableMinigame lobbyJoinableMinigame = (LobbyJoinableMinigame) minigamePlugin;

                boolean minigameDisabled = lobbyJoinableMinigame.disableJoining();
                boolean playerHasPermission = lobbyJoinableMinigame.hasPermissionToJoin(player);
                final boolean canJoin = !minigameDisabled && playerHasPermission;

                String minigameName = lobbyJoinableMinigame.getMinigameName();
                String colors = "";
                for (ChatColor color : lobbyJoinableMinigame.getMinigameTitleColor()) {
                    colors += color.toString();
                }

                Material minigameIcon = lobbyJoinableMinigame.getMinigameIcon();

                List<String> lore = new ArrayList<String>();
                lore.add(lobbyJoinableMinigame.getMinigameDescription());
                if (!canJoin) {
                    if (!playerHasPermission) {
                        lore.add(ChatColor.RED+"You do not have permission to join this minigame.");
                    } else {
                        lore.add(ChatColor.RED+"This minigame is currently disabled. Please try again later.");
                    }
                    minigameName = ChatColor.RESET+""+ChatColor.STRIKETHROUGH+""+ChatColor.RED+minigameName;
                }

                minigameName = colors+minigameName;
                Entry entry = new Entry(minigameIcon, minigameName, 1, entries.size(), lore.toArray(new String[lore.size()])) {
                    @Override
                    public void onClick(InventoryClickEvent event) {
                        if (!canJoin) {
                            if (!playerHasPermission) {
                                player.sendMessage(ChatColor.RED+"You do not have permission to join this minigame.");
                            } else {
                                player.sendMessage(ChatColor.RED+"This minigame is currently disabled. Please try again later.");
                            }
                            return;
                        }
                        event.getWhoClicked().closeInventory();
                        joinMinigame(player, lobbyJoinableMinigame);
                    }
                    @Override
                    public void onUpdate(Player player) {
                        // Do nothing
                    }
                };
                entries.add(entry);
            }
        }

        GUI gui = new GUI(this, player, "selectorui", "Select a Minigame", (int) Math.max(Math.ceil(entries.size() / 9f), 1) * 9, entries) {
            @Override
            public void onOpen(InventoryOpenEvent event) {

            }

            @Override
            public void onClose(InventoryCloseEvent event) {

            }
        };
        gui.showGUI();
    }

}