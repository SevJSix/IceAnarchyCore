package org.iceanarchy.core.patches.fixes;

import net.minecraft.server.v1_12_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.List;

public class ProjectileCrash implements Listener {

    private final List<EntityType> validEntities = Arrays.asList(EntityType.ARROW, EntityType.SPECTRAL_ARROW, EntityType.TIPPED_ARROW, EntityType.SNOWBALL, EntityType.ENDER_PEARL, EntityType.EGG, EntityType.LINGERING_POTION, EntityType.SPLASH_POTION, EntityType.THROWN_EXP_BOTTLE);

    public static float getMotionModifier(org.bukkit.entity.Entity entity) {
        switch (entity.getType()) {
            case FIREBALL:
            case SMALL_FIREBALL:
                return 1.10f;
            case WITHER_SKULL:
                return 1.15f;
            default:
                return 0.99f;
        }
    }

    public static float getGravityModifier(org.bukkit.entity.Entity entity) {
        switch (entity.getType()) {
            case SNOWBALL:
            case ENDER_PEARL:
            case EGG:
                return 0.03f;
            case ARROW:
            case SPECTRAL_ARROW:
            case TIPPED_ARROW:
            case LINGERING_POTION:
            case SPLASH_POTION:
                return 0.05f;
            case THROWN_EXP_BOTTLE:
                return 0.07f;
            default:
                return 0.0f;
        }
    }

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        Projectile projectile = event.getEntity();
        if (!(validEntities.contains(projectile.getType()))) return;
        MovingObjectPosition trace = traceEntity(projectile);
        if (trace == null || !(projectile.getWorld().getChunkAt(trace.a().getX(), trace.a().getZ()).isLoaded())) {
            projectile.remove();
        }
    }

    public MovingObjectPosition traceEntity(Entity entity) {
        World world = ((CraftWorld) entity.getWorld()).getHandle();
        net.minecraft.server.v1_12_R1.Entity nmsEntity = ((CraftEntity) entity).getHandle();
        Location loc = entity.getLocation();
        MovingObjectPosition landing = null;
        double posX = loc.getX();
        double posY = loc.getY();
        double posZ = loc.getZ();
        BlockPosition originalPosition = new BlockPosition(posX, posY, posZ);
        float gravityModifier = getGravityModifier(entity);
        float motionModifier = getMotionModifier(entity);

        Vector velocity = new Vector(nmsEntity.motX, nmsEntity.motY, nmsEntity.motZ);
        if (nmsEntity instanceof EntityFireball) {
            EntityFireball fireball = (EntityFireball) nmsEntity;
            velocity = new Vector(fireball.dirX, fireball.dirY, fireball.dirZ);
        }
        double motionX = velocity.getX();
        double motionY = velocity.getY();
        double motionZ = velocity.getZ();

        boolean hasLanded = false;
        while (!hasLanded && posY > 0.0D) {
            double fPosX = posX + motionX;
            double fPosY = posY + motionY;
            double fPosZ = posZ + motionZ;

            Vec3D start = new Vec3D(posX, posY, posZ);
            Vec3D future = new Vec3D(fPosX, fPosY, fPosZ);

            landing = world.rayTrace(start, future);
            hasLanded = (landing != null) && (landing.a() != null);

            posX = fPosX;
            posY = fPosY;
            posZ = fPosZ;
            motionX *= motionModifier;
            motionY *= motionModifier;
            motionZ *= motionModifier;
            motionY -= gravityModifier;

            double distSquared = originalPosition.distanceSquared(posX, posY, posZ);
            if (distSquared > 48000) break;
        }
        return landing;
    }
}
