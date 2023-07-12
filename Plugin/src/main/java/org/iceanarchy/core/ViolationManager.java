package org.iceanarchy.core;

import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.Field;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public abstract class ViolationManager {
    private final ConcurrentHashMap<Integer, Integer> map;
    private final int addAmount;
    private final int removeAmount;

    public ViolationManager(int addAmount) {
        this(addAmount, addAmount);
    }

    public ViolationManager(int addAmount, int removeAmount) {
        this.addAmount = addAmount;
        this.removeAmount = removeAmount;
        map = new ConcurrentHashMap<>();
        IceAnarchy.getInstance().registerViolationManager(this);
    }

    public void decrementAll() {
        map.forEach((key, val) -> {
            if (val <= removeAmount) {
                map.remove(key);
                return;
            }
            map.replace(key, val - removeAmount);
        });
    }

    public void increment(int uuid) {
        if (!map.containsKey(uuid)) {
            map.put(uuid, 0);
        } else map.replace(uuid, map.get(uuid) + addAmount);
    }

    public int getVLS(int id) {
        return map.getOrDefault(id, -1);
    }

    public void remove(int id) {
        map.remove(id);
    }
}
