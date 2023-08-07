package org.time.iic.gui.shop;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.time.iic.util.Util;

import com.google.common.collect.Lists;

public class SellItem extends ShopItem {
	private String name;
	private List<String> lore;
	private Map<Enchantment,Integer> enchants;
	private List<String> commands;
	public SellItem(Material material, double price) {
		this(material, price, null, null, null, null);
	}
	public SellItem(Material material, double price, String name, List<String> lore, Map<Enchantment,Integer> enchants) {
		this(material, price, name, lore, enchants, null);
	}
	public SellItem(Material material, double price, String name, List<String> lore, Map<Enchantment,Integer> enchants, List<String> commands) {
		super(material, price);
		this.name = name;
		this.lore = lore;
		this.enchants = enchants;
		this.commands = commands;
	}
	@Override
	public ItemStack getInventoryItem(String player) {
		ItemStack item = super.getItem().clone();
		if (item == null) return item;
		ItemMeta meta = item.getItemMeta();
		if (name != null) meta.setDisplayName(name);
		List<String> lore = Lists.newArrayList();
		if (this.lore != null) lore.addAll(this.lore);
		lore.add("");
		lore.add("§d出售价格 §7>> §e"+Util.format(getPrice(player))+" 元");
		lore.add("§a左键买入1个 §7| §a右键买入64个 §7| §a左键+shift填满背包");
		if (enchants!=null) item.addEnchantments(enchants);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	@Nullable
	public List<String> getCommands() {
		return commands;
	}
	public double getPrice(String player) {
		return Util.getSellMoney(price, getCount(player));
	}
}
