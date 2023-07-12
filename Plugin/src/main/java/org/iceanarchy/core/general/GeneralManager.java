package org.iceanarchy.core.general;

import net.minecraft.server.v1_12_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.general.gameplay.PistonDupe;
import org.iceanarchy.core.general.gameplay.RandomSpawn;
import org.iceanarchy.core.general.misc.AutoRestartListener;
import org.iceanarchy.core.general.misc.FirstJoinListener;
import org.iceanarchy.core.general.tasks.PlayTimes;
import org.iceanarchy.core.general.tasks.Tablist;
import org.iceanarchy.core.general.tasks.WorldStats;

public class GeneralManager extends Manager {

    private PlayTimes playTimes;

    @Override
    public void init(JavaPlugin plugin) {
        Common.registerTasks(plugin, Tablist.class, PlayTimes.class, WorldStats.class);
        playTimes = new PlayTimes();
        plugin.getServer().getPluginManager().registerEvents(playTimes, plugin);
        plugin.getServer().getPluginManager().registerEvents(new AutoRestartListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new RandomSpawn(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new FirstJoinListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new PistonDupe(), plugin);
    }

    @Override
    public void onShutdown(JavaPlugin plugin) {
        Bukkit.getOnlinePlayers().forEach(player -> playTimes.savePlayerPlayTime(player));
    }
}
