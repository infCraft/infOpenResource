package org.time.iic.gui;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryHelper {
	public static void drawInventory(Inventory inv, int[] slot,@Nullable ItemStack item) {
		for (int i: slot) inv.setItem(i, item);
	}
	public static void removeItem(Inventory inv, int... slot) {
		for (int i: slot) inv.setItem(i, null);
	}
	//获取玩家背包内相应物品的个数
	public static int getPlayerInventoryItemAmount(Player p, ItemStack item) {
		int count = 0;
		for (ItemStack it: p.getInventory().getContents()) {
			if (it == null) continue;
			if (item.isSimilar(it)) count+=it.getAmount();
		}
		return count;
	}
	//必须先判断
	//移除玩家背包内相应个数的物品
	public static void removePlayerInventoryItem(Player p, ItemStack item, int amount) {
		for (ItemStack it: p.getInventory().getContents()) {
			if (it == null) continue;
			if (item.isSimilar(it)) {
				if (it.getAmount()>=amount) {
					it.setAmount(it.getAmount()-amount);
					break;
				}
				amount-=it.getAmount();
				it.setAmount(0);
			}
		}
	}
	//获取空余位置
	public static int getIdleAmount(Player p, ItemStack item) {
		int count = 0;
		for (ItemStack it: p.getInventory().getStorageContents()) {
			int maxstack = item.getMaxStackSize();
			if (it == null) count += maxstack;
			if (item.isSimilar(it)) count += maxstack-it.getAmount();
		}
		return count;
	}
}
