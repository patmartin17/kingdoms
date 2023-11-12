package com.rivensoftware.hardcoresmp.messages;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import lombok.Getter;

public class MessageHandler 
{
	@Getter private int index;
	
	public MessageHandler(HardcoreSMP plugin)
	{
		index = plugin.getMessageConfig().getInt("CURRENT_INDEX");
		messages(plugin);
	}
	
	public void messages(HardcoreSMP plugin)
	{
		new BukkitRunnable() 
		{
			
			@Override
			public void run()
			{
				if(Boolean.valueOf(plugin.getMessageConfig().getBoolean("MESSAGES.MESSAGE_LIST." + index + ".WRAPPERS")))
					Bukkit.broadcastMessage(MessageTool.color(plugin.getMessageConfig().getString("MESSAGES.WRAPPER")));
				for(String string : plugin.getMessageConfig().getConfiguration().getStringList("MESSAGES.MESSAGE_LIST." + index + ".LIST"))
				{
					Bukkit.broadcastMessage(MessageTool.color(string));
				}
				if(Boolean.valueOf(plugin.getMessageConfig().getBoolean("MESSAGES.MESSAGE_LIST." + index + ".WRAPPERS")))
					Bukkit.broadcastMessage(MessageTool.color(plugin.getMessageConfig().getString("MESSAGES.WRAPPER")));

				if(!plugin.getMessageConfig().getConfiguration().getStringList("MESSAGES.MESSAGE_LIST." + (index + 1) + ".LIST").isEmpty())
				{
					plugin.getMessageConfig().getConfiguration().set("CURRENT_INDEX", (index + 1));	
					index = index + 1;
				}
				else
				{
					plugin.getMessageConfig().getConfiguration().set("CURRENT_INDEX", 0);	
					index = 0;
				}
				plugin.getMessageConfig().save();
			}
			
		}.runTaskTimer(plugin, 0, plugin.getMessageConfig().getInt("MESSAGE_INTERVAL"));
	}
}
