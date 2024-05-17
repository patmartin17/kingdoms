package com.rivensoftware.hardcoresmp.profile.protection;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

public class ProfileProtectionListeners implements Listener 
{

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		if (profile.isRespawning() && player.isDead())
			player.spigot().respawn(); 
	}

	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent event) 
	{
		Profile profile = Profile.getByPlayer(event.getEntity());
		profile.setProtection(null);
		profile.setRespawning(true);
	}

	@EventHandler
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		if (profile.isRespawning()) 
		{
			if (profile.getProtection() == null) 
			{
				profile.setProtection(new ProfileProtection(ProfileProtection.DEFAULT_DURATION));
				profile.getProtection().pause();
				profile.setLeftSpawn(false);
			} 
			profile.setRespawning(false);
		} 
	}

	@EventHandler(ignoreCancelled = true)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) 
	{
		if (event.getEntity() instanceof Player) 
		{
			Player damager, damaged = (Player)event.getEntity();
			if (event.getDamager() instanceof Player) 
			{
				damager = (Player)event.getDamager();
			} else if (event.getDamager() instanceof Projectile) 
			{
				if (((Projectile)event.getDamager()).getShooter() instanceof Player) 
				{
					damager = (Player)((Projectile)event.getDamager()).getShooter();
				} 
				else 
				{
					damager = null;
				} 
			} 
			else 
			{
				damager = null;
			} 
			if (damager != null && !damaged.getName().equals(damager.getName())) 
			{
				Profile profile = Profile.getByPlayer(damager);
				Profile damagedProfile = Profile.getByPlayer(damaged);
				if (profile.getProtection() != null) 
				{
					damager.sendMessage(MessageTool.color("&cYou can't do this whilst under PvP Protection.").replace("%TIME%", profile.getProtection().getTimeLeft()));
					event.setCancelled(true);
					return;
				} 
				
				if (damagedProfile.getProtection() != null) 
				{
					damager.sendMessage(MessageTool.color("&c&l%PLAYER%&c has &lPvP Protection&c!").replace("%PLAYER%", damaged.getName()));
					event.setCancelled(true);
					return;
				} 
			} 
		} 
	}

	@EventHandler
	public void onPotionSplashEvent(PotionSplashEvent event) 
	{
		if (event.getEntity().getShooter() instanceof Player) 
		{
			Player player = (Player)event.getEntity().getShooter();
			Profile profile = Profile.getByPlayer(player);
			if (profile.getProtection() != null)
				for (LivingEntity affected : event.getAffectedEntities()) 
				{
					if (affected instanceof Player)
						event.setIntensity(affected, 0.0D); 
				}  
			for (LivingEntity affected : event.getAffectedEntities()) 
			{
				if (affected instanceof Player) 
				{
					Player affectedPlayer = (Player)affected;
					if (affectedPlayer.equals(player))
						continue; 
					Profile affectedProfile = Profile.getByPlayer(affectedPlayer);
					if (affectedProfile.getProtection() != null)
						event.setIntensity((LivingEntity)affectedPlayer, 0.0D); 
				} 
			} 
		} 
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) 
	{
		if (event.getEntity() instanceof Player && (event.getCause().name().contains("FIRE") || event.getCause().name().contains("LAVA"))) {
			Player player = (Player)event.getEntity();
			Profile profile = Profile.getByPlayer(player);
			if (profile.getProtection() != null)
				event.setCancelled(true); 
		} 
	}
}
