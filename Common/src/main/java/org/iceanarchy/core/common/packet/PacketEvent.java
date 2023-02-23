package org.iceanarchy.core.common.packet;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minecraft.server.v1_12_R1.Packet;
import org.bukkit.entity.Player;

/**
 * @author SevJ6
 * @since 2/22/23
 * Purpose of this packet listener is simplicity
 * */
@Data
@RequiredArgsConstructor
public class PacketEvent {

    private final Packet<?> packet;
    private final Player player;
    private boolean cancelled = false;

}
