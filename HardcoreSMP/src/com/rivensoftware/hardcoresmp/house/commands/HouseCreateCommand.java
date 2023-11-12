package com.rivensoftware.hardcoresmp.house.commands;

import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerCreateHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.lives.LifeTransaction;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseCreateCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("create")
	@Syntax("<name>")
	public void createHouseCommand(CommandSender sender, String houseName) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		
		if (args.length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if (profile.getHouse() != null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.ALREADY_IN_HOUSE")));
			return;
		} 
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 
		String name = sb.toString().trim().replace(" ", "");
		
		if(houseName.length() < 3)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.TOO_SHORT")));
			return;
		}
		
		if(houseName.length() > 16)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.TOO_LONG")));
			return;
		}
		
		if (!StringUtils.isAlphanumeric(name)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.LETTERS_AND_NUMBERS")));
			return;
		} 
		
		for (String string : plugin.getMainConfig().getStringList("HOUSE_NAME.BLOCKED_NAMES"))
		{
			if (name.contains(string)) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.BLACKLISTED_NAME")));
				return;
			} 
		} 
		if (House.getByName(name) != null) 
		{
			
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.HOUSE_ALREADY_EXISTS")));
			return;
		} 
		
		LifeTransaction transaction = new LifeTransaction(profile);
		if(!transaction.removeLives(1.0, null))
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.NOT_ENOUGH_LIVES")));
			player.sendMessage(MessageTool.color("&cRequired lives: &e" + 1.0));
			player.sendMessage(MessageTool.color("&cYour lives&f: " + profile.getGeneralLives()));
			return;
		}
		
		House house = new House(name, player.getUniqueId(), UUID.randomUUID());
		profile.setHouse(house);
		house.setLives(1.0);
		house.setPowerless(false);
		Bukkit.broadcastMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.HOUSE_CREATED")).replace("%PLAYER%", player.getName()).replace("%NAME%", name));
		Bukkit.getPluginManager().callEvent((Event)new PlayerCreateHouseEvent(player, house));
		player.sendMessage(MessageTool.color(plugin.getMessageConfig().getString("MESSAGES.WRAPPER")));
		player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CREATE.DEDUCTED_LIVES")));
		player.sendMessage(MessageTool.color("&aRemaining lives&e: " + (profile.getGeneralLives() + profile.getSoulboundLives())));
		player.sendMessage(MessageTool.color(""));
		player.sendMessage(MessageTool.color("&c&lYou can now set your house banner by using /h banner"));
		player.sendMessage(MessageTool.color(plugin.getMessageConfig().getString("MESSAGES.WRAPPER")));
	}

}
