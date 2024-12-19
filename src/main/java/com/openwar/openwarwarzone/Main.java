package com.openwar.openwarwarzone;

import com.openwar.openwarcore.Utils.LevelSaveAndLoadBDD;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarwarzone.Handler.AllowedCommands;
import com.openwar.openwarwarzone.Handler.LootCrate;
import com.openwar.openwarwarzone.WarzoneCTF.CTFHandler;
import com.openwar.openwarwarzone.WarzoneCTF.FactionCaptureManager;
import com.openwar.openwarwarzone.WarzoneCTF.Zone;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private LevelSaveAndLoadBDD pl;
    private FactionManager fm;
    private Economy economy = null;

    private boolean setupDepend() {
        RegisteredServiceProvider<LevelSaveAndLoadBDD> levelProvider = getServer().getServicesManager().getRegistration(LevelSaveAndLoadBDD.class);
        RegisteredServiceProvider<FactionManager> factionDataProvider = getServer().getServicesManager().getRegistration(FactionManager.class);
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (    levelProvider == null || factionDataProvider == null || rsp == null) {
            System.out.println("ERROR !!!!!!!!!!!!!!!!!!!!");
            return false;
        }
        pl = levelProvider.getProvider();
        fm = factionDataProvider.getProvider();
        economy = rsp.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        System.out.println("====================================");
        System.out.println(" ");
        System.out.println(" OpenWar - Warzone loading...");
        if (!setupDepend()) {return;}
        Zone zone = new Zone("Building");
        FactionCaptureManager fcm = new FactionCaptureManager(zone);
        getServer().getPluginManager().registerEvents(new CTFHandler(zone, fcm,fm, this), this);
        getServer().getPluginManager().registerEvents(new LootCrate(pl, this), this);
        getServer().getPluginManager().registerEvents(new AllowedCommands(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
