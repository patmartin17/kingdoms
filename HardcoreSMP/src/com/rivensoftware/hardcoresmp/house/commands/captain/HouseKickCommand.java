package com.rivensoftware.hardcoresmp.house.commands.captain;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseKickCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("kick")
	public void kickCommand(CommandSender sender, String kickPlayer) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.KICK.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		
		if (kickPlayer.equalsIgnoreCase(player.getName())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.KICK.CANT_KICK_SELF")));
			return;
		} 
		
		Player toDemote = Bukkit.getPlayer(kickPlayer);
		
		if (toDemote == null) 
		{
			SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(kickPlayer);
			if (offlinePlayer != null)
			{
				uuid = offlinePlayer.getUuid();
				name = offlinePlayer.getName();
			} 
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NEVER_JOINED_SERVER")).replace("%PLAYER%", kickPlayer));
				return;
			} 
		} 
		else 
		{
			uuid = toDemote.getUniqueId();
			name = toDemote.getName();
		} 
		
		if (!house.getAllPlayerUuids().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.KICK.PLAYER_NOT_IN_HOUSE")).replace("%PLAYER%", name));
			return;
		} 
		
		if (house.getLord().equals(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.KICK.CANT_KICK_LORD")));
			return;
		} 
		
		if (house.getCaptains().contains(uuid) && house.getCaptains().contains(player.getUniqueId())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.KICK.CANT_KICK_CAPTAIN")));
			return;
		} 
		
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.KICK.PLAYER_HAS_KICKED")).replace("%KICKED_PLAYER%", name).replace("%PLAYER%", player.getName()));
		Profile kickProfile = Profile.getByUUID(uuid);
		kickProfile.setHouse(null);
		house.getCaptains().remove(uuid);
		house.getMembers().remove(uuid);
		Player playerToKick = Bukkit.getPlayer(uuid);
		
		if (playerToKick != null)
			Bukkit.getPluginManager().callEvent((Event)new PlayerLeaveHouseEvent(playerToKick, house)); 
	}

}
