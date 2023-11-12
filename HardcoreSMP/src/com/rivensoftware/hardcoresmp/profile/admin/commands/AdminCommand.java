package com.rivensoftware.hardcoresmp.profile.admin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.profile.admin.Admin;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("admin|a")
@Description("Main admin command.")
public class AdminCommand extends BaseCommand 
{	
	/*
	 * Main admin mode command. Only usage is /admin or /a. 
	 * 
	 * Puts the admin in opposite mode depending on current state.
	 */
	@Default
	@Syntax("&cUsage: /admin")
	public void adminMode(CommandSender sender)
	{	
		Player player = (Player) sender;
		Admin admin = Admin.getByPlayer(player);

		if(admin == null)
			return;

		if(admin.isInAdminMode())
		{
			//if(plugin.getAdminConfig().getStringList(player.getUniqueId().toString() + ".saved_inventory") != null)
			//{
			//	admin.giveSavedInventory();
			//}	

			player.sendMessage(MessageTool.color("&cYou have exited Admin mode."));
			admin.exitAdminMode();
		}
		else
		{
			player.sendMessage(MessageTool.color("&aYou have entered Admin mode."));
			admin.enterAdminMode();	
			admin.giveAdminInventory();
		}
	}
}