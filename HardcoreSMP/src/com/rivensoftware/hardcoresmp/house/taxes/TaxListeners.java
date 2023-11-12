package com.rivensoftware.hardcoresmp.house.taxes;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

public class TaxListeners implements Listener
{
	@EventHandler
	public void onLogin(PlayerLoginEvent event)
	{
		//if unpaid
		//if autopay is on
		//if time has passed to paid from taxTask
		//warn captain of unpaid balance and that the royal house has access to your location.
	}
	/*
	 * Idea, royal family has a reason to track you down because the members of the royal house have bounties on you that they can get paid.
	 * based on your balance.
	 * 
	 * "you killed a tax evador"
	 */
}
