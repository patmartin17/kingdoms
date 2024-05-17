package com.rivensoftware.hardcoresmp.house.listeners;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareAllyEvent;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareNeutralEvent;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareWarEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerDisbandHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerJoinHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class ScoreboardListeners implements Listener 
{
	@EventHandler
	public void onJoinHouse(PlayerJoinHouseEvent event) 
	{
		Bukkit.broadcastMessage("join");
		for (Player player : event.getHouse().getOnlinePlayers()) 
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			//profile.updateTab();
		} 
		for (House ally : event.getHouse().getAllies()) 
		{
			for (Player allyPlayer : ally.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
				//profile.updateTab();
			} 
		} 
	}

	@EventHandler
	public void onLeaveHouse(PlayerLeaveHouseEvent event) 
	{
		Bukkit.broadcastMessage("leave");
		Set<Player> toLoop = new HashSet<>(event.getHouse().getOnlinePlayers());
		toLoop.add(event.getPlayer());
		for (Player player : toLoop) 
		{
			Bukkit.broadcastMessage(player.getName());
			Profile profile = Profile.getByUUID(player.getUniqueId());
			//profile.updateTab();
		} 
		for (House ally : event.getHouse().getAllies()) 
		{
			for (Player allyPlayer : ally.getOnlinePlayers())
			{
				Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
				//profile.updateTab();
			} 
		} 
		
		//update enemies
	}

	@EventHandler
	public void onDisbandHouse(PlayerDisbandHouseEvent event) 
	{
		Bukkit.broadcastMessage("disband");
		for (Player player : event.getHouse().getOnlinePlayers())
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			//profile.updateTab();
		} 
		for (House ally : event.getHouse().getAllies()) 
		{
			for (Player allyPlayer : ally.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
				//profile.updateTab();
			} 
		} 
	}

	@EventHandler
	public void onAllyHouse(HouseDeclareAllyEvent event) 
	{
		Bukkit.broadcastMessage("ally");
		for (House house : event.getHouses()) 
		{
			for (Player player : house.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(player.getUniqueId());
				//profile.updateTab();
			} 
		} 
	}

	@EventHandler
	public void onNeutralHouse(HouseDeclareNeutralEvent event) 
	{
		Bukkit.broadcastMessage("neutral");
		for (House house : event.getHouses()) 
		{
			for (Player player : house.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(player.getUniqueId());
				//profile.updateTab();
			} 
		} 
	}

	@EventHandler
	public void onEnemyHouse(HouseDeclareWarEvent event)
	{
		Bukkit.broadcastMessage("enemy");
		for (House house : event.getHouses()) 
		{
			for (Player player : house.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(player.getUniqueId());
				//profile.updateTab();
			} 
		} 
	}
}
