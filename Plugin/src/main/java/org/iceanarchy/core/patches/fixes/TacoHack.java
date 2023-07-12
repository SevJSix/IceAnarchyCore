package org.iceanarchy.core.patches.fixes;

import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import net.minecraft.server.v1_12_R1.PacketPlayInWindowClick;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.iceanarchy.core.IceAnarchy;

public class TacoHack implements PacketListener {
    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        Player player = event.getPlayer();
        PacketPlayInWindowClick packetPlayInWindowClick = (PacketPlayInWindowClick) event.getPacket();
        int slot = packetPlayInWindowClick.b();
        if ((player.getOpenInventory() == null && slot > player.getInventory().getSize()) || slot > player.getOpenInventory().countSlots()) {
            event.setCancelled(true);
            kickPlayerAsync(player, "Invalid inventory slot clicked [" + "SlotID: " + slot + "]");
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing outgoing) throws Throwable {

    }

    private void kickPlayerAsync(Player player, String reason) {
        Bukkit.getScheduler().runTask(IceAnarchy.getInstance(), () -> {
            player.kickPlayer(ChatColor.translateAlternateColorCodes('&', reason));
        });
    }
}
