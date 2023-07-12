package org.iceanarchy.core.common.boiler.interfaces;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface ICommandExecutor {


    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    String getDescription();

    boolean onCommand(CommandSender sender, Command command, String label, String[] args);

}
