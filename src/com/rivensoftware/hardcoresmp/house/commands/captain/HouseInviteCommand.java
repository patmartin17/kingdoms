package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
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
public class HouseInviteCommand extends HouseCommand
{
	@Subcommand("invite")
	@Syntax("<player>")
	public void createHouseCommand(CommandSender sender, String target) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h invite &3<player>"));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		
		if (target.equalsIgnoreCase(player.getName())) 
		{
			player.sendMessage(MessageTool.color("&cYou can't invite yourself!"));
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
				player.sendMessage(MessageTool.color("&cPlayer '%PLAYER%' has never joined the server!").replace("%PLAYER%", target));
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
			player.sendMessage(MessageTool.color("&c%PLAYER% is already in your house!").replace("%PLAYER%", name));
			return;
		} 
		
		if (house.getInvitedPlayers().containsKey(uuid)) 
		{
			player.sendMessage(MessageTool.color("&c%PLAYER% has already been invited!").replace("%PLAYER%", name));
			return;
		} 
		
		if (toInvite != null) 
		{
			toInvite.sendMessage(MessageTool.color("&eYou&e have been invited to join &9%HOUSE%&e. &7(Type /h join %HOUSE% to join)").replace("%HOUSE%", house.getHouseName()));
		} 
		house.getInvitedPlayers().put(uuid, player.getUniqueId());
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has invited &9%INVITED_PLAYER%&e to your house").replace("%PLAYER%", player.getName()).replace("%INVITED_PLAYER%", name));
	}
}
