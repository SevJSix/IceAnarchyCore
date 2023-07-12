package org.iceanarchy.core.patches;

import me.txmc.protocolapi.PacketEvent;
import net.minecraft.server.v1_12_R1.*;
import org.bukkit.command.Command;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.patches.fixes.*;

import java.util.HashMap;

public class PatchManager extends Manager {

    @Override
    public void init(JavaPlugin plugin) {
        IceAnarchy main = (IceAnarchy) plugin;
        main.registerListener(new ProjectileCrash());
        main.registerListener(new Boatfly());
        main.registerListener(new Boatfly(), PacketPlayInUseEntity.class);
        main.registerListener(new RedstoneLimiter());
        main.registerListener(new PortalLag());
        main.registerListener(new Godmode());
        main.registerListener(new DispenserCrash());
        main.registerListener(new CrystalSlowdown());
        main.registerListener(new BlockPhysics());
        main.registerListener(new LeverSpam());
        main.registerListener(new EntityCollisions());
        main.registerListener(new PvPExploits());
        main.registerListener(new PvPExploits(), PacketPlayInUseEntity.class);
        main.registerListener(new PacketRateLimiter(), (Class<? extends Packet<?>>) null);
        main.registerListener(new NoCommentExploit(), PacketPlayOutBlockChange.class, PacketPlayInBlockDig.class);
        main.registerListener(new ConventionalPacketFly(), PacketPlayInTeleportAccept.class);
//        main.registerListener(new PhaseRelatedPacketFly(), PacketPlayInFlying.class, PacketPlayInFlying.PacketPlayInPosition.class, PacketPlayInFlying.PacketPlayInPositionLook.class);
//        main.registerListener(new LongJump(), PacketPlayInFlying.class, PacketPlayInFlying.PacketPlayInPosition.class, PacketPlayInFlying.PacketPlayInPositionLook.class, PacketPlayInTeleportAccept.class);
        main.registerListener(new TacoHack(), PacketPlayInWindowClick.class);
        main.registerListener(new EntityLimit());
        Common.registerTasks(plugin, EntityLimit.class);
        new LightLag();
    }

    @Override
    public void onShutdown(JavaPlugin plugin) {

    }

    public static void cancelAndLagback(PacketEvent.Incoming event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        EntityPlayer ep = ((CraftPlayer) player).getHandle();
        IceAnarchy.run(() -> ep.playerConnection.teleport(player.getLocation()));
    }
}
