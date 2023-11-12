package com.rivensoftware.hardcoresmp.profile.admin.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.profile.admin.Admin;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Description;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("vanish|v")
@Description("Main admin command.")
public class VanishCommand extends BaseCommand 
{
	@Default
    @Syntax("&cUsage: /vanish")
	public void vanishMode(CommandSender sender)
	{	
		Player player = (Player) sender;
		Admin admin = Admin.getByPlayer(player);
		
		if(admin == null)
			return;
		
		if(admin.isInVanish())
		{
			admin.exitVanishMode();
		}
		else
		{
			admin.enterVanishMode();	
		}
	}
}