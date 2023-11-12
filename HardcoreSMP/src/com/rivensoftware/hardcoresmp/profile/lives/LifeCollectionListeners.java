package com.rivensoftware.hardcoresmp.profile.lives;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class LifeCollectionListeners implements Listener
{
	/*
	 * Life listeners are how you get lives and how lives work
	 */
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent event)
	{
		/*
		 * Rare chance of getting part of a life while mining
		 */
	}
}
