package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.UUID;

@CommandAlias("house|h")
public class HouseInvitesCommand extends HouseCommand
{

	@Subcommand("invites")
	public void invitesCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		String invites = "";
		HashSet<House> housesInvitedTo = new HashSet<House>();
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		for (House houses : House.getHouses().values()) 
		{
			if (houses.getInvitedPlayers().containsKey(player.getUniqueId()))
				housesInvitedTo.add(houses); 
		} 
		
		String splitter = "&e, &7";
		if (housesInvitedTo.isEmpty()) 
		{
			invites = "&7No pending invites.";
		}
		else 
		{
			for (House houses : housesInvitedTo)
				invites = houses.getHouseName() + splitter; 
			invites = invites.substring(0, invites.lastIndexOf(splitter));
		} 
		
		player.sendMessage(MessageTool.color("&eYour invites: &7%INVITES%").replace("%INVITES%", invites));
		
		if (profile.getHouse() != null) 
		{
			House house = profile.getHouse();
			
			if (!house.getInvitedPlayers().isEmpty()) 
			{
				String invitedPlayers = "";
				for (UUID invitedPlayer : house.getInvitedPlayers().keySet()) 
				{
					SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByUuid(invitedPlayer);
					if (offlinePlayer != null)
						invitedPlayers = invitedPlayers + offlinePlayer.getName() + splitter; 
				} 
				
				invitedPlayers = invitedPlayers.substring(0, invitedPlayers.lastIndexOf(splitter));
				player.sendMessage(MessageTool.color("&eInvited to your team: &7%INVITES%").replace("%INVITES%", invitedPlayers));
			} 
		} 
	}
}
