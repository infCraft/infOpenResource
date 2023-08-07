package org.time.iic;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.time.iic.gui.Shop;
import org.time.iic.gui.shop.BuyItem;
import org.time.iic.gui.shop.SellItem;
import org.time.iic.util.Util;

import net.milkbowl.vault.economy.Economy;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class iIC extends JavaPlugin {
	private static iIC instance;
	private static Economy eco;
	public static FileConfiguration shop;
	public static FileConfiguration config;
	
	private static BukkitRunnable timer;
	public void onEnable() {
		instance = this;
		Bukkit.getPluginManager().registerEvents(new Shop(), instance);
		Bukkit.getPluginManager().registerEvents(new JoinListener(), instance);
		setupEconomy();
		loadFiles();
		Shop.loadShop(shop);
		Util.loadConfig();
		loadData();
		getLogger().info("§a成功加载智慧IC");
		
		// 开启计费
		timer = new CostTimer();
		timer.runTaskTimer(this, 0, 20);
	}
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
		saveData();
	}
	public static iIC getInstance() {
		return instance;
	}
	private void setupEconomy() {
		RegisteredServiceProvider<Economy> rsp = Bukkit.getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            getLogger().info("§cCannot hook Vault");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        eco = rsp.getProvider();
        getLogger().info("§aSuccessfully hooked Vault");
	}
	public static Economy getEconomy() {
		return eco;
	}
	public static void loadFiles() {
		shop = Util.loadFile(instance, "shop.yml");
		config = Util.loadFile(instance, "config.yml");
	}
	public static String getPrefix() {
		return "§f[§d智慧IC§f] §7>> §r";
	}
	private void reload() {
		saveData();
		setupEconomy();
		loadFiles();
		Shop.loadShop(shop);
		Util.loadConfig();
		loadData();
		timer.cancel();
		timer = new CostTimer();
		timer.runTaskTimer(this, 0, 20);
	}
	
	public void loadData() {
		File f = new File(this.getDataFolder(), "cache.yml");
		if (!f.exists()) return;
		FileConfiguration cache = YamlConfiguration.loadConfiguration(f);
		String dateStr = cache.getString("date");
		Date date = new Date();
		try {
			Date date1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(dateStr);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date1);
			if (calendar.get(Calendar.HOUR_OF_DAY) >= 3) calendar.add(Calendar.DAY_OF_MONTH, 1);
			calendar.set(Calendar.HOUR_OF_DAY, 3);
			calendar.set(Calendar.MINUTE, 1);
			calendar.set(Calendar.SECOND, 0);
			Date standard = calendar.getTime();
			
			if (date.after(standard)) return; // 在之后就别读取数据了
			for (SellItem sellItem: Shop.sell) {
				if (!cache.contains("sell."+sellItem.getMaterial().toString().toLowerCase())) continue;
				for (String player: cache.getConfigurationSection("sell."+sellItem.getMaterial().toString().toLowerCase()).getKeys(false)) {
					sellItem.setCount(player, cache.getInt("sell."+sellItem.getMaterial().toString().toLowerCase()+"."+player));
				}
			}
			for (BuyItem buyItem: Shop.buy) {
				if (!cache.contains("buy."+buyItem.getMaterial().toString().toLowerCase())) continue;
				for (String player: cache.getConfigurationSection("buy."+buyItem.getMaterial().toString().toLowerCase()).getKeys(false)) {
					buyItem.setCount(player, cache.getInt("buy."+buyItem.getMaterial().toString().toLowerCase()+"."+player));
				}
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
		getLogger().info("§a成功加载数据.");
	}
	public void saveData() {
		File f = new File(this.getDataFolder(), "cache.yml");
		if (f.exists()) f.delete();
		try {
			f.createNewFile();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		FileConfiguration cache = YamlConfiguration.loadConfiguration(f);
		cache.set("date", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		for (SellItem sellItem: Shop.sell) {
			for (String player: sellItem.getMap().keySet()) {
				cache.set("sell."+sellItem.getMaterial().toString().toLowerCase()+"."+player, sellItem.getCount(player));
			}
		}
		for (BuyItem buyItem: Shop.buy) {
			for (String player: buyItem.getMap().keySet()) {
				cache.set("buy."+buyItem.getMaterial().toString().toLowerCase()+"."+player, buyItem.getCount(player));
			}
		}
		try {
			cache.save(f);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		getLogger().info("§a成功保存数据.");
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("iic")) {
			if (args.length == 0) {
				if (!sender.isOp()) return true;
				sender.sendMessage("§6/iic shop §e系统商店");
				sender.sendMessage("§6/iic fly §e飞行模式");
				sender.sendMessage("§6/iic reload §e重载插件(会刷新买卖数据)");
				return true;
			}
			if (args.length == 1&&args[0].equalsIgnoreCase("shop")) {
				Player p = (Player) sender;
				p.openInventory(Shop.getInventory(p.getName(), true, 1));
				return true;
			}
			if (args.length == 1&&args[0].equalsIgnoreCase("fly")) {
				Player p = (Player) sender;
				if (Util.players.containsKey(p.getName())) {
					p.setAllowFlight(false);
					// 这次飞行的秒数
					long time = (System.currentTimeMillis()-Util.players.get(p.getName()))/1000;
					p.sendMessage(getPrefix()+"§a你已关闭飞行模式！在这次计费中，你一共飞行了 §e"+ time+"s §a共计花费 §e"+Util.format(time*Util.fly_cost)+" 金币.");
					Util.players.remove(p.getName());
					return true;
				}
				if (p.getAllowFlight()) {
					p.sendMessage(getPrefix()+"§c你本身就允许飞行！");
					return true;
				}
				double money = eco.getBalance(p);
				if (money<Util.fly_cost) {
					p.sendMessage(getPrefix()+"§c你的钱不足！");
					return true;
				}
				p.setAllowFlight(true);
				Util.players.put(p.getName(), System.currentTimeMillis());
				p.sendMessage(getPrefix()+"§a你已开启飞行！开始计费模式，价格为 §e"+Util.fly_cost+" §a金币每秒.");
				return true;
			}
			if (args.length == 1&&args[0].equalsIgnoreCase("reload")) {
				reload();
				sender.sendMessage(getPrefix()+"§adone.");
				return true;
			}
		}
		return false;
	}
}
