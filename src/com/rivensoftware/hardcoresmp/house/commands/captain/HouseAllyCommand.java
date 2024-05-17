package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareAllyEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@CommandAlias("house|h")
public class HouseAllyCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("ally")
	public void allyCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if ((getOrigArgs()).length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.USAGE")));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		House allyHouse = null;
		
		if (House.getByName(houseName) == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.NO_HOUSES_FOUND")).replace("%NAME%", houseName));
			return;
		} 
		else
		{
			allyHouse = House.getByName(houseName);
		}
		
		if (house.getHouseName().equals(allyHouse.getHouseName())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.CANT_ALLY_SELF")));
			return;
		} 
		
		if (allyHouse.getAllies().contains(house)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.ALREADY_HAVE_RELATIONSHIP")).replace("%HOUSE%", allyHouse.getHouseName()));
			return;
		} 
		
		if (allyHouse.getEnemies().contains(house)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.REQUEST_HOUSE_WAR_DECLARED")).replace("%HOUSE%", allyHouse.getHouseName()));
			return;
		} 
		
		if (house.getEnemies().contains(allyHouse)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.YOU_HAVE_WAR_DECLARED")).replace("%HOUSE%", allyHouse.getHouseName()));
			return;
		} 
		
		if (house.getRequestedAllies().contains(allyHouse.getUuid())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.ALLY_ALREADY_REQUESTED")).replace("%HOUSE%", allyHouse.getHouseName()));
			return;
		} 
		
		if (allyHouse.getAllies().size() >= 1) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.MAXIMUM_ALLIES_REACHED")).replace("%HOUSE%", allyHouse.getHouseName()));
			return;
		} 
		
		if (allyHouse.getRequestedAllies().contains(house.getUuid())) 
		{
			allyHouse.getRequestedAllies().remove(house.getUuid());
			allyHouse.getAllies().add(house);
			house.getAllies().add(allyHouse);
			allyHouse.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.SUCCESSFULLY_ALLIED")).replace("%HOUSE%", house.getHouseName()));
			house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.SUCCESSFULLY_ALLIED")).replace("%HOUSE%", allyHouse.getHouseName()));
			Bukkit.getPluginManager().callEvent((Event)new HouseDeclareAllyEvent(new House[] { house, allyHouse }));
		} 
		else 
		{
			house.getRequestedAllies().add(allyHouse.getUuid());
			house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.PLAYER_REQUESTED_ALLY")).replace("%PLAYER%", player.getName()).replace("%HOUSE%", allyHouse.getHouseName()));
			for (Player allyPlayer : allyHouse.getOnlinePlayers()) 
			{
				if (allyPlayer.getUniqueId().equals(allyHouse.getLord())) 
				{
					allyPlayer.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.RECEIVED_REQUEST_LORD")).replace("%HOUSE%", house.getHouseName()));
					continue;
				} 
				allyPlayer.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ALLY.RECEIVED_REQUEST_MEMBER")).replace("%HOUSE%", house.getHouseName()));
			} 
		} 
	}
}
