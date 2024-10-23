package com.openwar.openwarwarzone.Handler;

import com.openwar.openwarlevels.level.PlayerDataManager;
import com.openwar.openwarlevels.level.PlayerLevel;
import com.openwar.openwarwarzone.Main;
import com.openwar.openwarwarzone.Utils.Tuple;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.*;

public class LootCrate implements Listener{
    private final PlayerDataManager pl;
    private final Map<Location, Inventory> crateInventory;
    private Map<Location, Long> crateTimers = new HashMap<>();
    private JavaPlugin main;

    double exp;
    PlayerLevel xp;

    List<Tuple<String, Integer, Integer>> crates = new ArrayList<>();

    public LootCrate(PlayerDataManager pl, Main main) {
        this.pl = pl;
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
     public void onClose(InventoryCloseEvent event) {
         if (event.getPlayer().getWorld().getName().equals("warzone")) {
             if (event.getInventory().getName().contains("§8§l")) {
                 ItemStack[] content = event.getInventory().getContents();
                 if (content == null) {

                 }
             }
         }
     }

    @EventHandler(priority = EventPriority.HIGH)
    public void onLoot(PlayerInteractEvent event) {
        if (event.getPlayer().getWorld().getName().equals("warzone")) {
            Block block = event.getClickedBlock();
            if (block != null) {
                Location crateLoc = block.getLocation();
                Optional<Tuple<String, Integer, Integer>> found = crates.stream()
                        .filter(tuple -> tuple.getFirst().equals(block.getType().name()))
                        .findFirst();
                if (found.isPresent()) {
                    event.setCancelled(true);
                    Tuple<String, Integer, Integer> TriplesCouilles = found.get();
                    long cooldownTime = TriplesCouilles.getSecond() * 60 * 1000L;
                    long currentTime = System.currentTimeMillis();
                    if (crateTimers.containsKey(crateLoc)) {
                        long lastOpenTime = crateTimers.get(crateLoc);
                        long timeSinceLastOpen = currentTime - lastOpenTime;
                        if (timeSinceLastOpen >= cooldownTime) {
                            regenerateCrate(event, crateLoc, TriplesCouilles);
                        } else {
                            if (crateInventory.containsKey(crateLoc)) {
                                Inventory inv = crateInventory.get(crateLoc);
                                ItemStack[] content = inv.getContents();
                                if (content == null) {
                                    event.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent("§cX §7Empty"));
                                } else {
                                    event.getPlayer().openInventory(inv);
                                }
                            }
                        }
                    } else {
                        regenerateCrate(event, crateLoc, TriplesCouilles);
                    }
                }
            }
        }
    }

    private void regenerateCrate(PlayerInteractEvent event, Location crateLoc, Tuple<String, Integer, Integer> TriplesCouilles) {
        Map<ItemStack, Integer> loot = createLoot(TriplesCouilles);
        Inventory inv = createGUI(loot, TriplesCouilles);
        crateInventory.put(crateLoc, inv);
        crateTimers.put(crateLoc, System.currentTimeMillis());
        event.getPlayer().openInventory(inv);
    }

