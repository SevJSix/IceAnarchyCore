package org.iceanarchy.core.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;
import org.iceanarchy.core.common.boiler.interfaces.IgnoreInfo;

import java.util.List;
import java.util.UUID;

public class IgnoreListCommand implements ICommandExecutor, IgnoreInfo {

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Shows all of your ignored players";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (ignoreMap.containsKey(player.getUniqueId()) && ignoreMap.get(player.getUniqueId()).size() > 0) {
            StringBuilder builder = new StringBuilder();
            List<UUID> ignores = ignoreMap.get(player.getUniqueId());
            builder.append("Ignored Players (").append(ignores.size()).append("): ");
            for (int i = 0; i < ignores.size(); i++) {
                OfflinePlayer ignored = Bukkit.getOfflinePlayer(ignores.get(i));
                if (ignored == null) continue;
                if (i == ignores.size() - 1) {
                    builder.append("&a").append(ignored.getName()).append("&r");
                } else {
                    builder.append("&a").append(ignored.getName()).append("&r, ");
                }
            }
            Common.sendMessage(player, builder.toString());
        } else {
            Common.sendMessage(player, "&cYou are not ignoring anyone right now");
        }
        return true;
    }
}
