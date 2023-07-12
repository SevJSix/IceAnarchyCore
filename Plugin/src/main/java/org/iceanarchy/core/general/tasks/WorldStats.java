package org.iceanarchy.core.general.tasks;

import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.boiler.interfaces.ScheduledTask;

import java.io.File;
import java.util.Objects;

public class WorldStats {

    @ScheduledTask(delay = (3600 * 1000)) // recalculate every hour
    public static void updateWorldStatsData() {
        long start = System.currentTimeMillis();
        long size = 0L;
        IceAnarchy plugin = IceAnarchy.getInstance();
        for (String world : plugin.getConfig().getStringList("Worlds")) {
            File[] files = Objects.requireNonNull((new File(world)).listFiles());
            for (File file : files) {
                if (file.isFile()) {
                    size += file.length();
                }
            }
        }

        plugin.setFileSize(size / 1048576.0 / 1000.0);
        plugin.getLogger().info(String.format("Finished calculating world size in %sms", System.currentTimeMillis() - start));
    }
}
