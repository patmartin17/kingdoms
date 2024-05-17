package com.rivensoftware.hardcoresmp.profile.teleport;

import org.bukkit.Bukkit;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerInitiateHouseTeleportEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;

public class ProfileTeleportTask extends BukkitRunnable 
{
	private PlayerInitiateHouseTeleportEvent event;

	public PlayerInitiateHouseTeleportEvent getEvent() 
	{
		return this.event;
	}

	public ProfileTeleportTask(PlayerInitiateHouseTeleportEvent event) 
	{
		this.event = event;
		Bukkit.getPluginManager().callEvent((Event)event);
	}

	public void run() 
	{
		if (!this.event.isCancelled()) 
		{
			(new BukkitRunnable() 
			{
				public void run() 
				{
					ProfileTeleportTask.this.event.getPlayer().teleport(ProfileTeleportTask.this.event.getLocation());
				}
			}).runTask((Plugin)HardcoreSMP.getInstance());
			Profile.getByUUID(this.event.getPlayer().getUniqueId()).setTeleportWarmup(null);
		} 
	}
}
