package lmp;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.awt.Color;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class RandomItem{
    public static final Material[] m = Material.values();

    public static void getRandomItem(PlayerInteractEvent event) throws IOException {
        FileConfiguration discordTextCfg = Api.loadConfig(Constants.YML_CONFIG_FILE_NAME);
        int randomItemCost = discordTextCfg.getInt("randomItemGen.cost");
        RegisteredServiceProvider<Economy> rsp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        Economy econ;
        assert rsp != null;
        econ = rsp.getProvider();
        Player player = event.getPlayer();
        OfflinePlayer op = Bukkit.getOfflinePlayer(player.getUniqueId());
        Location leverLocation = new Location(event.getPlayer().getWorld(), discordTextCfg.getInt("randomItemGen.buttonLocation.x"), discordTextCfg.getInt("randomItemGen.buttonLocation.y"), discordTextCfg.getInt("randomItemGen.buttonLocation.z") );
        if(event.getClickedBlock() != null && event.getClickedBlock().getLocation().equals(leverLocation)){
            Block block = event.getClickedBlock();
            assert block != null;
            if (block.getLocation().equals(leverLocation)){
                if (econ.getBalance(op) >= randomItemCost) {
                    Random rand = new Random();
                    int n = rand.nextInt(m.length);
                    Material itemToGive = m[n];
                    if (String.valueOf(m[n]).equalsIgnoreCase("AIR")){
                        itemToGive = Material.BEACON;
                    }
                    Api.messageInConsole(ChatColor.GREEN + "Item to Give: " + ChatColor.GOLD + itemToGive);
                    ItemStack is = new ItemStack(Material.valueOf(String.valueOf(itemToGive)));
                    World world = player.getWorld();
                    Location dropLocation = new Location(event.getPlayer().getWorld(), discordTextCfg.getInt("randomItemGen.itemDropLocation.x"), discordTextCfg.getInt("randomItemGen.itemDropLocation.y"), discordTextCfg.getInt("randomItemGen.itemDropLocation.z") );
                    try {
                        world.dropItem(dropLocation,is);
                        //world.playSound()
                        econ.withdrawPlayer(op, randomItemCost);
                        player.sendMessage(ChatColor.GREEN + "You received a " + ChatColor.GOLD + m[n]);
                        EmbedBuilder eb = new EmbedBuilder();
                        SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
                        Date date = new Date(System.currentTimeMillis());
                        eb.setTitle("Discord Username: " + LatchDiscord.getJDA().getGuildById(Constants.GUILD_ID).getMemberById(Api.getDiscordIdFromMCid(player.getUniqueId().toString())).getUser().getName() + "\nMC Username: " + player.getName() + "\nRandom Item: " + m[n].toString() + "\nLocal Time: " + formatter.format(date), null);
                        eb.setColor(new Color(0xE1E2BF0D, true));
                        TextChannel randomItemLogChannel = LatchDiscord.jda.getTextChannelById(Constants.RANDOM_ITEM_LOG_CHANNEL_ID);
                        assert randomItemLogChannel != null;
                        randomItemLogChannel.sendMessageEmbeds(eb.build()).queue();
                    } catch (IllegalArgumentException e){
                        Api.messageInConsole(ChatColor.RED + "Can't give air in Random Item " + e);
                        player.sendMessage(ChatColor.RED + "An error occurred. Please click for a random item again :)");
                    }

                } else {
                    player.sendMessage(ChatColor.GREEN + "The cost of getting a random item is " + ChatColor.GOLD + "$" +randomItemCost);
                    player.sendMessage(ChatColor.RED + "Your available balance is only " + ChatColor.GOLD + "$" + econ.getBalance(op));
                }
            }
        }

    }
}
