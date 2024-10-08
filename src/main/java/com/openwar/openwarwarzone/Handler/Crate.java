package com.openwar.openwarwarzone.Handler;

import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarwarzone.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Crate implements Listener {

    private final PlayerDataManager pl;
    private final Map<String, Integer> crate;
    private final Map<Location, Long> crateCooldowns;
    private JavaPlugin main;
    private boolean canceled;
    public Crate(PlayerDataManager pl, Main main) {
        this.pl = pl;
        this.crate = new HashMap<>();
        this.crateCooldowns = new HashMap<>();
        this.main = main;
        loadCrates();
    }


    private void loadCrates() {
        crate.put("MWC_FRIDGE_CLOSED", 15);
        crate.put("MWC_FRIDGE_OPEN", 15);
        crate.put("MWC_FILINGCABINET_OPENED", 20);
        crate.put("MWC_FILINGCABINET", 20);
        crate.put("MWC_DUMPSTER", 10);
        crate.put("MWC_WOODEN_CRATE_OPENED", 27);
        crate.put("CFM_COUNTER_DRAWER", 18);
        crate.put("CFM_BEDSIDE_CABINET_OAK", 18);
        crate.put("CFM_DESK_CABINET_OAK", 18);
        crate.put("MWC_RUSSIAN_WEAPONS_CASE", 25);
        crate.put("MWC_WEAPONS_CASE", 35);
        crate.put("MWC_AMMO_BOX", 15);
        crate.put("MWC_WEAPONS_CASE_SMALL", 23);
        crate.put("MWC_WEAPONS_LOCKER", 30);
        crate.put("MWC_MEDICAL_CRATE", 18);
        crate.put("MWC_TRASH_BIN", 12);
        crate.put("MWC_VENDING_MACHINE", 18);
        crate.put("MWC_SUPPLY_DROP", 35);
        crate.put("MWC_SCP_LOCKER", 24);
        crate.put("MWC_LOCKER", 17);
        crate.put("MWC_ELECTRIC_BOX_OPENED", 10);
        crate.put("MWC_ELECTRIC_BOX", 10);
        crate.put("HBM_RADIOREC", 12);
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
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f» §7You need to wait §c"+timeLeft+" §7seconds"));
                return;
            }
        }
        int cooldown = crate.get(blockName);
        crateCooldowns.put(blockLocation, System.currentTimeMillis() + (cooldown * 60 * 1000L));
        triggerLootAnimation(player, block);
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8[§a✓§8]"));
    }


    public void triggerLootAnimation(Player player, Block block) {
        canceled = false;
        Location loc = player.getLocation();
        animationLoot(player, loc);
            if (!canceled) {
                Bukkit.getScheduler().runTaskLater(main, () -> {
                    newCrateLoot(player, block.getType().toString());
                }, 40L);
            }
    }

    private void newCrateLoot(Player player, String name) {
        switch (name) {
            case "MWC_FRIDGE_CLOSED":
                List<String> loot1 = new ArrayList<>();
                List<String> loot2 = new ArrayList<>();
                List<String> loot3 = new ArrayList<>();
                loot1.addAll(Arrays.asList("HARVESTCRAFT_GUMMYBEARSITEM", "HARVESTCRAFT_FRUITPUNCHITEM","HARVESTCRAFT_PERSIMMONYOGURTITEM"));
                loot2.addAll(Arrays.asList("HARVESTCRAFT_FOOTLONGITEM","HARVESTCRAFT_GLISTENINGSALADITEM","HARVESTCRAFT_PADTHAIITEM","HARVESTCRAFT_PORKRINDSITEM"));
                loot3.addAll(Arrays.asList("MWC_M17"));
                String l1 = getRandomItemWithChance(loot1, 0.01);
                String l2 = getRandomItemWithChance(loot2, 0.2);
                String l3 = getRandomItemWithChance(loot3, 0.9);
                giveItem(player, l1, l2, l3);
                break;
        }
    }
    private boolean hasEnoughSpace(Player player) {
        int count = 0;
        ItemStack[] contents = player.getInventory().getContents();
        for (int i = 0; i < contents.length - 5; i++) {
            if (contents[i] == null) {
                count++;
            }
        }
        return count > 0;
    }


    private void giveItem(Player player, String item1, String item2, String item3) {
        List<String> items = new ArrayList<>(Arrays.asList(item1, item2, item3));
        for (int i = 2; i >= 0; i--) {
            ItemStack item = getItemStackFromString(items.get(i));
            if (items.get(i).equals("AIR")) {
                continue;
            }
            if (hasEnoughSpace(player)) {
                player.getInventory().addItem(item);
                player.sendMessage("§b+ §7" + changeName(items.get(i)));
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.sendMessage("§c- §7" + changeName(items.get(i)) + " §8dropped on ground!");
            }
        }
    }


    public String changeName(String item) {
        if (item.startsWith("HARVESTCRAFT_") || item.startsWith("MWC_") || item.startsWith("HBM_")) {
            item = item.split("_")[1];
        }
        String[] parties = item.split("_");
        StringBuilder result = new StringBuilder();
        for (String part : parties) {
            result.append(part.substring(0, 1).toUpperCase())
                    .append(part.substring(1).toLowerCase())
                    .append(" ");
        }
        return result.toString().trim();
    }

    private String getRandomItemWithChance(List<String> loot, double airChance) {
        Random random = new Random();
        if (random.nextDouble() < airChance) {
            return "AIR";
        }
        return loot.get(random.nextInt(loot.size()));
    }


    private void animationLoot(Player player, Location loc1) {
        new BukkitRunnable() {
            int progress = 0;
            boolean canceled = false;

            @Override
            public void run() {
                if (canceled) {
                    this.cancel();
                    return;
                }

                StringBuilder progressBar = new StringBuilder("§8§l‖ ");
                for (int i = 0; i < 7; i++) {
                    if (i < progress) {
                        progressBar.append("§c▌");
                    } else {
                        progressBar.append("§7▌");
                    }
                }
                progressBar.append(" §8§l‖");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(progressBar.toString()));
                Location loc = player.getLocation();
                double distance = loc1.distance(loc);

                if (distance > 4D) {
                    canceled = true;
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7- §cCancelled"));
                } else {
                    progress++;
                    if (progress > 7) {
                        this.cancel();
                    }
                }
            }
        }.runTaskTimer(main, 0, 5);
    }

    public static ItemStack getItemStackFromString(String itemName) {
        Material material = Material.matchMaterial(itemName.toUpperCase());
        if (material == null) {
            return new ItemStack(Material.AIR);
        }

        return new ItemStack(material);
    }
}
