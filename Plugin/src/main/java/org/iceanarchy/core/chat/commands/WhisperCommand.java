package org.iceanarchy.core.chat.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.chat.ChatManager;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;

import java.util.Arrays;

public class WhisperCommand implements ICommandExecutor {

    private final ChatManager manager = IceAnarchy.getInstance().getChatManager();

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Send a private direct message to a player";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null && target.isOnline()) {
                    if (args.length > 1) {
                        manager.sendWhisper(player, target, String.join(" ", Arrays.copyOfRange(args, 1, args.length)));
                    } else {
                        Common.sendMessage(player, String.format("&c/%s %s <message>", label, args[0]));
                    }
                } else {
                    Common.sendMessage(player, String.format("&c%s is not online", args[0]));
                }
            } else {
                Common.sendMessage(player, String.format("&c/%s <player> <message>", label));
            }
        }
        return true;
    }
}
