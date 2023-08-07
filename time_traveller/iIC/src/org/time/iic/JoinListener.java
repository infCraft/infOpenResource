package org.time.iic;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.time.iic.util.Util;

public class JoinListener implements Listener {
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		p.setAllowFlight(false);
		Util.players.remove(p.getName());
	}
}
