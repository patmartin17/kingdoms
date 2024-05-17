package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerCreateHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.lives.LifeTransaction;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.UUID;

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
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h create &3<house name>"));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if (profile.getHouse() != null) 
		{
			player.sendMessage(MessageTool.color("&cYou're already in a faction!"));
			return;
		} 
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < args.length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 
		String name = sb.toString().trim().replace(" ", "");
		
		if(houseName.length() < 3)
		{
			player.sendMessage(MessageTool.color("&7The name of your house is too short (< 3 characters)."));
			return;
		}
		
		if(houseName.length() > 16)
		{
			player.sendMessage(MessageTool.color("&7The name of your house is too long (> 16 characters)."));
			return;
		}
		
		if (!StringUtils.isAlphanumeric(name)) 
		{
			player.sendMessage(MessageTool.color("&7The name of your house can only contain letters and numbers."));
			return;
		} 
		
		for (String string : plugin.getMainConfig().getStringList("FACTION_NAME.BLOCKED_NAMES"))
		{
			if (name.contains(string)) 
			{
				player.sendMessage(MessageTool.color("&cThat faction tag is blacklisted!"));
				return;
			} 
		} 
		if (House.getByName(name) != null) 
		{
			
			player.sendMessage(MessageTool.color("&7That faction already exists!"));
			return;
		} 
		
		LifeTransaction transaction = new LifeTransaction(profile);
		if(!transaction.removeLives(1.0, null))
		{
			player.sendMessage(MessageTool.color("&cYou do not have enough lives to start a house."));
			player.sendMessage(MessageTool.color("&cRequired lives: &e" + 1.0));
			player.sendMessage(MessageTool.color("&cYour lives&f: " + profile.getGeneralLives()));
			return;
		}
		
		House house = new House(name, player.getUniqueId(), UUID.randomUUID());
		profile.setHouse(house);
		house.setLives(1.0);
		house.setPowerless(false);
		Bukkit.broadcastMessage(MessageTool.color("&eHouse &9%NAME%&e has been &acreated&e by &f%PLAYER%").replace("%PLAYER%", player.getName()).replace("%NAME%", name));
		Bukkit.getPluginManager().callEvent((Event)new PlayerCreateHouseEvent(player, house));
		player.sendMessage(MessageTool.color(plugin.getMessageConfig().getString("MESSAGES.WRAPPER")));
		player.sendMessage(MessageTool.color("&aYou have been deducted one life to establish your house."));
		player.sendMessage(MessageTool.color("&aRemaining lives&e: " + (profile.getGeneralLives() + profile.getSoulboundLives())));
		player.sendMessage(MessageTool.color(""));
		player.sendMessage(MessageTool.color("&c&lYou can now set your house banner by using /h banner"));
		player.sendMessage(MessageTool.color(plugin.getMessageConfig().getString("MESSAGES.WRAPPER")));
	}

}
