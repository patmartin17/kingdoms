package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseSetHomeCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
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
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.SETHOME.PLAYER_UPDATED_HOME")).replace("%PLAYER%", player.getName()));
				return;
			} 
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NO_HOUSES_FOUND")));
				return;
			} 
		} 
		else 
		{
			house = profile.getHouse();
			if (house == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NOT_IN_HOUSE")));
				return;
			} 
			
			if (!house.getLord().equals(player.getUniqueId()) && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("kingdoms.admin")) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_LORD_OR_CAPTAIN")));
				return;
			} 
		} 

		for (Claim claim : house.getClaims()) 
		{
			if (claim.isInside(player.getLocation())) 
			{
				house.setKingdomCapital(LocationSerialization.serializeLocation(player.getLocation()));
				house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.SETHOME.PLAYER_UPDATED_CAPITAL")).replace("%PLAYER%", player.getName()));
				return;
			} 
		} 
		player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.SETHOME.MUST_BE_OWNED_LAND")));
	}
	
	@Subcommand("sethq")
	public void hqCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
			Bukkit.dispatchCommand(player, "h sethome");
		else
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_PLAYER")));
	}
	
	@Subcommand("setcapital")
	public void capitalCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
			Bukkit.dispatchCommand(player, "h sethome");
		else
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_PLAYER")));
	}
}
