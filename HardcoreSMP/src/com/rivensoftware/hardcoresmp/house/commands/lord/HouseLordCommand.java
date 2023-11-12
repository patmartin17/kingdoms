package com.rivensoftware.hardcoresmp.house.commands.lord;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseLordCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("lord")
	public void lordCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LORD.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NOT_IN_HOUSE")));
			return;
		}
		
		House house = profile.getHouse();
		Player toLord = Bukkit.getPlayer(playerName);
		
		
		if (toLord == null) 
		{
			SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(playerName);
			if (offlinePlayer != null)
			{
				uuid = offlinePlayer.getUuid();
				name = offlinePlayer.getName();
			}
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NEVER_JOINED")));
				return;
			} 
		} 
		else 
		{
			uuid = toLord.getUniqueId();
			name = toLord.getName();
		} 
		if (!house.getAllPlayerUuids().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NOT_IN_HOUSE")));
			return;
		} 
		if (player.getUniqueId().equals(house.getLord()) && uuid.equals(house.getLord())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LORD.ALREADY_LORD")));
			return;
		} 
		if (uuid.equals(house.getLord()) && !uuid.equals(player.getUniqueId())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LORD.PLAYER_IS_LORD")).replace("%PLAYER%", name));
			return;
		} 
		house.getMembers().remove(uuid);
		house.getCaptains().remove(uuid);
		house.getCaptains().add(house.getLord());
		house.setLord(uuid);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LORD.PLAYER_BEEN_MADE_LORD")).replace("%PLAYER%", name).replace("%LORD%", player.getName()));
	}

}
