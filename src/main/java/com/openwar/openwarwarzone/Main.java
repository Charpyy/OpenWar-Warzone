package com.openwar.openwarwarzone;

import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarwarzone.Handler.Crate;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Main extends JavaPlugin {
    private PlayerDataManager pl;
    private FactionManager fm;
    private boolean setupDepend() {
        RegisteredServiceProvider<PlayerDataManager> levelProvider = getServer().getServicesManager().getRegistration(PlayerDataManager.class);
        RegisteredServiceProvider<FactionManager> factionDataProvider = getServer().getServicesManager().getRegistration(FactionManager.class);
        if (    levelProvider == null || factionDataProvider == null) {
            System.out.println("ERROR !!!!!!!!!!!!!!!!!!!!");
            return false;
        }
        pl = levelProvider.getProvider();
        fm = factionDataProvider.getProvider();
        return true;
    }

    @Override
    public void onEnable() {
        System.out.println("====================================");
        System.out.println(" ");
        System.out.println(" OpenWar - Warzone loading...");
        if (!setupDepend()) {return;}
        getServer().getPluginManager().registerEvents(new Crate(pl, this), this);

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
