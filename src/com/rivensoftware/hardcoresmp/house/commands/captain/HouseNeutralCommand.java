package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareNeutralEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

@CommandAlias("house|h")
public class HouseNeutralCommand extends HouseCommand
{

	@Subcommand("neutral")
	@Syntax("<house>")
	public void enemyCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h neutral &3<house>"));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		House allyOrEnemyHouse = null;
		
		if (House.getByName(houseName) == null) 
		{
			player.sendMessage(MessageTool.color("&cNo houses found with player or name '%NAME%'.").replace("%NAME%", house.getHouseName()));
			return;
		} 
		else
		{
			allyOrEnemyHouse = House.getByName(houseName);
		}
		
		if (house.getHouseName().equals(allyOrEnemyHouse.getHouseName())) 
		{
			player.sendMessage(MessageTool.color("&cYou cannot enemy your own house!"));
			return;
		} 
		
		if(!(house.getEnemies().contains(allyOrEnemyHouse) || house.getAllies().contains(allyOrEnemyHouse)))
		{
			player.sendMessage(MessageTool.color("&cYou do not have a relationship with this house."));
			return;
		}
		
		if(house.getEnemies().contains(allyOrEnemyHouse))
		{
			house.getEnemies().remove(allyOrEnemyHouse);
			house.getAllEnemies().remove(allyOrEnemyHouse);
			
			allyOrEnemyHouse.getInvoulentaryEnemies().remove(house);
			allyOrEnemyHouse.getAllEnemies().remove(house);
			
			house.sendMessage(MessageTool.color("&7You have removed your war declaration on house &e%HOUSE%.").replace("%HOUSE%", allyOrEnemyHouse.getHouseName()));
			allyOrEnemyHouse.sendMessage(MessageTool.color("&7House &e%HOUSE% &7has removed their war declaration.").replace("%HOUSE%", house.getHouseName()));
		}
		
		if(house.getAllies().contains(allyOrEnemyHouse))
		{
			house.getAllies().remove(allyOrEnemyHouse);
			allyOrEnemyHouse.getAllies().remove(house);
			
			house.sendMessage(MessageTool.color("&7You have ended your alliance with house &e%HOUSE%.").replace("%HOUSE%", allyOrEnemyHouse.getHouseName()));
			allyOrEnemyHouse.sendMessage(MessageTool.color("&7House &e%HOUSE% &7has ended their alliance with your house.").replace("%HOUSE%", house.getHouseName()));
		}
		
		Bukkit.getPluginManager().callEvent((Event)new HouseDeclareNeutralEvent(new House[] { house, allyOrEnemyHouse }));
	}

}
