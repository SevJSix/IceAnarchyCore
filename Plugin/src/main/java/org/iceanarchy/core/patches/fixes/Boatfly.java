package org.iceanarchy.core.patches.fixes;

import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import net.minecraft.server.v1_12_R1.Entity;
import net.minecraft.server.v1_12_R1.EntityBoat;
import net.minecraft.server.v1_12_R1.PacketPlayInUseEntity;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.entity.Boat;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.iceanarchy.core.ViolationManager;

import java.util.ArrayList;
import java.util.List;

public class Boatfly extends ViolationManager implements PacketListener, Listener {

    private final List<Player> players = new ArrayList<>();
    private int time = 0;

    public Boatfly() {
        super(1, 5);
    }

    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        PacketPlayInUseEntity packet = (PacketPlayInUseEntity) event.getPacket();
        Player player = event.getPlayer();
        World world = ((CraftWorld) player.getWorld()).getHandle();
        PacketPlayInUseEntity.EnumEntityUseAction action = packet.a();
        Entity entity = packet.a(world);
        if (entity == null) return;
        if (entity instanceof EntityBoat && (action.equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT_AT) || action.equals(PacketPlayInUseEntity.EnumEntityUseAction.INTERACT))) {
            increment(player.getUniqueId().hashCode());
            if (getVLS(player.getUniqueId().hashCode()) > 15) {
                event.setCancelled(true);
                entity.die();
            }
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        this.time++;
        Player player = event.getPlayer();
        double ax = 0.0D;
        double ay = 0.0D;
        double az = 0.0D;
        if (this.time == 2) {
            ax = player.getLocation().getX();
            ay = player.getLocation().getY();
            az = player.getLocation().getZ();
            this.time = 0;
        }
        double x = 0.0D;
        double y = 0.0D;
        double z = 0.0D;
        if (player.isInsideVehicle() && player.getVehicle() instanceof Boat) {
            Boat boat = (Boat) player.getVehicle();
            Material m = boat.getLocation().getBlock().getType();
            if (m != Material.STATIONARY_WATER && m != Material.WATER && !boat.isOnGround()) {
                if (!players.contains(player)) {
                    players.add(player);
                    x = player.getLocation().getX();
                    y = player.getLocation().getY();
                    z = player.getLocation().getZ();
                }
                if ((boat.getVelocity().getY() > 0.0D || boat.getVelocity().getY() < 0.12D) &&
                        players.contains(player))
                    if (x != 0.0D && y != 0.0D && z != 0.0D) {
                        boat.teleport(new Location(boat.getWorld(), x, y, z));
                        player.teleport(new Location(player.getWorld(), x, y + 0.5D, z));
                        players.remove(player);
                    } else {
                        boat.teleport(new Location(boat.getWorld(), ax, ay, az));
                        player.teleport(new Location(player.getWorld(), ax, ay, az));
                    }
            }
        }
    }
}
