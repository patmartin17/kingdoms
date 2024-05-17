package com.rivensoftware.hardcoresmp.profile.protection.commands;

import org.bukkit.command.CommandSender;

import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("pvp")
public class ProfileProtectionCommand extends BaseCommand
{
	@Default
	public void defaultCommand(CommandSender sender)
	{	
		sender.sendMessage(MessageTool.color("&cUsage: /pvp <enable/time>"));
	}

	@Subcommand("help")
	public void helpCommand(CommandSender sender) 
	{
		sender.sendMessage(MessageTool.color("&cUsage: /pvp <enable/time>"));
	}

}
