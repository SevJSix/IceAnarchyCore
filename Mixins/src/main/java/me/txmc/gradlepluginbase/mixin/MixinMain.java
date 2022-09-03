package me.txmc.gradlepluginbase.mixin;

import me.txmc.rtmixin.RtMixin;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.instrument.Instrumentation;

public class MixinMain {

    public void init(JavaPlugin plugin) throws Throwable {
        plugin.getLogger().info(translate("&3Initializing mixins"));
        Instrumentation inst = RtMixin.attachAgent().orElseThrow(() -> new RuntimeException("Failed to attach agent"));
        plugin.getLogger().info(translate("&3Successfully attached agent and got instrumentation instance&r&a %s&r", inst.getClass().getName()));
        long start = System.currentTimeMillis();
        //Register your mixins here

//        RtMixin.processMixins(MixinNetworkManager.class);
        //---
        plugin.getLogger().info(translate("&3Preformed all mixins in&r&a %dms&r", (System.currentTimeMillis() - start)));
    }

    private String translate(String message, Object... args) {
        return ChatColor.translateAlternateColorCodes('&', String.format(message, args));
    }
}
