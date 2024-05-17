package com.rivensoftware.hardcoresmp.profile.deathban;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.scheduler.BukkitRunnable;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.addons.combatlogger.CombatLogger;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.lives.LifeTransaction;
import com.rivensoftware.hardcoresmp.profile.lives.LifeType;
import com.rivensoftware.hardcoresmp.tools.DateTool;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

public class ProfileDeathban 
{
	public static final String KICK_MESSAGE = MessageTool.color("&cYou have been deathbanned | It will expire in &c%TIME%. ");

	private final long createdAt;

	private final long duration;

	public long getCreatedAt() 
	{
		return this.createdAt;
	}

	public long getDuration() 
	{
		return this.duration;
	}

	public ProfileDeathban(long createdAt, long duration)
	{
		this.createdAt = createdAt;
		this.duration = duration;
	}

	public ProfileDeathban(long duration)
	{
		this(System.currentTimeMillis(), duration * 1000L);
	}

	public String getTimeLeft() 
	{
		return DateTool.readableTime(this.createdAt + this.duration - System.currentTimeMillis()).trim();
	}

	public static int getDuration(Player player) 
	{
		int duration = 0;
		if(player.isOp())
			return duration;
		if(player.hasPermission("kingdoms.admin"))
			return duration;
		for (PermissionAttachmentInfo info : player.getEffectivePermissions()) 
		{
			String perm = info.getPermission();
			if (perm.startsWith("deathban.")) 
			{
				int tempDuration = 0;
				try 
				{
					tempDuration = Integer.parseInt(perm.replace("deathban.", "").replace(" ", ""));
				} 
				catch (NumberFormatException numberFormatException) {}

				if (duration > 0 && tempDuration > duration)
					continue; 
				duration = tempDuration;
			} 
		} 
		return duration;
	}

	public static void handleDeath(Profile profile)
	{
		if(profile.isCombatLogged())
		{
			return;
		}

		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				LifeType lifeType = null;
				LifeTransaction transaction = new LifeTransaction(profile);

				if(profile.getSoulboundLives() >= 1.0)
				{
					lifeType = LifeType.SOULBOUND;
					if(transaction.removeLives(1.0, lifeType))
					{
						if(profile.getPlayer() != null && profile.getPlayer().isOnline())
						{
							profile.getPlayer().sendMessage(MessageTool.color("&cYou have died and lost a soulbound life."));
							profile.getPlayer().sendMessage(MessageTool.color("&cSoulbound lives remaining&f: " + profile.getSoulboundLives()));
						}
					}
					else
					{
						System.out.println("Soulbound life collection did not complete successfully after death: " + profile.getName());
					}
				}
				else if(profile.getGeneralLives() >= 1.0)
				{
					lifeType = LifeType.GENERAL;
					if(transaction.removeLives(1.0, lifeType))
					{
						if(profile.getPlayer() != null && profile.getPlayer().isOnline())
						{
							profile.getPlayer().sendMessage(MessageTool.color("&cYou have died and lost a general life."));
							profile.getPlayer().sendMessage(MessageTool.color("&cGeneral lives remaining&f: " + profile.getGeneralLives()));
						}
					}
					else
					{
						System.out.println("General life collection did not complete successfully after death: " + profile.getName());
					}
				}
				else if(profile.getHouse() != null)
				{
					House house = profile.getHouse();

					if(profile.getHouse().getLives() > 1.0)
					{
						double newTotal = house.getLives() - 1;
						house.setLives(newTotal);
					}
					else
					{
						double newTotal = house.getLives() - 1;
						house.setLives(newTotal);
						house.setPowerless(true);
						if(profile.getHouse().getLives() > -1 && profile.getHouse().getLives() <= 0)
						{
							profile.getHouse().sendMessage(MessageTool.color("&cYour house is now powerless and vulnerable to enemies!"));	
							Bukkit.broadcastMessage(MessageTool.color("&e&l" + profile.getHouse().getHouseName() + " &chas become powerless!"));
						}

						if(ProfileDeathban.getDuration(profile.getPlayer()) > 0)
						{
							profile.setDeathban(new ProfileDeathban(ProfileDeathban.getDuration(profile.getPlayer())));	
							new BukkitRunnable() 
							{
								@Override
								public void run() 
								{
									if(profile.getPlayer() != null && profile.getPlayer().isOnline())
									{
										profile.getPlayer().spigot().respawn();
										profile.getPlayer().kickPlayer(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", profile.getDeathban().getTimeLeft()));
									}
								}

							}.runTaskLater(HardcoreSMP.getInstance(), 1L);
						}
					}
					profile.getHouse().sendMessage(MessageTool.color("&c&lHouse member death: &f%PLAYER%&c Lives: &f%LIVES%&c.").replace("%PLAYER%", profile.getName()).replace("%LIVES%", profile.getHouse().getLives() + ""));
				}
				else
				{
					if(ProfileDeathban.getDuration(profile.getPlayer()) > 0)
					{
						profile.setDeathban(new ProfileDeathban(ProfileDeathban.getDuration(profile.getPlayer())));

						new BukkitRunnable() 
						{
							@Override
							public void run() 
							{
								if(profile.getPlayer() != null && profile.getPlayer().isOnline())
								{
									profile.getPlayer().spigot().respawn();
									profile.getPlayer().kickPlayer(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", profile.getDeathban().getTimeLeft()));	
								}
							}

						}.runTaskLater(HardcoreSMP.getInstance(), 1L);
					}
				}
			}
		}.runTaskLater(HardcoreSMP.getInstance(), 1L);
	}

