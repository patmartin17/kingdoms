package com.rivensoftware.hardcoresmp.event.capturepoint.commands;

import org.bukkit.command.CommandSender;

import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;

@CommandAlias("capturepoint|cp")
@Description("Main admin command.")
public class CapturePointCommand extends BaseCommand 
{
	@Default
	public void defaultCommand(CommandSender sender)
	{	
		sender.sendMessage(MessageTool.color("&cUsage: /capturepoint <start/stop/create> <name>"));
	}
}

