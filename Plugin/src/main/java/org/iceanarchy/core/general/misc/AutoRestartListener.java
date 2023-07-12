package org.iceanarchy.core.general.misc;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.event.ServerTickEvent;

public class AutoRestartListener implements Listener {

    @EventHandler
    public void onTick(ServerTickEvent event) {
        if (event.getTick() > 1728000) {
            restartTask(IceAnarchy.getInstance());
        }
    }

    public void restartTask(JavaPlugin plugin) {
        new Thread(() -> {
            try {
                broadcast("&eServer restarting in 1 minute...");
                Thread.sleep(60000);
                broadcast("&eServer restarting in 30 seconds...");
                Thread.sleep(30000);
                broadcast("&eServer restarting in 5 seconds...");
                Thread.sleep(1000);
                broadcast("&eServer restarting in 4 seconds...");
                Thread.sleep(1000);
                broadcast("&eServer restarting in 3 seconds...");
                Thread.sleep(1000);
                broadcast("&eServer restarting in 2 seconds...");
                Thread.sleep(1000);
                broadcast("&eServer restarting in 1 seconds...");
                Thread.sleep(1000);
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            if (Bukkit.getOnlinePlayers().size() > 0) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    Bukkit.getOnlinePlayers().forEach(player -> player.kickPlayer(ChatColor.GOLD + "Server restarting"));
                });
            }
            Bukkit.shutdown();
        }).start();
    }

    public void broadcast(String msg) {
        Bukkit.getServer().broadcastMessage(Common.translateAltColorCodes(msg));
    }
}
