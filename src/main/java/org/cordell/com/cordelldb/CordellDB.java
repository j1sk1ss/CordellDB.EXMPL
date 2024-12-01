package org.cordell.com.cordelldb;

import org.bukkit.plugin.java.JavaPlugin;
import org.cordell.com.cordelldb.link.CommandManager;

import java.util.List;
import java.util.Objects;


public final class CordellDB extends JavaPlugin {
    @Override
    public void onEnable() {
        var command_manager = new CommandManager();
        for (var command : List.of("cdb_get", "cdb_set"))
            Objects.requireNonNull(getCommand(command)).setExecutor(command_manager);

        System.out.println("CordellDB 2.0 Plugin Enabled");
    }

    @Override
    public void onDisable() {
        System.out.println("CordellDB Plugin Disabled");
    }
}
