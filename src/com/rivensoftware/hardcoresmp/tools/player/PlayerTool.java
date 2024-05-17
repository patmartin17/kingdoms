package com.rivensoftware.hardcoresmp.tools.player;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerTool 
{
	public static Collection<? extends Player> getOnlinePlayers() 
	{
		return Bukkit.getOnlinePlayers();
	}

	public static Set<String> getConvertedUuidSet(Set<UUID> uuids) 
	{
		Set<String> toReturn = new HashSet<>();
		for (UUID uuid : uuids)
			toReturn.add(uuid.toString()); 
		return toReturn;
	}
}
