package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareNeutralEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseNeutralCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("neutral")
	@Syntax("<house>")
	public void enemyCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.USAGE")));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		House allyOrEnemyHouse = null;
		
		if (House.getByName(houseName) == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NO_HOUSES_FOUND")).replace("%NAME%", house.getHouseName()));
			return;
		} 
		else
		{
			allyOrEnemyHouse = House.getByName(houseName);
		}
		
		if (house.getHouseName().equals(allyOrEnemyHouse.getHouseName())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.CANT_ENEMY_SELF")));
			return;
		} 
		
		if(!(house.getEnemies().contains(allyOrEnemyHouse) || house.getAllies().contains(allyOrEnemyHouse)))
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.NO_RELATIONSHIP")));
			return;
		}
		
		if(house.getEnemies().contains(allyOrEnemyHouse))
		{
			house.getEnemies().remove(allyOrEnemyHouse);
			house.getAllEnemies().remove(allyOrEnemyHouse);
			
			allyOrEnemyHouse.getInvoulentaryEnemies().remove(house);
			allyOrEnemyHouse.getAllEnemies().remove(house);
			
			house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.REMOVED_WAR_DECLARATION")).replace("%HOUSE%", allyOrEnemyHouse.getHouseName()));
			allyOrEnemyHouse.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.HOUSE_HAS_REMOVED_WAR_DECLARATION")).replace("%HOUSE%", house.getHouseName()));
		}
		
		if(house.getAllies().contains(allyOrEnemyHouse))
		{
			house.getAllies().remove(allyOrEnemyHouse);
			allyOrEnemyHouse.getAllies().remove(house);
			
			house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.ENDED_ALLIANCE")).replace("%HOUSE%", allyOrEnemyHouse.getHouseName()));
			allyOrEnemyHouse.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.NEUTRAL.HOUSE_HAS_ENDED_ALLIANCE")).replace("%HOUSE%", house.getHouseName()));
		}
		
		Bukkit.getPluginManager().callEvent((Event)new HouseDeclareNeutralEvent(new House[] { house, allyOrEnemyHouse }));
	}

}
