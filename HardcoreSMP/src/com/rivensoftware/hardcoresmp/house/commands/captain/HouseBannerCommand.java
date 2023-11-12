package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerViewMenu;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseBannerCommand extends HouseCommand
{	
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("banner")
	public void bannerCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByPlayer(player);
		
		if(getOrigArgs().length > 1 && !player.hasPermission("kingdoms.admin"))
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.BANNER.USAGE")));
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
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.BANNER.NO_ACCESS")));
				return;
			}
			if(!profile.getHouse().getLord().equals(player.getUniqueId()) && !profile.getHouse().getCaptains().contains(player.getUniqueId()))
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_LORD_OR_CAPTAIN")));
				return;
			}
			
			BannerMenu.displayBannerMenu(player);
		}
	}
}
