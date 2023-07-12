package org.iceanarchy.core.chat;

import lombok.Getter;
import lombok.SneakyThrows;
import net.minecraft.server.v1_12_R1.NBTBase;
import net.minecraft.server.v1_12_R1.NBTTagCompound;
import net.minecraft.server.v1_12_R1.NBTTagList;
import net.minecraft.server.v1_12_R1.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.iceanarchy.core.chat.listener.ChatListener;
import org.iceanarchy.core.chat.listener.CommandWhitelist;
import org.iceanarchy.core.chat.listener.TabCompleteListener;
import org.iceanarchy.core.common.Common;
import org.iceanarchy.core.common.boiler.Manager;
import org.iceanarchy.core.common.boiler.interfaces.IgnoreInfo;
import org.iceanarchy.core.common.boiler.tools.NBTHelper;

import java.io.File;
import java.util.*;

public class ChatManager extends Manager implements Listener, IgnoreInfo {

    @Getter
    private final HashMap<Player, Player> replyTargets = new HashMap<>();

    @Getter
    private File ignoreDataFolder;

    public void addIgnore(UUID player, UUID ignored) {
        List<UUID> ignores;
        if (!ignoreMap.containsKey(player)) {
            ignores = new ArrayList<>();
            ignores.add(ignored);
            ignoreMap.put(player, ignores);
            writeIgnore(player, ignored, false);
        } else if (!isIgnored(player, ignored)) {
            ignores = ignoreMap.get(player);
            ignores.add(ignored);
            ignoreMap.replace(player, ignores);
            writeIgnore(player, ignored, false);
        }
    }

    public void removeIgnore(UUID player, UUID ignored) {
        if (ignoreMap.containsKey(player) && ignoreMap.get(player).contains(ignored)) {
            List<UUID> ignores = ignoreMap.get(player);
            ignores.remove(ignored);
            ignoreMap.replace(player, ignores);
            writeIgnore(player, ignored, true);
        }
    }

    @SneakyThrows
    public void writeIgnore(UUID player, UUID ignored, boolean remove) {
        File file = new File(ignoreDataFolder, player.toString().concat(".nbt"));
        NBTTagCompound ignoreTag;
        NBTTagList ignores;
        if (!file.exists()) {
            ignoreTag = new NBTTagCompound();
            ignoreTag.setString("PlayerUUID", player.toString());
            ignores = new NBTTagList();
            ignores.add(new NBTTagString(ignored.toString()));
        } else {
            ignoreTag = NBTHelper.loadNBTFromFile(file);
            ignores = (NBTTagList) ignoreTag.map.get("Ignores");
            if (remove) {
                for (int i = 0; i < ignores.list.size(); i++) {
                    NBTTagString tag = (NBTTagString) ignores.list.get(i);
                    if (tag.c_().equalsIgnoreCase(ignored.toString())) {
                        ignores.remove(i);
                    }
                }
            } else {
                ignores.add(new NBTTagString(ignored.toString()));
            }
        }
        ignoreTag.set("Ignores", ignores);
        NBTHelper.writeAndFlush(ignoreTag, file);
    }

    public boolean isIgnored(UUID player, UUID ignored) {
        return ignoreMap.containsKey(player) && ignoreMap.get(player).contains(ignored);
    }

    public void sendWhisper(Player sender, Player recipient, String message) {
        if (sender == recipient) {
            Common.sendMessage(sender, "&cCannot send a whisper to yourself");
            return;
        }

        if (isIgnored(sender.getUniqueId(), recipient.getUniqueId())) {
            Common.sendMessage(sender, String.format("&cYou are ignoring %s, message failed.", recipient.getName()));
            return;
        }

        if (isIgnored(recipient.getUniqueId(), sender.getUniqueId())) {
            Common.sendMessage(sender, String.format("&c%s is ignoring you, message failed.", recipient.getName()));
            return;
        }

        Common.sendMessage(sender, String.format("&dTo %s: %s", recipient.getName(), message));
        Common.sendMessage(recipient, String.format("&dFrom %s: %s", sender.getName(), message));
        setReplyTarget(recipient, sender);
    }

    public Player getReplyTarget(Player player) {
        return replyTargets.getOrDefault(player, null);
    }

    public void setReplyTarget(Player player, Player target) {
        if (target == null) {
            replyTargets.remove(player);
            return;
        }
        if (!replyTargets.containsKey(player)) replyTargets.put(player, target);
        else replyTargets.put(player, target);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        replyTargets.remove(replyTargets.get(event.getPlayer()));
    }

    @Override
    public void init(JavaPlugin plugin) {
        this.ignoreDataFolder = new File(plugin.getDataFolder(), "PlayerIgnores");
        if (!ignoreDataFolder.exists()) ignoreDataFolder.mkdirs();
        Bukkit.getPluginManager().registerEvents(this, plugin);
        Bukkit.getPluginManager().registerEvents(new ChatListener(), plugin);
        Bukkit.getPluginManager().registerEvents(new CommandWhitelist(), plugin);
        Bukkit.getPluginManager().registerEvents(new TabCompleteListener(), plugin);
        for (File file : Objects.requireNonNull(ignoreDataFolder.listFiles())) {
            NBTTagCompound ignoreTag = NBTHelper.loadNBTFromFile(file);
            UUID player = UUID.fromString(ignoreTag.getString("PlayerUUID"));
            NBTTagList tagList = (NBTTagList) ignoreTag.map.get("Ignores");
            List<UUID> ignores = new ArrayList<>();
            for (NBTBase base : tagList.list) {
                NBTTagString tagString = (NBTTagString) base;
                ignores.add(UUID.fromString(tagString.c_()));
            }
            ignoreMap.put(player, ignores);
        }
    }

    @Override
    public void onShutdown(JavaPlugin plugin) {

    }
}
