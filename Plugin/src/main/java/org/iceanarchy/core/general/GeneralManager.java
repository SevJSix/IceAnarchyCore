package org.iceanarchy.core.general;

import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.general.tasks.PlayTimes;
import org.iceanarchy.core.general.tasks.Tablist;

public class GeneralManager extends Manager {

    @Override
    public void init(JavaPlugin plugin) {
        Common.registerTasks(plugin, Tablist.class, PlayTimes.class);
        plugin.getServer().getPluginManager().registerEvents(new PlayTimes(), plugin);
    }
}
