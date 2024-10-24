package com.openwar.openwarwarzone.Handler;


import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class AllowedCommands implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.isOp()) {return;}
        String command = event.getMessage().toLowerCase();
        String worldName = player.getWorld().getName();
        if (worldName.equalsIgnoreCase("warzone")) {
            if (command.startsWith("/f chat") || command.startsWith("/r") ||
                    command.startsWith("/msg") || command.startsWith("/money")) {
                return;
            } else {
                event.setCancelled(true);
                player.sendMessage("§8» §cYou can only use §7/f chat, /r, /msg, §cand §7/money §cin the warzone.");
            }
        }
    }
}