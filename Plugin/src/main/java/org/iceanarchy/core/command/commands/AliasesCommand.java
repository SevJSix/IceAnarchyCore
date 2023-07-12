package org.iceanarchy.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.command.CommandManager;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ITabExecutor;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class AliasesCommand implements ITabExecutor {

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Shows all aliases for a specified server command";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length > 0) {
            Map.Entry<String, Command> cmdEntry = Bukkit.getCommandMap().getKnownCommands().entrySet().stream().filter(entry -> Objects.equals(entry.getKey(), args[0]) || entry.getValue().getAliases().contains(args[0])).distinct().findAny().orElse(null);
            if (cmdEntry != null) {
                Command cmd = cmdEntry.getValue();
                StringBuilder builder = new StringBuilder();
                builder.append("Aliases of ").append(cmd.getName()).append(" (").append(cmd.getAliases().size()).append("): ");
                for (int i = 0; i < cmd.getAliases().size(); i++) {
                    if (i == cmd.getAliases().size() - 1) {
                        builder.append("&a").append(cmd.getAliases().get(i));
                    } else {
                        builder.append("&a").append(cmd.getAliases().get(i)).append("&r, ");
                    }
                }
                Common.sendMessage(sender, builder.toString());
            } else {
                Common.sendMessage(sender, "&cSorry, unknown command");
            }
        } else {
            Common.sendMessage(sender, String.format("&c/%s <command>", label));
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        return ((CraftServer) Bukkit.getServer()).getCommandMap().getCommands().stream().map(Command::getName).filter(str -> Bukkit.getPluginCommand(str) != null).distinct().collect(Collectors.toList());
    }
}
