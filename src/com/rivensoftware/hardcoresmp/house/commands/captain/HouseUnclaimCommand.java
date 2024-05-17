package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.house.banner.BannerType;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

@CommandAlias("house|h")
public class HouseUnclaimCommand extends HouseCommand
{

	@Subcommand("unclaim|unclaimall")
	public void unclaimCommand(CommandSender sender) 
	{
		House house;
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		String[] args = getOrigArgs();
		int argPos = 1;
		boolean skip = false;
		
		if (getOrigArgs().length >= 2 && player.hasPermission("kingdoms.admin"))
		{
			String name = getOrigArgs()[1];
			if (House.getAnyByString(name) != null)
			{
				house = House.getAnyByString(name);
				skip = true;
			} 
			else 
			{
				player.sendMessage(MessageTool.color("&cNo house found with player or name '%NAME%'.").replace("%NAME%", name));
				return;
			} 
		}
		else
		{
			house = profile.getHouse();
			if (house == null)
			{
				Claim claim = Claim.getProminentClaimAt(player.getLocation());
				if (claim != null && player.hasPermission("ekko.admin")) 
				{
					house = claim.getHouse();
				} 
				else
				{
					player.sendMessage(MessageTool.color("&7You're not in a house!"));
					return;
				} 
			} 
			if (house != null && !house.getLord().equals(player.getUniqueId()) && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("kingdoms.admin"))
			{
				player.sendMessage(MessageTool.color("&cYou must be either an captain or the lord of the house to do this!"));
				return;
			} 
		} 
		if (getExecSubcommand().equalsIgnoreCase("unclaimall") || args.length >= argPos) 
		{
			if (!getExecSubcommand().equalsIgnoreCase("unclaimall") && !skip && (args.length != argPos || !args[argPos - 1].equalsIgnoreCase("all")))
			{
				player.sendMessage(MessageTool.color("&cIncorrect usage. &eUse like this: &b/h unclaim &3(<all>)"));
				return;
			} 
			
			if (house != null && !house.getLord().equals(player.getUniqueId()) && !player.hasPermission("kingdoms.admin")) 
			{
				player.sendMessage(MessageTool.color("&cYou must be the lord of the house to do this!"));
				return;
			} 
			
			Set<Claim> set = house.getClaims();
			
			if (set.isEmpty()) 
			{
				player.sendMessage(MessageTool.color("&cYour house does not have any land!"));
				return;
			} 
			
			for (Claim claim : set) 
			{
				if (house.getKingdomCapital() != null && claim.isInside(LocationSerialization.deserializeLocation(house.getKingdomCapital())))
					house.setKingdomCapital(null); 
				claim.remove();
			} 
			
			String message = MessageTool.color("&ePlayer &a%PLAYER%&e has unclaimed all your land!").replace("%PLAYER%", player.getName());
			
			house.sendMessage(message);
			if (!house.getOnlinePlayers().contains(player))
				player.sendMessage(message); 
			return;
		} 
		Location location = player.getLocation();
		List<Claim> claims = Claim.getClaimsAt(location);
		if (claims != null) 
		{
			String message = MessageTool.color("&ePlayer &a%PLAYER%&e has unclaimed land!").replace("%PLAYER%", player.getName());
			for (Claim claim : claims) 
			{
				if (claim.getHouse() == house && claim.isInside(location)) 
				{
					if (house.getKingdomCapital() != null && claim.isInside(LocationSerialization.deserializeLocation(house.getKingdomCapital())))
						house.setKingdomCapital(null); 
					
					for(Banner banner : house.getBannerList())
					{
						if(claim.isInside(location))
						{
							if(!banner.getLocation().getBlock().getType().name().contains("BANNER"))
								continue;
							
							String blockData[] = banner.getLocation().getBlock().getBlockData().getAsString().split("facing=");
							String blockFaceString = blockData[1].replace("]", "").replace("=", "");
							
							profile.getHouse().getBannerList().remove(banner);
							Banner.activeBanners.remove(banner.getLocation());
							Banner.removeHouseBannerBlock(player, banner.getLocation().getBlock(), blockFaceString);
							
							ItemStack item = Banner.getBannerItem(BannerType.WALL, house, banner.getEffect().getEffectName());
							
							if(player.getInventory().firstEmpty() == -1)
							{
								player.getWorld().dropItem(player.getLocation(), item);
							}
							else
							{
								player.getInventory().addItem(item);
							}
							
						}
					}
					
					claim.remove();
					house.sendMessage(message);
					if (!house.getOnlinePlayers().contains(player))
						player.sendMessage(message); 
					return;
				} 
			} 
		} 
		player.sendMessage(MessageTool.color("&cYou must be in our own land to unclaim!"));
	}
}
