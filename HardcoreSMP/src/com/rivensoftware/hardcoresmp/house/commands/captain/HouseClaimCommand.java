package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.claim.ClaimProfile;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseClaimCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("claim")
	@Syntax("claim")
	public void claimCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		player.getInventory().remove(Claim.getWand());

		House house = profile.getHouse();

		if (house == null)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.NOT_IN_HOUSE")));
			return;
		}

		if (!house.getLord().equals(player.getUniqueId()) && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("admin")) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_LORD_OR_CAPTAIN")));
			return;
		}


		if (!(profile.isViewingMap())) 
		{
			if (!Claim.getNearbyClaimsAt(player.getLocation(), 64).isEmpty()) 
			{
				Bukkit.dispatchCommand(player, "h map");
			}
		}

		player.getInventory().addItem(Claim.getWand());
		player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CLAIM.RECEIVED_CLAIMING_WAND")));
		new ClaimProfile(player, house);
	}
}
