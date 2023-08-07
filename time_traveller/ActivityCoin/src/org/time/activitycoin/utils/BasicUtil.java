package org.time.activitycoin.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.attribute.AttributeModifier.Operation;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_16_R3.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import com.google.common.collect.Lists;

import net.minecraft.server.v1_16_R3.NBTTagCompound;

public class BasicUtil {
	//加载文件
	@NotNull
	public static YamlConfiguration loadFile(Plugin p, String file) {
		File f = new File(p.getDataFolder(), file);
		if (!f.exists()) createFile(p, f);
		p.getLogger().info("§a成功加载 §e"+f.getName());
		return YamlConfiguration.loadConfiguration(f);
	}
	//创建文件
	public static void createFile(Plugin p, File f) {
		p.getLogger().info("§c未找到 §e"+f.getName()+" §c正在重新创建...");
		p.saveResource(f.getName(), true);
	}
	//获取消耗的时间
	public static long getUsedTime(long start) {
		return System.currentTimeMillis()-start;
	}
	//替换某个lore里面的所有的某个字符
	public static List<String> changeLore(List<String> lore,String regax,String replacement) {
		List<String> lore2 = Lists.newArrayList();
		for (int i=0;i<lore.size();i++) {
			lore2.add(lore.get(i).replaceAll(regax, replacement));
		}
		return lore2;
	}
	//给一个物品加附魔
	public static ItemStack addEnchantmentToItem(ItemStack item, Enchantment enchant, int level) {
		ItemMeta meta = item.getItemMeta();
		meta.addEnchant(enchant, level, true);
		item.setItemMeta(meta);
		return item;
	}
	//设置数量
	public static ItemStack setAmount(ItemStack item, int amount) {
		ItemStack item2 = item.clone();
		item2.setAmount(amount);
		return item2;
	}
	//设置名称
	public static ItemStack setName(ItemStack item, String name) {
		ItemStack item2 = item.clone();
		ItemMeta meta = item2.getItemMeta();
		meta.setDisplayName(name);
		item2.setItemMeta(meta);
		return item2;
	}
	//创建新的物品
	public static ItemStack createItem(Material material,String name) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore, boolean unbreakable) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,List<String> lore,int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		if (!lore.isEmpty()) meta.setLore(lore);
		item.setItemMeta(meta);
		if (amount>64) amount = 64;
		item.setAmount(amount);
		return item;
	}
	public static ItemStack createItem(Material material, String name, List<String> lore, List<Enchantment> enchantment, List<Integer> level) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		for (int i=0;i<enchantment.size();i++) meta.addEnchant(enchantment.get(i), level.get(i), true);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material, String name, List<String> lore, List<Enchantment> enchantment, List<Integer> level, boolean unbreakable) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		for (int i=0;i<enchantment.size();i++) meta.addEnchant(enchantment.get(i), level.get(i), true);
		meta.setUnbreakable(unbreakable);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,String name,Enchantment enchantment,int level) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore,Enchantment enchantment,int level) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore,List<Enchantment> enchantment,HashMap<Enchantment,Integer> level) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		if (!enchantment.isEmpty()&&!level.isEmpty()) {
			for (Enchantment en:enchantment) {
				meta.addEnchant(en, level.get(en), true);
			}
		}
		item.setItemMeta(meta);
		return item;
	}
	public static ItemStack createItem(Material material,String name, int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore, int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}
	public static ItemStack createItem(Material material, String name, List<String> lore, List<Enchantment> enchantment, List<Integer> level, int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		for (int i=0;i<enchantment.size();i++) meta.addEnchant(enchantment.get(i), level.get(i), true);
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}
	public static ItemStack createItem(Material material,String name,Enchantment enchantment,int level, int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore,Enchantment enchantment,int level, int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		meta.addEnchant(enchantment, level, true);
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}
	public static ItemStack createItem(Material material,String name,List<String> lore,List<Enchantment> enchantment,HashMap<Enchantment,Integer> level, int amount) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		if (!enchantment.isEmpty()&&!level.isEmpty()) {
			for (Enchantment en:enchantment) {
				meta.addEnchant(en, level.get(en), true);
			}
		}
		item.setItemMeta(meta);
		item.setAmount(amount);
		return item;
	}
	/*
	 * 创建材质物品
	 * 但注意sfitem不可以这样使用！
	 */
	public static ItemStack createTextureItem(Material material,String name,List<String> lore, int customModelData) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		if (!lore.isEmpty()) meta.setLore(lore);
		meta.setCustomModelData(customModelData);
		item.setItemMeta(meta);
		return item;
	}
	//创建剑
	//attackspeed是从4开始的，要减！
	public static ItemStack createSword(Material material,String name,List<String> lore, double damage, double attackSpeed) {
		return createSword(material, name, lore, damage, attackSpeed, false);
	}
	public static ItemStack createSword(Material material,String name,List<String> lore, double damage, double attackSpeed, boolean unbreakable) {
		return createSword(material, name, lore, damage, attackSpeed, unbreakable, null, null);
	}
	public static ItemStack createSword(Material material,String name,List<String> lore, double damage, double attackSpeed, boolean unbreakable, List<Enchantment> enchantment, List<Integer> level) {
		ItemStack item = new ItemStack(material);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_DAMAGE, new AttributeModifier(UUID.randomUUID(), "awa1", damage, Operation.ADD_NUMBER, EquipmentSlot.HAND));
		meta.addAttributeModifier(Attribute.GENERIC_ATTACK_SPEED, new AttributeModifier(UUID.randomUUID(), "awa2", attackSpeed, Operation.ADD_NUMBER, EquipmentSlot.HAND));
		meta.setUnbreakable(unbreakable);
		if (enchantment != null) for (int i=0;i<enchantment.size();i++) meta.addEnchant(enchantment.get(i), level.get(i), true);
		item.setItemMeta(meta);
		return item;
	}
	//物品栏中是否有匹配的物品（不管数量多少）
	@Deprecated
	public static boolean hasItemWithoutAmount(Inventory inv, ItemStack item) {
		for (ItemStack item2: inv.getContents()) {
			if (item2 == null||item2.getType() == Material.AIR) continue;
			if (item.clone().asOne().equals(item2.clone().asOne())) return true;
		}
		return false;
	}
	//找到物品栏中匹配的物品（不管数量）
	@Deprecated
	public static ItemStack getSameItemWithoutAmount(Inventory inv, ItemStack item) {
		for (ItemStack item2: inv.getContents()) {
			if (item2 == null||item2.getType() == Material.AIR) continue;
			if (item.clone().asOne().equals(item2.clone().asOne())) return item2;
		}
		return null;
	}
	//复制文件
	public static void move(File src, File des) throws IOException {
		FileInputStream fis = new FileInputStream(src);
		FileOutputStream fos = new FileOutputStream(des);
		int len = 0;
		byte[] buffer = new byte[1024];
		while ((len=fis.read(buffer))!=-1) {
			fos.write(buffer, 0, len);
		}
		fis.close();
		fos.close();
	}
	//获取NBT
	public static NBTTagCompound getNBTTagCompound(ItemStack item) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		if (!nmsItem.hasTag()) return null;
		return nmsItem.getTag();
	}
	//设置NBT
	public static ItemStack setNBT(ItemStack item, String dataName, Object data) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.hasTag()?nmsItem.getTag():new NBTTagCompound();
		if (data instanceof Integer) compound.setInt(dataName, (int) data);
		if (data instanceof String) compound.setString(dataName, (String) data);
		nmsItem.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}
	//删除NBT
	public static ItemStack removeNBT(ItemStack item, String dataName) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.getTag();
		compound.remove(dataName);
		nmsItem.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}
	//批量设置NBT
	public static ItemStack setNBT(ItemStack item, HashMap<String,Object> map) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.hasTag()?nmsItem.getTag():new NBTTagCompound();
		for (String key: map.keySet()) {
			if (map.get(key) instanceof Integer) compound.setInt(key, (int) map.get(key));
			if (map.get(key) instanceof String) compound.setString(key, (String) map.get(key));
		}
		nmsItem.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}
	//批量删除NBT
	public static ItemStack removeNBT(ItemStack item, List<String> list) {
		net.minecraft.server.v1_16_R3.ItemStack nmsItem = CraftItemStack.asNMSCopy(item);
		NBTTagCompound compound = nmsItem.hasTag()?nmsItem.getTag():new NBTTagCompound();
		for (String key: list) {
			compound.remove(key);
		}
		nmsItem.setTag(compound);
		return CraftItemStack.asBukkitCopy(nmsItem);
	}
	//获取随机长度的String
	public static String getRandomString(int minLength, int maxLength) {
	    String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	    Random random = new Random();
	    int length = random.nextInt(maxLength) % (maxLength - minLength + 1) + minLength;
	    StringBuffer sb = new StringBuffer();
	    for (int i = 0; i < length; i++) {
	    	int number = random.nextInt(62);
	    	sb.append(str.charAt(number));
	    }
	    return sb.toString();
	}
	//直接添加lore
	public static ItemStack addLore(ItemStack item, String content) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.hasLore()?meta.getLore():Lists.newArrayList();
		lore.add(content);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	//直接替换物品lore
	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	//更改物品name和lore
	public static ItemStack setItem(ItemStack item, String name, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}
	//获取一定范围内的随机数(min-(max-1))
	public static int getRandomNumber(int min, int max) {
		return (new Random()).nextInt(max-min)+min;
	}
	//获取一定范围内的随机数(min~max)double类型
	public static double getRandomDouble(double min, double max) {
		return Math.random()*(max-min)+min;
	}
	//向配置文件中写入数据
	public static void write(Plugin p, String fileName, YamlConfiguration y, String path, Object value) {
		y.set(path, value);
		try {
			y.save(new File(p.getDataFolder(), fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	//往文件里追加东西
	public static void write(File f, String... msg) throws IOException {
		if (!f.exists()) f.createNewFile();
		FileWriter fw = new FileWriter(f, true);
		BufferedWriter bw = new BufferedWriter(fw, 1024);
		for (String ms: msg) {
			bw.write(ms);
			bw.write("\n");
		}
		bw.close();
	}
	//把数字固定在0-15(区块生成矿物)
	public static int get0to15(int num) {
		while (num<0||num>15) {
			if (num<0) num+=16;
			if (num>15) num-=16;
		}
		return num;
	}
	//冒泡排序(大到小)
	public static void sort(List<Integer> list) {
		for (int i = 0; i < list.size(); i++) {
			for (int j = 0; j < list.size() - 1; j++) {
				if (list.get(j) < list.get(j + 1)) {
					int t = list.get(j);
					list.set(j, list.get(j + 1));
					list.set(j + 1, t);
				}
			}
		}
	}
	
	/**
	 * 获取对应玩家的头颅
	 * @param player 玩家的UUID
	 * @return 该玩家的头颅
	 */
	@NotNull
	public static ItemStack getPlayerHead(@NotNull UUID player) {
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta) item.getItemMeta();
		meta.setOwningPlayer(Bukkit.getOfflinePlayer(player));
		item.setItemMeta(meta);
		return item;
	}
}
