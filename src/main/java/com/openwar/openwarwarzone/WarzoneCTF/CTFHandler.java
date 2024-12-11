package com.openwar.openwarwarzone.WarzoneCTF;

import com.openwar.openwarfaction.factions.Faction;
import com.openwar.openwarfaction.factions.FactionManager;
import com.openwar.openwarwarzone.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class CTFHandler implements Listener {

    private FactionManager fm;
    private Main main;
    private Economy economy;

    private String currentFaction = null;
    private Map<String, Integer> factionPresence = new HashMap<>();
    private int progress = 0;
    private BukkitTask captureTask = null;
    private long lastPlayerInZoneTime = System.currentTimeMillis();

    private static final long NEUTRALIZE_DELAY = 60_000; // 1 minute in milliseconds
    private static final int CAPTURE_PROGRESS_MAX = 100; // 100% capture progress

    public CTFHandler(FactionManager fm, Main main, Economy economy) {
        this.fm = fm;
        this.main = main;
        this.economy = economy;
    }

    @EventHandler
    public void onPlayerCapture(PlayerMoveEvent pl) {
        Player player = pl.getPlayer();
        if (player.getWorld().getName().equals("warzone") && isPlayerInRegion(
                player.getLocation().getX(),
                player.getLocation().getY(),
                player.getLocation().getZ(),
                2774D, 56D, 3085D, 2759D, 46D, 3115D)) {

            Faction faction = fm.getFactionByPlayer(player.getUniqueId());
            if (faction == null) return;
            String playerFaction = faction.getName();
            factionPresence.put(playerFaction, factionPresence.getOrDefault(playerFaction, 0) + 1);
            lastPlayerInZoneTime = System.currentTimeMillis();

            if (captureTask == null) {
                startCaptureTask();
            }
        } else {
            Faction faction = fm.getFactionByPlayer(player.getUniqueId());
            if (faction != null) {
                String playerFaction = faction.getName();
                factionPresence.put(playerFaction, factionPresence.getOrDefault(playerFaction, 0) - 1);
                if (factionPresence.get(playerFaction) <= 0) {
                    factionPresence.remove(playerFaction);
                }
            }
        }
    }

    private void startCaptureTask() {
        captureTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (!arePlayersInRegion()) {
                    resetCapture();
                    cancel();
                    System.out.println("RETURN1");
                    return;
                }

                if (System.currentTimeMillis() - lastPlayerInZoneTime > NEUTRALIZE_DELAY) {
                    neutralizeZone();
                    cancel();
                    System.out.println("RETURN2");
                    return;
                }

                Map.Entry<String, Integer> strongestFaction = getStrongestFaction();
                if (strongestFaction == null) {
                    return;
                }

                String leadingFaction = strongestFaction.getKey();
                int leadingCount = strongestFaction.getValue();
                int competingCount = factionPresence.values().stream().filter(count -> !count.equals(leadingCount)).mapToInt(Integer::intValue).sum();

                if (currentFaction == null || !currentFaction.equals(leadingFaction)) {
                    if (leadingCount > competingCount) {
                        progress++;
                        sendActionBarToPlayersInRegion("§8» §bProgress: §3" + progress + "§7%");

                        if (progress >= CAPTURE_PROGRESS_MAX) {
                            currentFaction = leadingFaction;
                            progress = 0;
                            Bukkit.broadcastMessage("§8» §4Warzone §8« §c" + currentFaction + " §7has captured the building!");
                            startFactionRewardTask();
                        }
                    } else {
                        progress = Math.max(0, progress - 1); // Reverse progress if contested
                    }
                }
            }
        }.runTaskTimer(main, 10, 10);
    }

    private boolean arePlayersInRegion() {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerInRegion(player.getLocation().getX(),
                    player.getLocation().getY(),
                    player.getLocation().getZ(),
                    2774D, 56D, 3085D, 2759D, 46D, 3115D)) {
                return true;
            }
        }
        return false;
    }

    private void resetCapture() {
        captureTask = null;
        progress = 0;
        factionPresence.clear();
    }

    private Map.Entry<String, Integer> getStrongestFaction() {
        return factionPresence.entrySet().stream()
                .max(Comparator.comparingInt(Map.Entry::getValue))
                .orElse(null);
    }

    private void neutralizeZone() {
        captureTask = null;
        currentFaction = null;
        progress = 0;
        factionPresence.clear();
        Bukkit.broadcastMessage("§8» §4Warzone §8« §7The building has been neutralized.");
    }

    private void startFactionRewardTask() {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (currentFaction == null) {
                    cancel();
                    return;
                }

                for (Player player : Bukkit.getOnlinePlayers()) {
                    Faction faction = fm.getFactionByPlayer(player.getUniqueId());
                    if (faction != null && faction.getName().equals(currentFaction)) {
                        economy.withdrawPlayer(player, 300);
                        player.sendMessage("§8» §f+§6300$");
                    }
                }
            }
        }.runTaskTimer(main, 1800, 1800);
    }

    private void sendActionBarToPlayersInRegion(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isPlayerInRegion(player.getLocation().getX(),
                    player.getLocation().getY(),
                    player.getLocation().getZ(),
                    2774D, 56D, 3085D, 2759D, 46D, 3115D)) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
            }
        }
    }

    public boolean isPlayerInRegion(double playerX, double playerY, double playerZ,
                                    double x1, double y1, double z1,
                                    double x2, double y2, double z2) {
        double minX = Math.min(x1, x2);
        double maxX = Math.max(x1, x2);
        double minY = Math.min(y1, y2);
        double maxY = Math.max(y1, y2);
        double minZ = Math.min(z1, z2);
        double maxZ = Math.max(z1, z2);
        return (playerX >= minX && playerX <= maxX) &&
                (playerY >= minY && playerY <= maxY) &&
                (playerZ >= minZ && playerZ <= maxZ);
    }
}