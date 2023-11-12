package com.rivensoftware.hardcoresmp.house.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.chat.ProfileChatType;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseChatCommand extends HouseCommand implements Listener
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("chat|c")
	@Syntax("<type>")
	public void createHouseCommand(CommandSender sender, String chatType) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		List<String> chatTypes = new ArrayList<>(Arrays.asList(new String[] { "a", "ally", "h", "house", "p", "public" }));
		Profile profile = Profile.getByUUID(player.getUniqueId());
		
		if (args.length == 0 || !chatTypes.contains(args[0])) 
		{
			ProfileChatType toToggle = getToToggle(profile);
			if (toToggle != ProfileChatType.PUBLIC && profile.getHouse() == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_IN_HOUSE")));
				return;
			} 
			setChatType(player, toToggle);
			return;
		} 
		
		String arg = args[0];
		
		if (arg.equalsIgnoreCase("public") || arg.equalsIgnoreCase("p")) 
		{
			setChatType(player, ProfileChatType.PUBLIC);
			return;
		} 
		
		if (profile.getHouse() == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_IN_HOUSE")));
			return;
		} 
		
		if (arg.equalsIgnoreCase("a") || arg.equalsIgnoreCase("ally") || arg.equalsIgnoreCase("alliance")) 
		{
			setChatType(player, ProfileChatType.ALLY);
			return;
		} 
		
		if (arg.equalsIgnoreCase("h") || arg.equalsIgnoreCase("house"))
			setChatType(player, ProfileChatType.HOUSE); 
	}

	private ProfileChatType getToToggle(Profile profile) 
	{
		if (profile.getHouse() == null && profile.getChatType() != ProfileChatType.PUBLIC)
			return ProfileChatType.PUBLIC; 
		switch (profile.getChatType()) 
		{
		case HOUSE:
			return ProfileChatType.ALLY;
		case ALLY:
			return ProfileChatType.PUBLIC;
		case PUBLIC:
			return ProfileChatType.HOUSE;
		default:
			break;
		} 
		return null;
	}

	private void setChatType(Player player, ProfileChatType type) 
	{
		Profile profile = Profile.getByUUID(player.getUniqueId());
		profile.setChatType(type);
		switch (type) 
		{
		case PUBLIC:
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.PUBLIC_CHAT")));
			break;
		case HOUSE:
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.HOUSE_CHAT")));
			break;
		case ALLY:
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.ALLIANCE_CHAT")));
			break;
		default:
			break;
		} 
	}

	@EventHandler(ignoreCancelled = true, priority = EventPriority.LOWEST)
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		if (event.getMessage().startsWith("!") && event.getMessage().length() > 1) 
		{
			event.setMessage(event.getMessage().substring(1, event.getMessage().length()));
			return;
		} 
		
		if (event.getMessage().startsWith("@") && event.getMessage().length() > 1) 
		{
			event.setCancelled(true);
			
			if (house == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_IN_HOUSE")));
				return;
			} 
			house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.HOUSE_MESSAGE")).replace("%PLAYER%", player.getName()).replace("%MESSAGE%", event.getMessage().substring(1, event.getMessage().length())).replace("%HOUSE%", house.getHouseName()));
			return;
		} 
		
		if (event.getMessage().startsWith("#") && event.getMessage().length() > 1) 
		{
			event.setCancelled(true);
			if (house == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_IN_HOUSE")));
				return;
			} 
			String message = MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.ALLIANCE_MESSAGE")).replace("%PLAYER%", player.getName()).replace("%MESSAGE%", event.getMessage()).replace("%HOUSE%", house.getHouseName());
			house.sendMessage(message);
			for (House allyHouse : house.getAllies())
				allyHouse.sendMessage(message); 
		} 
		
		boolean inHouseChat = (profile.getChatType() == ProfileChatType.HOUSE);
		
		if (inHouseChat || profile.getChatType() == ProfileChatType.ALLY) 
		{
			event.setCancelled(true);
			if (house == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.MUST_BE_IN_HOUSE")));
				return;
			} 
			if (inHouseChat) 
			{
				house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.HOUSE_MESSAGE")).replace("%PLAYER%", player.getName()).replace("%MESSAGE%", event.getMessage()).replace("%HOUSE%", house.getHouseName()));
			}
			else
			{
				String message = MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.CHAT.ALLIANCE_MESSAGE")).replace("%PLAYER%", player.getName()).replace("%MESSAGE%", event.getMessage()).replace("%HOUSE%", house.getHouseName());
				house.sendMessage(message);
				for (House allyHouse : house.getAllies())
					allyHouse.sendMessage(message); 
			} 
		} 
	}
}
