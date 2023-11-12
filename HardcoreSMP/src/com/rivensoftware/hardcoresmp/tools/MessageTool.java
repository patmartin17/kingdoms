package com.rivensoftware.hardcoresmp.tools;

import org.bukkit.ChatColor;

public class MessageTool 
{
	public static String prefix = color("&9&lHousing &7&l ");
	
	public static String color(String string)
	{
		return ChatColor.translateAlternateColorCodes('&', string);				
	}
	
	public static String getPrettyName(String string)
	{
		String finalString = "";
		
		String[] split = string.split("_");
		for(String parts : split)
		{
			String capital = parts.substring(0, 1);
			finalString = finalString + " " + capital + (parts.toLowerCase()).substring(1);
		}
		return color("&f" + finalString.substring(1));
 	}
}
