package org.time.iic.gui.shop;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.time.iic.util.Util;

public abstract class ShopItem {
	private Material material;
	protected double price;
	//买卖次数
	protected Map<String,Integer> map = new HashMap<>();


	public ShopItem(Material material, double price) {
		this.material = material;
		this.price = price;
	}
	//获取gui内的物品
	public abstract ItemStack getInventoryItem(String player);
	//获取普通的物品
	public ItemStack getItem() {
		return new ItemStack(material);
	}
	public Material getMaterial() {
		return material;
	}
	public double getPrice(String player) {
		return Util.getBuyMoney(price, getCount(player));
	}
	//卖出和买入的时候进行的加减操作
	public void addCount(String name, int num) {
		if (map.containsKey(name)) {
			map.replace(name, map.get(name)+num);
			return;
		}
		map.put(name, num);
	}
	public void setCount(String name, int num) {
		if (map.containsKey(name)) {
			map.replace(name, num);
			return;
		}
		map.put(name, num);
	}
	public int getCount(String name) {
		return map.containsKey(name)?map.get(name):0;
	}

	public Map<String, Integer> getMap() {
		return map;
	}
}
