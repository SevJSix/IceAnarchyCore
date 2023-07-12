package org.iceanarchy.core.command.commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;

import java.util.concurrent.TimeUnit;

public class DistanceCommand implements ICommandExecutor {

    private final double SPRINT_BPS = 5.56;
    private final double FLY_BPS = 50.00;

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "tells you how far away a set of coordinates is by walking and by flying";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) return true;
        Player player = (Player) sender;
        if (args.length == 3) {
            try {
                double x = Double.parseDouble(args[0]);
                double y = Double.parseDouble(args[1]);
                double z = Double.parseDouble(args[2]);
                Location location = new Location(player.getWorld(), x, y, z);
                int blocksAway = Common.getBlocksAwayFrom(player.getLocation(), location);
                long sprintingTime = TimeUnit.SECONDS.toMillis(Math.round(blocksAway / SPRINT_BPS));
                long flyingTime = TimeUnit.SECONDS.toMillis(Math.round(blocksAway / FLY_BPS));
                Common.sendMessage(player, "\n&bIt would roughly take &3" + Common.getFormattedInterval(sprintingTime) + " &bto walk &r" + blocksAway + " blocks &bat 20km/h assuming you are on a highway without stopping" + "&r\n\n" + "&bIt would roughly take &3" + Common.getFormattedInterval(flyingTime) + " &bto elytra fly &r" + blocksAway + " blocks &bat 216km/h assuming you are on a highway without stopping\n");
            } catch (Throwable ignored) {
                Common.sendMessage(sender, "&cEnter 3 coordinates please.");
            }
        } else {
            Common.sendMessage(sender, String.format("&c/%s <x> <y> <z>", label));
        }
        return true;
    }
}
