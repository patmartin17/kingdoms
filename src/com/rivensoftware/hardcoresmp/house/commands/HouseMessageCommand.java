package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("house|h")
public class HouseMessageCommand extends HouseCommand
{

	@Subcommand("message")
	public void messageCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		
		if (args.length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h msg &3<message>"));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		
		if (house == null) 
		{
			player.sendMessage(MessageTool.color("&cYou must belong to a house to use this chat mode!"));
			return;
		} 
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < (args).length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 
		
		String message = sb.toString().trim();
		house.sendMessage(MessageTool.color("&2(Team) %PLAYER%:&e %MESSAGE%").replace("%PLAYER%", player.getName()).replace("%MESSAGE%", message).replace("%HOUSE%", house.getHouseName()));
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
			sender.sendMessage(MessageTool.color("&7You must be a player to execute this command."));
	}

}
