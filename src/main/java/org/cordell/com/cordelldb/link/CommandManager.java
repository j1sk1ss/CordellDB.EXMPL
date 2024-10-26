package org.cordell.com.cordelldb.link;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.cordell.com.cordelldb.manager.Manager;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;


public class CommandManager implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String[] args) {
        if (sender instanceof Player player) {
            if (label.equalsIgnoreCase("cdb_get")) {
                var manager = new Manager(args[0]);
                try {
                    player.sendMessage(manager.getString(args[1]));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            else if (label.equalsIgnoreCase("cdb_set")) {
                var manager = new Manager(args[0]);
                try {
                    manager.setString(args[1], args[2]);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            return true;
        }

        return false;
    }
}