	public static void handleCombatLoggerDeath(Profile profile, CombatLogger logger)
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				LifeType lifeType = null;
				LifeTransaction transaction = new LifeTransaction(profile);

				if(profile.getSoulboundLives() >= 1.0)
				{
					lifeType = LifeType.SOULBOUND;
					if(transaction.removeLives(1.0, lifeType))
					{
						if(profile.getPlayer() != null && profile.getPlayer().isOnline())
						{
							profile.getPlayer().sendMessage(MessageTool.color("&cYou have died and lost a soulbound life."));
							profile.getPlayer().sendMessage(MessageTool.color("&cSoulbound lives remaining&f: " + profile.getSoulboundLives()));
						}
					}
					else
					{
						System.out.println("Soulbound life collection did not complete successfully after death: " + profile.getName());
					}
				}
				else if(profile.getGeneralLives() >= 1.0)
				{
					lifeType = LifeType.GENERAL;
					if(transaction.removeLives(1.0, lifeType))
					{
						if(profile.getPlayer() != null && profile.getPlayer().isOnline())
						{
							profile.getPlayer().sendMessage(MessageTool.color("&cYou have died and lost a general life."));
							profile.getPlayer().sendMessage(MessageTool.color("&cGeneral lives remaining&f: " + profile.getGeneralLives()));
						}
					}
					else
					{
						System.out.println("General life collection did not complete successfully after death: " + profile.getName());
					}
				}
				else if(profile.getHouse() != null)
				{
					House house = profile.getHouse();

					if(profile.getHouse().getLives() > 1.0)
					{
						double newTotal = house.getLives() - 1;
						house.setLives(newTotal);
					}
					else
					{
						double newTotal = house.getLives() - 1;
						house.setLives(newTotal);
						house.setPowerless(true);
						if(profile.getHouse().getLives() > -1 && profile.getHouse().getLives() <= 0)
						{
							profile.getHouse().sendMessage(MessageTool.color("&cYour house is now powerless and vulnerable to enemies!"));	
							Bukkit.broadcastMessage(MessageTool.color("&e&l" + profile.getHouse().getHouseName() + " &chas become powerless!"));
						}

						if(logger.getDeathbanDuration() > 0)
						{
							profile.setDeathban(new ProfileDeathban(logger.getDeathbanDuration()));
							profile.setCombatLogged(true);
							profile.save(HardcoreSMP.getInstance().getOptions());	
						}
					}
					profile.getHouse().sendMessage(MessageTool.color("&c&lHouse member death: &f%PLAYER%&c Lives: &f%LIVES%&c.").replace("%PLAYER%", profile.getName()).replace("%LIVES%", profile.getHouse().getLives() + ""));
				}
				else
				{
					if(logger.getDeathbanDuration() > 0)
					{
						profile.setDeathban(new ProfileDeathban(logger.getDeathbanDuration()));
						profile.setCombatLogged(true);
						profile.save(HardcoreSMP.getInstance().getOptions());
					}
				}
			}
		}.runTaskLater(HardcoreSMP.getInstance(), 1L);
	}
}
