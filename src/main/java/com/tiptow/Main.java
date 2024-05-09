package com.tiptow;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin implements Listener {
    private String permissionNode;
    private ChatColor privateChatColor;

    @Override
    public void onEnable() {
        // Register event listener
        getServer().getPluginManager().registerEvents(this, this);

        // Load config
        loadConfig();
    }

    private void loadConfig() {
        // Save default config if not present
        saveDefaultConfig();

        // Load config values
        FileConfiguration config = getConfig();
        permissionNode = config.getString("permission_node", "privatechat.view");
        String colorName = config.getString("private_chat_color", "AQUA");

        // Convert color name to ChatColor
        try {
            privateChatColor = ChatColor.valueOf(colorName);
        } catch (IllegalArgumentException e) {
            getLogger().warning("Invalid color name in config. Using default color AQUA");
            privateChatColor = ChatColor.AQUA;
        }
    }

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        // Check if the message starts with the prefix
        if (message.startsWith("@")) {
            // Check if the sender has the permission to send private messages
            if (sender.hasPermission(permissionNode)) {
                // Send the formatted message to players who have the permission to view private messages
                for (Player recipient : Bukkit.getOnlinePlayers()) {
                    if (recipient.hasPermission(permissionNode)) {
                        recipient.sendMessage(privateChatColor + "[Private] " + ChatColor.RESET + sender.getName() + ": " + ChatColor.WHITE + message.substring(1)); // Exclude the "@" prefix
                    }
                }
                // Cancel the global chat message
                event.setCancelled(true);
            }
        }
    }
}
