package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@CommandAlias("house|h")
public class HouseLeaveCommand extends HouseCommand
{
	@Subcommand("leave|quit")
	public void leaveCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color("&cYou are not in a house."));
			return;
		}
		House house = profile.getHouse();
		if (house.getLord().equals(player.getUniqueId())) 
		{
			player.sendMessage(MessageTool.color("&cYou cannot leave as lord!"));
			return;
		} 
		player.sendMessage(MessageTool.color("&eYou left &9%HOUSE%&e.").replace("%HOUSE%", house.getHouseName()));
		house.getMembers().remove(player.getUniqueId());
		house.getCaptains().remove(player.getUniqueId());
		profile.setHouse(null);
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has left your house").replace("%PLAYER%", player.getName()));
		Bukkit.getPluginManager().callEvent((Event)new PlayerLeaveHouseEvent(player, house));
	}
}

