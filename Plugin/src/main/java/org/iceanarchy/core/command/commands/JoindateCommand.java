package org.iceanarchy.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;

import java.util.Arrays;
import java.util.Date;

public class JoindateCommand implements ICommandExecutor {

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Shows the exact time and date when a player first joined";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        OfflinePlayer target;
        if (args.length > 0) {
            target = Arrays.stream(Bukkit.getOfflinePlayers()).filter(player -> player.getName().equalsIgnoreCase(args[0])).findAny().orElse(null);
        } else if (sender instanceof Player) {
            target = (OfflinePlayer) sender;
        } else {
            Common.sendMessage(sender, "&cMust be a player");
            return true;
        }

        if (target != null) {
            Common.sendMessage(sender, String.format("&b%s &rjoined on &b%s", target.getName(), new Date(target.getFirstPlayed())));
        } else {
            Common.sendMessage(sender, "&cThat player has not joined before");
        }
        return true;
    }
}
