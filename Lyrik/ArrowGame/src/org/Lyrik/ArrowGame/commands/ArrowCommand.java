package org.Lyrik.ArrowGame.commands;

import org.Lyrik.ArrowGame.ArrowGame;
import org.Lyrik.ArrowGame.ArrowGameMain;
import org.Lyrik.ArrowGame.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ArrowCommand implements CommandExecutor{
	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender sender,Command command,String label,String[] args) {
		if(label.equalsIgnoreCase("arrow")) {
			if( args.length == 0 || ( args.length == 1 && args[0].equalsIgnoreCase("help") ) ) {
				sender.sendMessage("");
                sender.sendMessage("§f========== §bArrowGame §f==========");
                sender.sendMessage("§6/arrow help §e打开此帮助");
                if (sender.isOp()) {
                    sender.sendMessage("§6/arrow start、begin [时间(s)] [场地大小(m)] §e在以当前位置为中心的指定范围内开启一场弓箭大战");
                    sender.sendMessage("§6/arrow stop §e停止本次弓箭大战");
                    sender.sendMessage("§6/arrow pause §e暂停本次弓箭大战");
                    sender.sendMessage("§6/arrow resume §e继续本次弓箭大战");
                    sender.sendMessage("§6/arrow reload §e重载插件(不会终止已开启的弓箭大战)");
                }
                sender.sendMessage("§6/arrow join §e加入进行中的弓箭大战");
                return true;
			}
			if( args.length == 1 ) {
				if (args[0].equalsIgnoreCase("stop")) {
		            if (!sender.isOp()) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你没有权限使用此指令！");
		                return true;
		            }
		            if ( ArrowGameMain.arrowgame == null ) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c没有正在进行中的比赛！");
		            	return true;
		            }
		            sender.sendMessage("§f[§bArrowGame§f] §c比赛已经停止！");
		            ArrowGameMain.arrowgame.stop();
		            return true;
				}
				if (args[0].equalsIgnoreCase("pause")) {
		            if (!sender.isOp()) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你没有权限使用此指令！");
		                return true;
		            }
		            if ( ArrowGameMain.arrowgame == null ) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c没有正在进行中的比赛！");
		            	return true;
		            }
		            Bukkit.broadcastMessage("§f[§bArrowGame§f] §c比赛暂停！");
		            ArrowGameMain.arrowgame.setPause();
		            return true;
				}
				if (args[0].equalsIgnoreCase("resume")||args[0].equalsIgnoreCase("continue")) {
		            if (!sender.isOp()) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你没有权限使用此指令！");
		                return true;
		            }
		            if ( ArrowGameMain.arrowgame == null ) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c没有正在进行中的比赛！");
		            	return true;
		            }
		            Bukkit.broadcastMessage("§f[§bArrowGame§f] §c比赛继续！");
		            ArrowGameMain.arrowgame.setResume();
		            return true;
				}
				if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("join")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§f[§bArrowGame§f] §c仅玩家操作！");
						return true;
					}
					if ( ArrowGameMain.arrowgame == null ) {
						sender.sendMessage("§f[§bArrowGame§f] §c没有正在报名中的比赛！");
		            	return true;
		            }
		            if ( ArrowGameMain.arrowgame != null) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你已经加入弓箭大战！");
		            	ArrowGameMain.arrowgame.addPlayer((Player)sender, 0);
		                return true;
		            }
		            return false;
				}
				if (args[0].equalsIgnoreCase("leave") || args[0].equalsIgnoreCase("quit")) {
					if(!(sender instanceof Player)) {
						sender.sendMessage("§f[§bArrowGame§f] §c仅玩家操作！");
						return true;
					}
					if ( !ArrowGameMain.arrowgame.containPlayer((Player)sender) ) {
						sender.sendMessage("§f[§bArrowGame§f] §c你不在比赛中！");
		            	return true;
		            }
		            if ( ArrowGameMain.arrowgame.containPlayer((Player)sender) ) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你已经退出弓箭大战！");
		            	ArrowGameMain.arrowgame.delPlayer((Player)sender);
		                return true;
		            }
		            return false;
				}
				if (args[0].equalsIgnoreCase("reload")) {
		            if (!sender.isOp()) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你没有权限使用此指令！");
		                return true;
		            }
		            ArrowGameMain.load();
		            sender.sendMessage("§f[§bArrowGame§f] §c重新加载完成！");
		            sender.sendMessage("§f[§bArrowGame§f] §c准备时长："+ConfigUtils.readyTime+" 排行间隔："+ConfigUtils.boardTime);
		            sender.sendMessage("§f[§bArrowGame§f] §c击杀得分："+ConfigUtils.kill+" 连杀加分："+ConfigUtils.combo+" 死亡扣分："+ConfigUtils.death);
		            return true;
				}
				return false;
			}
			if ( args.length == 3 ) {
				if (args[0].equalsIgnoreCase("start")||args[0].equalsIgnoreCase("begin")) {
					if (!sender.isOp()) {
		            	sender.sendMessage("§f[§bArrowGame§f] §c你没有权限使用此指令！");
		                return true;
		            }
					if(!(sender instanceof Player)) {
						sender.sendMessage("§f[§bArrowGame§f] §c仅玩家操作！");
						return true;
					}
					if( ArrowGameMain.arrowgame != null ) {	
						sender.sendMessage("§f[§bArrowGame§f] §c有正在进行中的比赛！");
						return true;
					}
					sender.sendMessage("§f[§bArrowGame§f] §c比赛开始！");
					Bukkit.broadcastMessage("§f[§bArrowGame§f] §c弓箭大战开始！使用/arrow add加入！");
					ArrowGameMain.arrowgame = new ArrowGame(Integer.parseInt(args[1]),Integer.parseInt(args[2]));
					ArrowGameMain.arrowgame.initialize((Player)sender);
					return true;
				}
			}
			return false;
        }
		return false;
	}
}
