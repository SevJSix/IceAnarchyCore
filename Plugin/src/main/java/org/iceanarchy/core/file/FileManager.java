package org.iceanarchy.core.file;

import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.common.boiler.tools.NBTHelper;

import java.io.File;

public class FileManager extends Manager {

    @Getter private static File playtimes;
    @Getter private static File timeEpoch;

    @SneakyThrows
    @Override
    public void init(JavaPlugin plugin) {
        playtimes = new File(plugin.getDataFolder(), "PlayTimes");
        if (!playtimes.exists()) playtimes.mkdirs();

        timeEpoch = new File(plugin.getDataFolder(), "epoch.nbt");
        if (!timeEpoch.exists()) {
            timeEpoch.createNewFile();
            NBTTagCompound compound = new NBTTagCompound();
            compound.setLong("Time", System.currentTimeMillis());
            NBTHelper.writeAndFlush(compound, timeEpoch);
        }
    }

    @Override
    public void onShutdown(JavaPlugin plugin) {

    }
}
