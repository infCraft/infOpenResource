package org.time.activitycoin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.time.activitycoin.gui.GuiItem;
import org.time.activitycoin.utils.Utils;

import com.google.common.collect.Lists;

public class ActivityCoin extends JavaPlugin implements Listener {
	// 配置文件
	public static YamlConfiguration config;
	// 玩家数据文件
	public static YamlConfiguration players;
	
	public static ActivityCoin instance;
	
	public static final String prefix = "§e活动代币商城 §7>> §r";
	
	public void onEnable() {
		instance = this;
		
		Bukkit.getPluginCommand("acoin").setExecutor(new Commands());
		Bukkit.getPluginCommand("acoin").setTabCompleter(new Commands());
		Bukkit.getPluginManager().registerEvents(this, this);
		
		config = Utils.loadFile(instance, "config.yml");
		players = Utils.loadFile(instance, "players.yml");
		
		readConfig();
		readPlayerData();
		
		// 定时保存数据
		new BukkitRunnable() {

			@Override
			public void run() {
				savePlayerData();
			}
			
		}.runTaskTimer(instance, 12000L, 12000L);
	}
	
	public void onDisable() {
		savePlayerData();
	}
	
	/**
	 * 重载插件
	 */
	public static void reload() {
		Bukkit.getScheduler().cancelTasks(instance);
		savePlayerData();
		
		config = Utils.loadFile(instance, "config.yml");
		players = Utils.loadFile(instance, "players.yml");
		
		readConfig();
		readPlayerData();
		
		new BukkitRunnable() {

			@Override
			public void run() {
				savePlayerData();
			}
			
		}.runTaskTimer(instance, 12000L, 12000L);
	}
	
	/**
	 * 读取config.yml
	 */
	private static void readConfig() {
		List<GuiItem> list = Lists.newArrayList();
		for (String id: config.getConfigurationSection("Gui").getKeys(false)) {
			list.add(readGuiItem(id));
		}
		Cache.guiitems = list;
 	}
	
	/**
	 * 反序列化guiitem
	 * @param id 对应guiitem的id
	 * @return 创建的GuiItem对象
	 */
	@NotNull
	private static GuiItem readGuiItem(String id) {
		ItemStack item = new ItemStack(Material.getMaterial(config.getString("Gui."+id+".Material").toUpperCase()));
		ItemMeta meta = item.getItemMeta();
		
		// 判断name,lore,enchant
		String name = config.getString("Gui."+id+".Name");
		if (name != null) meta.setDisplayName(Utils.replaceAll(name));
		List<String> lore = config.getStringList("Gui."+id+".Lore");
		if (lore != null) meta.setLore(Utils.changeLore(lore, "&", "§"));
		List<String> enchs = config.getStringList("Gui."+id+".Enchantments");
		Map<Enchantment,Integer> enchants = Utils.getEnchantsFromStringList(enchs);
		item.setItemMeta(meta);
		item.addUnsafeEnchantments(enchants);
		
		// 开始创建对象
		int price = config.getInt("Gui."+id+".Price");
		List<String> commands = Utils.changeLore(config.getStringList("Gui."+id+".Action"), "&", "§");
		GuiItem guiitem = new GuiItem(id, item, price, commands);
		return guiitem;
	}
	
	/**
	 * 读取playerdata
	 */
	private static void readPlayerData() {
		Map<String,Integer> map = new HashMap<>();
		for (String id: players.getConfigurationSection("").getKeys(false)) {
			map.put(id, players.getInt(id));
		}
		Cache.data = map;
	}
	
	/**
	 * 保存玩家数据
	 */
	private static void savePlayerData() {
		for (String player: Cache.data.keySet()) {
			players.set(player, Cache.data.get(player));
		}
		try {
			players.save(new File(instance.getDataFolder(), "players.yml"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 监听器
	 * @param e
	 */
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		Inventory inv = e.getInventory();
		// 先判断是否为活动代币商城
		if (!e.getView().getTitle().equalsIgnoreCase("活动代币商城")) return;
		
		// 取消事件
		e.setCancelled(true);
		
		// 判断是否点的是打开的gui，不是就不执行下面的代码
		if (inv != e.getClickedInventory()) return;
		
		// 接下来获取相关信息
		int page = Integer.parseInt(inv.getItem(47).getItemMeta().getDisplayName());
		int slot = e.getSlot();
		Player p = (Player) e.getWhoClicked();
		
		// 点击空白处slot = -999
		//p.sendMessage(slot+"");
		// 判断是否为最后一行
		if (slot < 0||slot == 45||slot == 47||slot == 48||slot == 49||slot == 50||slot == 51||slot == 53) return;
		// 翻页操作
		else if (slot == 46) {
			if (inv.getItem(46).getType() != Material.GREEN_STAINED_GLASS_PANE) return;
			p.openInventory(Utils.buildPreset(page-1, p.getUniqueId()));
		}
		else if (slot == 52) {
			if (inv.getItem(52).getType() != Material.GREEN_STAINED_GLASS_PANE) return;
			p.openInventory(Utils.buildPreset(page+1, p.getUniqueId()));
		}
		// 接下来就只剩点击配置的物品和空气了
		else {
			if (inv.getItem(slot) == null) return;
			
			// 获取所点物品的序号并获取对象
			int number = page*45+slot;
			GuiItem guiitem = Cache.guiitems.get(number);
			// 判断是否够钱
			if (!Cache.data.containsKey(p.getName())) Utils.createNewPlayerData(p.getName());
			int money = Cache.data.get(p.getName());
			if (money<guiitem.getPrice()) {
				p.sendMessage(prefix+"§c你没有足够的活动代币！");
				return;
			}
			// 购买
			Cache.data.replace(p.getName(), money-guiitem.getPrice());
			
			// 执行指令
			guiitem.runCommands(p.getName());
			p.sendMessage(prefix+"§a购买成功！");
			Utils.log(p.getName(), guiitem);
			
			// 刷新gui
			inv.setItem(49, Utils.createItem(Material.PAINTING, "§6"+p.getName(), Lists.newArrayList("§f活动代币数量: §e"+Cache.data.get(p.getName()))));
		}
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		Player p = e.getPlayer();
		if (!Cache.data.containsKey(p.getName())) Cache.data.put(p.getName(), 0);
	}
}
