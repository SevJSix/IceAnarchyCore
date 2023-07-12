package org.iceanarchy.core.patches.fixes;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEnderCrystal;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class CrystalSlowdown implements Listener {
    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player) || !(event.getEntity() instanceof EnderCrystal)) return;
        EntityEnderCrystal crystal = ((CraftEnderCrystal) event.getEntity()).getHandle();
        event.setCancelled(crystal.a < 2);
    }
}
