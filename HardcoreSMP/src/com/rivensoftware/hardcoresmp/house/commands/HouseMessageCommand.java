package com.rivensoftware.hardcoresmp.house.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseMessageCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("message")
	public void messageCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		
		if (args.length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.MESSAGE.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		
		if (house == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.MESSAGE.MUST_BELONG_TO_HOUSE")));
			return;
		} 
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < (args).length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 
		
		String message = sb.toString().trim();
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.MESSAGE.HOUSE_MESSAGE")).replace("%PLAYER%", player.getName()).replace("%MESSAGE%", message).replace("%HOUSE%", house.getHouseName()));
	}

	@Subcommand("msg")
	public void msgCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
		{
			StringBuilder sb = new StringBuilder();
			
			for (int i = 0; i < (getOrigArgs()).length; i++)
				sb.append(getOrigArgs()[i]).append(" "); 
			
			String message = sb.toString().trim();
			
			Bukkit.dispatchCommand(player, "h message " + message);
		}
		else
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_PLAYER")));
	}

}
