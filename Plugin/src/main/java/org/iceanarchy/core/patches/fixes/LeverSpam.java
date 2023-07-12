package org.iceanarchy.core.patches.fixes;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.ViolationManager;

public class LeverSpam extends ViolationManager implements Listener {
    public LeverSpam() {
        super(1);
    }

    @EventHandler
    public void onFlick(PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;
        if (event.getClickedBlock().getType() == Material.LEVER) {
            Player player = event.getPlayer();
            increment(player.getUniqueId().hashCode());
            if (getVLS(player.getUniqueId().hashCode()) > 20) {
                IceAnarchy.run(() -> player.kickPlayer("Flicking lever too often"));
                remove(player.hashCode());
            }
        }
    }
}
