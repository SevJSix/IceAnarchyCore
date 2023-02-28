package org.iceanarchy.core.common.boiler;

import lombok.Data;
import org.bukkit.plugin.java.JavaPlugin;

@Data
public abstract class Manager {

    public abstract void init(JavaPlugin plugin);

}