package org.Lyrik.ArrowGame.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.Lyrik.ArrowGame.ArrowGameMain;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

public class ConfigUtils {
	public static double kill;
	public static double boardTime;
	public static double death;
	public static double combo;
	public static int readyTime;
	public static List<ItemStack> itemsList = new ArrayList<>();
    public static void loadConfig(String file) {
        File f = new File(ArrowGameMain.getInstance().getDataFolder(), file);
        if (!f.exists()) {
        	ArrowGameMain.getInstance().saveResource(file, true);
        	ArrowGameMain.getInstance().getLogger().info("§e未检测到 config.yml");
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(f);
        //ArrowGameMain.getInstance().saveConfig();
        kill=config.getDouble("score.kill");
        death=config.getDouble("score.death");
        combo=config.getDouble("score.combo");
        boardTime=config.getDouble("time.boardTime");
        readyTime=config.getInt("time.readyTime");
		ConfigurationSection items = config.getConfigurationSection("items");
    	if (items != null) {
    		ArrowGameMain.getInstance().getLogger().info("检测物品文件");
    	    for (String key : items.getKeys(false)) {
    	        ConfigurationSection itemSection = items.getConfigurationSection(key);
    	        if (itemSection != null) {
    	            String itemType = itemSection.getString("item");
    	            int itemAmount = itemSection.getInt("amount", 1);
    	            Material material = Material.matchMaterial(itemType);
    	            ItemStack itemStack = new ItemStack(material, itemAmount);
    	            itemsList.add(itemStack);
    	            if(itemStack!=null) ArrowGameMain.getInstance().getLogger().info("检测物品:"+itemType+"×"+itemAmount);
    	        }
    	    }
    	}
        ArrowGameMain.getInstance().getLogger().info("§a成功加载 config.yml");
    }
}