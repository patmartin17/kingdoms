package com.rivensoftware.hardcoresmp.event.capturepoint.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.event.Event;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.event.capturepoint.CapturePointEvent;
import com.rivensoftware.hardcoresmp.tools.DateTool;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("capturepoint|cp")
public class CapturePointStartCommand extends BaseCommand
{
	@Subcommand("start")
	@Syntax("<point>")
	public void capturePointStartCommand(CommandSender sender, String chatType) 
	{

		String[] args = getOrigArgs();
		if (args.length == 0) 
		{
			sender.sendMessage(ChatColor.RED + "/cp start <point> [<duration>]");
			return;
		} 

		Event event = EventManager.getInstance().getByName(args[0]);

		if (event == null || !(event instanceof CapturePointEvent))
		{
			sender.sendMessage(ChatColor.RED + "Please specify a valid capture point.");
			return;
		} 

		long capTime = 900000L;

		if (args.length > 1)
			try
			{
				capTime = System.currentTimeMillis() - DateTool.parseDateDiff(args[1], false);
			}
			catch (Exception exception) 
			{
				exception.printStackTrace();
			}  

		CapturePointEvent capturePoint = (CapturePointEvent)event;

		if (capturePoint.isActive())
		{
			sender.sendMessage(ChatColor.RED + "Capture Point " + capturePoint.getName() + " is already active!");
			return;
		} 
		capturePoint.start(capTime);
	}
}
