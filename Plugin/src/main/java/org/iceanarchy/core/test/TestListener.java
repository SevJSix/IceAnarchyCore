package org.iceanarchy.core.test;

import org.iceanarchy.core.common.packet.PacketEvent;
import org.iceanarchy.core.common.packet.PacketListener;

public class TestListener implements PacketListener {

    @Override
    public void handlePacketEvent(PacketEvent event) {
        System.out.println(event.getPacket());
    }
}
