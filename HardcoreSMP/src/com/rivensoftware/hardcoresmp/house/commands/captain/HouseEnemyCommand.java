package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareWarEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseEnemyCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Subcommand("enemy")
	@Syntax("<house>")
	public void enemyCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.USAGE")));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		House enemyHouse = null;
		
		if (House.getByName(houseName) == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NO_HOUSES_FOUND")).replace("%NAME%", house.getHouseName()));
			return;
		} 
		else
		{
			enemyHouse = House.getByName(houseName);
		}
		
		if (house.getHouseName().equals(enemyHouse.getHouseName())) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.CANT_ENEMY_SELF")));
			return;
		} 
		
		if (house.getEnemies().size() >= 3) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.EXCEEDED_MAX_WARS")));
			return;
		} 
		
		if (house.getEnemies().contains(house)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.ALREADY_HAVE_WAR_DECLARED")).replace("%HOUSE%", enemyHouse.getHouseName()));
			return;
		} 
		
		if (enemyHouse.getAllies().contains(house)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.ALREADY_HAVE_RELATIONSHIP")).replace("%HOUSE%", enemyHouse.getHouseName()).replace("%RELATION%", "Ally"));
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.MUST_BE_NEUTRAL")));
			return;
		}
		
		house.getEnemies().add(enemyHouse);
		house.getAllEnemies().add(enemyHouse);
		
		enemyHouse.getInvoulentaryEnemies().add(house);
		enemyHouse.getAllEnemies().add(house);
		
		
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.YOU_DECLARED_WAR")).replace("%HOUSE%", enemyHouse.getHouseName()));
		enemyHouse.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ENEMY.HOUSE_ENEMIED_YOU")).replace("%HOUSE%", house.getHouseName()));
		
		Bukkit.getPluginManager().callEvent((Event)new HouseDeclareWarEvent(new House[] { house, enemyHouse }));
	}

}
