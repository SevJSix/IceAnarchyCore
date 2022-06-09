package me.txmc.gradlepluginbase;

import org.bukkit.plugin.java.JavaPlugin;

public final class GradlePluginBase extends JavaPlugin {

    @Override
    public void onEnable() {
        System.out.println("This is a base plugin with gradle KTS as the build system");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
