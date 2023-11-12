package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseRenameCommand extends HouseCommand
{	
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("rename")
	public void renameCommand(CommandSender sender, String rename) 
	{
		House house;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.USAGE")));
			return;
		} 

		if (getOrigArgs().length >= 2 && player.hasPermission("kingdoms.admin")) 
		{
			if (House.getAnyByString(getOrigArgs()[0]) != null) 
			{
				house = House.getAnyByString(getOrigArgs()[0]);
			}
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NO_HOUSES_FOUND")).replace("%NAME%", rename));
				return;
			} 
		} 
		else 
		{
			house = Profile.getByUUID(player.getUniqueId()).getHouse();
			if (house == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.NOT_IN_HOUSE")));
				return;
			} 

			if (!house.getLord().equals(player.getUniqueId()) && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("ekko.system"))
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_LORD_OR_CAPTAIN")));
				return;
			} 
		} 

		StringBuilder sb = new StringBuilder();
		int start = 0;
		if (getOrigArgs().length >= 2 && player.hasPermission("kingdoms.admin"))
			start = 1; 

		for (int i = start; i < (getOrigArgs()).length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 

		rename = sb.toString().trim().replace(" ", "");
		if (rename.length() < plugin.getHouseConfig().getInt("HOUSE.HOUSE_NAME.MIN_CHARACTERS")) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.MINIMUM")));
			return;
		} 

		if (rename.length() > plugin.getHouseConfig().getInt("HOUSE.HOUSE_NAME.MAX_CHARACTERS")) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.MAXIMUM")));
			return;
		} 

		if (!StringUtils.isAlphanumeric(rename)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.ALPHANUMERIC")));
			return;
		} 

		for (String string : plugin.getHouseConfig().getStringList("HOUSE.HOUSE_NAME.BLOCKED_NAMES")) 
		{
			if (rename.contains(string)) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.BLACKLISTED")));
				return;
			} 
		} 

		House otherHouse = House.getByName(rename);
		if (otherHouse != null)
			if (otherHouse.equals(house)) 
			{
				if (otherHouse.getHouseName().equals(rename)) 
				{
					player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.ALREADY_EXISTS")));
					return;
				} 
			} 
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.ALREADY_EXISTS")));
				return;
			}  
		
		Bukkit.broadcastMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.RENAME.HOUSE_HAS_RENAMED")).replace("%OLD_NAME%", house.getHouseName()).replace("%NEW_NAME%", rename).replace("%PLAYER%", player.getName()));
		house.setHouseName(rename);
	}

}
