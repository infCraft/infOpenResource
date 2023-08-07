package org.Lyrik.ArrowGame;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class ArrowTabCompleter implements TabCompleter {
	@Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (command.getName().equalsIgnoreCase("arrow")) {
            List<String> completions = new ArrayList<>();
            if (args.length == 1) {
                completions.add("start");
                completions.add("join");
                completions.add("pause");
                completions.add("resume");
                completions.add("leave");
                completions.add("reload");
                return completions;
            } else if ( args.length==2 && args[0]=="start" ) {
            	completions.add("[时间(s)]");
            	return completions;
            } else if ( args.length==3 && args[0]=="start" ) {
            	completions.add("[场地大小(m)]");
            	return completions;
            }
        }
        return null;
    }
}
