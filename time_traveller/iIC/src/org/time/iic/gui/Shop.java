package org.time.iic.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.time.iic.iIC;
import org.time.iic.gui.shop.BuyItem;
import org.time.iic.gui.shop.SellItem;
import org.time.iic.util.Util;

import com.google.common.collect.Lists;

public class Shop implements Listener {
	public static String name;
	public static double tax;
	public static String taxplayer;
	public static List<SellItem> sell = Lists.newArrayList();
	public static List<BuyItem> buy = Lists.newArrayList();
	public static void loadShop(FileConfiguration f) {
		sell.clear();
		buy.clear();
		name = f.getString("name").replaceAll("&", "§");
		tax = f.getDouble("tax");
		taxplayer = f.getString("tax-player");
		for (String key: f.getConfigurationSection("buy").getKeys(false)) {
			buy.add(new BuyItem(Material.getMaterial(f.getString("buy."+key+".material").toUpperCase()), f.getDouble("buy."+key+".price")));
		}
		for (String key: f.getConfigurationSection("sell").getKeys(false)) {
			String name = f.contains("sell."+key+".name")?f.getString("sell."+key+".name").replaceAll("&", "§"):null;
			List<String> lore = f.contains("sell."+key+".lore")?Util.changeLore(f.getStringList("sell."+key+".lore"), "&", "§"):null;
			List<String> commands = f.getStringList("sell."+key+".commands");
			Map<Enchantment,Integer> ench = new HashMap<>();
			if (f.contains("sell."+key+".enchantments")) {
				List<String> en = f.getStringList("sell."+key+".enchantments");
				for (int i=0;i<en.size();i++) ench.put(Enchantment.getByName(en.get(i).split(",")[0].toUpperCase()), Integer.parseInt(en.get(i).split(",")[1]));
			}
			sell.add(new SellItem(Material.getMaterial(f.getString("sell."+key+".material").toUpperCase()), f.getDouble("sell."+key+".price"), name, lore, ench, commands));
		}
 	}
	public static Inventory getInventory(String player, boolean isSell, int page) {
		if (name == null) return null;
		Inventory inv = Bukkit.createInventory(null, 54, name);
		InventoryHelper.drawInventory(inv, new int[] {45,47,48,50,51,53}, Util.createItem(Material.GRAY_STAINED_GLASS_PANE, page+""));
		int total = getTotalPage(isSell);
		if (total == page) inv.setItem(52, Util.createItem(Material.BLACK_STAINED_GLASS_PANE, "§6下一页", Lists.newArrayList("", "§e第 "+page+" 页，共 "+total+" 页")));
		else inv.setItem(52, Util.createItem(Material.GREEN_STAINED_GLASS_PANE, "§6下一页", Lists.newArrayList("", "§e第 "+page+" 页，共 "+total+" 页")));
		
		if (page == 1) inv.setItem(46, Util.createItem(Material.BLACK_STAINED_GLASS_PANE, "§6上一页", Lists.newArrayList("", "§e第 "+page+" 页，共 "+total+" 页")));
		else inv.setItem(46, Util.createItem(Material.GREEN_STAINED_GLASS_PANE, "§6上一页", Lists.newArrayList("", "§e第 "+page+" 页，共 "+total+" 页")));
		
		if (isSell) {
			for (int i=(page-1)*45;i<sell.size();i++) {
				int num = i-(page-1)*45;
				if (num > 44) break;
				inv.setItem(num, sell.get(i).getInventoryItem(player));
			}
			inv.setItem(49, Util.createItem(Material.PINK_CONCRETE, "§d切换到收购商店"));
		}
		else {
			for (int i=(page-1)*45;i<buy.size();i++) {
				int num = i-(page-1)*45;
				if (num > 44) break;
				inv.setItem(i-(page-1)*45, buy.get(i).getInventoryItem(player));
			}
			inv.setItem(49, Util.createItem(Material.BLUE_CONCRETE, "§b切换到出售商店"));
		}
		return inv;
	}
	protected static int getTotalPage(boolean isSell) {
		if (isSell) return (sell.size()-1)/45+1;
		return (buy.size()-1)/45+1;
	}
	@EventHandler
	public void onClick(InventoryClickEvent e) {
		InventoryView view = e.getView();
		if (!view.getTitle().equalsIgnoreCase(name)) return;
		e.setCancelled(true);
		Inventory inv = e.getInventory();
		if (!inv.equals(e.getClickedInventory())) return;
		int slot = e.getSlot();
		if (inv.getItem(slot) == null) return;
		boolean isSell = false;
		if (inv.getItem(49).getType() == Material.PINK_CONCRETE) isSell = true;
		int page = Integer.parseInt(inv.getItem(48).getItemMeta().getDisplayName());
		Player p = (Player) e.getWhoClicked();
		//1.翻页判断
		if (slot == 46) {
			if (inv.getItem(slot).getType() == Material.BLACK_STAINED_GLASS_PANE) return;
			p.openInventory(getInventory(p.getName(), isSell, page-1));
			return;
		}
		if (slot == 52) {
			if (inv.getItem(slot).getType() == Material.BLACK_STAINED_GLASS_PANE) return;
			p.openInventory(getInventory(p.getName(), isSell, page+1));
			return;
		}
		//2.切换判断
		if (slot == 49) {
			if (isSell) p.openInventory(getInventory(p.getName(), !isSell, 1));
			else p.openInventory(getInventory(p.getName(), !isSell, 1));
			return;
		}
		if (slot > 44) return;
		/*
		 * pickup_all leftclick
		 * pickup_half rightclick
		 * move_to_other_inventory shift+click
		 */
		int num = slot+(page-1)*45;
		InventoryAction action = e.getAction();
		//3.收购物品！
		if (!isSell) {
			int need = 0;
			int amount = InventoryHelper.getPlayerInventoryItemAmount(p, buy.get(num).getItem());
			if (action == InventoryAction.PICKUP_ALL) need = 1;
			if (action == InventoryAction.PICKUP_HALF) need = 64;
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) need = amount;
			if (amount<need) {
				p.sendMessage(iIC.getPrefix()+"§c你背包内没有足够的物品！");
				return;
			}
			InventoryHelper.removePlayerInventoryItem(p, buy.get(num).getItem(), need);
			double price = 0;
			for (int i=0;i<need;i++) {
				price += buy.get(num).getPrice(p.getName());
				buy.get(num).addCount(p.getName(), 1);
				//iIC.getEconomy().depositPlayer(p, buy.get(num).getPrice(p.getName()));
			}
			if (need>0) {
				p.sendMessage(iIC.getPrefix()+"§a成功卖出 §e"+buy.get(num).getItem().getType().name().toLowerCase()+" §6x"+need);
				double get = price*(1-tax);
				iIC.getEconomy().depositPlayer(p, get);
				iIC.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(taxplayer), price*tax);
				p.sendMessage(iIC.getPrefix()+"§a你获得了 §e$"+Util.format(get)+" §a,交了 §e$"+Util.format(price*tax)+" §a的税.");
				Util.log(p.getName(), "sold", buy.get(num).getItem(), need, get);
			}
			inv.setItem(slot, buy.get(num).getInventoryItem(p.getName()));
			return;
		}
		else {
			if (sell.get(num).getCommands() != null&&sell.get(num).getCommands().size()>0) {
				if (iIC.getEconomy().getBalance(p)<sell.get(num).getPrice(p.getName())) {
					p.sendMessage(iIC.getPrefix()+"§c你没有足够的钱！");
					return;
				}
				iIC.getEconomy().withdrawPlayer(p, sell.get(num).getPrice(p.getName()));
				p.sendMessage(iIC.getPrefix()+"§a你花费了 §e$"+Util.format(sell.get(num).getPrice(p.getName())));
				Util.log(p.getName(), "activated", buy.get(num).getItem(), 1, sell.get(num).getPrice(p.getName()));
				p.sendMessage(iIC.getPrefix()+"§a成功激活指令");
				for (int i=0;i<sell.get(num).getCommands().size();i++) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), sell.get(num).getCommands().get(i).replaceAll("&", "§").replaceAll("%player%", p.getName()));
				return;
			}
			int need = 0;
			int idle = InventoryHelper.getIdleAmount(p, sell.get(num).getItem());
			if (action == InventoryAction.PICKUP_ALL) need = 1;
			if (action == InventoryAction.PICKUP_HALF) need = 64;
			if (action == InventoryAction.MOVE_TO_OTHER_INVENTORY) need = idle;
			int count = 0;
			double price = 0;
			for (int i=0;i<need;i++) {
				if (iIC.getEconomy().getBalance(p)<sell.get(num).getPrice(p.getName())) {
					p.sendMessage(iIC.getPrefix()+"§c你没有足够的钱！");
					break;
				}
				if (idle<=0) {
					p.sendMessage(iIC.getPrefix()+"§c你的背包没有空间了！");
					break;
				}
				iIC.getEconomy().withdrawPlayer(p, sell.get(num).getPrice(p.getName()));
				price += sell.get(num).getPrice(p.getName());
				sell.get(num).addCount(p.getName(), 1);
				p.getInventory().addItem(sell.get(num).getItem());
				idle--;
				count++;
			}
			if (count>0) {
				p.sendMessage(iIC.getPrefix()+"§a成功买入 §e"+sell.get(num).getItem().getType().name().toLowerCase()+" §6x"+count);
				p.sendMessage(iIC.getPrefix()+"§a你花费了 §e$"+Util.format(price));
				Util.log(p.getName(), "bought", buy.get(num).getItem(), count, price);
			}
			inv.setItem(slot, sell.get(num).getInventoryItem(p.getName()));
		}
	}
}
