package com.openwar.openwarwarzone.Handler;


import com.openwar.openwarlevels.level.PlayerDataManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class Crate implements Listener {

    private PlayerDataManager pl;

    public Crate(PlayerDataManager pl) {
        this.pl = pl;
    }

    @EventHandler
    public void onLoot(PlayerInteractEvent event) {

    }
}
