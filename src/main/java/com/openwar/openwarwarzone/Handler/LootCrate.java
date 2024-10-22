package com.openwar.openwarwarzone.Handler;

import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarlevels.level.PlayerLevel;
import com.openwar.openwarwarzone.Main;
import com.openwar.openwarwarzone.Utils.Tuple;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Array;
import java.util.*;
import java.util.logging.Logger;

public class LootCrate {
    private final PlayerDataManager pl;
    private final Map<Location, Long> crateCooldowns;
    private final Map<Location, UUID> crateLootPlayer;
    private final Map<Location, Inventory> crateInventory;
    private JavaPlugin main;

    double exp;
    PlayerLevel xp;

    List<Tuple<String, Integer, Integer>> crates = new ArrayList<>();

    public LootCrate(PlayerDataManager pl, Main main) {
        this.pl = pl;
        this.crateCooldowns = new HashMap<>();
        this.crateLootPlayer = new HashMap<>();
        this.crateInventory = new HashMap<>();
        this.main = main;
        loadCrates();
    }

    private void loadCrates() {
        crates.add(new Tuple<>("MWC_FRIDGE_CLOSED", 15, 6));
        crates.add(new Tuple<>("MWC_FRIDGE_OPEN", 15, 6));
        crates.add(new Tuple<>("MWC_FILINGCABINET_OPENED", 20, 2));
        crates.add(new Tuple<>("MWC_FILINGCABINET", 20, 2));
        crates.add(new Tuple<>("MWC_DUMPSTER", 10, 6));
        crates.add(new Tuple<>("MWC_WOODEN_CRATE_OPENED", 27, 18));
        crates.add(new Tuple<>("CFM_COUNTER_DRAWER", 18, 2));
        crates.add(new Tuple<>("CFM_BEDSIDE_CABINET_OAK", 18, 2));
        crates.add(new Tuple<>("CFM_DESK_CABINET_OAK", 18, 2));
        crates.add(new Tuple<>("MWC_RUSSIAN_WEAPONS_CASE", 25, 18));
        crates.add(new Tuple<>("MWC_WEAPONS_CASE", 35, 18));
        crates.add(new Tuple<>("MWC_AMMO_BOX", 15, 6));
        crates.add(new Tuple<>("MWC_WEAPONS_CASE_SMALL", 23, 6));
        crates.add(new Tuple<>("MWC_WEAPONS_LOCKER", 30, 18));
        crates.add(new Tuple<>("MWC_MEDICAL_CRATE", 18, 6));
        crates.add(new Tuple<>("MWC_TRASH_BIN", 12, 4));
        crates.add(new Tuple<>("MWC_VENDING_MACHINE", 18, 2));
        crates.add(new Tuple<>("MWC_SUPPLY_DROP", 35, 27));
        crates.add(new Tuple<>("MWC_SCP_LOCKER", 24, 3));
        crates.add(new Tuple<>("MWC_LOCKER", 17, 3));
        crates.add(new Tuple<>("MWC_ELECTRIC_BOX_OPENED", 10, 4));
        crates.add(new Tuple<>("MWC_ELECTRIC_BOX", 10, 4));
        crates.add(new Tuple<>("HBM_RADIOREC", 12, 2));
    }




    @EventHandler
    public void onLoot(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().getName().equals("warzone")) {
            Block block = event.getClickedBlock();
            if (block != null && crates.contains(block.getType().name())) {
                Location crateLoc = block.getLocation();
                Tuple<String, Integer, Integer> TriplesCouilles = null;
                for (int i = 0; i < crates.size(); i++) {
                    TriplesCouilles = crates.get(i);
                }
                if (TriplesCouilles != null) {
                    if (crateInventory.containsKey(crateLoc)) {
                        Inventory inv = crateInventory.get(crateLoc);
                        event.getPlayer().openInventory(inv);
                    }
                    else {
                        Map<ItemStack, Integer> loot = createLoot(TriplesCouilles);
                        crateInventory.put(crateLoc, createGUI(loot, TriplesCouilles));
                    }
                }
            }
        }
    }

    private Map<ItemStack, Integer> createLoot(Tuple<String, Integer, Integer> tuple) {
        String type = tuple.getFirst();
        List<Tuple<String, Integer, Integer>> items = new ArrayList<>();
        Map<ItemStack, Integer> finalItem = new HashMap<>();
        switch (type) {
            case "MWC_FRIDGE_CLOSED":
                items.add(new Tuple<>("HARVESTCRAFT_GUMMYBEARSITEM", 3, 60));
                items.add(new Tuple<>("HARVESTCRAFT_FRUITPUNCHITEM", 1, 50));
                items.add(new Tuple<>("HARVESTCRAFT_PERSIMMONYOGURTITEM", 1, 60));
                items.add(new Tuple<>("HARVESTCRAFT_FOOTLONGITEM", 1, 40));
                items.add(new Tuple<>("HARVESTCRAFT_GLISTENINGSALADITEM", 1, 50));
                items.add(new Tuple<>("HARVESTCRAFT_PADTHAIITEM", 1, 50));
                items.add(new Tuple<>("HARVESTCRAFT_PORKRINDSITEM", 1, 40));
                items.add(new Tuple<>("HARVESTCRAFT_GLISTENINGSALADITEM", 1, 50));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 1, 30));
                items.add(new Tuple<>("MWC_M17", 1, 5));
                return finalItem = generateLoot(items);
            case "":

                return finalItem = generateLoot(items);
        }
        return null;
    }

    private Map<ItemStack, Integer> generateLoot(List<Tuple<String, Integer, Integer>> items) {
        Random rand = new Random();
        Map<ItemStack, Integer> finalItem = new HashMap<>();
        boolean isSelected = false;
        //TODO YAURA QUUN SEUL ITEM IL FAUT FAIRE UNE BOUCLE FOR AVEC LE NOMBRE QUON VEUT  un randint en fonction du reste de la division du nb slot
        while (!isSelected) {
            int i = rand.nextInt(items.size());
            Tuple<String, Integer, Integer> item = items.get(i);
            isSelected = rand.nextInt(100) < item.getThird();
            if (isSelected) {
                if (item.getFirst().startsWith("MONEY")) {
                    finalItem.put(genMoney(), item.getSecond());
                } else {
                    finalItem.put(getItemStackFromString(item.getFirst()), item.getSecond());
                }
                return finalItem;
            }
        }
        return null;
    }

    private Inventory createGUI(Map<ItemStack, Integer> loot, Tuple<String, Integer, Integer> crate) {

    }

    private String getDisName(String type) {
        if (type.startsWith("MWC")) {
            String[] names = type.toLowerCase().split("MWC_");
            String name = names[1].replace("_", " ");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
        if (type.startsWith("HBM")) {
            String[] names = type.toLowerCase().split("HBM_");
            String name = names[1].replace("_", " ");
            name = name.substring(0, 1).toUpperCase() + name.substring(1);
            return name;
        }
        System.out.println("Error getDisName for type : " +type);
        return null;
    }
    public static ItemStack getItemStackFromString(String itemName) {
        Material material = Material.matchMaterial(itemName.toUpperCase());
        if (material == null) {
            System.out.println("Error get Item Stack from sting with "+itemName);
            return new ItemStack(Material.AIR);
        }
        return new ItemStack(material);
    }

    private ItemStack genMoney() {
        Random random = new Random();
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
        return money;
    }
}
