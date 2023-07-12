package org.iceanarchy.core.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.chat.ChatManager;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ITabExecutor;
import org.iceanarchy.core.common.boiler.interfaces.IgnoreInfo;

import java.util.*;
import java.util.stream.Collectors;

public class IgnoreCommand implements ITabExecutor, IgnoreInfo {

    ChatManager manager = IceAnarchy.getInstance().getChatManager();

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Disable all chat messages and whispers from a specified player";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new Thread(() -> {
            if (!(sender instanceof Player)) return;

            if (!(args.length > 0)) {
                Common.sendMessage(sender, String.format("&c/%s <player>", label));
                return;
            }

            if (args[0].equalsIgnoreCase(sender.getName())) {
                Common.sendMessage(sender, "&cCannot ignore yourself!");
                return;
            }

            OfflinePlayer ignored = Bukkit.getOfflinePlayer(args[0]);
            if (!Bukkit.getOfflinePlayer(ignored.getUniqueId()).hasPlayedBefore()) {
                Common.sendMessage(sender, String.format("&cCannot %s %s because they have never joined", command.getName(), args[0]));
                return;
            }

            UUID playerUUID = ((Player) sender).getUniqueId();
            UUID ignoredUUID = ignored.getUniqueId();
            if (label.equalsIgnoreCase("unignore") || command.getName().equalsIgnoreCase("unignore")) {
                if (ignoreMap.containsKey(playerUUID) && ignoreMap.get(playerUUID).contains(ignoredUUID)) {
                    manager.removeIgnore(playerUUID, ignoredUUID);
                    Common.sendMessage(sender, String.format("&4No longer ignoring %s", ignored.getName()));
                } else {
                    Common.sendMessage(sender, String.format("&cNot currently ignoring %s", ignored.getName()));
                }
            } else {
                if (!(ignoreMap.containsKey(playerUUID) && ignoreMap.get(playerUUID).contains(ignoredUUID))) {
                    manager.addIgnore(playerUUID, ignoredUUID);
                    Common.sendMessage(sender, String.format("&4Now ignoring %s", ignored.getName()));
                } else {
                    Common.sendMessage(sender, String.format("&cAlready ignoring %s", ignored.getName()));
                }
            }
        }).start();
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return null;
        if (label.equalsIgnoreCase("unignore") || command.getName().equalsIgnoreCase("unignore")) {
            Player player = (Player) sender;
            if (ignoreMap.containsKey(player.getUniqueId())) {
                return ignoreMap.get(player.getUniqueId()).stream().map(uuid -> Bukkit.getOfflinePlayer(uuid).getName()).collect(Collectors.toList());
            }
        }
        return Bukkit.getOnlinePlayers().stream().map(HumanEntity::getName).collect(Collectors.toList());
    }
}
