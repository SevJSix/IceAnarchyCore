package org.iceanarchy.core.patches.fixes;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;

public class ProjectileVelocity implements Listener {

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Projectile projectile = event.getEntity();
        event.setCancelled(projectile.getVelocity().lengthSquared() > 10);
    }
}
