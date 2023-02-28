package org.iceanarchy.core.general.tasks;

import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.iceanarchy.core.common.boiler.interfaces.ScheduledTask;
import org.iceanarchy.core.common.boiler.tools.NBTHelper;
import org.iceanarchy.core.file.FileManager;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.concurrent.ConcurrentHashMap;

public class PlayTimes implements Listener {

    private static final ConcurrentHashMap<Player, Integer> playerTimeMap = new ConcurrentHashMap<>();

    @ScheduledTask
    public static void increment() {
        if (playerTimeMap.size() < 1) return;
        playerTimeMap.forEach((player, time) -> playerTimeMap.replace(player, time + 1));
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onJoin(PlayerJoinEvent event) {
        playerTimeMap.putIfAbsent(event.getPlayer(), 0);
    }

    @SneakyThrows
    @EventHandler(priority = EventPriority.NORMAL)
    public void onQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        File playerFile = new File(FileManager.getPlaytimes(), player.getUniqueId().toString().concat(".nbt"));
        NBTTagCompound compound = new NBTTagCompound();
        if (!playerFile.exists()) {
            playerFile.createNewFile();
            compound.setInt("TimePlayed", playerTimeMap.get(player));
        } else compound.setInt("TimePlayed", NBTHelper.loadNBTFromFile(playerFile).getInt("TimePlayed") + playerTimeMap.get(player));
        FileOutputStream fos = new FileOutputStream(playerFile);
        DataOutputStream out = new DataOutputStream(fos);
        NBTHelper.writeNBT(compound, out);
        out.flush();
        out.close();
        fos.close();
    }
}
