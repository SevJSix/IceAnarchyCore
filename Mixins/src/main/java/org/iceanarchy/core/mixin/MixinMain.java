package org.iceanarchy.core.mixin;

import lombok.Getter;
import me.txmc.rtmixin.RtMixin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.mixin.mixins.MixinEndCrystal;
import org.iceanarchy.core.mixin.mixins.MixinMinecraftServer;
import org.iceanarchy.core.mixin.mixins.MixinWorld;

import java.lang.instrument.Instrumentation;

public class MixinMain {

    @Getter
    private JavaPlugin javaPlugin;

    @Getter
    private static MixinMain instance;

    public void init(JavaPlugin plugin) throws Throwable {
        instance = this;
        this.javaPlugin = plugin;
        plugin.getLogger().info(translate("&3Initializing mixins"));
        Instrumentation inst = RtMixin.attachAgent().orElseThrow(() -> new RuntimeException("Failed to attach agent"));
        plugin.getLogger().info(translate("&3Successfully attached agent and got instrumentation instance&r&a %s&r", inst.getClass().getName()));
        long start = System.currentTimeMillis();
        //Register your mixins here
        RtMixin.processMixins(MixinEndCrystal.class);
        RtMixin.processMixins(MixinMinecraftServer.class);
        RtMixin.processMixins(MixinWorld.class);
        //---
        plugin.getLogger().info(translate("&3Preformed all mixins in&r&a %dms&r", (System.currentTimeMillis() - start)));
    }

    private String translate(String message, Object... args) {
        return ChatColor.translateAlternateColorCodes('&', String.format(message, args));
    }
}
