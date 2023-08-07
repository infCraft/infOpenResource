package org.time.activitycoin.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.time.activitycoin.ActivityCoin;
import org.time.activitycoin.Cache;
import org.time.activitycoin.gui.GuiItem;

import com.google.common.collect.Lists;

public class Utils extends BasicUtil {
	/**
	 * 指令帮助
	 * @param sender 指令发送者
	 * @param isOp 是否为Op
	 */
	public static void sendHelp(CommandSender sender, boolean isOp) {
		sender.sendMessage("§f=============== §eActivityCoin §f===============");
		sender.sendMessage("§6/acoin help §e打开指令帮助");
		sender.sendMessage("§6/acoin shop §e打开活动代币商城");
		if (isOp) sender.sendMessage("§6/acoin give [玩家] [活动代币数量] §e给予某个玩家一定数量的活动代币");
		if (isOp) sender.sendMessage("§6/acoin take [玩家] [活动代币数量] §e拿走某个玩家一定数量的活动代币");
		if (isOp) sender.sendMessage("§6/acoin set [玩家] [活动代币数量] §e设置某个玩家的活动代币数量");
		if (isOp) sender.sendMessage("§6/acoin see [玩家] §e查看某个玩家的活动代币数量");
		if (isOp) sender.sendMessage("§6/acoin reload §e重载插件");
	}
	
	/**
     * 在gui中批量添加物品
     * @param inv 背包
     * @param slot 要添加的格子
     * @param item 物品模板
     */
    public static void drawBackground(Inventory inv, int[] slot, ItemStack item) {
    	for (int i=0;i<slot.length;i++) inv.setItem(slot[i], item);
    }
    
    /**
     * 创建GUI
     * @param page 当前页数（从0开始）
     * @param maxpage 最大页数
     * @return 创建好的GUI
     */
    @NotNull
	public static Inventory buildPreset(int page, @NotNull UUID player) {
		Inventory inv = Bukkit.createInventory(null, 54, "活动代币商城");
		
		// 计算最大页数
		int size = Cache.guiitems.size();
		int maxpage = size == 0?0:(size-1)/45+1;
		
		// 最下面一排
		drawBackground(inv, new int[] {45,47,48,50,51,53}, Utils.createItem(Material.GRAY_STAINED_GLASS_PANE, page+""));
		
		// 玩家头颅物品，用于显示玩家数据
		// 现在改成了画
		/*ItemStack head = getPlayerHead(player);
		ItemMeta meta = head.getItemMeta();
		meta.setDisplayName("§6"+Bukkit.getOfflinePlayer(player).getName());
		List<String> lore = Lists.newArrayList("§f活动代币数量: §e"+Cache.data.get(Bukkit.getOfflinePlayer(player).getName()));
		meta.setLore(lore);
		head.setItemMeta(meta);*/
		inv.setItem(49, Utils.createItem(Material.PAINTING, "§6"+Bukkit.getOfflinePlayer(player).getName(), Lists.newArrayList("§f活动代币数量: §e"+Cache.data.get(Bukkit.getOfflinePlayer(player).getName()))));
		
		// 翻页按钮
		inv.setItem(46, Utils.createItem(Material.GREEN_STAINED_GLASS_PANE, "§a上一页", Lists.newArrayList("", "§e第"+(page+1)+"页 共"+maxpage+"页")));
		inv.setItem(52, Utils.createItem(Material.GREEN_STAINED_GLASS_PANE, "§a下一页", Lists.newArrayList("", "§e第"+(page+1)+"页 共"+maxpage+"页")));
		if (page == 0) inv.setItem(46, Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, "§a上一页", Lists.newArrayList("", "§e第"+(page+1)+"页 共"+maxpage+"页")));
		if (page == maxpage-1) inv.setItem(52, Utils.createItem(Material.BLACK_STAINED_GLASS_PANE, "§a下一页", Lists.newArrayList("", "§e第"+(page+1)+"页 共"+maxpage+"页")));

		// 商品按钮
		for (int i=0;i<45;i++) {
			int number = page*45+i;
			if (number>=size) break;
			inv.setItem(i, Cache.guiitems.get(number).getItem());
		}
		
		return inv;
	}
    
    /**
     * 替换所有需要替换的字符
     * ActivityCoin插件专用
     * @param str 需要替换的字符串
     * @return 替换后的字符串
     */
    public static String replaceAll(String str) {
    	return str.replaceAll("&", "§");
    }
    
    /**
     * 反序列化配置文件中的Enchantments
     * @param enchants StringList
     * @return 反序列化后的Map<Enchantment,Integer>，请使用addUnsafeEnchantments
     */
    @NotNull
    public static Map<Enchantment,Integer> getEnchantsFromStringList(@NotNull List<String> enchants) {
    	Map<Enchantment,Integer> enchs = new HashMap<>();
    	for (String str: enchants) {
    		Enchantment ench = Enchantment.getByName(str.split(",")[0].toUpperCase());
    		int level = Integer.parseInt(str.split(",")[1]);
    		enchs.put(ench, level);
    	}
    	return enchs;
    }
    
    /**
     * 创建新的玩家数据到cache中
     * @param name 玩家名字
     */
    public static void createNewPlayerData(@NotNull String name) {
    	Cache.data.put(name, 0);
    }
    
    /**
     * 记录玩家的购买数据到logs.txt中
     * @param player 玩家
     * @param guiitem 点击的gui物品
     */
    public static void log(String player, @Nonnull GuiItem guiitem) {
		File f = new File(ActivityCoin.instance.getDataFolder(), "logs.txt");
		try {
			if (!f.exists()) f.createNewFile();
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw, 2048);
			bw.write("["+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+"] "+player+" bought "+guiitem.getId()+" $"+guiitem.getPrice()+"\n");
			bw.close();
			fw.close();
		} catch (IOException e) {
			System.err.println("ActivityCoin saving logs gged!");
			e.printStackTrace();
		}
	}
}
