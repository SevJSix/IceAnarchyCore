package org.iceanarchy.core.patches.fixes;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.vehicle.VehicleEntityCollisionEvent;
import org.iceanarchy.core.ViolationManager;

import java.util.Arrays;

public class EntityCollisions extends ViolationManager implements Listener {
    public EntityCollisions() {
        super(1);
    }

    @EventHandler
    public void onCollide(VehicleEntityCollisionEvent event) {
        if (event.getEntity().getVehicle() == event.getVehicle()) return;
        Vehicle vehicle = event.getVehicle();
        increment(vehicle.getChunk().hashCode());
        if (getVLS(vehicle.getChunk().hashCode()) > 5000) {
            Vehicle[] vehicles = Arrays.stream(vehicle.getChunk().getEntities()).filter(e -> e instanceof Vehicle).toArray(Vehicle[]::new);
            Arrays.stream(vehicles).forEach(Entity::remove);
            remove(vehicle.getChunk().hashCode());
        }
    }
}
