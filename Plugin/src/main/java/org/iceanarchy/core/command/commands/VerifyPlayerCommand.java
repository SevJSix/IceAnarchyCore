package org.iceanarchy.core.command.commands;

import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.VerifiedPlayer;
import org.iceanarchy.core.common.boiler.interfaces.ICommandExecutor;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class VerifyPlayerCommand implements ICommandExecutor {

    /**
     * @return the functionality of the command to help the user understand how to use the command
     */
    @Override
    public String getDescription() {
        return "Determines if a specified username is an authenticated minecraft account";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        new Thread(() -> {
            if (!(args.length > 0)) {
                Common.sendMessage(sender, String.format("&c/%s <username:uuid>", label));
                return;
            }

            Common.sendMessage(sender, "&7Checking mojang authentication servers...");
            VerifiedPlayer player = getVerifiedPlayer(args[0]);
            if (player == null) {
                Common.sendMessage(sender, String.format("&c%s is not authenticated by mojang", args[0]));
            } else {
                Common.sendMessage(sender, String.format("&a%s is an authenticated minecraft account!", player.getName()));
                BaseComponent uuidComponent = new TextComponent(Common.translateAltColorCodes("&bClick to copy UUID"));
                uuidComponent.setBold(true);
                uuidComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new BaseComponent[]{new TextComponent(Common.translateAltColorCodes(String.format("&7%s", player.getUniqueId())))}));
                uuidComponent.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, Common.translateAltColorCodes(player.getUniqueId().toString())));
                sender.sendMessage(uuidComponent);
            }
        }).start();
        return true;
    }

    private Map<String, UUID> verify(String in) {
        try {
            URL url = new URL("https://api.ashcon.app/mojang/v2/user/" + in);
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JSONObject UUIDObject = (JSONObject) JSONValue.parseWithException(reader);
            if (UUIDObject.containsKey("uuid") && UUIDObject.containsKey("username")) {
                return new HashMap<String, UUID>() {{
                    put(UUIDObject.get("username").toString(), UUID.fromString(UUIDObject.get("uuid").toString()));
                }};
            }
        } catch (Throwable t) {
            return null;
        }
        return null;
    }

    private VerifiedPlayer getVerifiedPlayer(String name) {
        Map<String, UUID> verified = verify(name);
        if (verified == null) return null;
        String verifiedName = new ArrayList<>(verified.keySet()).get(0);
        UUID verifiedUUID = new ArrayList<>(verified.values()).get(0);
        return new VerifiedPlayer(verifiedName, verifiedUUID);
    }
}
