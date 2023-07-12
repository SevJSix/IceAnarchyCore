package org.iceanarchy.core.general.gameplay;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ThreadLocalRandom;

public class PistonDupe implements Listener {

    private boolean brokeByPiston(HangingBreakEvent event) {
        for (BlockFace value : BlockFace.values()) {
            Material type = event.getEntity().getLocation().getBlock().getRelative(value).getType();
            if (type == Material.PISTON_MOVING_PIECE) {
                return true;
            }
        }
        return false;
    }

    @EventHandler
    public void onItemFrame(HangingBreakEvent event) {
        if (event.getCause() == HangingBreakEvent.RemoveCause.PHYSICS && brokeByPiston(event)) {
            ItemFrame itemFrame = (ItemFrame) event.getEntity();
            ItemStack item = itemFrame.getItem();
            if (item != null && !(item.getType() == Material.AIR)) {
                World world = itemFrame.getWorld();
                BlockFace blockFace = itemFrame.getAttachedFace();
                Location location = itemFrame.getLocation();
                Block block = world.getBlockAt(location);
                if (!(block.getRelative(blockFace).getType() == Material.AIR)) {
                    for (int i = 0; i < ThreadLocalRandom.current().nextInt(1, 4); i++) {
                        world.dropItemNaturally(location, item);
                    }
                }
            }
        }
    }
}
