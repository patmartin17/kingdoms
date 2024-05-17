package com.rivensoftware.hardcoresmp.house.listeners;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.io.File;

public class ChatListeners implements Listener 
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Getter public File file = new File(plugin.getDataFolder(), "/config.yml");
	@Getter public YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);


	@EventHandler(ignoreCancelled = true, priority = EventPriority.NORMAL)
	public void onPlayerChat(AsyncPlayerChatEvent event) 
	{
		if (getConfiguration().getBoolean("HOUSE.HOUSE_CHAT.ENABLED"))
		{
			event.setCancelled(true);
			Player player = event.getPlayer();
			Profile profile = Profile.getByUUID(player.getUniqueId());
			House house = profile.getHouse();
			event.setFormat("{HOUSE}" + event.getFormat());
			String actualMessage = event.getFormat().replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage());
			Bukkit.getLogger().info(ChatColor.stripColor(actualMessage.replace("{HOUSE}", "")));
			for (Player recipient : event.getRecipients()) 
			{
				Profile recipientProfile = Profile.getByUUID(recipient.getUniqueId());
				if (house == null) 
				{
					recipient.sendMessage(MessageTool.color(event.getFormat().replace("{HOUSE}", getConfiguration().getString("HOUSE.HOUSE_CHAT.NO_HOUSE")).replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
					continue;
				} 
				if (recipientProfile.getHouse() != null) 
				{
					if (recipientProfile.getHouse().getUuid().equals(house.getUuid())) 
					{
						recipient.sendMessage(MessageTool.color(event.getFormat().replace("{HOUSE}", getConfiguration().getString("HOUSE.HOUSE_CHAT.FRIENDLY").replace("%TAG%", house.getHouseName())).replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
						continue;
					} 
					if (recipientProfile.getHouse().getAllies().contains(house)) 
					{
						recipient.sendMessage(MessageTool.color(event.getFormat().replace("{HOUSE}", getConfiguration().getString("HOUSE.HOUSE_CHAT.ALLY").replace("%TAG%", house.getHouseName())).replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
						continue;
					} 
				} 
				recipient.sendMessage(MessageTool.color(event.getFormat().replace("{HOUSE}", getConfiguration().getString("HOUSE.HOUSE_CHAT.ENEMY").replace("%TAG%", house.getHouseName())).replace("%1$s", player.getDisplayName()).replace("%2$s", event.getMessage())));
			} 
		} 
	}
}
