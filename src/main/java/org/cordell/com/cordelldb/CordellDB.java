package org.cordell.com.cordelldb;

import org.bukkit.plugin.java.JavaPlugin;

public final class CordellDB extends JavaPlugin {
    @Override
    public void onEnable() {
        System.out.println("CordellDB Plugin Enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("CordellDB Plugin Disabled");
    }
}
