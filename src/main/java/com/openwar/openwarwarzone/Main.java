package com.openwar.openwarwarzone;

import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarwarzone.Handler.AllowedCommands;
import com.openwar.openwarwarzone.Handler.LootCrate;
import com.openwar.openwarwarzone.WarzoneCTF.CTFHandler;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private PlayerDataManager pl;
    private FactionManager fm;
    private Economy economy = null;

    private boolean setupDepend() {
        RegisteredServiceProvider<PlayerDataManager> levelProvider = getServer().getServicesManager().getRegistration(PlayerDataManager.class);
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
        getServer().getPluginManager().registerEvents(new CTFHandler(fm, this, economy), this);
        getServer().getPluginManager().registerEvents(new LootCrate(pl, this), this);
        getServer().getPluginManager().registerEvents(new AllowedCommands(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
