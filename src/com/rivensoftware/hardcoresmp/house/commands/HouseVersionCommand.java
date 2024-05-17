package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.command.CommandSender;

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
