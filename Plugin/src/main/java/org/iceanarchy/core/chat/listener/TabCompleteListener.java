package org.iceanarchy.core.chat.listener;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.TabCompleteEvent;
import org.iceanarchy.core.IceAnarchy;
import org.iceanarchy.core.command.CommandManager;

import java.util.List;

public class TabCompleteListener implements Listener {

    private final CommandManager manager = IceAnarchy.getInstance().getCommandManager();

    @EventHandler
    public void onTab(TabCompleteEvent event) {
        if (event.getSender().isOp()) return;
        if (!event.getBuffer().equalsIgnoreCase("/")) return;
        List<String> completions = event.getCompletions();
        completions.removeIf(completion -> !manager.isCommandValid(completion.replace("/", "")));
        event.setCompletions(completions);
    }
}
