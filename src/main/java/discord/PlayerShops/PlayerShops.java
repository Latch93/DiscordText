package discord.PlayerShops;

import discord.Constants;
import discord.Main;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class PlayerShops {
    public static void savePlayerShop(InventoryCloseEvent e) throws IOException {
        File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
        FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
        UUID playerUUID = e.getPlayer().getUniqueId();
        String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
        String playerName = arr[0];
        for (int i = 0; i < e.getInventory().getSize(); i++){
            if (e.getInventory().getItem(i) != null){
                String itemName = Objects.requireNonNull(e.getInventory().getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(e.getInventory().getItem(i)).getAmount());
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".material", itemName);
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(e.getInventory().getItem(i)).getItemMeta();
                assert im != null;
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                Map<Enchantment, Integer> enchants = Objects.requireNonNull(e.getInventory().getItem(i)).getEnchantments();
                Iterator it = enchants.entrySet().iterator();
                int count = 0;
                while (it.hasNext()) {
                    // get the pair
                    Map.Entry pair = (Map.Entry)it.next();
                    // using WordUtils.capitalize to produce a nice output like "Durability" instead of "DURABILITY"
                    // the pair's key would be the Enchantment object and the value would be the level in the map.
                    // you can probably use some util online if you wanna convert that int to a roman number
                    Enchantment enchantment = (Enchantment)  pair.getKey();
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".enchant", enchantment.getKey().getKey());
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".level", pair.getValue());
                    count++;
                }
            } else {
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i, null);
            }
        }
        configCfg.save(configFile);
    }

    public static Inventory updateInventory(Inventory inv, Player player) throws IOException {
        File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
        FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
        String playerName = player.getName();
        for (int i = 0; i < inv.getSize(); i++){
            if (inv.getItem(i) != null){
                String itemName = Objects.requireNonNull(inv.getItem(i)).getType().toString();
                String itemAmount = String.valueOf(Objects.requireNonNull(inv.getItem(i)).getAmount());
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".material", itemName);
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".amount", itemAmount);
                ItemMeta im = Objects.requireNonNull(inv.getItem(i)).getItemMeta();
                assert im != null;
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".displayName", im.getDisplayName());
                Map<Enchantment, Integer> enchants = Objects.requireNonNull(inv.getItem(i)).getEnchantments();
                Iterator it = enchants.entrySet().iterator();
                int count = 0;
                while (it.hasNext()) {
                    // get the pair
                    Map.Entry pair = (Map.Entry)it.next();
                    // using WordUtils.capitalize to produce a nice output like "Durability" instead of "DURABILITY"
                    // the pair's key would be the Enchantment object and the value would be the level in the map.
                    // you can probably use some util online if you wanna convert that int to a roman number
                    Enchantment enchantment = (Enchantment)  pair.getKey();
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".enchant", enchantment.getKey().getKey());
                    configCfg.set("players." + playerName + Constants.YML_SLOTS + i + ".enchants." +count + ".level", pair.getValue());
                    count++;
                }
            } else {
                configCfg.set("players." + playerName + Constants.YML_SLOTS + i, null);
            }
        }
        configCfg.save(configFile);
        return inv;
    }

    public static void illegalPlayerShopItems(InventoryClickEvent e, Player player){
        if (e.getCurrentItem().getType().toString().contains("SHULKER_BOX") || e.getCurrentItem().getType().toString().contains("PLAYER_HEAD") ){
            e.setCancelled(true);
            player.sendMessage(ChatColor.RED + "Unable to sell " + ChatColor.GOLD + e.getCurrentItem().getType() + "'s" + ChatColor.RED + " in player shops at this time");
        }
    }
    public static void itemWorthNotSet(InventoryClickEvent e, Player player, FileConfiguration playerShopCfg){
        String material = e.getCurrentItem().getType().toString();
        if (!playerShopCfg.isSet("players." + player.getName() + ".itemWorth." + Objects.requireNonNull(e.getCurrentItem()).getType()) && !playerShopCfg.isSet("players." + player.getName() + ".itemWorth." + material + getStringBuilder(e.getCurrentItem()))) {
            e.setCancelled(true);
            player.closeInventory();
            player.sendMessage(ChatColor.RED + "You need to set this item's worth with " + ChatColor.AQUA + "/ps setworth [amount]" + ChatColor.RED + " before you can add it to your shop.");
        }
    }

    @NotNull
    public static StringBuilder getStringBuilder(ItemStack im) {
        Set<Map.Entry<Enchantment, Integer>> enchants = im.getEnchantments().entrySet();
        StringBuilder enchantString = new StringBuilder();
        int counter = 0;
        int enchantSize = enchants.size();
        for (Map.Entry<Enchantment, Integer> enchant : enchants){
            if (counter == 0){
                enchantString.append("-Enchant:" + enchant.getKey().getKey().getKey() + "|Level:"+enchant.getValue());
            } else if (counter > 0 && counter < enchantSize - 1){
                enchantString.append("-Enchant:" + enchant.getKey().getKey().getKey() + "|Level:"+enchant.getValue() + "-");
            } else {
                enchantString.append("Enchant:" + enchant.getKey().getKey().getKey() + "|Level:"+enchant.getValue());
            }
            counter++;
        }
        return enchantString;
    }

    public static void sd(InventoryClickEvent e, Economy econ, Player player) throws IOException {
        OfflinePlayer offlineBuyer = null;
        OfflinePlayer offlineSeller = null;
        double buyerBalance = 0;
        if (Objects.requireNonNull(e.getClickedInventory()).getSize() == 27) {
            String[] arr = e.getView().getTitle().split(Constants.YML_POSSESSIVE_PLAYER_SHOP);
            String sellerShopPlayerName = arr[0];
            for (OfflinePlayer olp : Bukkit.getWhitelistedPlayers()) {
                Player temp = (Player) e.getWhoClicked();
                if (temp.getName().equalsIgnoreCase(olp.getName())) {
                    offlineBuyer = olp;
                }
                if (sellerShopPlayerName.equalsIgnoreCase(olp.getName())) {
                    offlineSeller = olp;
                }
            }
            buyerBalance = econ.getBalance(offlineBuyer);
            File configFile = new File(Main.getPlugin(Main.class).getDataFolder(), "playerShops.yml");
            FileConfiguration configCfg = YamlConfiguration.loadConfiguration(configFile);
            int itemCost = 0;
            if (configCfg.isSet("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType()  + getStringBuilder(e.getCurrentItem()))){
                itemCost = configCfg.getInt("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType() + getStringBuilder(e.getCurrentItem()));
            } else if (configCfg.isSet("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType())){
                itemCost = configCfg.getInt("players." + offlineSeller.getName() + ".itemWorth." + e.getCurrentItem().getType());
            }
            int totalAmount = e.getCurrentItem().getAmount();
            ItemMeta itemMeta = e.getCurrentItem().getItemMeta();
            itemMeta.setLore(null);
            if (e.getClick().toString().equalsIgnoreCase("LEFT")){
                if (buyerBalance < itemCost){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy this item.");
                } else {
                    ItemStack im = e.getCurrentItem();
                    ItemStack test = e.getCurrentItem();
                    im.setAmount(im.getAmount() - 1);
                    e.getClickedInventory().setItem(e.getSlot(), im);
                    test.setAmount(1);
                    test.setItemMeta(itemMeta);
                    player.getInventory().addItem(test);
                    econ.withdrawPlayer(offlineBuyer, itemCost);
                    econ.depositPlayer(offlineSeller, itemCost);
                    player.updateInventory();
                }
                e.setCancelled(true);
            } else if (e.getClick().toString().equalsIgnoreCase("RIGHT")){
                if (e.getCurrentItem().getAmount() >= 10) {
                    int itemTotalCost = itemCost * 10;
                    if (buyerBalance < itemTotalCost){
                        player.sendMessage(ChatColor.RED + "Not enough money to buy these 10 items.");
                    } else {
                        ItemStack im = e.getCurrentItem();
                        ItemStack test = e.getCurrentItem();
                        im.setAmount(im.getAmount() - 10);
                        e.getClickedInventory().setItem(e.getSlot(), im);
                        test.setAmount(10);
                        test.setItemMeta(itemMeta);
                        player.getInventory().addItem(test);
                        econ.withdrawPlayer(offlineBuyer, itemTotalCost);
                        econ.depositPlayer(offlineSeller, itemTotalCost);
                        player.updateInventory();
                    }
                }
                e.setCancelled(true);
            } else {
                if (buyerBalance < (totalAmount * itemCost)){
                    player.sendMessage(ChatColor.RED + "Not enough money to buy whole item stack.");
                } else {
                    int itemTotalCost = itemCost * totalAmount;
                    ItemStack im = e.getCurrentItem();
                    ItemStack test = e.getCurrentItem();
                    im.setAmount(im.getAmount() - totalAmount);
                    e.getClickedInventory().setItem(e.getSlot(), im);
                    test.setAmount(totalAmount);
                    test.setItemMeta(itemMeta);
                    player.getInventory().addItem(test);
                    econ.withdrawPlayer(offlineBuyer, itemTotalCost);
                    econ.depositPlayer(offlineSeller, itemTotalCost);
                    player.updateInventory();
                }
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
}