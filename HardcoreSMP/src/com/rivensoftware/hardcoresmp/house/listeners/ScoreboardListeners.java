package com.rivensoftware.hardcoresmp.house.listeners;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareAllyEvent;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareNeutralEvent;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareWarEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerDisbandHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerJoinHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;

public class ScoreboardListeners implements Listener 
{
	@EventHandler
	public void onJoinHouse(PlayerJoinHouseEvent event) 
	{
		for (Player player : event.getHouse().getOnlinePlayers())
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			profile.updateTeams();
		} 
		for (House ally : event.getHouse().getAllies()) 
		{
			for (Player allyPlayer : ally.getOnlinePlayers())
			{
				Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
				profile.updateTeams();
			} 
		} 
	}

	@EventHandler
	public void onLeaveHouse(PlayerLeaveHouseEvent event) 
	{
		Set<Player> toLoop = new HashSet<>(event.getHouse().getOnlinePlayers());
		toLoop.add(event.getPlayer());
		for (Player player : toLoop) 
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			profile.updateTeams();
		} 
		for (House ally : event.getHouse().getAllies()) 
		{
			for (Player allyPlayer : ally.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
				profile.updateTeams();
			} 
		} 
	}

	@EventHandler
	public void onDisbandHouse(PlayerDisbandHouseEvent event) 
	{
		for (Player player : event.getHouse().getOnlinePlayers()) 
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			profile.updateTeams();
		} 
		for (House ally : event.getHouse().getAllies()) 
		{
			for (Player allyPlayer : ally.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
				profile.updateTeams();
			} 
		} 
	}

	@EventHandler
	public void onAllyHouse(HouseDeclareAllyEvent event) 
	{
		for (House house : event.getHouses()) 
		{
			for (Player player : house.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(player.getUniqueId());
				profile.updateTeams();
			} 
		} 
	}
	
	@EventHandler
	public void onNeutralHouse(HouseDeclareNeutralEvent event) 
	{
		for (House house : event.getHouses()) 
		{
			for (Player player : house.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(player.getUniqueId());
				profile.updateTeams();
			} 
		} 
	}

	@EventHandler
	public void onEnemyHouse(HouseDeclareWarEvent event) 
	{
		for (House house : event.getHouses()) 
		{
			for (Player player : house.getOnlinePlayers()) 
			{
				Profile profile = Profile.getByUUID(player.getUniqueId());
				profile.updateTeams();
			} 
		} 
	}
}
