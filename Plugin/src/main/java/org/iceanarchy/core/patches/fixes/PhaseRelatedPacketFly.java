package org.iceanarchy.core.patches.fixes;

import io.netty.util.internal.ConcurrentSet;
import lombok.SneakyThrows;
import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.iceanarchy.core.patches.PatchManager;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class PhaseRelatedPacketFly implements PacketListener {
    public static final ConcurrentSet<Player> chorusExempted = new ConcurrentSet<>();

    private final List<Material> safeBlocks = Arrays.asList(Material.SIGN, Material.SIGN_POST, Material.WALL_SIGN, Material.GOLD_PLATE,
            Material.IRON_PLATE, Material.WOOD_PLATE, Material.STONE_PLATE, Material.BED, Material.BED_BLOCK, Material.SKULL,
            Material.TRAP_DOOR, Material.FENCE, Material.FENCE_GATE, Material.IRON_FENCE, Material.COBBLE_WALL, Material.BANNER,
            Material.STANDING_BANNER, Material.WALL_BANNER, Material.WOODEN_DOOR, Material.WOOD_DOOR, Material.BIRCH_DOOR, Material.ACACIA_DOOR,
            Material.DARK_OAK_DOOR, Material.IRON_DOOR, Material.JUNGLE_DOOR, Material.SPRUCE_DOOR, Material.STAINED_GLASS_PANE, Material.THIN_GLASS,
            Material.FENCE, Material.IRON_FENCE, Material.BIRCH_FENCE, Material.ACACIA_FENCE, Material.DARK_OAK_FENCE, Material.JUNGLE_FENCE,
            Material.NETHER_FENCE, Material.SPRUCE_FENCE, Material.PISTON_EXTENSION, Material.PISTON_MOVING_PIECE, Material.PISTON_BASE);

    private final Field hasPos;

    @SneakyThrows
    public PhaseRelatedPacketFly() {
        this.hasPos = PacketPlayInFlying.class.getDeclaredField("hasPos");
        this.hasPos.setAccessible(true);
    }


    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        PacketPlayInFlying packet = (PacketPlayInFlying) event.getPacket();
        Player player = event.getPlayer();
        if (player.isGliding()) return;
        if (chorusExempted.contains(player)) {
            chorusExempted.remove(player);
            return;
        }
        Location playerLocation = player.getLocation();
        Location packetLocation = new Location(player.getWorld(), packet.a(playerLocation.getX()), packet.b(playerLocation.getY()), packet.c(playerLocation.getZ()));
        if (checkDelta(packet, player)) {
            PatchManager.cancelAndLagback(event);
            return;
        }
        if (playerLocation.getBlockX() != packetLocation.getBlockX() || playerLocation.getBlockZ() != packetLocation.getBlockZ()) {
            if (safeBlocks.contains(packetLocation.getBlock().getType()) || safeBlocks.contains(packetLocation.getBlock().getRelative(BlockFace.UP).getType()))
                return;
            if (safeBlocks.contains(playerLocation.getBlock().getType()) || safeBlocks.contains(playerLocation.getBlock().getRelative(BlockFace.UP).getType()))
                return;
            if (isIllegalLocation(packetLocation)) {
                PatchManager.cancelAndLagback(event);
            }
        }
    }

    private boolean isIllegalLocation(Location packetLocation) {
        Block blockInFeet = packetLocation.getBlock();
        Block blockInHead = packetLocation.getBlock().getRelative(BlockFace.UP);
        double yHeight = packetLocation.getY();
        double blockY = packetLocation.getBlockY();
        return (blockInFeet.getType().isSolid() || blockInHead.getType().isSolid())
                && (yHeight - blockY == 0.0D || yHeight - blockY < 0.225);
    }

    @SneakyThrows
    private boolean hasPos(PacketPlayInFlying packet) {
        return hasPos.getBoolean(packet);
    }

    private boolean checkDelta(PacketPlayInFlying packet, Player player) {
        Location originalLocation = player.getLocation();
        double originalY = originalLocation.getY();
        double packetY = packet.b(originalY);
        return Math.abs(packetY - originalY) > 10.0D;
    }

    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }
}
