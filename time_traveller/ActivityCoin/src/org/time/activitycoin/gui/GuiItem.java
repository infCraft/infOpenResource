package org.time.activitycoin.gui;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.time.activitycoin.utils.Utils;

public class GuiItem {
	
	private String id;
	private ItemStack item;
	private int price;
	private List<String> commands;
	
	/**
	 * 放在GUI内的物品
	 * @param id 唯一ID
	 * @param item 展示物品
	 * @param price 价格，即消耗的活动代币
	 * @param commands 执行的指令
	 */
	public GuiItem(String id, ItemStack item, int price, List<String> commands) {
		this.id = id;
		this.item = item;
		this.price = price;
		this.commands = commands;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
	}
	public int getPrice() {
		return price;
	}
	public void setPrice(int price) {
		this.price = price;
	}
	public List<String> getCommands() {
		return commands;
	}
	public void setCommands(List<String> commands) {
		this.commands = commands;
	}
	/**
	 * 以控制台身份执行指令
	 * @param player 点击GUI的玩家名称
	 */
	public void runCommands(String player) {
		for (int i=0;i<commands.size();i++) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.replaceAll(commands.get(i)).replaceAll("%player%", player));
	}
}
