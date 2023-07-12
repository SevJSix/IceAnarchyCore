package org.iceanarchy.core.general.gameplay;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RandomSpawn implements Listener {

    private final List<Material> blocked = Arrays.asList(Material.LAVA, Material.STATIONARY_LAVA, Material.WATER, Material.STATIONARY_WATER, Material.CACTUS, Material.WEB, Material.MAGMA, Material.AIR);

    @EventHandler
    public void onRespawn(PlayerRespawnEvent event) {
        if (event.isBedSpawn()) return;
        event.setRespawnLocation(calcRandomSpawn());
    }

    @EventHandler
    public void onFirstJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPlayedBefore()) return;
        event.getPlayer().teleport(calcRandomSpawn());
    }

    private Location calcRandomSpawn() {
        int x = 200, z = 200;
        int randX = ThreadLocalRandom.current().nextInt(-x, x);
        int randZ = ThreadLocalRandom.current().nextInt(-z, z);
        Location loc = null;
        World world = Bukkit.getWorld("world");
        int attempts = 0;
        while (loc == null) {
            loc = new Location(world, randX + 0.5, world.getHighestBlockYAt(randX, randZ), randZ + 0.5);
            if (attempts > 1500) break;
            if (isBadLocation(loc)) loc = null;
            attempts++;
        }
        return loc;
    }

    private boolean isBadLocation(Location location) {
        Block block = location.getBlock();
        if (blocked.contains(block.getType())) return true;
        for (BlockFace value : BlockFace.values()) {
            if (blocked.contains(block.getRelative(value).getType())) return true;
        }
        return false;
    }
}
