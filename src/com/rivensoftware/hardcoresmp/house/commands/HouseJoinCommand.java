package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerJoinHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@CommandAlias("house|h")
public class HouseJoinCommand extends HouseCommand
{

	@Subcommand("join")
	@Syntax("<house>")
	public void joinCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h join &3<player/house>"));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if (profile.getHouse() != null) 
		{
			player.sendMessage(MessageTool.color("&cYou're already in a house!"));
			return;
		} 
		
		House house = House.getByName(houseName);
		
		if (house == null || !house.getInvitedPlayers().containsKey(player.getUniqueId())) 
		{
			house = House.getByPlayerName(houseName);
			if (house == null || (!house.getInvitedPlayers().containsKey(player.getUniqueId()) && !player.hasPermission("kingdoms.admin"))) 
			{
				player.sendMessage(MessageTool.color("&cThat house hasn't invited you."));
				return;
			} 
		} 
		if (house.getAllPlayerUuids().size() >= 30) 
		{
			player.sendMessage(MessageTool.color("&House '%HOUSE%' has reach the maximum player limit!").replace("%HOUSE%", house.getHouseName()));
			return;
		} 
		
		player.sendMessage(MessageTool.color("&eYou joined &9%HOUSE%&e.").replace("%HOUSE%", house.getHouseName()));
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has joined your house.").replace("%PLAYER%", player.getName()));
		house.getInvitedPlayers().remove(player.getUniqueId());
		house.getMembers().add(player.getUniqueId());
		profile.setHouse(house);
		
		Bukkit.getPluginManager().callEvent((Event)new PlayerJoinHouseEvent(player, house));
	}
}
