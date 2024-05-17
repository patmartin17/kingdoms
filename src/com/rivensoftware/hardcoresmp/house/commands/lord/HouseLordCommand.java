package com.rivensoftware.hardcoresmp.house.commands.lord;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("house|h")
public class HouseLordCommand extends HouseCommand
{

	@Subcommand("lord")
	public void lordCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h lord &3<player>"));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color("&7You are not in a house!"));
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
				player.sendMessage(MessageTool.color("&cPlayer '%PLAYER%' has never joined the server!"));
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
			player.sendMessage(MessageTool.color("&3%PLAYER% is not in your house!"));
			return;
		} 
		if (player.getUniqueId().equals(house.getLord()) && uuid.equals(house.getLord())) 
		{
			player.sendMessage(MessageTool.color("&cYou're already the lord!"));
			return;
		} 
		if (uuid.equals(house.getLord()) && !uuid.equals(player.getUniqueId())) 
		{
			player.sendMessage(MessageTool.color("&c%PLAYER% is already lord of the house!").replace("%PLAYER%", name));
			return;
		} 
		house.getMembers().remove(uuid);
		house.getCaptains().remove(uuid);
		house.getCaptains().add(house.getLord());
		house.setLord(uuid);
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has been made lord by &f%LORD%").replace("%PLAYER%", name).replace("%LORD%", player.getName()));
	}

}
