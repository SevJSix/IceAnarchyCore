package org.iceanarchy.core.mixin.mixins;

import co.aikar.timings.Timing;
import co.aikar.timings.Timings;
import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import net.minecraft.server.v1_12_R1.MinecraftServer;
import org.bukkit.Bukkit;
import org.iceanarchy.core.common.boiler.event.ServerTickEvent;

public class MixinMinecraftServer {

    @Inject(info = @MethodInfo(_class = MinecraftServer.class, name = "C", rtype = void.class), at = @At(pos = At.Position.HEAD))
    public static void onTick(CallbackInfo ci) {
        MinecraftServer mc = (MinecraftServer) ci.getSelf();
        Timing timing = Timings.of(Bukkit.getPluginManager().getPlugins()[0], "Tick Event");
        timing.startTiming();
        ServerTickEvent event = new ServerTickEvent(mc.aq(), mc);
        Bukkit.getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) ci.cancel();
        timing.stopTiming();
    }
}
