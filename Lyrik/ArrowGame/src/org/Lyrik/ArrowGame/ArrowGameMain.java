package org.Lyrik.ArrowGame;

import org.Lyrik.ArrowGame.commands.ArrowCommand;
import org.Lyrik.ArrowGame.listeners.ArrowListener;
import org.Lyrik.ArrowGame.utils.ConfigUtils;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ArrowGameMain extends JavaPlugin {
	private static ArrowGameMain instance;
    public static ArrowGameMain getInstance() {
        return instance;
    }
    public static ArrowGame arrowgame;
    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getPluginCommand("arrow").setExecutor(new ArrowCommand());
        getCommand("arrow").setTabCompleter(new ArrowTabCompleter());
        Bukkit.getPluginManager().registerEvents(new ArrowListener(), this);
        load();
    }
    @Override
    public void onDisable() {
    	if(arrowgame!=null) {
    		arrowgame.stop();
    	}
    }
    public static void load() {
        ConfigUtils.loadConfig("config.yml");
    }
}
