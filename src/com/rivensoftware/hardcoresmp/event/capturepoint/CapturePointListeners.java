package com.rivensoftware.hardcoresmp.event.capturepoint;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.event.Event;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

public class CapturePointListeners implements Listener 
{
	public CapturePointListeners() 
	{
		(new BukkitRunnable() 
		{
			public void run() 
			{
				for (Event possibleEvent : EventManager.getInstance().getEvents()) 
				{
					if (possibleEvent instanceof CapturePointEvent && possibleEvent.isActive()) 
					{
						CapturePointEvent capturePoint = (CapturePointEvent)possibleEvent;
						if (capturePoint.isFinished()) 
						{
							capturePoint.stop(false);
							continue;
						} 
						if (capturePoint.getCappingPlayer() != null) 
						{
							Player player = capturePoint.getCappingPlayer();
							if (player.isDead() || !player.isValid() || !player.isOnline() || !capturePoint.getZone().isInside(player.getLocation())) 
							{
								capturePoint.setCappingPlayer(null);
								continue;
							} 
							if (capturePoint.getDeciSecondsLeft() % 600L == 0L && capturePoint.getDeciSecondsLeft() != capturePoint.getCapTime() / 100L && capturePoint.getDeciSecondsLeft() != 0L)
								Bukkit.broadcastMessage(MessageTool.color("&9[&7Capture&9] &9%CAPTURE_POINT%&7 is being contested. &9(%TIME%)").replace("%CAPTURE_POINT%", capturePoint.getName()).replace("%TIME%", capturePoint.getTimeLeft()).replace("%PLAYER%", player.getName()));
							continue;
						} 
						if (!capturePoint.isGrace())
							for (Player player : Bukkit.getOnlinePlayers()) 
							{
								Profile profile = Profile.getByPlayer(player);
								if (!player.isDead())
									if (profile.getProtection() == null)
										if (capturePoint.getZone().isInside(player.getLocation())) 
										{
											capturePoint.setCappingPlayer(player);
											break;
										}   
							}  
					} 
				} 
			}
		}).runTaskTimerAsynchronously(HardcoreSMP.getInstance(), 2L, 2L);
	}
}
