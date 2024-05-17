package com.rivensoftware.hardcoresmp.profile.protection;

import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.thebigdolphin1.tagspawnprotection.region.Region;
import com.thebigdolphin1.tagspawnprotection.region.RegionDataManager;

import lombok.Getter;

@Getter
public class ProfileProtection
{
	public static final int DEFAULT_DURATION = 3600;

	private long createdAt;

	private long duration;

	private boolean paused;

	public ProfileProtection(long duration) 
	{
		this.createdAt = System.currentTimeMillis();
		this.duration = duration * 1000L + 999L;
	}

	public String getTimeLeft() 
	{
		if((getDurationLeft() * 1000) <= 0)
			return "00:00";
		return DurationFormatUtils.formatDuration((getDurationLeft() * 1000), "mm:ss");
	}

	public int getDurationLeft() 
	{
		if (this.paused)
			return (int)this.duration / 1000; 
		return (int)(this.createdAt + this.duration - System.currentTimeMillis()) / 1000;
	}

	public void pause() 
	{
		this.duration = (getDurationLeft() * 1000);
		this.paused = true;
	}

	public void unpause() 
	{
		this.paused = false;
		this.createdAt = System.currentTimeMillis() + 999L;
	}

	public static void run(HardcoreSMP plugin) 
	{
		(new BukkitRunnable() 
		{
			public void run() 
			{
				for (Profile profile : Profile.getProfiles()) 
				{
					ProfileProtection protection = profile.getProtection();
					if (protection != null) 
					{
						if (protection.getDurationLeft() <= 0L) 
						{
							profile.setProtection(null);
							continue;
						} 
						Player player = Bukkit.getPlayer(profile.getUuid());
						if (player != null) 
						{
							if(!isWithin(player.getLocation()))
							{
								if (protection.isPaused()) 
								{
									protection.unpause();
									profile.setLeftSpawn(true);
									continue;
								}
							} 
							else
							{
								protection.pause();
								profile.setLeftSpawn(false);
								continue;
							}
						} 
					} 
				} 
			}
		}).runTaskTimerAsynchronously(plugin, 2L, 2L);
	}

	public static boolean isWithin(Location location) 
	{
		for(Region region : RegionDataManager.getRegions())
		{
			if(region.getAreaDeny().isWithin(location))
				return true;
		}
		return false;
	}
}
