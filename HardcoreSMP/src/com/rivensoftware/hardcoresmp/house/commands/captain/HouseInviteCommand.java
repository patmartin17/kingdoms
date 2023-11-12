package com.rivensoftware.hardcoresmp.house.commands.captain;

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
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseInviteCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("invite")
	@Syntax("<player>")
	public void createHouseCommand(CommandSender sender, String target) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.INVITE.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NOT_IN_HOUSE")));
			return;
		}
		
		House house = profile.getHouse();
		
		if (target.equalsIgnoreCase(player.getName())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.INVITE.CANT_INVITE_SELF")));
			return;
		} 
		
		Player toInvite = Bukkit.getPlayer(target);
		
		if (toInvite == null) 
		{
			SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(target);
			if (offlinePlayer != null) 
			{
				uuid = offlinePlayer.getUuid();
				name = offlinePlayer.getName();
			} 
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NEVER_JOINED")).replace("%PLAYER%", target));
				return;
			} 
		} 
		else 
		{
			uuid = toInvite.getUniqueId();
			name = toInvite.getName();
		} 
		
		if (house.getAllPlayerUuids().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.INVITE.PLAYER_ALREADY_IN_HOUSE")).replace("%PLAYER%", name));
			return;
		} 
		
		if (house.getInvitedPlayers().containsKey(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.INVITE.PLAYER_ALREADY_INVITED")).replace("%PLAYER%", name));
			return;
		} 
		
		if (toInvite != null) 
		{
			toInvite.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.INVITE.YOU_HAVE_BEEN_INVITED")).replace("%HOUSE%", house.getHouseName()));
		} 
		house.getInvitedPlayers().put(uuid, player.getUniqueId());
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.INVITE.PLAYER_HAS_INVITED")).replace("%PLAYER%", player.getName()).replace("%INVITED_PLAYER%", name));
	}
}
