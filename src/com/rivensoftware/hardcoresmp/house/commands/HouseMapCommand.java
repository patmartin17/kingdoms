package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.claim.ClaimPillar;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

@CommandAlias("house|h")
public class HouseMapCommand extends HouseCommand
{
	@Subcommand("map")
	@Syntax("map")
	public void showMapCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isViewingMap()) 
		{
			for (ClaimPillar claimPillar : profile.getMapPillars()) 
			{
				claimPillar.remove();
			}
			profile.getMapPillars().clear();
			player.sendMessage(MessageTool.color("&eMap pillars hidden..."));
			profile.setViewingMap(false);
			return;
		}

		Set<Claim> toDisplay = new HashSet<Claim>();
		int[] pos = new int[]{player.getLocation().getBlockX(), player.getLocation().getBlockZ()};

		for (int x = pos[0] - 64; x < pos[0] + 64; x++) 
		{
			for (int z = pos[1] - 64; z < pos[1] + 64; z++) 
			{
				Location location = new Location(player.getWorld(), x, 0, z);
				ArrayList<Claim> claims = Claim.getClaimsAt(location);
				if (claims != null) 
				{
					for (Claim claim : claims) 
					{
						if (claim.getWorldName().equalsIgnoreCase(location.getWorld().getName())) 
						{
							toDisplay.add(claim);
						}
					}
				}
			}
		}

		if (toDisplay.isEmpty()) 
		{
			player.sendMessage(MessageTool.color("&cThere are no nearby claims to display!"));
			return;
		}

		Map<House, Material> shown = new HashMap<House, Material>();
		for (Claim claim : toDisplay) 
		{
			House house = claim.getHouse();

			Material material;

			if (house == profile.getHouse()) 
			{
				material = Material.DIAMOND_BLOCK;
			} 
			else if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
			{
				material = Material.LAPIS_BLOCK;
			} 
			else 
			{
				material = Material.REDSTONE_BLOCK;
			}
			if (!(shown.containsKey(house))) 
			{
				shown.put(claim.getHouse(), material);

				for (Location corner : claim.getCorners()) 
				{
					profile.getMapPillars().add(new ClaimPillar(player, corner).show(material, 0));
				}

				String name = material.name().toLowerCase();
				name = name.replace("_", " ");
				String[] segments = name.split(" ");
				name = "";
				for (String segment : segments) 
				{
					segment = segment.substring(0, 1).toUpperCase() + segment.substring(1, segment.length());
					if (name.equals("")) 
					{
						name = segment;
					} 
					else 
					{
						name = name + " " + segment;
					}
				}

				if (profile.getHouse() == house) 
				{
					player.sendMessage(MessageTool.color("&eDisplaying &a%HOUSE%&e with &b%BLOCK%&e.").replace("%BLOCK%", name).replace("%HOUSE%", house.getHouseName()));
				} 
				else if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
				{
					player.sendMessage(MessageTool.color("&eDisplaying &9%HOUSE%&e with &9%BLOCK%&e.").replace("%BLOCK%", name).replace("%HOUSE%", house.getHouseName()));
				} 
				else 
				{
					player.sendMessage(MessageTool.color("&eDisplaying &c%HOUSE%&e with &c%BLOCK%&e.").replace("%BLOCK%", name).replace("%HOUSE%", house.getHouseName()));
				}

			} 
			else 
			{
				for (Location corner : claim.getCorners()) 
				{
					profile.getMapPillars().add(new ClaimPillar(player, corner).show(shown.get(claim.getHouse()), 0));
				}
			}
		}

		profile.setViewingMap(true);

	}
}
