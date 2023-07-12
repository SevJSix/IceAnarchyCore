package org.iceanarchy.core.patches.fixes;

import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import net.minecraft.server.v1_12_R1.EntityPlayer;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PacketPlayInTeleportAccept;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.NumberConversions;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.ViolationManager;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Set;

public class LongJump extends ViolationManager implements PacketListener {

    private final double THRESHOLD = 0.55D;
    private Field hasPos;
    private Field packetX;
    private Field packetY;
    private Field packetZ;
    private Set<Player> set;

    public LongJump() {
        super(1, 5);
        try {
            this.hasPos = PacketPlayInFlying.class.getDeclaredField("hasPos");
            this.hasPos.setAccessible(true);
            this.packetX = PacketPlayInFlying.class.getDeclaredField("x");
            this.packetX.setAccessible(true);
            this.packetY = PacketPlayInFlying.class.getDeclaredField("y");
            this.packetY.setAccessible(true);
            this.packetZ = PacketPlayInFlying.class.getDeclaredField("z");
            this.packetZ.setAccessible(true);
            this.set = new HashSet<>();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        checkChorus(event);
        if (event.getPacket() instanceof PacketPlayInFlying) {
            PacketPlayInFlying packet = (PacketPlayInFlying) event.getPacket();
            try {
                if (hasPos.getBoolean(packet)) {
                    Player player = event.getPlayer();
                    if (this.set.contains(player)) return;
                    EntityPlayer ep = getEntityPlayer(player);
                    if (player.getVehicle() != null || player.isGliding() || player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR || player.isOp())
                        return;
                    Location packetLocation = new Location(player.getWorld(), packetX.getDouble(packet), packetY.getDouble(packet), packetZ.getDouble(packet), ep.yaw, ep.pitch);
                    Location playerLocation = player.getLocation();
                    double dist = getDistanceOn2DPlaneAxis(playerLocation, packetLocation);
                    PotionEffect speed = player.getActivePotionEffects().stream().filter(p -> p.getType().equals(PotionEffectType.SPEED)).findFirst().orElse(null);
                    double threshold = (speed == null) ? THRESHOLD : (speed.getAmplifier() == 1) ? THRESHOLD * 1.25 : (speed.getAmplifier() == 0) ? THRESHOLD * 1.15 : THRESHOLD;
                    if (tooFar(dist, ep)) {
                        lagBack(player, playerLocation);
                        return;
                    }
                    if (dist > threshold && !(dist > 20)) {
                        increment(player.getUniqueId().hashCode());
                        if (getVLS(player.getUniqueId().hashCode()) >= 10) {
                            lagBack(player, playerLocation);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void checkChorus(PacketEvent.Incoming event) {
        if (event.getPacket() instanceof PacketPlayInTeleportAccept) {
            Player player = event.getPlayer();
            ItemStack item = (player.getInventory().getItemInOffHand().getType() == Material.CHORUS_FRUIT) ? player.getEquipment().getItemInOffHand() : (player.getInventory().getItemInMainHand().getType() == Material.CHORUS_FRUIT) ? player.getEquipment().getItemInMainHand() : null;
            if (item != null) {
                this.set.add(player);
                Bukkit.getScheduler().runTaskLater(IceAnarchy.getInstance(), () -> this.set.remove(player), 20L);
            }
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }

    private void lagBack(Player player, Location location) {
        handleTask(() -> {
            player.teleport(location, PlayerTeleportEvent.TeleportCause.PLUGIN);
        });
    }

    private boolean tooFar(double dist, EntityPlayer ep) {
        boolean og = ep.onGround;
        float fd = ep.fallDistance;
        if (dist > 20) return false;
        return (dist > 1.5D && og)
                || (dist >= 1.6D && dist < 1.7D && !og && fd < 4.5F)
                || (dist >= 1.7D && dist < 1.8D && !og && fd < 8.5F)
                || (dist >= 1.8D && dist < 1.9D && !og && fd < 12.5F)
                || (dist >= 1.9D && !og && fd < 20.0F);
    }

    public void handleTask(Runnable task) {
        IceAnarchy.run(task);
    }

    public double getDistanceOn2DPlaneAxis(Location l1, Location l2) {
        double x1 = l1.getX();
        double x2 = l2.getX();
        double z1 = l1.getZ();
        double z2 = l2.getZ();
        double dSquared = NumberConversions.square((x2 - x1)) + NumberConversions.square((z2 - z1));
        return Math.sqrt(dSquared);
    }

    private EntityPlayer getEntityPlayer(Player player) {
        return ((CraftPlayer) player).getHandle();
    }
}
