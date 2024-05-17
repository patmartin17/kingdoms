package com.rivensoftware.hardcoresmp.event;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class EventManager 
{
	private static EventManager instance = new EventManager(HardcoreSMP.getInstance());

	@Getter private HardcoreSMP plugin;

	@Getter private List<Event> events;

	public EventManager(HardcoreSMP plugin) 
	{
		this.plugin = plugin;
		this.events = new ArrayList<Event>();
	}

	public Event getByName(String name) 
	{
		for (Event event : this.events) 
		{
			if (event.getName().equalsIgnoreCase(name))
				return event; 
		} 
		return null;
	}

	public static EventManager getInstance() 
	{
		return instance;
	}
}
