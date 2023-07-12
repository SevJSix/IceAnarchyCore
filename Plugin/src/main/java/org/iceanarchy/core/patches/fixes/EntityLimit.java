package org.iceanarchy.core.patches.fixes;

import com.destroystokyo.paper.event.entity.EntityAddToWorldEvent;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.interfaces.ScheduledTask;

import java.util.*;

public class EntityLimit implements Listener {

    private static final HashMap<EntityType, Integer> entityPerChunk = new HashMap<EntityType, Integer>(){{
        put(EntityType.WITHER, 6);
        put(EntityType.ARMOR_STAND, 15);
        put(EntityType.ENDER_CRYSTAL, 35);
        put(EntityType.WITHER_SKULL, 10);
    }};

    @EventHandler
    public void onEntitySpawn(EntityAddToWorldEvent event) {
        Entity entity = event.getEntity();
        if (!entityPerChunk.containsKey(entity.getType())) return;
        int amt = enumerate(entity.getLocation().getChunk(), entity.getType());
        int max = entityPerChunk.get(entity.getType());
        if (amt >= max) {
            entity.remove();
        }
    }

    @ScheduledTask(delay = 1000L * 30L)
    public static void cullEntities() {
        for (Chunk chunk : getChunks()) {
            for (Map.Entry<EntityType, Integer> entry : entityPerChunk.entrySet()) {
                removeAmount(entry.getKey(), entry.getValue(), chunk);
            }
        }
    }

    private static ArrayList<Chunk> getChunks() {
        ArrayList<Chunk> chunks = new ArrayList<>();
        for (World world : Bukkit.getWorlds()) {
            chunks.addAll(Arrays.asList(world.getLoadedChunks()));
        }
        return chunks;
    }

    private int enumerate(Chunk chunk, EntityType entityType) {
        return (int) Arrays.stream(chunk.getEntities()).filter(e -> e.getType() == entityType).count();
    }

    private static void removeAmount(EntityType type, int maxAmount, Chunk chunk) {
        List<Entity> correctType = new ArrayList<>();
        Arrays.stream(chunk.getEntities()).filter(entity -> entity.getType() == type).forEach(correctType::add);
        int entityAmount = correctType.size();
        if (entityAmount <= maxAmount) return;
        List<Entity> sized = correctType.subList(0, entityAmount - maxAmount);
        sized.forEach(Entity::remove);
    }
}
