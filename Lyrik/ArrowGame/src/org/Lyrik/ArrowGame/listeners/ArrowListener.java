package org.Lyrik.ArrowGame.listeners;

import org.Lyrik.ArrowGame.ArrowGameMain;
import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

public class ArrowListener implements Listener {
	private boolean PlayerInGame(Player player) {
		return ArrowGameMain.arrowgame.containPlayer(player);
	}
	private boolean IsOutside(Location location) {
		return !ArrowGameMain.arrowgame.isInside(location);
	}
	@EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player && event.getEntity() instanceof Player) {
                Player shooter = (Player) arrow.getShooter();
                Player target = (Player) event.getEntity();
                if(ArrowGameMain.arrowgame.isPause()) return;
                if(!PlayerInGame(shooter)||!PlayerInGame(target)) return; 
                if(!shooter.equals(target)) {
                	shooter.sendMessage("§f[§bArrowGame§f] §c 你命中了 "+target.getName());
                	//shooter.sendActionBar("§c 你命中了 "+target.getName());
                	/*TODO*/
                }
                if (target.getHealth() - event.getFinalDamage() <= 0) {
                	shooter.sendMessage("§f[§bArrowGame§f] §c 你击杀了 "+target.getName());
                	ArrowGameMain.arrowgame.PlayerKillPlayer((Player)shooter,(Player)target);
                }
            }
        }
    }
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event){
	    if(ArrowGameMain.arrowgame == null || !PlayerInGame(event.getPlayer())) return;
	    event.setRespawnLocation(ArrowGameMain.arrowgame.getLocation());
	}
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event){
	    if(ArrowGameMain.arrowgame == null || !PlayerInGame(event.getPlayer())) return;
	    Location to = event.getTo();
		if(IsOutside(to)) {
			event.setCancelled(true);
			event.getPlayer().sendMessage("§f[§bArrowGame§f] §c不能离开比赛范围");
		}
	}
}