    private Map<ItemStack, Integer> createLoot(Tuple<String, Integer, Integer> tuple) {
        String type = tuple.getFirst();
        System.out.println("type: "+type);
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
                finalItem = generateLoot(items, 3);
                return finalItem;
            case "MWC_FRIDGE_OPEN":
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
                finalItem = generateLoot(items, 3);
                return finalItem;
            case "MWC_FILINGCABINET":
                items.add(new Tuple<>("MONEY", 1, 20));
                items.add(new Tuple<>("MWC_BULLET44", 16 , 10));
                items.add(new Tuple<>("MWC_BULLET45ACP", 18 , 20));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 12 , 50));
                items.add(new Tuple<>("MWC_BULLET9X19MM", 14 , 60));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 1 , 50));
                items.add(new Tuple<>("MWC_M17", 1 , 5));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_FILINGCABINET_OPENED":
                items.add(new Tuple<>("MONEY", 1, 20));
                items.add(new Tuple<>("MWC_BULLET44", 16 , 10));
                items.add(new Tuple<>("MWC_BULLET45ACP", 18 , 20));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 12 , 50));
                items.add(new Tuple<>("MWC_BULLET9X19MM", 14 , 60));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 1 , 50));
                items.add(new Tuple<>("MWC_M17", 1 , 5));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_DUMPSTER":
                items.add(new Tuple<>("ROTTEN_FLESH", 2, 60));
                items.add(new Tuple<>("POISONOUS_POTATO", 2, 50));
                items.add(new Tuple<>("HARVESTCRAFT_SWEETPICKLEITEM", 3, 40));
                items.add(new Tuple<>("HBM_CANNED_TOMATO", 1, 30));
                items.add(new Tuple<>("HARVESTCRAFT_ZOMBIEJERKYITEM", 1, 20));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 2, 10));
                items.add(new Tuple<>("HBM_WIRE_COPPER", 6, 10));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_WOODEN_CRATE_OPENED":
                items.add(new Tuple<>("MWC_BULLET9X19MM", 40, 60));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 38, 60));
                items.add(new Tuple<>("MWC_BULLET45ACP", 35, 55));
                items.add(new Tuple<>("MWC_BULLET762X39", 30, 45));
                items.add(new Tuple<>("MWC_BULLET762X54", 38, 45));
                items.add(new Tuple<>("MWC_BULLET556X45", 35, 50));
                items.add(new Tuple<>("MWC_BULLET545X39", 32, 50));
                items.add(new Tuple<>("MWC_SV98MAG_2", 2, 20));
                items.add(new Tuple<>("MWC_SOCOM_MAG",2 , 30 ));
                items.add(new Tuple<>("MWC_M38MAG_2", 2, 30 ));
                items.add(new Tuple<>("MWC_M4A1MAG_2", 2, 30 ));
                items.add(new Tuple<>("MWC_AK74MAG", 2, 30 ));
                items.add(new Tuple<>("MWC_AK47MAG", 2, 30 ));
                items.add(new Tuple<>("MWC_AK47PMAGTAN", 2, 30 ));
                items.add(new Tuple<>("MWC_AK15MAG_2", 2, 30 ));
                items.add(new Tuple<>("MWC_AK74", 1, 5 ));
                items.add(new Tuple<>("MWC_AK47", 1, 5 ));
                items.add(new Tuple<>("MWC_MAC10", 1, 15 ));
                items.add(new Tuple<>("MWC_MAC10MAG", 3, 25));
                finalItem = generateLoot(items, 3);
                return finalItem;
            case "MWC_WEAPONS_CASE":
                items.add(new Tuple<>("MWC_SOCOM_MAG",2 , 45 ));
                items.add(new Tuple<>("MWC_SV98MAG_2", 2, 40));
                items.add(new Tuple<>("MWC_M38MAG_2", 2, 50 ));
                items.add(new Tuple<>("MWC_M4A1MAG_2", 2, 60 ));
                items.add(new Tuple<>("MWC_M38_DMR", 1, 30));
                items.add(new Tuple<>("MWC_M4A1&1", 1, 40));
                items.add(new Tuple<>("MWC_SV98&1", 1 , 10));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_AMMO_BOX":
                items.add(new Tuple<>("MWC_BULLET9X19MM", 40, 60));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 38, 60));
                items.add(new Tuple<>("MWC_BULLET45ACP", 35, 55));
                items.add(new Tuple<>("MWC_BULLET762X39", 30, 45));
                items.add(new Tuple<>("MWC_BULLET762X54", 38, 45));
                items.add(new Tuple<>("MWC_BULLET556X45", 35, 50));
                items.add(new Tuple<>("MWC_BULLET545X39", 32, 50));
                items.add(new Tuple<>("MWC_SV98MAG_2", 2, 20));
                items.add(new Tuple<>("MWC_SOCOM_MAG",2 , 30 ));
                items.add(new Tuple<>("MWC_M38MAG_2", 2, 30 ));
                items.add(new Tuple<>("MWC_M4A1MAG_2", 2, 30 ));
                items.add(new Tuple<>("MWC_AK74MAG", 2, 30 ));
                items.add(new Tuple<>("MWC_AK47MAG", 2, 30 ));
                items.add(new Tuple<>("MWC_AK47PMAGTAN", 2, 30 ));
                items.add(new Tuple<>("MWC_AK15MAG_2", 2, 30 ));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_VENDING_MACHINE":
                items.add(new Tuple<>("HARVESTCRAFT_SNICKERSBARITEM", 2, 40));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 2, 40));
                items.add(new Tuple<>("HARVESTCRAFT_CHOCOLATEMILKITEM", 2, 30));
                items.add(new Tuple<>("HBM_CANNED_TOMATO", 1 , 40));
                items.add(new Tuple<>("HARVESTCRAFT_CRISPYRICEPUFFBARSITEM", 1, 45));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 2, 56));
                items.add(new Tuple<>("HARVESTCRAFT_BBQPOTATOCHIPSITEM", 4 , 35));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_TRASH_BIN":
                items.add(new Tuple<>("ROTTEN_FLESH", 2, 60));
                items.add(new Tuple<>("POISONOUS_POTATO", 2, 50));
                items.add(new Tuple<>("HARVESTCRAFT_SWEETPICKLEITEM", 3, 40));
                items.add(new Tuple<>("HBM_CANNED_TOMATO", 1, 30));
                items.add(new Tuple<>("HARVESTCRAFT_ZOMBIEJERKYITEM", 1, 20));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 2, 10));
                items.add(new Tuple<>("HBM_WIRE_COPPER", 6, 10));
                items.add(new Tuple<>("HBM_NUCLEAR_WASTE", 1, 1));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_WEAPONS_CASE_SMALL":
                items.add(new Tuple<>("MWC_APSMAG_2", 2, 30));
                items.add(new Tuple<>("MWC_MAKAROVMAG", 2, 40));
                items.add(new Tuple<>("MWC_GLOCKMAG13", 1 , 35));
                items.add(new Tuple<>("MWC_MAKAROV_PM", 1, 30));
                items.add(new Tuple<>("MWC_APS", 1 , 20));
                items.add(new Tuple<>("MWC_GLOCK_18C", 1 , 20));
                items.add(new Tuple<>("MWC_SILENCER9MM", 1 , 10));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "CFM_COUNTER_DRAWER":
                items.add(new Tuple<>("MONEY", 1, 20));
                items.add(new Tuple<>("MWC_BULLET44", 16 , 10));
                items.add(new Tuple<>("MWC_BULLET45ACP", 18 , 20));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 12 , 50));
                items.add(new Tuple<>("MWC_BULLET9X19MM", 14 , 60));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 1 , 50));
                items.add(new Tuple<>("MWC_M17", 1 , 5));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "CFM_BEDSIDE_CABINET_OAK":
                items.add(new Tuple<>("MONEY", 1, 20));
                items.add(new Tuple<>("MWC_BULLET44", 16 , 10));
                items.add(new Tuple<>("MWC_BULLET45ACP", 18 , 20));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 12 , 50));
                items.add(new Tuple<>("MWC_BULLET9X19MM", 14 , 60));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 1 , 50));
                items.add(new Tuple<>("MWC_M17", 1 , 5));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "CFM_DESK_CABINET_OAK":
                items.add(new Tuple<>("MONEY", 1, 20));
                items.add(new Tuple<>("MWC_BULLET44", 16 , 10));
                items.add(new Tuple<>("MWC_BULLET45ACP", 18 , 20));
                items.add(new Tuple<>("MWC_BULLET9X18MM", 12 , 50));
                items.add(new Tuple<>("MWC_BULLET9X19MM", 14 , 60));
                items.add(new Tuple<>("HARVESTCRAFT_ENERGYDRINKITEM", 1 , 50));
                items.add(new Tuple<>("MWC_M17", 1 , 5));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_RUSSIAN_WEAPONS_CASE":
                items.add(new Tuple<>("MWC_AK74MAG", 2, 50 ));
                items.add(new Tuple<>("MWC_AK47MAG", 2, 50 ));
                items.add(new Tuple<>("MWC_AK47PMAGTAN", 2, 50 ));
                items.add(new Tuple<>("MWC_AK15MAG_2", 2, 35 ));
                items.add(new Tuple<>("MWC_AK74", 1, 35 ));
                items.add(new Tuple<>("MWC_AK47", 1, 45 ));
                items.add(new Tuple<>("MWC_MAC10", 1, 55 ));
                finalItem = generateLoot(items, 2);
                return finalItem;
            case "MWC_SUPPLY_DROP":
                items.add(new Tuple<>("MWC_ACOG", 1, 45));
                items.add(new Tuple<>("MWC_MICROREFLEX", 1, 50));
                items.add(new Tuple<>("MWC_SPECTER", 1, 50));
                items.add(new Tuple<>("MWC_HOLOGRAPHIC2", 1 , 45));
                items.add(new Tuple<>("MCHELI_FIM92", 1, 35));
                items.add(new Tuple<>("MCHELI_FGM148", 1 , 35));
                items.add(new Tuple<>("MWC_SOCOM_MAG",3 , 45 ));
                items.add(new Tuple<>("MWC_SV98MAG_2", 3, 40));
                items.add(new Tuple<>("MWC_M38MAG_2", 3, 50 ));
                items.add(new Tuple<>("MWC_M4A1MAG_2", 3, 60));
                items.add(new Tuple<>("MWC_M38_DMR", 1, 30));
                items.add(new Tuple<>("MWC_M4A1", 1, 40));
                items.add(new Tuple<>("MWC_SV98", 1 , 20));
                finalItem = generateLoot(items, 4);
                return finalItem;
        }
        return null;
    }

    private Map<ItemStack, Integer> generateLoot(List<Tuple<String, Integer, Integer>> items, int nb) {
        Random rand = new Random();
        Map<ItemStack, Integer> finalItem = new HashMap<>();
        nb = getWeightedRandom(nb);
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
                    System.out.println("GENERATING LOOT3: " + finalItem);
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
        Inventory gui = Bukkit.createInventory(null, crate.getThird(), "§8§l" + name);

        Set<Integer> occupiedSlots = new HashSet<>();
        List<Map.Entry<ItemStack, Integer>> lootList = new ArrayList<>(loot.entrySet());

        for (Map.Entry<ItemStack, Integer> entry : lootList) {
            ItemStack item = entry.getKey();
            if (item == null || entry.getValue() <= 0) {
                continue;
            }

            int slot;
            do {
                slot = random.nextInt(crate.getThird());
            } while (occupiedSlots.contains(slot));

            occupiedSlots.add(slot);

            ItemStack cobweb = new ItemStack(Material.COMMAND_MINECART);
            ItemMeta cobwebMeta = cobweb.getItemMeta();
            cobwebMeta.setDisplayName("§7Searching...");
            cobwebMeta.setLore(Collections.singletonList("§8[§7     §8]"));
            cobweb.setItemMeta(cobwebMeta);
            gui.setItem(slot, cobweb);
        }

        processNextItem(gui, lootList, crate, occupiedSlots, 0);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onInventoryClick(InventoryClickEvent event) {
                if (event.getInventory().equals(gui)) {
                    if (event.getCurrentItem() != null && event.getCurrentItem().getType() == Material.COMMAND_MINECART) {
                        event.setCancelled(true);
                    }
                }
            }
        }, main);

        return gui;
    }

    private void processNextItem(Inventory gui, List<Map.Entry<ItemStack, Integer>> lootList, Tuple<String, Integer, Integer> crate, Set<Integer> occupiedSlots, int currentIndex) {
        if (currentIndex >= lootList.size()) {
            return;
        }

        Map.Entry<ItemStack, Integer> entry = lootList.get(currentIndex);
        ItemStack item = entry.getKey();
        if (item == null || entry.getValue() <= 0) {
            processNextItem(gui, lootList, crate, occupiedSlots, currentIndex + 1);
            return;
        }

        final ItemStack finalItem = item.clone();
        finalItem.setAmount(getWeightedRandom(entry.getValue()));

        int slot = occupiedSlots.stream()
                .filter(s -> gui.getItem(s) != null && gui.getItem(s).getType() == Material.COMMAND_MINECART)
                .findFirst()
                .orElse(-1);

        ItemStack cobweb = new ItemStack(Material.COMMAND_MINECART);
        ItemMeta cobwebMeta = cobweb.getItemMeta();
        cobwebMeta.setDisplayName("§7Searching...");
        StringBuilder progressBar = new StringBuilder("§8[§7     §8]");
        cobwebMeta.setLore(Collections.singletonList(progressBar.toString()));
        cobweb.setItemMeta(cobwebMeta);
        gui.setItem(slot, cobweb);

        final int finalSlot = slot;
        Bukkit.getScheduler().runTaskLater(main, new Runnable() {
            int progress = 0;
            final int maxProgress = 5;

            @Override
            public void run() {
                if (progress < maxProgress) {
                    progress++;

                    ItemMeta meta = cobweb.getItemMeta();
                    StringBuilder progressBar = new StringBuilder("§8[§7");
                    for (int i = 0; i < progress; i++) {
                        progressBar.append("█");
                    }
                    for (int i = progress; i < maxProgress; i++) {
                        progressBar.append(" ");
                    }
                    progressBar.append("§8]");

                    meta.setLore(Collections.singletonList(progressBar.toString()));
                    cobweb.setItemMeta(meta);
                    gui.setItem(finalSlot, cobweb);

                    Bukkit.getScheduler().runTask(main, () -> {
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            player.playSound(player.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 0.4f, 1.0f);
                        }
                    });

                    Bukkit.getScheduler().runTaskLater(main, this, 5L);
                } else {
                    gui.setItem(finalSlot, finalItem);
                    Bukkit.getScheduler().runTaskLater(main, () -> {
                        processNextItem(gui, lootList, crate, occupiedSlots, currentIndex + 1);
                    }, 5L);
                }
            }
        }, 5L);
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
    public static int getWeightedRandom(int nb) {
        int totalWeight = (nb * (nb + 1)) / 2;
        Random random = new Random();
        int randomNumber = random.nextInt(totalWeight);

        for (int i = 1; i <= nb; i++) {
            randomNumber -= i;
            if (randomNumber < 0) {
                return i;
            }
        }
        return nb;
    }
}