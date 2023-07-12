package org.iceanarchy.core.command.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.iceanarchy.core.command.CommandManager;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;

import java.util.Map;

public class HelpCommand implements ICommandExecutor {

    private final CommandManager manager;

    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Gives details on available commands to players";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        StringBuilder builder = new StringBuilder();
        builder.append("\n");
        for (Map.Entry<String, Object> entry : manager.getCommandMap().entrySet()) {
            builder.append("&b/").append(entry.getKey()).append(" &r- &7").append(manager.getDescription(entry.getValue())).append("\n&r");
        }
        Common.sendChatPacket((Player) sender, builder.toString());
        return true;
    }
}
