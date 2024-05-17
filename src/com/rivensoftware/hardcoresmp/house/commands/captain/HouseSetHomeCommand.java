package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("house|h")
public class HouseSetHomeCommand extends HouseCommand
{
	@Subcommand("sethome|sethq|setcapital")
	public void createHouseCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = null;

		if ((getOrigArgs()).length >= 1 && player.hasPermission("kingdoms.admin")) 
		{
			String name = getOrigArgs()[0];
			House otherHouse = House.getAnyByString(name);

			if (otherHouse != null) 
			{

				house = otherHouse;

				house.setKingdomCapital(LocationSerialization.serializeLocation(player.getLocation()));
				player.sendMessage(MessageTool.color("&3%PLAYER% has updated the kingdom capital location").replace("%PLAYER%", player.getName()));
				return;
			} 
			else 
			{
				player.sendMessage(MessageTool.color("&cNo houses found with player or name '%NAME%'."));
				return;
			} 
		} 
		else 
		{
			house = profile.getHouse();
			if (house == null) 
			{
				player.sendMessage(MessageTool.color("&7You're not in a house!"));
				return;
			} 
			
			if (!house.getLord().equals(player.getUniqueId()) && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("kingdoms.admin")) 
			{
				player.sendMessage(MessageTool.color("&cYou must be either a captain or the lord of your kingdom to do this!"));
				return;
			} 
		} 

		for (Claim claim : house.getClaims()) 
		{
			if (claim.isInside(player.getLocation())) 
			{
				house.setKingdomCapital(LocationSerialization.serializeLocation(player.getLocation()));
				house.sendMessage(MessageTool.color("&3%PLAYER% has updated the kingdom capital location").replace("%PLAYER%", player.getName()));
				return;
			} 
		} 
		player.sendMessage(MessageTool.color("&cYour kingdom capital can only be set in your own land!"));
	}
	
	@Subcommand("sethq")
	public void hqCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
			Bukkit.dispatchCommand(player, "h sethome");
		else
			sender.sendMessage(MessageTool.color("&7You must be a player to execute this command."));
	}
	
	@Subcommand("setcapital")
	public void capitalCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
			Bukkit.dispatchCommand(player, "h sethome");
		else
			sender.sendMessage(MessageTool.color("&7You must be a player to execute this command."));
	}
}
