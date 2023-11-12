package com.rivensoftware.hardcoresmp.house.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerJoinHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseJoinCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("join")
	@Syntax("<house>")
	public void joinCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.JOIN.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if (profile.getHouse() != null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.JOIN.ALREADY_IN_HOUSE")));
			return;
		} 
		
		House house = House.getByName(houseName);
		
		if (house == null || !house.getInvitedPlayers().containsKey(player.getUniqueId())) 
		{
			house = House.getByPlayerName(houseName);
			if (house == null || (!house.getInvitedPlayers().containsKey(player.getUniqueId()) && !player.hasPermission("kingdoms.admin"))) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.JOIN.NOT_INVITED")));
				return;
			} 
		} 
		if (house.getAllPlayerUuids().size() >= 30) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.JOIN.HOUSE_REACHED_MAX")).replace("%HOUSE%", house.getHouseName()));
			return;
		} 
		
		player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.JOIN.YOU_JOINED")).replace("%HOUSE%", house.getHouseName()));
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.JOIN.PLAYER_JOINED_HOUSE")).replace("%PLAYER%", player.getName()));
		house.getInvitedPlayers().remove(player.getUniqueId());
		house.getMembers().add(player.getUniqueId());
		profile.setHouse(house);
		
		Bukkit.getPluginManager().callEvent((Event)new PlayerJoinHouseEvent(player, house));
	}
}
