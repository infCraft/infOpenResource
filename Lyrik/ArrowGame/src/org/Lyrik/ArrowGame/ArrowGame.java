package org.Lyrik.ArrowGame;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.Lyrik.ArrowGame.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

public class ArrowGame {
	private int period,readyPeriod;
	private int count,ready;
	private int size;
	private boolean pause;
	private List<Player> players;
    private List<Double> scores;
    private List<Integer> continuousKill;
    private BukkitRunnable runnable;
    private BossBar bar;
    private World world;
    private Map<Player, ItemStack[]> playerInventoryBackup = new HashMap<>();
	PotionEffect glowingEffect = new PotionEffect(PotionEffectType.GLOWING, Integer.MAX_VALUE, 0, false, false);
    public ArrowGame(int time,int size) {
		period = time;
		count = 0;
		ready = 0;
		this.size=size;
		readyPeriod=ConfigUtils.readyTime;
		players = new ArrayList<>();
	    scores = new ArrayList<>();
	    continuousKill = new ArrayList<>();
	    pause = false;
	    bar = Bukkit.createBossBar("§b弓箭大战", BarColor.YELLOW, BarStyle.SOLID);
        bar.setProgress(1);
        for (Player p: Bukkit.getOnlinePlayers()) bar.addPlayer(p); 
        runnable = new BukkitRunnable() {
        	@Override
        	public void run() {
        		if(count>=period) {
        			stop(); 
        			return;
        		}
        		if (pause) return;
        		if ( count!=0 && count%((int)ConfigUtils.boardTime)==0)
        			showScore();
                for (Player p: Bukkit.getOnlinePlayers()) if (!bar.getPlayers().contains(p)) bar.addPlayer(p);
        		if(ready<readyPeriod) {
        			int remain = readyPeriod-ready;
	                bar.setProgress((remain)*1.0/readyPeriod);
	                if (remain % 60 == 0) {
	                    bar.setTitle("§b弓箭大战报名时间 §f(剩余 §e"+(remain/60)+" §f分钟)");
	                }
	                else {
	                    if (remain/60 == 0) {
	                        bar.setTitle("§b弓箭大战报名时间 §f(剩余 §e"+(remain%60)+" §f秒)");
	                    }
	                    else {
	                        bar.setTitle("§b弓箭大战报名时间 §f(剩余 §e"+(remain/60)+" §f分 §e"+(remain%60)+" §f秒)");
	                    }
	                }
	                ready++;
        		} else {
	                int remain = period-count;
	                bar.setProgress((remain)*1.0/period);
	                if (remain % 60 == 0) {
	                    bar.setTitle("§b弓箭大战 §f(剩余 §e"+(remain/60)+" §f分钟)");
	                }
	                else {
	                    if (remain/60 == 0) {
	                        bar.setTitle("§b弓箭大战 §f(剩余 §e"+(remain%60)+" §f秒)");
	                    }
	                    else {
	                        bar.setTitle("§b弓箭大战 §f(剩余 §e"+(remain/60)+" §f分 §e"+(remain%60)+" §f秒)");
	                    }
	                }
	                count++;
        		}
        	}
        };
        runnable.runTaskTimer(ArrowGameMain.getInstance(), 0L, 20L);
	}
    private int X1,X2,Z1,Z2;
	@SuppressWarnings("deprecation")
	public void initialize(Player sender) {
		int x=(int)sender.getLocation().getX(),z=(int)sender.getLocation().getZ();
		world=sender.getWorld();
		X1=x-size; X2=x+size;
		Z1=z-size; Z2=z+size;
		Bukkit.broadcastMessage("§f[§bArrowGame§f] §c比赛区域范围: ("+X1+","+Z1+") ("+X2+","+Z2+")");
	}
	private boolean isPassable(Block block) {
        return block.getType() == Material.AIR;
    }
	private boolean isSafeLocation(Location location) {
        Block feetBlock = location.getBlock();
        Block headBlock = feetBlock.getRelative(0, 1, 0);
        if (!isPassable(feetBlock) || !isPassable(headBlock)) {
            return false;
        }
        Block groundBlock = feetBlock.getRelative(0, -1, 0);
        if (groundBlock.getType().isSolid()) {
            return false;
        }
        return true;
    }
    private synchronized void swap(int i,int j) {
    	double count = scores.get(j);   scores.set(j,scores.get(i));   scores.set(i,count);
    	Player player = players.get(j); players.set(j,players.get(i)); players.set(i,player);
    	Integer cKill = continuousKill.get(j);
    	continuousKill.set(j,continuousKill.get(i)); continuousKill.set(i,cKill);
    }
    //沿用传统的冒泡
    private void sort() {
        for (int i=1;i<scores.size();i++) {
            for (int j=0;j<scores.size()-i;j++) {
                if (scores.get(j)<scores.get(j+1)) {
                    swap(j,j+1);
                }
            }
        }
    }
	public Location getLocation() {
		double x,y,z;
		int cnt=0;
		Location location;
		do {
			x = (int)(X1+(X2-X1)*Math.random());
		    z = (int)(Z1+(Z2-Z1)*Math.random());
		    y = world.getHighestBlockYAt((int)x, (int)z);
		    location = new Location(world,x,y,z);
		    if( cnt++ >20) break;
		} while(!isSafeLocation(location));
		return location;
	}
	public void spawn(Player player) {
		player.teleport(getLocation());
	}
	@SuppressWarnings("unused")
	private void storeItems(Player player) {
		playerInventoryBackup.put(player, player.getInventory().getContents());
        player.getInventory().clear();
	}
	@SuppressWarnings("unused")
	private void giveItems(Player player) {
		List<ItemStack> itemsList = ConfigUtils.itemsList;
	    for(ItemStack itemStack:itemsList) {
	           player.getInventory().addItem(itemStack);
	    }
	}
	@SuppressWarnings("unused")
	private void restoreItems(Player player) {
		player.getInventory().clear();
		player.getInventory().setContents(playerInventoryBackup.get(player));
        playerInventoryBackup.remove(player);
	}
    @SuppressWarnings("deprecation")
	public void showScore() {
    	sort();
    	Bukkit.broadcastMessage("§f========== §6排行榜 §f==========");
        for (int i=0;i<scores.size();i++) {
        	if (i==0) Bukkit.broadcastMessage("§c1. "+players.get(i).getName()+" §7- §e"+new DecimalFormat("0.0#").format(scores.get(i)));
        	else if (i==1) Bukkit.broadcastMessage("§b2. "+players.get(i).getName()+" §7- §e"+new DecimalFormat("0.0#").format(scores.get(i)));
        	else if (i==2) Bukkit.broadcastMessage("§a3. "+players.get(i).getName()+" §7- §e"+new DecimalFormat("0.0#").format(scores.get(i)));	
        	else Bukkit.broadcastMessage("§f"+(i+1)+". "+players.get(i).getName()+" §7- §e"+new DecimalFormat("0.0#").format(scores.get(i)));
        }
    }
	public void setPause() { 
		pause=true; 
	}
	public void setResume() { 
		pause=false; 
	}
	public boolean isPause() { 
		return pause; 
	}
	public boolean inProgress() { 
		return count!=0; 
	}
	public boolean containPlayer(Player player) { 
		return players.contains(player); 
	}
	public boolean isInside(Location location) { 
		int x = (int)location.getX(), z= (int)location.getZ();
		return ( X1 <= x && x <= X2 )&&( Z1<=z && z<= Z2 );
	}
	public synchronized void addPlayer(Player player,double score) {
		storeItems(player);
		giveItems(player);
		players.add(player);
		scores.add(score);
		continuousKill.add(0); 
		spawn(player);
	}
	public synchronized void delPlayer(Player player) {
    	restoreItems(player);
		int index=players.indexOf(player);
		swap(index,players.size()-1);
		players.remove(index);
		scores.remove(index);
		continuousKill.remove(index); 
	}
	public void addPlayerScore(Player player,double score) {
		if(!containPlayer(player)) addPlayer(player,0);
		int index = players.indexOf(player);
		scores.set(index, scores.get(index)+score);
	}
	@SuppressWarnings("deprecation")
	public void PlayerKillPlayer(Player killer,Player victim) {
		if(isPause()) return;
		int index = players.indexOf(killer),cKill=continuousKill.get(index);
		double goal = ConfigUtils.kill+1.0*ConfigUtils.combo*cKill;
		if(cKill>2) {
			Bukkit.broadcastMessage("§f[§bArrowGame§f] §c"+killer.getName()+"击杀了"+victim.getName()+",获得"+goal+"分！"+(cKill+1)+"连击杀！");
	        killer.addPotionEffect(glowingEffect);
		} else
			Bukkit.broadcastMessage("§f[§bArrowGame§f] §c"+killer.getName()+"击杀了"+victim.getName()+",获得"+goal+"分！");
		addPlayerScore(killer,goal);
		continuousKill.set(index, continuousKill.get(index)+1);
		index = players.indexOf(victim);
		for (PotionEffect effect : victim.getActivePotionEffects())
            if (effect.getType() == PotionEffectType.GLOWING) {
            	victim.removePotionEffect(PotionEffectType.GLOWING);
            	break;
            }
		addPlayerScore(victim,ConfigUtils.death);
		continuousKill.set(index, 0);
	}
	@SuppressWarnings("deprecation")
	public void stop() {
		Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage(" §c弓箭大战结束！ ");
        Bukkit.broadcastMessage(" §c弓箭大战结束！ ");
        Bukkit.broadcastMessage(" §c弓箭大战结束！ ");
        Bukkit.broadcastMessage("");
        showScore();
        for(Player player : players) {
        	restoreItems(player);
        	for (PotionEffect effect : player.getActivePotionEffects())
                if (effect.getType() == PotionEffectType.GLOWING) {
                	player.removePotionEffect(PotionEffectType.GLOWING);
            }
        }
        bar.removeAll();
        runnable.cancel();
        ArrowGameMain.arrowgame = null;
	}
}
