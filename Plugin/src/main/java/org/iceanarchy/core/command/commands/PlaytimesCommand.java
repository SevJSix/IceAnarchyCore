package org.iceanarchy.core.command.commands;

import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;
import org.iceanarchy.core.common.boiler.tools.NBTHelper;
import org.iceanarchy.core.file.FileManager;

import java.io.File;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PlaytimesCommand implements ICommandExecutor {

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Shows a player's total time spent playing on the server";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (args.length > 0 && args[0].equalsIgnoreCase("top")) {
                List<File> files = Arrays.stream(Objects.requireNonNull(FileManager.getPlaytimes().listFiles())).sorted(Comparator.comparingInt(value -> NBTHelper.loadNBTFromFile((File) value).getInt("TimePlayed")).reversed()).collect(Collectors.toList());
                if (files.size() > 21) files = files.subList(0, 20);
                for (int i = 0; i < files.size(); i++) {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(files.get(i).getName().replace(".nbt", "")));
                    Common.sendMessage(player, String.format("&7%s. &b%s &r- &7%s", i + 1, offlinePlayer.getName(), Common.getFormattedInterval(TimeUnit.SECONDS.toMillis(NBTHelper.loadNBTFromFile(files.get(i)).getInt("TimePlayed")))));
                }

                return true;
            }

            File playerFile = new File(FileManager.getPlaytimes(), player.getUniqueId().toString().concat(".nbt"));
            if (!playerFile.exists()) {
                Common.sendMessage(player, "&cPlaytime not yet calculated. Re-log to see playtime");
                return true;
            }

            try {
                NBTTagCompound compound = NBTHelper.loadNBTFromFile(playerFile);
                Common.sendMessage(player, String.format("&3Your total playtime is &a%s", Common.getFormattedInterval(TimeUnit.SECONDS.toMillis(compound.getInt("TimePlayed")))));
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        return true;
    }
}
