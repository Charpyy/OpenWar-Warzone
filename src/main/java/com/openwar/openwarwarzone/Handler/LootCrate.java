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
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class LootCrate implements Listener{
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
        crates.add(new Tuple<>("MWC_FRIDGE_CLOSED", 15, 9));
        crates.add(new Tuple<>("MWC_FRIDGE_OPEN", 15, 9));
        crates.add(new Tuple<>("MWC_FILINGCABINET_OPENED", 20, 9));
        crates.add(new Tuple<>("MWC_FILINGCABINET", 20, 9));
        crates.add(new Tuple<>("MWC_DUMPSTER", 10, 9));
        crates.add(new Tuple<>("MWC_WOODEN_CRATE_OPENED", 27, 18));
        crates.add(new Tuple<>("CFM_COUNTER_DRAWER", 18, 9));
        crates.add(new Tuple<>("CFM_BEDSIDE_CABINET_OAK", 18, 9));
        crates.add(new Tuple<>("CFM_DESK_CABINET_OAK", 18, 9));
        crates.add(new Tuple<>("MWC_RUSSIAN_WEAPONS_CASE", 25, 18));
        crates.add(new Tuple<>("MWC_WEAPONS_CASE", 35, 18));
        crates.add(new Tuple<>("MWC_AMMO_BOX", 15, 9));
        crates.add(new Tuple<>("MWC_WEAPONS_CASE_SMALL", 23, 9));
        crates.add(new Tuple<>("MWC_WEAPONS_LOCKER", 30, 18));
        crates.add(new Tuple<>("MWC_MEDICAL_CRATE", 18, 9));
        crates.add(new Tuple<>("MWC_TRASH_BIN", 12, 9));
        crates.add(new Tuple<>("MWC_VENDING_MACHINE", 18, 9));
        crates.add(new Tuple<>("MWC_SUPPLY_DROP", 35, 27));
        crates.add(new Tuple<>("MWC_SCP_LOCKER", 24, 9));
        crates.add(new Tuple<>("MWC_LOCKER", 17, 9));
        crates.add(new Tuple<>("MWC_ELECTRIC_BOX_OPENED", 10, 9));
        crates.add(new Tuple<>("MWC_ELECTRIC_BOX", 10, 9));
        crates.add(new Tuple<>("HBM_RADIOREC", 12, 9));
    }




    @EventHandler
    public void onLoot(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().getName().equals("warzone")) {
            Block block = event.getClickedBlock();
            System.out.println("1");
            if (block != null) {
                boolean found = crates.stream().anyMatch(tuple -> tuple.getFirst().equals(block.getType().name()));
                if (found) {
                    System.out.println("2");
                    Location crateLoc = block.getLocation();
                    Tuple<String, Integer, Integer> TriplesCouilles = null;
                    for (int i = 0; i < crates.size(); i++) {
                        TriplesCouilles = crates.get(i);
                        System.out.println("" + i);
                    }
                    if (TriplesCouilles != null) {
                        System.out.println("3");
                        if (crateInventory.containsKey(crateLoc)) {
                            Inventory inv = crateInventory.get(crateLoc);
                            System.out.println("4");
                            event.getPlayer().openInventory(inv);
                        } else {
                            System.out.println("5");
                            Map<ItemStack, Integer> loot = createLoot(TriplesCouilles);
                            System.out.println("loot "+loot+ "  ");
                            crateInventory.put(crateLoc, createGUI(loot, TriplesCouilles));
                        }
                    }
                }
            }
        }
    }

    private Map<ItemStack, Integer> createLoot(Tuple<String, Integer, Integer> tuple) {
        String type = tuple.getFirst();
        List<Tuple<String, Integer, Integer>> items = new ArrayList<>();
        Map<ItemStack, Integer> finalItem;
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
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "":

                return finalItem = generateLoot(items, 2);
        }
        return null;
    }

    private Map<ItemStack, Integer> generateLoot(List<Tuple<String, Integer, Integer>> items, int nb) {
        Random rand = new Random();
        Map<ItemStack, Integer> finalItem = new HashMap<>();

        nb = rand.nextInt(nb) + 1; 
        for (int x = 0; x < nb; x++) {
            boolean isSelected = false;

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
                    System.out.println("GENERATING LOOT: " + finalItem);
                }
            }
        }
        return finalItem;
    }

    private Inventory createGUI(Map<ItemStack, Integer> loot, Tuple<String, Integer, Integer> crate) {
        if (loot == null || crate == null) {
            throw new IllegalArgumentException("Loot map or crate cannot be null.");
        }

        Random random = new Random();
        String name = getDisName(crate.getFirst());
        Inventory gui = Bukkit.createInventory(null, crate.getThird(), "§7" + name);

        Set<Integer> occupiedSlots = new HashSet<>();

        for (Map.Entry<ItemStack, Integer> entry : loot.entrySet()) {
            ItemStack item = entry.getKey();
            if (item == null || entry.getValue() <= 0) {
                continue;
            }

            final ItemStack finalItem = item.clone();
            finalItem.setAmount(entry.getValue());

            int slot;
            do {
                slot = random.nextInt(crate.getThird());
            } while (occupiedSlots.contains(slot));

            occupiedSlots.add(slot);
            gui.setItem(slot, new ItemStack(Material.WEB));

            final int finalSlot = slot;
            Bukkit.getScheduler().runTaskLater(main, () -> {
                gui.setItem(finalSlot, finalItem);
            }, 40L);
        }

        return gui;
    }

    private String getDisName(String type) {
        if (type.startsWith("MWC") || type.startsWith("HBM")) {
            String[] names = type.toLowerCase().split("_");
            if (names.length > 1) {
                String name = names[1].replace("_", " ");
                name = name.substring(0, 1).toUpperCase() + name.substring(1);
                return name;
            }
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
