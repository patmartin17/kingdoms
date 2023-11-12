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
public class HouseDemoteCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Subcommand("demote")
	public void demoteCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.DEMOTE.USAGE")));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		Player toDemote = Bukkit.getPlayer(playerName);
		if (toDemote == null) 
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
			uuid = toDemote.getUniqueId();
			name = toDemote.getName();
		} 
		if (name.equalsIgnoreCase(player.getName()) && player.getUniqueId().equals(house.getLord())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.DEMOTE.CANT_DEMOTE_SELF")));
			return;
		} 
		if (!house.getAllPlayerUuids().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NOT_IN_HOUSE")).replace("%PLAYER%", name));
			return;
		} 
		if (!house.getCaptains().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.DEMOTE.PLAYER_NOT_CAPTAIN")).replace("%PLAYER%", name));
			return;
		} 
		house.getCaptains().remove(uuid);
		house.getMembers().add(uuid);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.DEMOTE.PLAYER_DEMOTED")).replace("%PLAYER%", name).replace("%LORD%", player.getName()));
	}

}
