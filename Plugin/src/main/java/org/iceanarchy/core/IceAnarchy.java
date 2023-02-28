package org.iceanarchy.core;

import lombok.Getter;
import me.txmc.protocolapi.reflection.ClassProcessor;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.common.boiler.tools.NBTHelper;
import org.iceanarchy.core.file.FileManager;
import org.iceanarchy.core.general.GeneralManager;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.concurrent.TimeUnit;

@Getter
public final class IceAnarchy extends JavaPlugin {

    @Getter private static IceAnarchy instance;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadMixins();
        initializeManagers(new FileManager(), new GeneralManager());
        registerCommand("reloadconfig", (commandSender, command, s, strings) -> {
            super.reloadConfig();
            Common.sendMessage(commandSender, "&aReloaded core config.");
            return true;
        });
        registerCommand("playtime", ((commandSender, command, s, strings) -> {
            if (commandSender instanceof Player) {
                Player player = (Player) commandSender;
                File playerFile = new File(FileManager.getPlaytimes(), player.getUniqueId().toString().concat(".nbt"));
                if (!playerFile.exists()) {
                    Common.sendMessage(player, "&cLooks like this may be the first time joining. Re-log to see your playtime");
                    return true;
                }

                try {
                    NBTTagCompound compound = NBTHelper.loadNBTFromFile(playerFile);
                    Common.sendMessage(player, String.format("&3Your total playtime is &a%s", Common.getFormattedInterval(TimeUnit.SECONDS.toMillis(compound.getInt("TimePlayed")))));
                } catch (Throwable t) {
                    t.printStackTrace();
                }
            }
            return true;
        }));
    }

    private void initializeManagers(Manager... managers) {
        for (Manager manager : managers) {
            manager.init(this);
        }
    }

    private void loadMixins() {
        File mixinJar = new File(".", "mixins-temp.jar");
        if (mixinJar.exists()) mixinJar.delete();
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("mixins.dat");
            if (is == null) throw new RuntimeException("The plugin jar is missing the mixins");
            Files.copy(is, mixinJar.toPath());
            URLClassLoader ccl = new URLClassLoader(new URL[]{mixinJar.toURI().toURL()});
            Class<?> mixinMainClass = Class.forName(String.format("%s.mixin.MixinMain", getClass().getPackage().getName()), true, ccl);
            Object instance = mixinMainClass.newInstance();
            Method mainM = instance.getClass().getDeclaredMethod("init", JavaPlugin.class);
            mainM.invoke(instance, this);
        } catch (Throwable t) {
            getLogger().severe(String.format("Failed to load mixins due to %s. Please see the stacktrace below for more info", t.getClass().getName()));
            t.printStackTrace();
        } finally {
            if (mixinJar.exists()) mixinJar.delete();
        }
    }

    @Override
    public void onDisable() {
    }

    public void registerListener(Listener listener) {
        if (ClassProcessor.hasAnnotation(listener)) ClassProcessor.process(listener);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void registerCommand(String name, CommandExecutor command) {
        try {
            CraftServer cs = (CraftServer) Bukkit.getServer();
            if (ClassProcessor.hasAnnotation(command)) ClassProcessor.process(command);
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand pluginCommand = constructor.newInstance(name, this);
            pluginCommand.setExecutor(command);
            cs.getCommandMap().register(name, pluginCommand);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
