package org.time.iic;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.time.iic.util.Util;

public class CostTimer extends BukkitRunnable {
	@Override
	public void run() {
		for (String player: Util.players.keySet()) {
			Player p = Bukkit.getPlayer(player);
			if (p == null) continue;
			double money = iIC.getEconomy().getBalance(p);
			if (money<Util.fly_cost) {
				p.setAllowFlight(false);
				// 这次飞行的秒数
				long time = (System.currentTimeMillis()-Util.players.get(p.getName()))/1000;
				p.sendMessage(iIC.getPrefix()+"§c你的余额已经不足，已经自动为你关闭飞行模式！");
				p.sendMessage(iIC.getPrefix()+"§a在这次计费中，你一共飞行了 §e"+ time+"s §a共计花费 §e"+Util.format(time*Util.fly_cost)+" 金币.");
				Util.players.remove(p.getName());
				continue;
			}
			iIC.getEconomy().withdrawPlayer(p, Util.fly_cost);
		}
	}
}
