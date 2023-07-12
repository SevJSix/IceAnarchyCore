package org.iceanarchy.core.patches.fixes;

import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class Godmode implements Listener {

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        if (event.getPlayer().isInsideVehicle()) {
            Player player = event.getPlayer();
            Vehicle vehicle = (Vehicle) player.getVehicle();
            if (vehicle != null) {
                Chunk playerChunk = player.getChunk();
                Chunk vehicleChunk = vehicle.getChunk();
                if (!vehicleChunk.isLoaded()) vehicleChunk.load();
                if (playerChunk != vehicleChunk) {
                    vehicle.eject();
                    vehicle.remove();
                }
            }
        }
    }
}
