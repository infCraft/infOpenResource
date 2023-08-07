package org.time.iic.gui.shop;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.time.iic.util.Util;

import com.google.common.collect.Lists;

public class BuyItem extends ShopItem {
	public BuyItem(Material material, double price) {
		super(material, price);
	}
	@Override
	public ItemStack getInventoryItem(String player) {
		ItemStack item = super.getItem().clone();
		if (item == null) return item;
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore()?meta.getLore():Lists.newArrayList();
		lore.add("");
		lore.add("§d收购价格 §7>> §e"+Util.format(getPrice(player))+" 元");
		lore.add("§a左键卖出1个 §7| §a右键卖出64个 §7| §a左键+shift卖出背包全部");
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
}
