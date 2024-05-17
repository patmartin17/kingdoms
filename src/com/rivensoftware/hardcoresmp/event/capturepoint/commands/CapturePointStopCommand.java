package com.rivensoftware.hardcoresmp.event.capturepoint.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.event.Event;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.event.capturepoint.CapturePointEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

@CommandAlias("capturepoint|cp")
public class CapturePointStopCommand extends BaseCommand
{
	@Subcommand("stop")
	@Syntax("<point>")
	public void capturePointStartCommand(CommandSender sender, String chatType) 
	{
		String[] args = getOrigArgs();
		if (args.length == 0) {
			sender.sendMessage(ChatColor.RED + "/capturepoint stop <koth>");
			return;
		} 
		
		Event event = EventManager.getInstance().getByName(args[0]);
		
		if (event == null || !(event instanceof CapturePointEvent)) 
		{
			sender.sendMessage(ChatColor.RED + "Please specify a valid capture point.");
			return;
		} 
		
		CapturePointEvent capturePoint = (CapturePointEvent)event;
		
		if (!capturePoint.isActive()) 
		{
			sender.sendMessage(ChatColor.RED + "Capture Point " + capturePoint.getName() + " isn't active!");
			return;
		} 
		capturePoint.stop(true);
	}
}
