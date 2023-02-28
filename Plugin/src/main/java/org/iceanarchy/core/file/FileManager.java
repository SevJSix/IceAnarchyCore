package org.iceanarchy.core.file;

import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.common.boiler.Manager;

import java.io.File;

public class FileManager extends Manager {

    @Getter private static File playtimes;

    @Override
    public void init(JavaPlugin plugin) {
        playtimes = new File(plugin.getDataFolder(), "PlayTimes");
        if (!playtimes.exists()) playtimes.mkdirs();
    }
}
