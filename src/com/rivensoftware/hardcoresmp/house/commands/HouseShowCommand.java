package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@CommandAlias("house|h")
public class HouseShowCommand extends HouseCommand
{	
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Subcommand("show|who|info|house")
	public void showCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		if (getOrigArgs().length == 0) 
		{
			House house = profile.getHouse();
			if (house == null) 
			{
				player.sendMessage(MessageTool.color("&7You do not belong to a house!"));
				return;
			} 
			sendHouseInformation(player, house);
			return;
		} 

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < getOrigArgs().length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 

		String name = sb.toString().trim().replace(" ", "");
		Set<House> matchedHouses = House.getAllByString(name);

		if (matchedHouses.isEmpty()) 
		{
			player.sendMessage(MessageTool.color("&cNo house found with player or name '%NAME%'.").replace("%NAME%", name));
			return;
		} 
		for (House houses : matchedHouses)
			sendHouseInformation(player, houses); 
	}

	@SuppressWarnings("deprecation")
	private void sendHouseInformation(Player player, House house) 
	{
		List<String> toSend = new ArrayList<String>();

		ChatColor offlineColor = ChatColor.valueOf(plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.OFFLINE_COLOR").toUpperCase());
		ChatColor onlineColor = ChatColor.valueOf(plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.ONLINE_COLOR").toUpperCase());
		ChatColor powerlessColor = ChatColor.valueOf(plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.LIVES_COLOR.POWERLESS").toUpperCase());
		ChatColor powerColor = ChatColor.valueOf(plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.LIVES_COLOR.POWER").toUpperCase());
		String splitNamesFormat = plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.SPLIT_NAMES.FORMAT");
		boolean splitNamesEnabled = plugin.getHouseConfig().getBoolean("HOUSE_SHOW.SETTINGS.SPLIT_NAMES.ENABLED");

		for (String string : plugin.getHouseConfig().getStringList("HOUSE_SHOW.MESSAGE")) 
		{
			string = string.replace("%HOUSE%", house.getHouseName());
			string = string.replace("%ONLINE_COUNT%", house.getOnlinePlayers().size() + "");
			string = string.replace("%MAX_COUNT%", house.getAllPlayerUuids().size() + "");
			if (string.contains("%CAPITAL%"))

				if (house.getKingdomCapital() == null) 
				{
					string = string.replace("%CAPITAL%", plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.CAPITAL_PLACEHOLDER"));
				} 
				else 
				{
					Location homeLocation = LocationSerialization.deserializeLocation(house.getKingdomCapital());
					string = string.replace("%CAPITAL%", homeLocation.getBlockX() + ", " + homeLocation.getBlockZ());
				} 

			if (string.contains("%LORD%")) 
			{
				String lordString;
				UUID lord = house.getLord();
				SimpleOfflinePlayer lordPlayer = SimpleOfflinePlayer.getByUuid(lord);
				if (lordPlayer == null)
					continue; 

				if (Bukkit.getPlayer(lord) == null) 
				{
					lordString = offlineColor + lordPlayer.getName();
				} 
				else 
				{
					lordString = onlineColor + lordPlayer.getName();
				} 

				string = string.replace("%LORD%", lordString);
			} 
			if (string.contains("%CAPTAINS%")) 
			{
				String captainString = "";
				if (house.getCaptains().isEmpty())
					continue; 

				for (UUID uuid : house.getCaptains()) 
				{
					SimpleOfflinePlayer captain = SimpleOfflinePlayer.getByUuid(uuid);
					if (captain == null)
						continue; 
					if (Bukkit.getPlayer(uuid) == null) 
					{
						captainString = captainString + offlineColor + captain.getName();
					} 
					else 
					{
						captainString = captainString + onlineColor + captain.getName();
					} 

					if (splitNamesEnabled)
						captainString = captainString + splitNamesFormat; 
				} 
				string = string.replace("%CAPTAINS%", captainString);
			} 

			if (string.contains("%MEMBERS%")) 
			{
				String memberString = "";
				if (house.getMembers().isEmpty())
					continue; 

				for (UUID uuid : house.getMembers()) 
				{
					SimpleOfflinePlayer member = SimpleOfflinePlayer.getByUuid(uuid);
					if (member == null)
						continue; 

					if (Bukkit.getPlayer(uuid) == null) 
					{
						memberString = memberString + offlineColor + member.getName();
					} 
					else 
					{
						memberString = memberString + onlineColor + member.getName();
					} 

					if (splitNamesEnabled)
						memberString = memberString + splitNamesFormat; 
				} 

				string = string.replace("%MEMBERS%", memberString);
			} 

			if (string.contains("%ALLIES%")) 
			{
				if (house.getAllies().isEmpty())
					continue; 

				ChatColor allyColor = ChatColor.BLUE;
				String allies = "";

				for (House ally : house.getAllies()) 
				{
					allies = allies + allyColor + ally.getHouseName();

					if (splitNamesEnabled)
						allies = allies + splitNamesFormat; 
				} 

				string = string.replace("%ALLIES%", allies);
			} 
			
			if (string.contains("%ENEMIES%")) 
			{
				if (house.getEnemies().isEmpty())
					continue; 

				ChatColor enemeyColor = ChatColor.RED;
				String enemies = "";

				for (House enemy : house.getEnemies()) 
				{
					enemies = enemies + enemeyColor + enemy.getHouseName();

					if (splitNamesEnabled)
						enemies = enemies + splitNamesFormat; 
				} 

				string = string.replace("%ENEMIES%", enemies);
			} 

			if (string.contains("%LIVES%"))
				if (house.isPowerless()) 
				{
					string = string.replace("%LIVES%", powerlessColor + "" + house.getLives());
				} 
				else 
				{
					string = string.replace("%LIVES%", powerColor + "" + house.getLives());
				}  

			if (string.contains("%LIVES_SYMBOL%"))
				if (house.getLives() > 0) 
				{
					string = string.replace("%LIVES_SYMBOL%", plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.LIVES_SYMBOL.POWER"));
				} 
				else 
				{
					string = string.replace("%LIVES_SYMBOL%", plugin.getHouseConfig().getString("HOUSE_SHOW.SETTINGS.LIVES_SYMBOL.POWERLESS"));
				} 

			string = string.replace("%BALANCE%", house.getBalance() + "");

			if (string.contains("%ANNOUNCEMENT%"))
			{
				if (house.getAnnouncement() == null || !house.getOnlinePlayers().contains(player))
					continue; 
				string = string.replace("%ANNOUNCEMENT%", house.getAnnouncement());
			} 

			if (splitNamesEnabled && string.contains(splitNamesFormat))
				string = string.substring(0, string.lastIndexOf(splitNamesFormat)); 
			toSend.add(string);
		} 

		for (String message : toSend)
		{
			if (message.contains("%BANNER%")) 
			{
				if(house.getPrimaryBanner() != null)
				{
					TextComponent changeMessage = new TextComponent(message.replace("%BANNER%", ""));
					TextComponent component = new TextComponent(MessageTool.color("&7[View Banner]"));
					component.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/h banner " + house.getHouseName()));
					component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder("Click to view " + house.getHouseName() + "'s banner").create()));

					player.spigot().sendMessage(changeMessage, component);	
				}
				else
				{
					TextComponent changeMessage = new TextComponent(message.replace("%BANNER%", ""));
					TextComponent component = new TextComponent(MessageTool.color("&7[No Banner Set]"));
					component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(house.getHouseName() + " has no banner set").create()));

					player.spigot().sendMessage(changeMessage, component);	
				}
			}	
			else
			{
				player.sendMessage(MessageTool.color(message)); 		
			}
		}
	}

}
