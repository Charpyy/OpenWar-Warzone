package com.openwar.openwarwarzone.Commands;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTType;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import de.tr7zw.nbtapi.NBTItem;

public class CommandDebugItems implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be executed by a player.");
            return true;
        }

        Player player = (Player) sender;
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item == null || item.getType() == Material.AIR) {
            player.sendMessage("You are not holding an item.");
            return true;
        }
        displayItemMeta(item);
        displayAllNBT(item);

        return true;
    }

    private void displayItemMeta(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            Bukkit.getLogger().info("Item Meta:");
            Bukkit.getLogger().info("Display Name: " + meta.getDisplayName());
            Bukkit.getLogger().info("Lore: " + (meta.getLore() != null ? String.join(", ", meta.getLore()) : "No lore"));
        } else {
            Bukkit.getLogger().info("No item meta available.");
        }
    }

    private void displayAllNBT(ItemStack item) {
        NBTItem nbtItem = new NBTItem(item);
        NBTCompound compound = nbtItem.getCompound();
        if (compound != null) {
            for (String key : compound.getKeys()) {
                NBTType type = compound.getType(key);
                switch (type) {
                    case NBTTagString:
                        System.out.println(key + ": " + compound.getString(key));
                        break;
                    case NBTTagInt:
                        System.out.println(key + ": " + compound.getInteger(key));
                        break;
                    case NBTTagDouble:
                        System.out.println(key + ": " + compound.getDouble(key));
                        break;
                    case NBTTagFloat:
                        System.out.println(key + ": " + compound.getFloat(key));
                        break;
                    case NBTTagLong:
                        System.out.println(key + ": " + compound.getLong(key));
                        break;
                    case NBTTagShort:
                        System.out.println(key + ": " + compound.getShort(key));
                        break;
                    case NBTTagByte:
                        System.out.println(key + ": " + compound.getByte(key));
                        break;
                    case NBTTagCompound:
                        System.out.println(key + ": " + compound.getCompound(key).toString());
                        break;
                    default:
                        System.out.println(key + ": [Unknown Type]");
                        break;
                }
            }
        } else {
            System.out.println("No NBT data found for this item.");
        }
    }
}
