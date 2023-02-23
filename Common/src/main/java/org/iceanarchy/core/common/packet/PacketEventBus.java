package org.iceanarchy.core.common.packet;

import io.netty.util.internal.ConcurrentSet;

/**
 * @author SevJ6
 * @since 2/22/23
 * Purpose of this packet listener is simplicity
 * */
public class PacketEventBus {

    private final ConcurrentSet<PacketListener> listeners = new ConcurrentSet<>();

    public void register(PacketListener listener) {
        listeners.add(listener);
    }

    public void post(PacketEvent event) {
        listeners.forEach(listener -> listener.handlePacketEvent(event));
    }
}
