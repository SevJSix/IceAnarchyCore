package org.iceanarchy.core.general.tasks;

import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ScheduledTask;

import java.util.List;

public class Tablist {

    public static final long startTime = System.currentTimeMillis();

    @ScheduledTask
    public static void update() {
        Bukkit.getOnlinePlayers().forEach(player -> {
            List<String> header = IceAnarchy.getInstance().getConfig().getStringList("TabHeader");
            List<String> footer = IceAnarchy.getInstance().getConfig().getStringList("TabFooter");
            TextComponent componentHeader = new TextComponent(parsePlaceHolders(String.join("\n", header), player));
            TextComponent componentFooter = new TextComponent(parsePlaceHolders(String.join("\n", footer), player));
            player.setPlayerListHeaderFooter(componentHeader, componentFooter);
        });
    }

    private static String parsePlaceHolders(String input, Player player) {
        double tps = ((CraftServer) Bukkit.getServer()).getServer().recentTps[0];
        String strTps = (tps >= 20) ? String.format("%s*20.0", ChatColor.GREEN) : String.format("%s%.2f", Common.getTPSColor(tps), tps);
        String uptime = Common.getFormattedInterval(System.currentTimeMillis() - startTime);
        String online = String.valueOf(Bukkit.getOnlinePlayers().size());
        String ping = String.valueOf(getPing(player));
        return Common.translateAltColorCodes(input.replace("%tps%", strTps).replace("%players%", online)).replace("%ping%", ping).replace("%uptime%", uptime);
    }

    private static int getPing(Player player) {
        return ((CraftPlayer) player).getHandle().ping;
    }
}
