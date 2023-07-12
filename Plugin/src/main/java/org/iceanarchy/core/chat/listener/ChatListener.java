package org.iceanarchy.core.chat.listener;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.IgnoreInfo;

import java.util.logging.Level;

public class ChatListener implements Listener, IgnoreInfo {

    @EventHandler(priority = EventPriority.HIGH)
    public void onChat(AsyncPlayerChatEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        String ogMessage = event.getMessage();
        String playerName = player.getDisplayName() + ChatColor.RESET;
        String message = (ogMessage.startsWith(">")) ? String.format("<%s>%s %s", playerName, ChatColor.GREEN, ogMessage) : String.format("<%s> %s", playerName, ogMessage);
        IceAnarchy.getInstance().getLogger().log(Level.INFO, message);
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            if (ignoreMap.containsKey(onlinePlayer.getUniqueId()) && ignoreMap.get(onlinePlayer.getUniqueId()).contains(player.getUniqueId()))
                return;
            onlinePlayer.sendMessage(message);
        }
    }
}
