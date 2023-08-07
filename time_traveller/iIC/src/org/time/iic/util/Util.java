package org.time.iic.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.inventory.ItemStack;
import org.time.iic.iIC;


public class Util extends BasicUtil {
	
	public static int fly_cost;
	
	public static Map<String,Long> players = new HashMap<>();
	//价格计算公式
	public static double getBuyMoney(double price, int count) {
		return price*g(count);
	}
	public static double getSellMoney(double price, int count) {
		return price*f(count);
	}
	
	// sell
	public static double f(double x) {
		return Math.pow(2, x/800.0);
	}
	// buy
	public static double g(double x) {
		return Math.pow(2, -x/800.0);
	}
	
	public static String format(double num) {
		return new DecimalFormat("###,###,###,###,###,###.##").format(num);
	}
	public static String format2(double num) {
		return new DecimalFormat("###,###,###,###,###,##0.0").format(num);
	}
	public static String format3(double num) {
		return new DecimalFormat("###,###,###,###,###,##0.00").format(num);
	}
	public static void log(String player, String state,@Nonnull ItemStack item, int num, double price) {
		File f = new File(iIC.getInstance().getDataFolder(), "logs.txt");
		String ite = item.hasItemMeta()&&item.getItemMeta().hasDisplayName()?item.getItemMeta().getDisplayName():item.getType().name();
		try {
			if (!f.exists()) f.createNewFile();
			FileWriter fw = new FileWriter(f, true);
			BufferedWriter bw = new BufferedWriter(fw, 2048);
			bw.write("["+(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()))+"] "+player+" "+state+" "+ite+" x"+num+", $"+Util.format(price)+"\n");
			bw.close();
			fw.close();
		} catch (IOException e) {
			iIC.getInstance().getLogger().info("gged!");
			e.printStackTrace();
		}
	}
	/**
	 * 加载config
	 */
	public static void loadConfig() {
		fly_cost = iIC.config.getInt("fly-cost");
	}
}
