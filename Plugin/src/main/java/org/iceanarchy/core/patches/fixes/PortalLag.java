package org.iceanarchy.core.patches.fixes;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalExitEvent;
import org.iceanarchy.core.common.Common;

import java.util.Arrays;

public class PortalLag implements Listener {

    @EventHandler
    public void onPortalEnter(EntityPortalEnterEvent event) {
        Entity[] entities = event.getLocation().getNearbyEntities(4, 4, 4).toArray(new Entity[0]);
        if (entities.length > 30 || Bukkit.getTPS()[0] < 19.3) {
            Arrays.stream(entities).filter(entity -> entity instanceof Item).forEach(Entity::remove);
        }
    }

    @EventHandler
    public void onPortalExit(EntityPortalExitEvent event) {
        if (event.getTo().getChunk().getEntities().length > 20 || Common.getTPS() < 19.3) {
            event.setCancelled(true);
            event.setTo(event.getFrom());
        }
    }
}
