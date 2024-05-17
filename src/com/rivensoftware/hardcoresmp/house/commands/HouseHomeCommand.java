package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerInitiateHouseTeleportEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.teleport.ProfileTeleportTask;
import com.rivensoftware.hardcoresmp.profile.teleport.ProfileTeleportType;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;

@CommandAlias("house|h")
public class HouseHomeCommand extends HouseCommand
{

	@Subcommand("home")
	public void homeCommand(CommandSender sender) 
	{
		House house = null;
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.getTeleportWarmup() != null)
			return; 

		if ((getOrigArgs()).length >= 1 && player.hasPermission("kingdoms.admin")) 
		{
			String name = getOrigArgs()[0];
			House otherHouse = House.getAnyByString(name);
			
			house = otherHouse;

			if (house != null) 
			{
				player.teleport(LocationSerialization.deserializeLocation(house.getKingdomCapital()));
				return;
			} 
			else 
			{
				player.sendMessage(MessageTool.color("&cNo online or offline house found with player or name '%NAME%'.").replace("%NAME%", name));
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
		} 

		if (house.getKingdomCapital() == null) 
		{
			player.sendMessage(MessageTool.color("&cKingdom capital not set!"));
			return;
		} 
		Location location = LocationSerialization.deserializeLocation(house.getKingdomCapital());
		Claim claim = Claim.getProminentClaimAt(player.getLocation());

		if (claim != null) 
		{
			player.teleport(LocationSerialization.deserializeLocation(house.getKingdomCapital()));
			return;
		} 

		if (player.hasPermission("kingdoms.admin") && (getOrigArgs()).length >= 1) 
		{
			player.teleport(LocationSerialization.deserializeLocation(house.getKingdomCapital()));
		} 
		else 
		{
			String formatted;
			int time = 0;
			String worldName = player.getLocation().getWorld().getName();
			//String root = "TELEPORT_COUNTDOWN.HOME";
			//for (String world : new String[] { "OVERWORLD", "NETHER", "END" }) 
			//{
			if (worldName.equalsIgnoreCase(worldName)) 
			{
				//DISABLE WORLD HOME TELEPORT (END/HERE)
				//player.sendMessage(this.langConfig.getString("ERROR.NO_HOME_TELEPORT_IN_WORLD"));
				//return;
				time = 10;
			} 
			//} 
			long hours = TimeUnit.SECONDS.toHours(time);
			long minutes = TimeUnit.SECONDS.toMinutes(time) - hours * 60L;
			long seconds = TimeUnit.SECONDS.toSeconds(time) - hours * 60L * 60L + minutes * 60L;

			if (hours == 0L && minutes > 0L && seconds > 0L) 
			{
				formatted = minutes + " minutes and " + seconds + " seconds";
			} 
			else if (hours == 0L && minutes > 0L && seconds == 0L) 
			{
				formatted = minutes + " minutes";
			}
			else if (hours == 0L && minutes == 0L && seconds > 0L) 
			{
				formatted = seconds + " seconds";
			} 
			else if (hours > 0L && minutes > 0L && seconds == 0L) 
			{
				formatted = hours + " hours and " + minutes + " minutes";
			}
			else if (hours > 0L && minutes == 0L && seconds > 0L) 
			{
				formatted = hours + " hours and " + seconds + " seconds";
			} 
			else 
			{
				formatted = hours + "hours, " + minutes + " minutes and " + seconds + " seconds";
			} 
			if (hours == 1L)
				formatted = formatted.replace("hours", "hour"); 

			if (minutes == 1L)
				formatted = formatted.replace("minutes", "minute"); 

			if (seconds == 1L)
				formatted = formatted.replace("seconds", "second"); 

			player.sendMessage(MessageTool.color("&eYou will be teleported to your house home in %TIME%...").replace("%TIME%", formatted + ""));
			profile.setTeleportWarmup(new ProfileTeleportTask(new PlayerInitiateHouseTeleportEvent(player, profile.getHouse(), ProfileTeleportType.HOME_TELEPORT, time, location, player.getLocation())));
			profile.getTeleportWarmup().runTaskLaterAsynchronously(HardcoreSMP.getInstance(), (time * 20));
		} 
	}
	
	@Subcommand("hq")
	public void hqCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
			Bukkit.dispatchCommand(player, "h home");
		else
			sender.sendMessage(MessageTool.color("&7You must be a player to execute this command."));
	}
	
	@Subcommand("capital")
	public void capitalCommandAlias(CommandSender sender) 
	{
		Player player = (Player) sender;
		if(sender instanceof Player)
			Bukkit.dispatchCommand(player, "h home");
		else
			sender.sendMessage(MessageTool.color("&7You must be a player to execute this command."));
	}
	
}
