package org.iceanarchy.core.mixin.mixins;

import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import net.minecraft.server.v1_12_R1.Block;
import net.minecraft.server.v1_12_R1.EntityEnderCrystal;
import net.minecraft.server.v1_12_R1.World;
import org.bukkit.event.block.BlockBreakEvent;

public class MixinEndCrystal {

    @Inject(info = @MethodInfo(_class = EntityEnderCrystal.class, name = "<init>", sig = World.class, rtype = void.class), at = @At(pos = At.Position.TAIL))
    public static void mixinEnderCrystal(CallbackInfo ci) {
        EntityEnderCrystal crystal = (EntityEnderCrystal) ci.getSelf();
        crystal.a = 0;
    }
}
