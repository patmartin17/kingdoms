package com.rivensoftware.hardcoresmp.house.commands;

import org.bukkit.command.CommandSender;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseVersionCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("version")
	public void versionCommand(CommandSender sender) 
	{
	    sender.sendMessage(MessageTool.color("&9HardcoreSMP &7v" + plugin.getDescription().getVersion() + " created by Pattt&9."));
	}

}
