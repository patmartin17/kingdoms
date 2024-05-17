package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@CommandAlias("house|h")
@Description("Main admin command.")
public class HouseCommand extends BaseCommand 
{
	private File file = new File(HardcoreSMP.getInstance().getDataFolder(), "/help.yml");
	private YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

	@Default
	public void defaultCommand(CommandSender sender)
	{	
		sendHelp(sender, 0);
	}
	
	@Subcommand("help")
	@Syntax("<page>")
	public void helpCommand(CommandSender sender, int page) 
	{
		sendHelp(sender, page);
	}
	
	private void sendHelp(CommandSender sender, int page)
	{
		List<List<String>> help = new ArrayList<List<String>>();
		
		for (String key : configuration.getConfigurationSection("house-help").getKeys(false))
			help.add(configuration.getStringList("house-help." + key)); 
		
		if (page > 0 && page <= help.size()) 
		{
			for (String msg : help.get(page - 1))
				sender.sendMessage(MessageTool.color(msg)); 
		} 
		else
		{
			for (String msg : help.get(0))
				sender.sendMessage(MessageTool.color(msg)); 	
		}
	}
}