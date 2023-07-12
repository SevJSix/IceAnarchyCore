package org.iceanarchy.core;

import lombok.Getter;
import lombok.Setter;
import me.txmc.protocolapi.PacketEventDispatcher;
import me.txmc.protocolapi.PacketListener;
import me.txmc.protocolapi.reflection.ClassProcessor;
import net.minecraft.server.v1_12_R1.Packet;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.PluginCommand;
import org.bukkit.craftbukkit.v1_12_R1.CraftServer;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.chat.ChatManager;
import org.iceanarchy.core.command.CommandManager;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.file.FileManager;
import org.iceanarchy.core.general.GeneralManager;
import org.iceanarchy.core.patches.PatchManager;
import org.iceanarchy.core.pvp.PvPManager;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public final class IceAnarchy extends JavaPlugin {

    public static final long startTime = System.currentTimeMillis();
    @Getter
    private static IceAnarchy instance;
    @Getter
    @Setter
    private double fileSize;
    @Getter
    private ScheduledExecutorService violationService;
    @Getter
    private List<ViolationManager> violationManagers;
    @Getter
    private PacketEventDispatcher dispatcher;
    @Getter
    private ChatManager chatManager;
    @Getter
    private CommandManager commandManager;
    @Getter
    private GeneralManager generalManager;
    @Getter
    private List<Manager> managers;

    public static void run(Runnable runnable) {
        Bukkit.getScheduler().runTask(IceAnarchy.getInstance(), runnable);
    }

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        loadMixins();
        violationManagers = new ArrayList<>();
        violationService = Executors.newScheduledThreadPool(4);
        violationService.scheduleAtFixedRate(() -> violationManagers.forEach(ViolationManager::decrementAll), 0, 1, TimeUnit.SECONDS);
        dispatcher = new PacketEventDispatcher(this);
        chatManager = new ChatManager();
        commandManager = new CommandManager();
        generalManager = new GeneralManager();
        managers = new ArrayList<>();
        initializeManagers(new FileManager(), generalManager, chatManager, commandManager, new PatchManager(), new PvPManager());
        registerCommand("reloadconfig", (sender, command, label, args) -> {
            super.reloadConfig();
            Common.sendMessage(sender, "&aReloaded core config.");
            return true;
        }, true, "rlconfig", "rlc", "rlconf");
    }

    private void initializeManagers(Manager... managers) {
        for (Manager manager : managers) {
            this.managers.add(manager);
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

    public void registerViolationManager(ViolationManager violationManager) {
        if (violationManagers.contains(violationManager)) return;
        violationManagers.add(violationManager);
    }

    @Override
    public void onDisable() {
        managers.forEach(manager -> manager.onShutdown(this));
    }

    public void registerListener(Listener listener) {
        if (ClassProcessor.hasAnnotation(listener)) ClassProcessor.process(listener);
        getServer().getPluginManager().registerEvents(listener, this);
    }

    @SafeVarargs
    public final void registerListener(PacketListener listener, Class<? extends Packet<?>>... packets) {
        if (ClassProcessor.hasAnnotation(listener)) ClassProcessor.process(listener);
        dispatcher.register(listener, packets);
    }

    public void registerCommand(String name, CommandExecutor command, boolean setAlias, String... aliases) {
        try {
            CraftServer cs = (CraftServer) Bukkit.getServer();
            if (ClassProcessor.hasAnnotation(command)) ClassProcessor.process(command);
            Constructor<PluginCommand> constructor = PluginCommand.class.getDeclaredConstructor(String.class, Plugin.class);
            constructor.setAccessible(true);
            PluginCommand pluginCommand = constructor.newInstance(name, this);
            if (setAlias) pluginCommand.setAliases(Arrays.asList(aliases));
            pluginCommand.setExecutor(command);
            cs.getCommandMap().register(name, pluginCommand);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
