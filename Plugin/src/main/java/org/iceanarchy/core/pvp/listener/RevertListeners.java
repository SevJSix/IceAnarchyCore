package org.iceanarchy.core.pvp.listener;

import lombok.RequiredArgsConstructor;
import org.bukkit.block.ShulkerBox;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerAttemptPickupItemEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.iceanarchy.core.pvp.PvPManager;

@RequiredArgsConstructor
public class RevertListeners implements Listener {

    private final PvPManager manager;

    @EventHandler
    public void onOpen(InventoryOpenEvent event) {
        manager.revertInventory(event.getInventory());
        manager.revertInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        manager.revertInventory(event.getInventory());
        manager.revertInventory(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onPickup(PlayerAttemptPickupItemEvent event) {
        if (manager.isIllegal(event.getItem().getItemStack())) {
            event.setCancelled(true);
            event.getItem().remove();
        }
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent event) {
        if (manager.isIllegal(event.getItemDrop().getItemStack())) {
            event.setCancelled(true);
            event.getItemDrop().remove();
            manager.revertInventory(event.getPlayer().getInventory());
        }
    }

    @EventHandler
    public void onDispense(BlockDispenseEvent event) {
        if (event.getItem() != null && manager.isIllegal(event.getItem())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPortal(EntityPortalEvent event) {
        if (event.getEntity() instanceof Item) {
            ItemStack item = ((Item) event.getEntity()).getItemStack();
            if (item != null && manager.isIllegal(item)) {
                event.setCancelled(true);
                event.getEntity().remove();
            }
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        if (manager.isIllegal(event.getMainHandItem())) manager.revertItemStack(event.getMainHandItem());
        else if (manager.isIllegal(event.getOffHandItem())) manager.revertItemStack(event.getOffHandItem());
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        if (event.getBlockPlaced().getState() instanceof ShulkerBox) {
            ShulkerBox box = (ShulkerBox) event.getBlockPlaced().getState();
            manager.revertInventory(box.getInventory());
        }
    }
}
