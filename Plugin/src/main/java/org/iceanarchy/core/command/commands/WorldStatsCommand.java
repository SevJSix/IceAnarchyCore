package org.iceanarchy.core.command.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;
import org.iceanarchy.core.common.boiler.tools.NBTHelper;
import org.iceanarchy.core.file.FileManager;

import java.text.DecimalFormat;
import java.util.Calendar;

public class WorldStatsCommand implements ICommandExecutor {

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Shows the age, unique joins, and total file size of the server";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        IceAnarchy plugin = IceAnarchy.getInstance();
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis() - NBTHelper.loadNBTFromFile(FileManager.getTimeEpoch()).getLong("Time"));
        int year = calendar.get(Calendar.YEAR) - 1970;
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        if (year < 0) {
            year = 0;
            month = 0;
            day = 0;
        }

        Common.sendMessage(sender, String.join("\n", plugin.getConfig().getStringList("WorldstatsMessage"))
                .replace("%years%", String.valueOf(year))
                .replace("%months%", String.valueOf(month))
                .replace("%days%", String.valueOf(day))
                .replace("%fileSize%", new DecimalFormat("#.##").format(plugin.getFileSize()))
                .replace("%totalPlayers%", String.valueOf(Bukkit.getOfflinePlayers().length)));
        return true;
    }
}
