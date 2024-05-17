package com.rivensoftware.hardcoresmp.event;

import lombok.Getter;
import org.bukkit.Location;

public class EventZone 
{
	@Getter private final Location firstLocation;

	@Getter private final Location secondLocation;

	public EventZone(Location firstLocation, Location secondLocation) 
	{
		this.firstLocation = firstLocation;
		this.secondLocation = secondLocation;
	}

	public int getFirstX() 
	{
		return Math.min(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
	}

	public int getSecondX() 
	{
		return Math.max(this.firstLocation.getBlockX(), this.secondLocation.getBlockX());
	}

	public int getFirstZ() 
	{
		return Math.min(this.firstLocation.getBlockZ(), this.secondLocation.getBlockZ());
	}

	public int getSecondZ() 
	{
		return Math.max(this.firstLocation.getBlockZ(), this.secondLocation.getBlockZ());
	}

	public int getFirstY() 
	{
		return Math.min(this.firstLocation.getBlockY(), this.secondLocation.getBlockY());
	}

	public int getSecondY() 
	{
		return Math.max(this.firstLocation.getBlockY(), this.secondLocation.getBlockY());
	}

	public boolean isInside(Location location) 
	{
		return (location.getWorld().getName().equalsIgnoreCase(this.firstLocation.getWorld().getName()) && location.getBlockX() >= getFirstX() && location.getBlockX() <= getSecondX() && location.getBlockZ() >= getFirstZ() && location.getBlockZ() <= getSecondZ() && location.getBlockY() >= getFirstY() && location.getBlockY() <= getSecondY());
	}
}
