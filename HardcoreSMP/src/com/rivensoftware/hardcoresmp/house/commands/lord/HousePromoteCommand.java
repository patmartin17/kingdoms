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
public class HousePromoteCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("promote")
	public void promoteCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.PROMOTE.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		Player toPromote = Bukkit.getPlayer(playerName);
		
		if (toPromote == null) 
		{
			SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(playerName);
			if (offlinePlayer != null) 
			{
				uuid = offlinePlayer.getUuid();
				name = offlinePlayer.getName();
			} 
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NEVER_JOINED")).replace("%PLAYER%", playerName));
				return;
			} 
		} 
		else 
		{
			uuid = toPromote.getUniqueId();
			name = toPromote.getName();
		} 
		
		if (name.equalsIgnoreCase(player.getName()) && player.getUniqueId().equals(house.getLord())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.PROMOTE.CANT_PROMOTE_SELF")));
			return;
		} 
		
		if (!house.getAllPlayerUuids().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NOT_IN_HOUSE")).replace("%PLAYER%", name));
			return;
		} 
		
		if (house.getCaptains().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.PROMOTE.ALREADY_CAPTAIN")).replace("%PLAYER%", name));
			return;
		} 
		house.getMembers().remove(uuid);
		house.getCaptains().add(uuid);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.PROMOTE.PLAYER_PROMOTED")).replace("%PLAYER%", name).replace("%LORD%", player.getName()));
	}

}
