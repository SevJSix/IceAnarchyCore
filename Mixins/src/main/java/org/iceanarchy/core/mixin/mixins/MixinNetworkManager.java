package org.iceanarchy.core.mixin.mixins;

import io.netty.channel.ChannelHandlerContext;
import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import net.minecraft.server.v1_12_R1.NetworkManager;
import net.minecraft.server.v1_12_R1.Packet;
import net.minecraft.server.v1_12_R1.PacketPlayInFlying;
import net.minecraft.server.v1_12_R1.PlayerConnection;
import org.bukkit.entity.Player;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.mixin.MixinMain;
import org.iceanarchy.core.common.packet.PacketEvent;

/**
 * @author SevJ6
 * @since 2/22/23
 * TODO: Tell mason to fix the classloader issue
 * */
public class MixinNetworkManager {

    @Inject(info = @MethodInfo(_class = NetworkManager.class, name = "sendPacket", sig = Packet.class, rtype = void.class), at = @At(pos = At.Position.HEAD))
    public static void onOutgoingPacket(CallbackInfo ci) {
        NetworkManager manager = (NetworkManager) ci.getSelf();
        if (manager.i() instanceof PlayerConnection) {
            Packet<?> packet = (Packet<?>) ci.getParameters()[0];
            Player player = ((PlayerConnection) manager.i()).player.getBukkitEntity();
            PacketEvent event = new PacketEvent(packet, player);
            Common.PACKET_EVENT_BUS.post(event);
            if (event.isCancelled()) ci.cancel();
        }
    }

    @Inject(info = @MethodInfo(_class = NetworkManager.class, name = "channelRead0", sig = {ChannelHandlerContext.class, Packet.class}, rtype = void.class), at = @At(pos = At.Position.HEAD))
    public static void onIncomingPacket(CallbackInfo ci) {
        NetworkManager manager = (NetworkManager) ci.getSelf();
        if (manager.i() instanceof PlayerConnection) {
            Packet<?> packet = (Packet<?>) ci.getParameters()[1];
            Player player = ((PlayerConnection) manager.i()).player.getBukkitEntity();
            PacketEvent event = new PacketEvent(packet, player);
            Common.PACKET_EVENT_BUS.post(event);
            if (event.isCancelled()) ci.cancel();
        }
    }
}
