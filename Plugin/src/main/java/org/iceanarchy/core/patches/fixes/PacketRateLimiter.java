package org.iceanarchy.core.patches.fixes;

import me.txmc.protocolapi.PacketEvent;
import me.txmc.protocolapi.PacketListener;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.ViolationManager;

public class PacketRateLimiter extends ViolationManager implements PacketListener {

    public PacketRateLimiter() {
        super(1, 300);
    }

    @Override
    public void incoming(PacketEvent.Incoming event) throws Throwable {
        increment(event.getPlayer().getUniqueId().hashCode());
        int vls = getVLS(event.getPlayer().getUniqueId().hashCode());
        if (vls > 300) {
            remove(event.getPlayer().getUniqueId().hashCode());
            IceAnarchy.run(() -> event.getPlayer().kickPlayer(String.format("Packet limit reached. %s vls", vls)));
        }
    }

    @Override
    public void outgoing(PacketEvent.Outgoing event) throws Throwable {

    }
}
