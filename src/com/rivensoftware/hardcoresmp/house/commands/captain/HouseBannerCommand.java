package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerViewMenu;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("house|h")
public class HouseBannerCommand extends HouseCommand
{	
	@Subcommand("banner")
	public void bannerCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByPlayer(player);
		
		if(getOrigArgs().length > 1 && !player.hasPermission("kingdoms.admin"))
		{
			player.sendMessage(MessageTool.color("&cUsage: /h banner"));
			return;
		}
		
		if(getOrigArgs().length == 1)
		{
			String houseName = getOrigArgs()[0];
			
			House house = House.getByName(houseName);
			
			BannerViewMenu.displayBannerViewMenu(player, house);
		}
		else
		{
			if(profile.getHouse() == null)
			{
				player.sendMessage(MessageTool.color("&cYou must belong to a house to access the banner menu."));
				return;
			}
			if(!profile.getHouse().getLord().equals(player.getUniqueId()) && !profile.getHouse().getCaptains().contains(player.getUniqueId()))
			{
				player.sendMessage(MessageTool.color("&cYou must be a lord or a captain to set the house banner."));
				return;
			}
			
			BannerMenu.displayBannerMenu(player);
		}
	}
}
