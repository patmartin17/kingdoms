package com.rivensoftware.hardcoresmp.storage.file;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import lombok.Getter;

public class ConfigFile 
{
	@Getter private File file;

	@Getter private YamlConfiguration configuration;

	public ConfigFile(JavaPlugin plugin, String name) 
	{
		this.file = new File(plugin.getDataFolder(), name + ".yml");
		if (!this.file.getParentFile().exists())
			this.file.getParentFile().mkdir(); 
		plugin.saveResource(name + ".yml", false);
		this.configuration = YamlConfiguration.loadConfiguration(this.file);
	}

	public double getDouble(String path) 
	{
		if (this.configuration.getDouble(path) != 0.0)
			return this.configuration.getDouble(path); 
		return 0.0D;
	}

	public int getInt(String path) 
	{
		if (this.configuration.getInt(path) != 0)
			return this.configuration.getInt(path); 	
		return 0;
	}

	public boolean getBoolean(String path) 
	{
		if (this.configuration.getBoolean(path) != false)
			return this.configuration.getBoolean(path); 
		return false;
	}

	public String getString(String path) 
	{
		if (this.configuration.getString(path) != null)
			return ChatColor.translateAlternateColorCodes('&', this.configuration.getString(path)); 
		return "ERROR: STRING NOT FOUND";
	}

	public List<String> getReversedStringList(String path) 
	{
		List<String> list = getStringList(path);
		if (list != null) 
		{
			int size = list.size();
			List<String> toReturn = new ArrayList<>();
			for (int i = size - 1; i >= 0; i--)
				toReturn.add(list.get(i)); 
			return toReturn;
		} 
		return Arrays.asList(new String[] { "ERROR: STRING LIST NOT FOUND!" });
	}

	public List<String> getStringList(String path) 
	{
		if (this.configuration.getStringList(path) != null) 
		{
			ArrayList<String> strings = new ArrayList<>();
			for (String string : this.configuration.getStringList(path))
				strings.add(ChatColor.translateAlternateColorCodes('&', string)); 
			return strings;
		} 
		return Arrays.asList(new String[] { "ERROR: STRING LIST NOT FOUND!" });
	}
	
	public void save()
	{
		try 
		{
			configuration.save(file);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
}
