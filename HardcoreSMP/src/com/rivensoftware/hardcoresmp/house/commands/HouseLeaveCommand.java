package com.rivensoftware.hardcoresmp.house.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseLeaveCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("leave|quit")
	public void leaveCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NOT_IN_HOUSE")));
			return;
		}
		House house = profile.getHouse();
		if (house.getLord().equals(player.getUniqueId())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LEAVE.CANNOT_LEAVE_AS_LORD")));
			return;
		} 
		player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LEAVE.YOU_LEFT")).replace("%HOUSE%", house.getHouseName()));
		house.getMembers().remove(player.getUniqueId());
		house.getCaptains().remove(player.getUniqueId());
		profile.setHouse(null);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LEAVE.PLAYER_LEFT_HOUSE")).replace("%PLAYER%", player.getName()));
		Bukkit.getPluginManager().callEvent((Event)new PlayerLeaveHouseEvent(player, house));
	}
}

