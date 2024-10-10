package com.openwar.openwarwarzone.Handler;

import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarlevels.level.PlayerLevel;
import com.openwar.openwarwarzone.Main;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class Crate implements Listener {

    private final PlayerDataManager pl;
    private final Map<String, Integer> crate;
    private final Map<Location, Long> crateCooldowns;
    private final Map<Location, UUID> crateLootPlayer;
    private JavaPlugin main;
    boolean canceled;
    boolean looting;
    List<String> loot1 = new ArrayList<>();
    List<String> loot2 = new ArrayList<>();
    List<String> loot3 = new ArrayList<>();
    String l1 = "";
    String l2 = "";
    String l3 = "";
    double exp;
    PlayerLevel xp;

    public Crate(PlayerDataManager pl, Main main) {
        this.pl = pl;
        this.crate = new HashMap<>();
        this.crateCooldowns = new HashMap<>();
        this.crateLootPlayer = new HashMap<>();
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
        if (!looting) {
            Block block = event.getClickedBlock();
            if (block == null) return;
            String blockName = block.getType().toString();
            if (!crate.containsKey(blockName)) return;
            if (crateLootPlayer.containsKey(block.getLocation())) return;
            Player player = event.getPlayer();
            Location blockLocation = block.getLocation();
            double distance = player.getLocation().distance(blockLocation);
            if (distance > 3D) { return;}
            if (crateCooldowns.containsKey(blockLocation)) {
                long timeLeft = (crateCooldowns.get(blockLocation) - System.currentTimeMillis()) / 1000;
                if (timeLeft > 0) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§f» §7You need to wait §c" + timeLeft + " §7seconds"));
                    return;
                }
            }
            crateLootPlayer.put(block.getLocation(), player.getUniqueId());
            looting = true;
            canceled = false;
            triggerLootAnimation(player, block);
        }
    }

    public void triggerLootAnimation(Player player, Block block) {
        Location loc = block.getLocation();
        animationLoot(player, loc);
        if (!canceled) {
            Bukkit.getScheduler().runTaskLater(main, () -> {
                newCrateLoot(player, block.getType().toString());
                if (!canceled) {
                    looting = false;
                    System.out.println("COOLDOWN SET");
                    int cooldown = crate.get(block.getType().toString());
                    crateCooldowns.put(block.getLocation(), System.currentTimeMillis() + (cooldown * 60 * 1000L));
                    crateLootPlayer.remove(block.getLocation());
                }
            }, 40L);
        }
    }

    private void newCrateLoot(Player player, String name) {
        loot1.clear();
        loot2.clear();
        loot3.clear();
        switch (name) {
            case "MWC_FRIDGE_CLOSED":
                loot1.addAll(Arrays.asList("HARVESTCRAFT_GUMMYBEARSITEM&3", "HARVESTCRAFT_FRUITPUNCHITEM&1", "HARVESTCRAFT_PERSIMMONYOGURTITEM&1"));
                loot2.addAll(Arrays.asList("HARVESTCRAFT_FOOTLONGITEM&1", "HARVESTCRAFT_GLISTENINGSALADITEM&1", "HARVESTCRAFT_PADTHAIITEM&1", "HARVESTCRAFT_PORKRINDSITEM&1"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.01);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.9);
                giveItem(name, player, l1, l2, l3);
                break;

            case "MWC_FRIDGE_OPEN":
                loot1.addAll(Arrays.asList("HARVESTCRAFT_GUMMYBEARSITEM&3", "HARVESTCRAFT_FRUITPUNCHITEM&1", "HARVESTCRAFT_PERSIMMONYOGURTITEM&1"));
                loot2.addAll(Arrays.asList("HARVESTCRAFT_FOOTLONGITEM&1", "HARVESTCRAFT_GLISTENINGSALADITEM&1", "HARVESTCRAFT_PADTHAIITEM&1", "HARVESTCRAFT_PORKRINDSITEM&1"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.01);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.9);

                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_FILINGCABINET":
                loot1.addAll(Arrays.asList("MONEY"));
                loot2.addAll(Arrays.asList("MWC_BULLET44&16", "MWC_BULLET45ACP&20", "MWC_BULLET9X18MM&17", "MWC_BULLET9X19MM&18"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.3);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.8);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_FILINGCABINET_OPENED":
                loot1.addAll(Arrays.asList("MONEY"));
                loot2.addAll(Arrays.asList("MWC_BULLET44&16", "MWC_BULLET45ACP&20", "MWC_BULLET9X18MM&17", "MWC_BULLET9X19MM&18"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.3);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.8);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_DUMPSTER":
                loot1.addAll(Arrays.asList("MINECRAFT_ROTTEN_FLESH&2","MINECRAFT_POISONOUS_POTATO&2", "HARVESTCRAFT_SWEETPICKLEITEM&3"));
                loot2.addAll(Arrays.asList("HBM_CANNED_TOMATO&1","HARVESTCRAFT_ZOMBIEJERKYITEM&1"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&2", "HBM_WIRE_COPPER&4"));
                l1 = getRandomItemWithChance(loot1, 0.01);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.8);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_WOODEN_CRATE_OPENED":
                loot1.addAll(Arrays.asList("MWC_SV98MAG&2","MWC_SOCOM_MAG&2","MWC_M38MAG&2","MWC_M4A1MAG&2","MWC_AK74MAG&2","MWC_AK47MAG&2","MWC_AK47PMAGTAN&2","MWC_AK15MAG_2&2"));
                loot2.addAll(Arrays.asList("MWC_AK74&1","MWC_AK47&1","MWC_MAC10&1"));
                loot3.addAll(Arrays.asList("MWC_MAC10MAG&3"));
                l1 = getRandomItemWithChance(loot1, 0.3);
                l2 = getRandomItemWithChance(loot2, 0.6);
                l3 = getRandomItemWithChance(loot3, 0.1);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_WEAPONS_CASE":

                loot1.addAll(Arrays.asList("MWC_SV98MAG&2","MWC_SOCOM_MAG&2","MWC_M38MAG&2","MWC_M4A1MAG&2"));
                loot2.addAll(Arrays.asList("MWC_M38_DMR&1","MWC_M4A1&1"));
                loot3.addAll(Arrays.asList("MWC_SV98&1"));
                l1 = getRandomItemWithChance(loot1, 0.3);
                l2 = getRandomItemWithChance(loot2, 0.6);
                l3 = getRandomItemWithChance(loot3, 0.9);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_AMMO_BOX":
                loot1.addAll(Arrays.asList("MWC_BULLET9X19MM&40","MWC_BULLET9X18MM&38","MWC_BULLET45ACP&35"));
                loot2.addAll(Arrays.asList("MWC_BULLET762X39&40","MWC_BULLET762X54&38","MWC_BULLET556X45&35","MWC_BULLET545X39&32"));
                loot3.addAll(Arrays.asList("MWC_SV98MAG&2","MWC_SOCOM_MAG&2","MWC_M38MAG&2","MWC_M4A1MAG&2","MWC_AK74MAG&2","MWC_AK47MAG&2","MWC_AK47PMAGTAN&2","MWC_AK15MAG_2&2"));
                l1 = getRandomItemWithChance(loot1, 0.1);
                l2 = getRandomItemWithChance(loot2, 0.3);
                l3 = getRandomItemWithChance(loot3, 0.7);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_VENDING_MACHINE":
                System.out.println("Experience get= " + exp);
                loot1.addAll(Arrays.asList("HARVESTCRAFT_SNICKERSBARITEM&2","HARVESTCRAFT_ENERGYDRINKITEM&2", "HARVESTCRAFT_CHOCOLATEMILKITEM&3"));
                loot2.addAll(Arrays.asList("HBM_CANNED_TOMATO&1","HARVESTCRAFT_CRISPYRICEPUFFBARSITEM&1"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&2", "HARVESTCRAFT_BBQPOTATOCHIPSITEM&4"));
                l1 = getRandomItemWithChance(loot1, 0.01);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.2);

                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_TRASH_BIN":

                loot1.addAll(Arrays.asList("MINECRAFT_ROTTEN_FLESH&2","MINECRAFT_POISONOUS_POTATO&2", "HARVESTCRAFT_SWEETPICKLEITEM&3"));
                loot2.addAll(Arrays.asList("HBM_CANNED_TOMATO&1","HARVESTCRAFT_ZOMBIEJERKYITEM&1"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&2", "HBM_WIRE_COPPER&4"));
                l1 = getRandomItemWithChance(loot1, 0.01);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.8);
                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_WEAPONS_CASE_SMALL":
                loot1.addAll(Arrays.asList("MWC_APSMAG_2&2","MWC_MAKAROVMAG&2", "MWC_GLOCKMAG13&1"));
                loot2.addAll(Arrays.asList("MWC_MAKAROV_PM&1","MWC_APS&1","MWC_GLOCK_18C&1"));
                loot3.addAll(Arrays.asList("MWC_SILENCER9MM&1"));
                l1 = getRandomItemWithChance(loot1, 0.01);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.9);

                giveItem(name, player, l1, l2, l3);
                break;
            case "CFM_COUNTER_DRAWER":

                loot1.addAll(Arrays.asList("MONEY"));
                loot2.addAll(Arrays.asList("MWC_BULLET44&16", "MWC_BULLET45ACP&20", "MWC_BULLET9X18MM&17", "MWC_BULLET9X19MM&18"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.4);
                l2 = getRandomItemWithChance(loot2, 0.2);
                l3 = getRandomItemWithChance(loot3, 0.8);

                giveItem(name, player, l1, l2, l3);
                break;
            case "CFM_BEDSIDE_CABINET_OAK":
                loot1.addAll(Arrays.asList("MONEY"));
                loot2.addAll(Arrays.asList("MWC_BULLET44&16", "MWC_BULLET45ACP&20", "MWC_BULLET9X18MM&17", "MWC_BULLET9X19MM&18"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.3);
                l2 = getRandomItemWithChance(loot2, 0.3);
                l3 = getRandomItemWithChance(loot3, 0.8);

                giveItem(name, player, l1, l2, l3);
                break;
            case "CFM_DESK_CABINET_OAK":
                loot1.addAll(Arrays.asList("MONEY"));
                loot2.addAll(Arrays.asList("MWC_BULLET44&16", "MWC_BULLET45ACP&20", "MWC_BULLET9X18MM&17", "MWC_BULLET9X19MM&18"));
                loot3.addAll(Arrays.asList("HARVESTCRAFT_ENERGYDRINKITEM&1","MWC_M17&1"));

                l1 = getRandomItemWithChance(loot1, 0.5);
                l2 = getRandomItemWithChance(loot2, 0.6);
                l3 = getRandomItemWithChance(loot3, 0.8);

                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_RUSSIAN_WEAPONS_CASE":
                loot1.addAll(Arrays.asList("MWC_AK74MAG&2","MWC_AK47MAG&2","MWC_AK47PMAGTAN&2","MWC_MAC10MAG&3"));
                loot2.addAll(Arrays.asList("MWC_AK74&1","MWC_AK47&1","MWC_MAC10&1"));
                loot3.addAll(Arrays.asList("MWC_AK15MAG_2&2"));

                l1 = getRandomItemWithChance(loot1, 0.2);
                l2 = getRandomItemWithChance(loot2, 0.5);
                l3 = getRandomItemWithChance(loot3, 0.8);

                giveItem(name, player, l1, l2, l3);
                break;
            case "MWC_SUPPLY_DROP":
                loot1.addAll(Arrays.asList("MWC_SV98&1","MWC_M38_DMR&1","MWC_M4A1&1","MWC_AK74&1","MWC_AK47&1"));
                loot2.addAll(Arrays.asList("MWC_ACOG&1", "MWC_MICROREFLEX&1","MWC_SPECTER&1","MWC_HOLOGRAPHIC2&1"));
                loot3.addAll(Arrays.asList("MCHELI_FIM92&1", "MCHELI_FGM148&1"));
                l1 = getRandomItemWithChance(loot1, 0.2);
                l2 = getRandomItemWithChance(loot2, 0.5);
                l3 = getRandomItemWithChance(loot3, 0.8);

                giveItem(name, player, l1, l2, l3);
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


    private void giveItem(String namee, Player player, String item1, String item2, String item3) {
        if (canceled) {
            return;
        }
        System.out.println("CRATE: " +namee);
        exp = crate.get(namee)*2;
        xp = pl.loadPlayerData(player.getUniqueId(), null);
        xp.setExperience(xp.getExperience() + exp, player);
        player.sendMessage("§b+ §e" + exp + " §6XP");
        pl.savePlayerData(player.getUniqueId(), xp);
        System.out.println("Experience get= " + exp);
        List<String> items = new ArrayList<>(Arrays.asList(item1, item2, item3));
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§8[§a✓§8]"));
        System.out.println(items+" LISTES DES ITEMS A GIVE");
        Random random = new Random();
        for (int i = 2; i >= 0; i--) {
            String name =  items.get(i);
            ItemStack item = getItemStackFromString(name.split("&") [0]);
            if (name.equals("AIR")) {
                continue;
            }
            if (items.get(i).equals("MONEY")) {

                int choice = random.nextInt(3);
                int amoney = 100;
                if (choice == 0) {
                    amoney = 50;
                } else if (choice == 1) {
                    amoney = 150;
                } else if (choice == 2) {
                    amoney = 200;
                }
                ItemStack money = new ItemStack(Material.matchMaterial("openwarprops:money"));
                ItemMeta meta = money.getItemMeta();
                meta.setLore(Arrays.asList("§7$"+amoney));
                meta.setDisplayName("§6Money");
                money.setItemMeta(meta);
                player.getInventory().addItem(money);
                player.sendMessage("§b+ §6$"+amoney);
                continue;
            }

            int amount = Integer.parseInt((name.split("&") [1]));
            System.out.println("Amount : "+amount);
            System.out.println("Item: "+item.getType());
            if (amount == 0) {
                amount+=1;
            }
            if (hasEnoughSpace(player)) {
                if (amount == 1) {
                    player.sendMessage("§b+ §81 §7" + changeName(items.get(i)));
                    player.getInventory().addItem(item);
                    continue;
                }
                System.out.println(amount+ "AMOUNT");
                int randomItem = random.nextInt(amount + 1);
                if (randomItem == 0) {
                    randomItem+=1;
                }
                System.out.println(randomItem+ " RAND AMOUNT");
                item.setAmount(randomItem);
                player.getInventory().addItem(item);
                player.sendMessage("§b+ §8" + randomItem + " §7" + changeName(items.get(i)));
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), item);
                player.sendMessage("§c- §7" + changeName(items.get(i)) + " §8dropped on ground !");
            }
        }
        items.addAll(Arrays.asList("AIR", "AIR","AIR"));
        looting = false;
        crateLootPlayer.remove(player.getUniqueId());
    }


    public String changeName(String item) {
        if (item.startsWith("HARVESTCRAFT_") || item.startsWith("MWC_") || item.startsWith("HBM_")|| item.startsWith("MINECRAFT_") || item.startsWith("CFM_") ||item.startsWith("MCHELI_")) {
            item = item.split("_")[1];
            item = item.split("&") [0];
            if (item.endsWith("ITEM")) {
                item = item.split("ITEM") [0];
            }
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
        String selectedItem = loot.get(random.nextInt(loot.size()));
        return selectedItem;
    }


    private void animationLoot(Player player, Location loc1) {
        new BukkitRunnable() {
            int progress = 0;

            @Override
            public void run() {
                if (canceled) {
                    this.cancel();
                    return;
                }

                StringBuilder progressBar = new StringBuilder("§8§l‖ ");
                for (int i = 0; i < 7; i++) {
                    if (i < progress) {
                        player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1.0F, 1.0F);
                        progressBar.append("§c▌");
                    } else {
                        progressBar.append("§7▌");
                    }
                }
                progressBar.append(" §8§l‖");
                player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(progressBar.toString()));
                Location loc = player.getLocation();
                double distance = loc1.distance(loc);

                if (distance > 3D) {
                    canceled = true;
                    looting = false;
                    crateLootPlayer.remove(loc1);
                    System.out.println("CANCELED TRUE");
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§7- §cCancelled"));
                    this.cancel();
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
