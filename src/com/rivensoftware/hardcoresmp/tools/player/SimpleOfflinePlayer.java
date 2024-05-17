package com.rivensoftware.hardcoresmp.tools.player;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SimpleOfflinePlayer implements Serializable 
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2710475582668478652L;

	private static Set<SimpleOfflinePlayer> offlinePlayers = new HashSet<SimpleOfflinePlayer>();

	private static HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Getter @Setter private String name;

	@Getter @Setter private UUID uuid;

	@Getter @Setter private int kills;

	@Getter @Setter private int deaths;

	public SimpleOfflinePlayer(String name, UUID uuid) 
	{
		this.name = name;
		this.uuid = uuid;
		offlinePlayers.add(this);
	}

	public SimpleOfflinePlayer(Player player) 
	{
		this(player.getName(), player.getUniqueId());
	}

	public static void save(JavaPlugin main) throws IOException 
	{
		if (!offlinePlayers.isEmpty()) 
		{
			File file = new File(main.getDataFolder(), "offlineplayers.json");
			if (!file.exists()) 
			{
				plugin.saveResource("offlineplayers.json", false);
			}
			FileWriter writer = new FileWriter(file);
			Gson gson = (new GsonBuilder()).setPrettyPrinting().create();
			String json = gson.toJson(offlinePlayers);
			writer.write(json);
			writer.close();
		} 
	}

	public static void load(JavaPlugin main) 
	{
		File file = new File(main.getDataFolder(), "offlineplayers.json");

		if (file.exists()) 
		{
			Gson gson = new Gson();
			try 
			{
				BufferedReader reader = new BufferedReader(new FileReader(file));
				Type type = (new TypeToken<Set<SimpleOfflinePlayer>>() {

				}).getType();
				@SuppressWarnings("unchecked")
				Set<SimpleOfflinePlayer> set = (Set<SimpleOfflinePlayer>)gson.fromJson(reader, type);
				if (set != null)
					offlinePlayers.addAll(set); 
			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
		} 
		for (Player player : PlayerTool.getOnlinePlayers()) 
		{
			if (getByUuid(player.getUniqueId()) == null)
				new SimpleOfflinePlayer(player); 
		} 
	}

	public static SimpleOfflinePlayer getByUuid(UUID uuid) 
	{
		for (SimpleOfflinePlayer offlinePlayer : getOfflinePlayers()) 
		{
			if (offlinePlayer.getUuid().equals(uuid))
				return offlinePlayer; 
		} 
		return null;
	}

	public static SimpleOfflinePlayer getByName(String name) 
	{
		for (SimpleOfflinePlayer offlinePlayer : getOfflinePlayers()) 
		{
			if (offlinePlayer.getName().equalsIgnoreCase(name))
				return offlinePlayer; 
		} 
		return null;
	}

	public static Set<SimpleOfflinePlayer> getOfflinePlayers() 
	{
		return offlinePlayers;
	}
}
