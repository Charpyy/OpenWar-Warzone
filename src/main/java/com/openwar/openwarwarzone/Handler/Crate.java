package com.openwar.openwarwarzone.Handler;

import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarwarzone.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Crate implements Listener {

    private final PlayerDataManager pl;
    private final Map<String, Integer> crate;
    private final Map<Location, Long> crateCooldowns;
    private JavaPlugin main;
    public Crate(PlayerDataManager pl, Main main) {
        this.pl = pl;
        this.crate = new HashMap<>();
        this.crateCooldowns = new HashMap<>();
        this.main = main;
        loadCrates();
    }


    private void loadCrates() {
        crate.put("MWC:FRIDGE_CLOSED", 15);
        crate.put("MWC:FRIDGE_OPEN", 15);
        crate.put("MWC FILINGCABINET OPENED", 20);
        crate.put("MWC FILINGCABINET", 20);
        crate.put("MWC DUMPSTER", 10);
        crate.put("MWC WOODEN CRATE OPENED", 27);
        crate.put("CFM COUNTER DRAWER", 18);
        crate.put("CFM BEDSIDE CABINET OAK", 18);
        crate.put("CFM DESK CABINET OAK", 18);
        crate.put("MWC RUSSIAN WEAPONS CASE", 25);
        crate.put("MWC WEAPONS CASE", 35);
        crate.put("MWC AMMO BOX", 15);
        crate.put("MWC WEAPONS CASE SMALL", 23);
        crate.put("MWC WEAPONS LOCKER", 30);
        crate.put("MWC MEDICAL CRATE", 18);
        crate.put("MWC TRASH BIN", 12);
        crate.put("MWC VENDING MACHINE", 18);
        crate.put("MWC SUPPLY DROP", 35);
        crate.put("MWC SCP LOCKER", 24);
        crate.put("MWC LOCKER", 17);
        crate.put("MWC ELECTRIC BOX OPENED", 10);
        crate.put("MWC ELECTRIC BOX", 10);
        crate.put("HBM RADIOREC", 12);
    }

    @EventHandler
    public void onLoot(PlayerInteractEvent event) {
        Block block = event.getClickedBlock();
        if (block == null) return;
        String blockName = block.getType().toString();
        if (!crate.containsKey(blockName)) return;
        Player player = event.getPlayer();
        Location blockLocation = block.getLocation();
        if (crateCooldowns.containsKey(blockLocation)) {
            long timeLeft = (crateCooldowns.get(blockLocation) - System.currentTimeMillis()) / 1000;
            if (timeLeft > 0) {
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8You need to wait §c"+timeLeft+" §8secondes"));
                return;
            }
        }
        int cooldown = crate.get(blockName);
        crateCooldowns.put(blockLocation, System.currentTimeMillis() + (cooldown * 60 * 1000L));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8[§a✓§8]"));
        triggerLootAnimation(player, block);
    }


    public void triggerLootAnimation(Player player, Block block) {
        animationLoot(player);
        Bukkit.getScheduler().runTaskLater(main, () -> {
            newCrateLoot(player, block.getType().toString());
        }, 70L);
    }

    private void newCrateLoot(Player player, String name) {
        switch (name) {
            case "MWC:FRIDGE_CLOSED":
                List<String> loot = new ArrayList<>();
                loot.add(" ");
        }

    }
    private void animationLoot(Player player) {
        new BukkitRunnable() {
            int progress = 0;
            @Override
            public void run() {
                StringBuilder progressBar = new StringBuilder();
                for (int i = 0; i < 7; i++) {
                    if (i < progress) {
                        progressBar.append("§c█");
                    } else {
                        progressBar.append("§7█");
                    }
                }
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(progressBar.toString()));
                progress++;
                if (progress > 7) {
                    this.cancel();
                }
            }
        }.runTaskTimer(main, 0, 10);
    }

}
