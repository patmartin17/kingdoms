package com.rivensoftware.hardcoresmp.house.commands.lord;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.mongodb.client.model.Filters;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerDisbandHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@CommandAlias("house|h")
public class HouseDisbandCommand extends HouseCommand
{
	private static HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("disband")
	public void disbandCommand(CommandSender sender) 
	{
		House house;
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		if (getOrigArgs().length >= 1 && player.hasPermission("kingdoms.admin")) {
			String name = getOrigArgs()[0];
			
			if (House.getAnyByString(name) != null) 
			{
				house = House.getAnyByString(name);
			} 
			else 
			{
				player.sendMessage(MessageTool.color("&cNo houses found with player or name '%NAME%'.").replace("%NAME%", name));
				return;
			} 
		} 
		else 
		{
			house = profile.getHouse();
			if (house == null) 
			{
				player.sendMessage(MessageTool.color("&7You're not in a house!"));
				return;
			} 
			if (!house.getLord().equals(player.getUniqueId())) 
			{
				player.sendMessage(MessageTool.color("&cYou must be the lord of your house to do this!"));
				return;
			} 
		}
		
		Banner.removeHouseBanners(player);
		
		for (UUID member : house.getAllPlayerUuids()) 
		{
			Profile memberProfile = Profile.getByUUID(member);
			if (memberProfile != null && memberProfile.getHouse().equals(house))
				memberProfile.setHouse(null); 
		} 
		
		
		plugin.getEconomy().depositPlayer((OfflinePlayer)player, house.getBalance());
		Bukkit.getPluginManager().callEvent((Event)new PlayerDisbandHouseEvent(player, house));
		Bukkit.broadcastMessage(MessageTool.color("&eHouse &9%NAME%&e has been &cdisbanded&e by &f%PLAYER%").replace("%PLAYER%", player.getName()).replace("%NAME%", house.getHouseName()));
		House.getHouses().remove(house.getUuid());
		plugin.getKingdomsDatabase().getHouses().deleteOne(Filters.eq("uuid", house.getUuid().toString()));
		for (House ally : house.getAllies())
			ally.getAllies().remove(house); 
		Set<Claim> claims = new HashSet<>(house.getClaims());
		for (Claim claim : claims)
			claim.remove(); 
	}

}
