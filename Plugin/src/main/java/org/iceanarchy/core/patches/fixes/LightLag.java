package org.iceanarchy.core.patches.fixes;

import org.bukkit.Bukkit;
import org.iceanarchy.core.IceAnarchy;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.logging.Level;

public class LightLag {
    public LightLag() {
        int time = 2000;
        String ver = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            Class<?> lightQueue = Class.forName("net.minecraft.server." + ver + ".PaperLightingQueue");
            Field maxTimeF = lightQueue.getDeclaredField("MAX_TIME");
            maxTimeF.setAccessible(true);
            modifiersField.setInt(maxTimeF, maxTimeF.getModifiers() & ~Modifier.FINAL);
            maxTimeF.set(null, (long) (time / 20 * 1.15));
            IceAnarchy.getInstance().getLogger().log(Level.INFO, "Initialized lighting queue");
        } catch (Throwable t) {
            IceAnarchy.getInstance().getLogger().severe("Failed to initialize lighting queue");
            t.printStackTrace();
        }
    }
}
