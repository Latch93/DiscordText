package lmp.Configurations;

import lmp.Constants;
import lmp.Main;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

import static org.bukkit.plugin.java.JavaPlugin.getPlugin;

public class BossConfig {
    private static final Main plugin = getPlugin(Main.class);
    // Set up boss.yml configuration file
    public void setup(){
        FileConfiguration bossCfg;
        File bossFile;
        // if the DiscordText folder does not exist, create the DiscordText folder
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdir();
        }
        bossFile = new File(plugin.getDataFolder(), Constants.YML_BOSS_FILE_NAME + ".yml");
        bossCfg = YamlConfiguration.loadConfiguration(bossFile);
        //if the boss.yml does not exist, create it
        if(!bossFile.exists()){
            try {
                bossCfg.save(bossFile);

            }
            catch(IOException e){
                System.out.println(ChatColor.RED + "Could not create the " + Constants.YML_BOSS_FILE_NAME + ".yml file");
            }
        }
    }
}
