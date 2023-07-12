package org.iceanarchy.core.patches.fixes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockFromToEvent;
import org.iceanarchy.core.common.Common;

public class BlockPhysics implements Listener {

    @EventHandler
    public void onPhysics(BlockFromToEvent event) {
        event.setCancelled(Common.getTPS() < 13);
    }
}
