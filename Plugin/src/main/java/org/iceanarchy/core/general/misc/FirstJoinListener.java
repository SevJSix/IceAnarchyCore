package org.iceanarchy.core.general.misc;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.iceanarchy.core.common.Common;

public class FirstJoinListener implements Listener {

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) return;
        Common.sendMessage(event.getPlayer(), "&bWelcome. Join the discord https://discord.gg/hFYgcWVFhu" + "\n" + "&bThis message will not be displayed again.");
    }
}
