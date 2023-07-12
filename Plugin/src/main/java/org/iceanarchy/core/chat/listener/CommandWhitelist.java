package org.iceanarchy.core.chat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.command.CommandManager;
import org.iceanarchy.core.common.Common;

public class CommandWhitelist implements Listener {

    private final CommandManager manager = IceAnarchy.getInstance().getCommandManager();

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncChat(PlayerCommandPreprocessEvent event) {
        if (!event.getMessage().startsWith("/")) return;
        if (event.getPlayer().isOp()) return;
        if (event.getMessage().toLowerCase().startsWith("/kill")) {
            event.setCancelled(true);
            event.getPlayer().setHealth(0.0D);
            return;
        }
        if (!manager.isCommandValid(event.getMessage().split(" ")[0].replace("/", ""))) {
            event.setCancelled(true);
            Common.sendMessage(event.getPlayer(), "&4Command not supported.");
        }
    }
}
