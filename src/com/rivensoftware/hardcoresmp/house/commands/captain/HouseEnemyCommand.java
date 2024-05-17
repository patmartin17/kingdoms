package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareWarEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@CommandAlias("house|h")
public class HouseEnemyCommand extends HouseCommand
{

	@Subcommand("enemy")
	@Syntax("<house>")
	public void enemyCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h enemy &3<house>"));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		House enemyHouse = null;
		
		if (House.getByName(houseName) == null) 
		{
			player.sendMessage(MessageTool.color("&cNo houses found with player or name '%NAME%'.").replace("%NAME%", house.getHouseName()));
			return;
		} 
		else
		{
			enemyHouse = House.getByName(houseName);
		}
		
		if (house.getHouseName().equals(enemyHouse.getHouseName())) 
		{
			player.sendMessage(MessageTool.color("&cYou cannot enemy your own house!"));
			return;
		} 
		
		if (house.getEnemies().size() >= 3) 
		{
			player.sendMessage(MessageTool.color("&cYou can not have more than three wars declared at once."));
			return;
		} 
		
		if (house.getEnemies().contains(house)) 
		{
			player.sendMessage(MessageTool.color("&cYou already have a war declaration against house %HOUSE%.").replace("%HOUSE%", enemyHouse.getHouseName()));
			return;
		} 
		
		if (enemyHouse.getAllies().contains(house)) 
		{
			player.sendMessage(MessageTool.color("&cYour house already has this relationship with %HOUSE% (Ally).").replace("%HOUSE%", enemyHouse.getHouseName()));
			player.sendMessage(MessageTool.color("&7(You must neutral this house before declaring war)"));
			return;
		}
		
		house.getEnemies().add(enemyHouse);
		house.getAllEnemies().add(enemyHouse);
		
		enemyHouse.getInvoulentaryEnemies().add(house);
		enemyHouse.getAllEnemies().add(house);
		
		
		house.sendMessage(MessageTool.color("&c&lYour house has declared war on house &e&l%HOUSE%!").replace("%HOUSE%", enemyHouse.getHouseName()));
		enemyHouse.sendMessage(MessageTool.color("&c&lHouse &e&l%HOUSE% &c&lhas declared war on your house!").replace("%HOUSE%", house.getHouseName()));
		
		Bukkit.getPluginManager().callEvent((Event)new HouseDeclareWarEvent(new House[] { house, enemyHouse }));
	}

}
