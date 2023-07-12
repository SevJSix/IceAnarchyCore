package org.iceanarchy.core.patches.fixes;

import org.bukkit.block.Block;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.iceanarchy.core.ViolationManager;
import org.iceanarchy.core.common.Common;

import java.util.concurrent.ThreadLocalRandom;

public class RedstoneLimiter extends ViolationManager implements Listener {

    private final ThreadLocalRandom random = ThreadLocalRandom.current();
    private final double STRICT_TPS = 13;
    private final double STRICT_MAX_VLS = 70;
    private final double REGULAR_MAX_VLS = 20000;

    public RedstoneLimiter() {
        super(1, 300);
    }

    @EventHandler
    public void onRedstoneEvent(BlockRedstoneEvent event) {
        process(event);
    }

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        process(event);
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        process(event);
    }


    private void process(BlockEvent event) {
        Block block = event.getBlock();
        int vls = getVLS(block.getChunk().hashCode());

        increment(block.getChunk().hashCode());
        if (Common.getTPS() < STRICT_TPS && vls > STRICT_MAX_VLS) {
            cancelEvent(event);
            if (shouldBreakBlock()) block.breakNaturally();
        } else {
            if (vls > REGULAR_MAX_VLS) {
                if (shouldBreakBlock()) event.getBlock().breakNaturally();
                cancelEvent(event);
            }
        }
    }
    private void cancelEvent(BlockEvent event) {
        if (event instanceof BlockRedstoneEvent) {
            ((BlockRedstoneEvent)event).setNewCurrent(0);
        } else ((Cancellable)event).setCancelled(true);
    }

    private boolean shouldBreakBlock() {
        return random.nextInt(0, 10) == 1;
    }
}
