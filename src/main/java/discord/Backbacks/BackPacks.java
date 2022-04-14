package discord.Backbacks;

import discord.Constants;
import discord.Main;

import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BackPacks {
    public static void saveCustomInventory(InventoryCloseEvent e, File configFile) throws IOException {
        FileConfiguration inventoryCfg = Main.getFileConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String playerName = e.getPlayer().getName();
        for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()){
            if (inventoryCfg.isSet(olp.getName() + Constants.YML_SIZE) && e.getView().getTitle().contains(olp.getName())){
                playerName = olp.getName();
            }
        }
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
//                String itemName = Objects.requireNonNull(e.getInventory().getItem(i)).getType().toString();
//                int itemAmount = Objects.requireNonNull(e.getInventory().getItem(i)).getAmount();
                inventoryCfg.set(playerName + Constants.YML_SLOTS + i, e.getInventory().getItem(i));
//                ItemMeta im = Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
//                assert im != null;
//                inventoryCfg.set(playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
//                Map<Enchantment, Integer> enchants;
//                if (itemName.equalsIgnoreCase("ENCHANTED_BOOK")){
//                    EnchantmentStorageMeta meta =(EnchantmentStorageMeta) e.getInventory().getItem(i).getItemMeta();
//                    enchants = meta.getStoredEnchants();
//                    Iterator it = enchants.entrySet().iterator();
//                    int count = 0;
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry)it.next();
//                        String holder = pair.getKey().toString().replace("Enchantment[minecraft:", "");
//                        holder = holder.replace(" ", "");
//                        holder = holder.replace("]", "");
//                        String[] arr = holder.split(",");
//                        inventoryCfg.set(playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", arr[0]);
//                        inventoryCfg.set(playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
//                        count++;
//                    }
//
//                } else {
//                    enchants = Objects.requireNonNull(e.getInventory().getItem(i)).getEnchantments();
//                    Iterator it = enchants.entrySet().iterator();
//                    int count = 0;
//                    while (it.hasNext()) {
//                        // get the pair
//                        Map.Entry pair = (Map.Entry)it.next();
//                        // using WordUtils.capitalize to produce a nice output like "Durability" instead of "DURABILITY"
//                        // the pair's key would be the Enchantment object and the value would be the level in the map.
//                        // you can probably use some util online if you wanna convert that int to a roman number
//                        Enchantment enchantment = (Enchantment)  pair.getKey();
//                        inventoryCfg.set(playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".enchant", enchantment.getKey().getKey());
//                        inventoryCfg.set(playerName + Constants.YML_SLOTS + i + Constants.YML_ENCHANTS +count + ".level", pair.getValue());
//                        count++;
//                    }
//                }
            } else {
                inventoryCfg.set(playerName + Constants.YML_SLOTS + i, null);
            }
        }
        if (inventoryCfg.get(playerName + Constants.YML_SIZE) == null){
            inventoryCfg.set(playerName + Constants.YML_SIZE, e.getInventory().getSize());
        } else {
            inventoryCfg.set(playerName + Constants.YML_SIZE, inventoryCfg.getInt(playerName + Constants.YML_SIZE));
        }
        inventoryCfg.set(playerName + ".name", playerName);
        inventoryCfg.save(configFile);
    }

    public static Inventory setInventoryWhenOpened(Player player, String fileName, int slots, String invTitle, String playerShopToOpen){
        FileConfiguration inventoryConfig = Main.loadConfig(fileName);
        Inventory inv = null;
        String playerName = "";
        if (playerShopToOpen == null || playerShopToOpen.equalsIgnoreCase(player.getName())){
            playerName = player.getName();
        } else {
            playerName = playerShopToOpen;
        }
        if (inventoryConfig.get(playerName + Constants.YML_SIZE) != null) {
            inv = Bukkit.createInventory(null, slots, invTitle);
            if ((inventoryConfig.get(playerName + ".slots") != null)){
                for(String users : inventoryConfig.getConfigurationSection(playerName + ".slots").getKeys(false)) {
                    ItemStack is = inventoryConfig.getItemStack(playerName + Constants.YML_SLOTS + users);
//                    ItemMeta im = is.getItemMeta();
//                    if (inventoryConfig.isSet(playerName + Constants.YML_SLOTS + users + ".enchants")) {
//                        for(String test : inventoryConfig.getConfigurationSection(playerName + Constants.YML_SLOTS + users + ".enchants").getKeys(false)) {
//                            assert im != null;
//                            if (inventoryConfig.getString(playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant") != null && inventoryConfig.getInt(playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level") != 0 ){
//                                if (is.getType().equals(Material.ENCHANTED_BOOK)){
//                                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) im;
//                                    esm.addStoredEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(inventoryConfig.getString(playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant"))))), inventoryConfig.getInt(playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level"), true);
//                                    is.setItemMeta(esm);
//                                } else {
//                                    im.addEnchant(Objects.requireNonNull(Enchantment.getByKey(NamespacedKey.minecraft(Objects.requireNonNull(inventoryConfig.getString(playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".enchant"))))), inventoryConfig.getInt(playerName + Constants.YML_SLOTS + users + Constants.YML_ENCHANTS + test + ".level"), true );
//                                }
//                            }
//                        }
//                    }
//                    assert im != null;
//                    if (inventoryConfig.getString(playerName + Constants.YML_SLOTS + users + ".displayName") != null ){
//                        im.setDisplayName(inventoryConfig.getString(playerName + Constants.YML_SLOTS + users + ".displayName"));
//                        is.setItemMeta(im);
//                    }
                    inv.setItem(Integer.parseInt(users), is);
                }
            }
        } else if (invTitle.toLowerCase().contains("backpack")){
            player.sendMessage(ChatColor.RED + "You need to purchase a backpack before you use this command");
        }
        return inv;
    }

    public static ItemStack getItemWorthString(ItemStack itemStack){
        ItemStack is = itemStack;
        is.setAmount(1);
        return is;
    }


    public static Inventory setLoreInPlayerShop(String playerShopToOpen, Inventory inv, String playerName){
        FileConfiguration playerShopCfg = Main.loadConfig(Constants.YML_PLAYER_SHOP_FILE_NAME);
        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) != null){
                ItemStack itemStack = inv.getItem(i);
                assert itemStack != null;
                int itemStackAmount = itemStack.getAmount();
                itemStack.setAmount(1);
                if (playerShopCfg.isSet(playerShopToOpen + ".itemWorth." + itemStack)){
                    itemStack.setAmount(itemStackAmount);
                    List<String> loreList = new ArrayList<>();
                    ItemMeta im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
                    int totalAmount = itemStack.getAmount();
                    itemStack.setAmount(1);
                    int itemWorth = 0;
                    if (itemStack.getType().toString().contains("SHULKER") && itemStack.getItemMeta() != null && Boolean.FALSE.equals(itemStack.getItemMeta().getDisplayName().isEmpty())){
                        System.out.println("asd");
                        itemWorth = playerShopCfg.getInt(playerShopToOpen + ".itemWorth." + itemStack.getItemMeta().getDisplayName());
                    } else {
                        itemWorth = playerShopCfg.getInt(playerShopToOpen + ".itemWorth." + itemStack);
                    }
                    itemStack.setAmount(itemStackAmount);
                    int totalWorth = totalAmount * itemWorth;
                    if (!playerShopToOpen.equalsIgnoreCase(playerName)){
                        loreList.add(ChatColor.GREEN + "Cost per item " + ChatColor.GOLD + "$" + itemWorth);
                        loreList.add(ChatColor.GREEN + "Left click to purchase " + ChatColor.GOLD + "1" + ChatColor.GREEN + " item.");
                        if (totalAmount > 9) {
                            loreList.add(ChatColor.GREEN + "Right click to purchase " + ChatColor.GOLD + "10" + ChatColor.GREEN + " items. Total cost: " + ChatColor.GOLD + "$" + Math.multiplyExact(10,itemWorth));
                        }
                        loreList.add(ChatColor.GREEN + "Middle click to purchase all items. Total cost: " + ChatColor.GOLD + "$" + totalWorth);
                    } else {
                        loreList.add(ChatColor.GREEN + "Cost per item: " + ChatColor.GOLD + "$" + itemWorth );
                    }
                    assert im != null;
                    im.setLore(loreList);
                    itemStack.setItemMeta(im);
                }
            }
        }
        return inv;
    }

}
