package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h tag &3<new house name>"));
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
				player.sendMessage(MessageTool.color("&cNo house found with player or name '%NAME%'.").replace("%NAME%", rename));
				return;
			} 
		} 
		else 
		{
			house = Profile.getByUUID(player.getUniqueId()).getHouse();
			if (house == null) 
			{
				player.sendMessage(MessageTool.color("&7You're not in a house!"));
				return;
			} 

			if (!house.getLord().equals(player.getUniqueId()) && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("ekko.system"))
			{
				player.sendMessage(MessageTool.color("&cYou must be either an captain or the lord of the house to do this!"));
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
			player.sendMessage(MessageTool.color("&cMinimum house name size is 3 characters!"));
			return;
		} 

		if (rename.length() > plugin.getHouseConfig().getInt("HOUSE.HOUSE_NAME.MAX_CHARACTERS")) 
		{
			player.sendMessage(MessageTool.color("&cMaximum house name size is 16 characters!"));
			return;
		} 

		if (!StringUtils.isAlphanumeric(rename)) 
		{
			player.sendMessage(MessageTool.color("&cHouse tag must be alphanumeric!"));
			return;
		} 

		for (String string : plugin.getHouseConfig().getStringList("HOUSE.HOUSE_NAME.BLOCKED_NAMES")) 
		{
			if (rename.contains(string)) 
			{
				player.sendMessage(MessageTool.color("&cThat house tag is blacklisted!"));
				return;
			} 
		} 

		House otherHouse = House.getByName(rename);
		if (otherHouse != null)
			if (otherHouse.equals(house)) 
			{
				if (otherHouse.getHouseName().equals(rename)) 
				{
					player.sendMessage(MessageTool.color("&7That house already exists!"));
					return;
				} 
			} 
			else 
			{
				player.sendMessage(MessageTool.color("&7That house already exists!"));
				return;
			}  
		
		Bukkit.broadcastMessage(MessageTool.color("&eHouse &9%OLD_NAME%&e has been &arenamed&e to &9%NEW_NAME%&e by &f%PLAYER%").replace("%OLD_NAME%", house.getHouseName()).replace("%NEW_NAME%", rename).replace("%PLAYER%", player.getName()));
		house.setHouseName(rename);
	}

}
