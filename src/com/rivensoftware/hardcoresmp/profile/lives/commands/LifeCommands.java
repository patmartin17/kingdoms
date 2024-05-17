package com.rivensoftware.hardcoresmp.profile.lives.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.lives.LifeType;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("lives|life")
public class LifeCommands extends BaseCommand
{
	@Default
	public void livesCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(getOrigArgs().length > 0 && !getOrigArgs()[0].equals("add") && !getOrigArgs()[0].equals("remove") && !getOrigArgs()[0].equals("send"))
		{
			if(player.hasPermission("kingdoms.admin"))
				player.sendMessage(MessageTool.color("&cUsage: /lives <add/remove/send> <player> <lifetype> <amount>"));
			else
				player.sendMessage(MessageTool.color("&cUsage: /lives send <player> <amount>"));	
		}
		else
		{
			Profile profile = Profile.getByPlayer(player);
			player.sendMessage(MessageTool.color("&7&m                                       "));
			player.sendMessage(MessageTool.color("&aGeneral Lives&f: " + profile.getGeneralLives()));
			player.sendMessage(MessageTool.color("&aSoulbound Lives&f: " + profile.getSoulboundLives()));
			player.sendMessage(MessageTool.color("&7&m                                       "));
		}

	}

	/*
	 * This needs to work with offline players by connecting to the profile database setting their lives correctly and saving
	 */
	@Subcommand("add|a")
	@CommandPermission("kingdoms.admin")
	@Syntax("<player> <lifetype> <amount>")
	public void livesAddCommand(CommandSender sender, String target, String lifeTypeString, double lives) 
	{
		Player player = (Player) sender;
		Player targetPlayer = Bukkit.getPlayer(target);

		if(targetPlayer.isOnline())
		{
			Profile targetProfile = Profile.getByPlayer(targetPlayer);
			if(lifeTypeString.equalsIgnoreCase("general") || lifeTypeString.equalsIgnoreCase("soulbound"))
			{
				LifeType lifeType = LifeType.valueOf(lifeTypeString.toUpperCase());
				double total = lifeType.getLives(targetProfile) + lives;
				lifeType.setLives(total, targetProfile);
				player.sendMessage(MessageTool.color("&aAdded &f" + lives + " &ato " + target + "&a's balance"));
			}
			else
			{
				player.sendMessage(MessageTool.color("&cValid life types are Soulbound and General."));
			}
		}
		else
		{
			player.sendMessage(MessageTool.color("&cNeed to set this up for offline players."));
		}
	}

	@Subcommand("remove|r")
	@CommandPermission("kingdoms.admin")
	@Syntax("<player> <lifetype> <amount>")
	public void livesRemoveCommand(CommandSender sender, String target, String lifeTypeString, double lives) 
	{
		Player player = (Player) sender;
		Player targetPlayer = Bukkit.getPlayer(target);

		if(targetPlayer.isOnline())
		{
			Profile targetProfile = Profile.getByPlayer(targetPlayer);
			if(lifeTypeString.equalsIgnoreCase("general") || lifeTypeString.equalsIgnoreCase("soulbound"))
			{
				LifeType lifeType = LifeType.valueOf(lifeTypeString.toUpperCase());
				if(lifeType.getLives(targetProfile) - lives >= 0)
				{
					double total = lifeType.getLives(targetProfile) - lives;
					lifeType.setLives(total, targetProfile);
					player.sendMessage(MessageTool.color("&aRemoved &f" + lives + " &afrom " + target + "&a's balance"));	
				}
				else
				{
					player.sendMessage(MessageTool.color("&cYou cannot set a player below zero lives."));	
				}
			}
			else
			{
				player.sendMessage(MessageTool.color("&cValid life types are Soulbound and General."));
			}
		}
		else
		{
			player.sendMessage(MessageTool.color("&cNeed to set this up for offline players."));
		}
	}
	
	
	/*
	 * 	@Subcommand("send|s")
	@CommandPermission("kingdoms.admin")
	@Syntax("<player> <amount>")
	public void livesSendCommand(CommandSender sender, String target, double lives) 
	{
		Player player = (Player) sender;
		Player targetPlayer = Bukkit.getPlayer(target);

		if(targetPlayer.isOnline())
		{
			Profile targetProfile = Profile.getByPlayer(targetPlayer);
			if(lifeTypeString.equalsIgnoreCase("general") || lifeTypeString.equalsIgnoreCase("soulbound"))
			{
				LifeType lifeType = LifeType.valueOf(lifeTypeString.toUpperCase());
				if(lifeType.getLives(targetProfile) - lives >= 0)
				{
					double total = lifeType.getLives(targetProfile) - lives;
					lifeType.setLives(total, targetProfile);
					player.sendMessage(MessageTool.color("&aRemoved &f" + lives + " &afrom " + target + "&a's balance"));	
				}
				else
				{
					player.sendMessage(MessageTool.color("&cYou cannot set a player below zero lives."));	
				}
			}
			else
			{
				player.sendMessage(MessageTool.color("&cValid life types are Soulbound and General."));
			}
		}
		else
		{
			player.sendMessage(MessageTool.color("&cNeed to set this up for offline players."));
		}
	}
	 */
}
