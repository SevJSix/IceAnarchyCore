package org.iceanarchy.core.patches.fixes;

import org.bukkit.block.Dispenser;
import org.bukkit.block.Dropper;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.iceanarchy.core.common.Common;

public class DispenserCrash implements Listener {

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        if (event.getBlock().getState() instanceof Dropper) return;
        int height = event.getBlock().getY();
        Dispenser dispenser = (Dispenser) event.getBlock().getState();
        if (!hasShulker(dispenser)) return;
        if (height == 255 || height <= 1) {
            event.setCancelled(true);
        }
    }

    private boolean hasShulker(Dispenser dispenser) {
        for (ItemStack item : dispenser.getInventory()) {
            if (item != null && isShulker(item)) {
                return true;
            }
        }
        return false;
    }

    private boolean isShulker(ItemStack item) {
        return item.hasItemMeta()
                && item.getItemMeta() instanceof BlockStateMeta
                && ((BlockStateMeta) item.getItemMeta()).getBlockState() instanceof ShulkerBox;
    }
}
