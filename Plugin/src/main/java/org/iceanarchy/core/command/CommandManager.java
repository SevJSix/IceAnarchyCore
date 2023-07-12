package org.iceanarchy.core.command;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.chat.commands.IgnoreCommand;
import org.iceanarchy.core.chat.commands.IgnoreListCommand;
import org.iceanarchy.core.chat.commands.ReplyCommand;
import org.iceanarchy.core.chat.commands.WhisperCommand;
import org.iceanarchy.core.command.commands.*;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;
import org.iceanarchy.core.common.boiler.interfaces.ITabExecutor;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class CommandManager extends Manager {

    @Getter
    private final HashMap<String, Object> commandMap = new HashMap<>();

    @Override
    public void init(JavaPlugin plugin) {
        registerCommand("playtime", new PlaytimesCommand(), true, "pt");
        registerCommand("help", new HelpCommand(this), false);
        registerCommand("joindate", new JoindateCommand(), true, "jd");
        registerCommand("stats", new WorldStatsCommand(), true, "worldstats");
        registerCommand("whisper", new WhisperCommand(), true, "w", "msg", "message", "tell", "t");
        registerCommand("reply", new ReplyCommand(), true, "r");
        registerCommand("verify", new VerifyPlayerCommand(), false);
        registerCommand("distance", new DistanceCommand(), true, "dist", "howfar");
        registerTabCompleter("alias", new AliasesCommand(), false);
        registerCommand("ignorelist", new IgnoreListCommand(), true, "ilist", "iglist", "ignored", "ignores");
        IgnoreCommand ignoreCommand = new IgnoreCommand();
        registerTabCompleter("ignore", ignoreCommand, false);
        registerTabCompleter("unignore", ignoreCommand, false);
    }

    @Override
    public void onShutdown(JavaPlugin plugin) {

    }

    public boolean isCommandValid(String commandEntered) {
        for (Map.Entry<String, Object> entry : commandMap.entrySet()) {
            Command command = Bukkit.getPluginCommand(entry.getKey());
            if (command.getName().equalsIgnoreCase(commandEntered) || command.getAliases().contains(commandEntered)) {
                return true;
            }
        }
        return false;
    }

    public String getDescription(Object cmd) {
        try {
            Method m = cmd.getClass().getDeclaredMethod("getDescription");
            m.setAccessible(true);
            return (String) m.invoke(cmd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

    public void registerCommand(String name, ICommandExecutor executor, boolean setAlias, String... aliases) {
        IceAnarchy.getInstance().registerCommand(name, executor::onCommand, setAlias, aliases);
        this.commandMap.put(name, executor);
    }

    public void registerTabCompleter(String name, ITabExecutor executor, boolean setAlias, String... aliases) {
        IceAnarchy.getInstance().registerCommand(name, executor::onCommand, setAlias, aliases);
        Bukkit.getServer().getPluginCommand(name).setTabCompleter(executor::onTabComplete);
        this.commandMap.put(name, executor);
    }
}
