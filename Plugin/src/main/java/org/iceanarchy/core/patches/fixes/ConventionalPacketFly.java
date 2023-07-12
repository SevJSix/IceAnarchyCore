package org.iceanarchy.core.patches.fixes;

import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.ViolationManager;
import org.iceanarchy.core.patches.PatchManager;

public class ConventionalPacketFly extends ViolationManager implements PacketListener {
    public ConventionalPacketFly() {
        super(1, 40);
    }

    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        Player player = event.getPlayer();
        checkChorus(player);
        addVLS(player);
        int vLs = getVLS(player.getUniqueId().hashCode());
        if (vLs > 80) {
            PatchManager.cancelAndLagback(event);
            if (vLs > 400) {
                IceAnarchy.run(() -> player.kickPlayer(String.format("Kicked for excess teleport packets. %s vls", vLs)));
                remove(player.getUniqueId().hashCode());
            }
        }
    }

    private void checkChorus(Player player) {
        if (PhaseRelatedPacketFly.chorusExempted.contains(player)) return;
        ItemStack item = (player.getInventory().getItemInOffHand().getType() == Material.CHORUS_FRUIT) ? player.getEquipment().getItemInOffHand() : (player.getInventory().getItemInMainHand().getType() == Material.CHORUS_FRUIT) ? player.getEquipment().getItemInMainHand() : null;
        if (item != null) PhaseRelatedPacketFly.chorusExempted.add(player);
    }

    private void addVLS(Player player) {
        if (((CraftPlayer) player).getHandle().onGround) {
            increment(player.getUniqueId().hashCode());
        } else {
            for (int i = 0; i < 5; i++) {
                increment(player.getUniqueId().hashCode());
            }
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }
}
