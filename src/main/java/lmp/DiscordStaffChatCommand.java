package lmp;

import net.dv8tion.jda.api.entities.TextChannel;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DiscordStaffChatCommand implements CommandExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            try {
                Player player = (Player) sender;
                if (args[0] != null){
                    if (player.hasPermission("group.jr-mod")){
                        String playerName = player.getName();
                        TextChannel discordStaffChannel = LatchDiscord.jda.getTextChannelById(Constants.DISCORD_STAFF_CHAT_CHANNEL_ID);
                        StringBuilder messageString = new StringBuilder();
                        for (int i = 0; i < args.length; i++){
                            if (i != args.length-1){
                                messageString.append(args[i]).append(" ");
                            } else {
                                messageString.append(args[i]);
                            }
                        }
                        assert discordStaffChannel != null;
                        String convertedMessage = Api.convertMinecraftMessageToDiscord(playerName, String.valueOf(messageString));
                        String[] messageArr = convertedMessage.split(" [Test Server] » ");
                        for (Player p : Bukkit.getOnlinePlayers()){
                            if (p.hasPermission("group.jr-mod")){
                                p.sendMessage("[" + ChatColor.LIGHT_PURPLE + "DTSC" + ChatColor.WHITE + "] - " + ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE + " [Test Server] » " + ChatColor.AQUA + messageArr[1]);
                            }
                        }
                        discordStaffChannel.sendMessage("[DTSC] - " + convertedMessage).queue();
                    }
                }
            } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ignored) {
            }
        }
    return false;
    }
}
