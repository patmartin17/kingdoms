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
public class HouseDemoteCommand extends HouseCommand
{

	@Subcommand("demote")
	public void demoteCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h demote &3<player>"));
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
				player.sendMessage(MessageTool.color("&cPlayer '%PLAYER%' has never joined the server!").replace("%PLAYER%", playerName));
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
			player.sendMessage(MessageTool.color("&cYou cannot demote yourself!"));
			return;
		} 
		if (!house.getAllPlayerUuids().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color("&3%PLAYER% is not in your house!").replace("%PLAYER%", name));
			return;
		} 
		if (!house.getCaptains().contains(uuid)) 
		{
			player.sendMessage(MessageTool.color("&c%PLAYER% is not an captain!").replace("%PLAYER%", name));
			return;
		} 
		house.getCaptains().remove(uuid);
		house.getMembers().add(uuid);
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has been demoted to a member by &f%LORD%").replace("%PLAYER%", name).replace("%LORD%", player.getName()));
	}

}
