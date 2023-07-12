package org.iceanarchy.core.mixin.mixins;

import lombok.SneakyThrows;
import me.txmc.rtmixin.CallbackInfo;
import me.txmc.rtmixin.mixin.At;
import me.txmc.rtmixin.mixin.Inject;
import me.txmc.rtmixin.mixin.MethodInfo;
import me.txmc.rtmixin.mixin.Replace;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_12_R1.block.CraftBlockState;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftMagicNumbers;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.material.MaterialData;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.mixin.MixinMain;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

public class MixinWorld {

    private final List<Material> indestructable = Arrays.asList(Material.BEDROCK, Material.ENDER_PORTAL, Material.ENDER_PORTAL_FRAME);

    @Replace(info = @MethodInfo(_class = World.class, name = "setTypeAndData", sig = {BlockPosition.class, IBlockData.class, int.class}, rtype = boolean.class))
    public static boolean onTypeUpdate(CallbackInfo ci) {
        BlockState blockstate;
        World world = (World) ci.getSelf();
        BlockPosition blockposition = (BlockPosition) ci.getParameters()[0];
        IBlockData iblockdata = (IBlockData) ci.getParameters()[1];
        int i = (int) ci.getParameters()[2];
        Material type = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()).getState().getType();
        if (type == Material.BEDROCK || type == Material.ENDER_PORTAL) {
            System.out.println("Stopped unbreakable block from being broken at " + world.getWorld().getName() + " " + blockposition);
            return false;
        }

        if (world.captureTreeGeneration) {
            BlockState state = null;
            Iterator<BlockState> it = world.capturedBlockStates.iterator();

            while(it.hasNext()) {
                state = it.next();
                if (state.getX() == blockposition.getX() && state.getY() == blockposition.getY() && state.getZ() == blockposition.getZ()) {
                    it.remove();
                    break;
                }
            }

            if (state == null) {
                state = CraftBlockState.getBlockState(world, blockposition.getX(), blockposition.getY(), blockposition.getZ(), i);
            }

            state.setTypeId(CraftMagicNumbers.getId(iblockdata.getBlock()));
            state.setRawData((byte)iblockdata.getBlock().toLegacyData(iblockdata));
            world.capturedBlockStates.add(state);
            return true;
        } else if (blockposition.isInvalidYLocation()) {
            return false;
        } else if (!world.isClientSide && world.worldData.getType() == WorldType.DEBUG_ALL_BLOCK_STATES) {
            return false;
        } else {
            Chunk chunk = world.getChunkAtWorldCoords(blockposition);
            Block block = iblockdata.getBlock();
            blockstate = null;
            if (world.captureBlockStates) {
                blockstate = world.getWorld().getBlockAt(blockposition.getX(), blockposition.getY(), blockposition.getZ()).getState();
                world.capturedBlockStates.add(blockstate);
            }

            IBlockData iblockdata1 = chunk.a(blockposition, iblockdata);
            if (iblockdata1 == null) {
                if (world.captureBlockStates) {
                    world.capturedBlockStates.remove(blockstate);
                }

                return false;
            } else {
//                if (iblockdata.c() != iblockdata1.c() || iblockdata.d() != iblockdata1.d()) {
//                    world.methodProfiler.a("checkLight");
//                    chunk.runOrQueueLightUpdate(() -> {
//                        world.w(blockposition);
//                    });
//                    world.methodProfiler.b();
//                }

                if (!world.captureBlockStates) {
                    world.notifyAndUpdatePhysics(blockposition, chunk, iblockdata1, iblockdata, i);
                }

                return true;
            }
        }
    }
}
