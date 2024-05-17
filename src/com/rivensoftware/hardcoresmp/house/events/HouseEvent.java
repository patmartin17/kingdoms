package com.rivensoftware.hardcoresmp.house.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class HouseEvent extends Event 
{
	private static final HandlerList handlers = new HandlerList();

	public HandlerList getHandlers() 
	{
		return handlers;
	}

	public static HandlerList getHandlerList() 
	{
		return handlers;
	}
}
