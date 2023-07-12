package org.iceanarchy.core.pvp;

import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.pvp.listener.RevertListeners;

public class PvPManager extends Manager {

    @Override
    public void init(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new RevertListeners(this), plugin);
    }

    @Override
    public void onShutdown(JavaPlugin plugin) {

    }

    public boolean isIllegal(ItemStack itemStack) {
        if (isOverStacked(itemStack)) return true;
        else return isOverEnchanted(itemStack);
    }

    public boolean isOverEnchanted(ItemStack itemStack) {
        if (itemStack == null) return false;
        if (!itemStack.hasItemMeta()) return false;
        if (!itemStack.getItemMeta().hasEnchants()) return false;
        return itemStack.getItemMeta().getEnchants().entrySet().stream().anyMatch(entry -> entry.getValue() > entry.getKey().getMaxLevel());
    }

    public boolean isOverStacked(ItemStack itemStack) {
        if (itemStack == null) return false;
        return itemStack.getAmount() > itemStack.getMaxStackSize();
    }

    public void revertItemStack(ItemStack itemStack) {
        if (itemStack == null) return;
        if (!itemStack.hasItemMeta()) return;
        if (!itemStack.getItemMeta().hasEnchants()) return;
        ItemMeta meta = itemStack.getItemMeta();
        meta.getEnchants().forEach((key, value) -> {
            if (value > key.getMaxLevel()) {
                meta.removeEnchant(key);
                meta.addEnchant(key, key.getMaxLevel(), true);
            }
        });
        itemStack.setItemMeta(meta);
        if (isOverStacked(itemStack)) itemStack.setAmount(itemStack.getMaxStackSize());
    }

    public void revertInventory(Inventory inventory) {
        for (ItemStack content : inventory.getContents()) {
            revertItemStack(content);
        }
    }
}
