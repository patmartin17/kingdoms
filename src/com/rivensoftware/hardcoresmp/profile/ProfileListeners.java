package com.rivensoftware.hardcoresmp.profile;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.mongodb.client.model.UpdateOptions;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerCancelHouseTeleportEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerDisbandHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.admin.Admin;
import com.rivensoftware.hardcoresmp.profile.deathban.ProfileDeathban;
import com.rivensoftware.hardcoresmp.profile.protection.ProfileProtection;
import com.rivensoftware.hardcoresmp.profile.teleport.ProfileTeleportType;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;

public class ProfileListeners implements Listener
{
	UpdateOptions options = new UpdateOptions().upsert(true);
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) 
	{
		Player player = event.getPlayer();
		Profile.sendPlayerTabUpdate(player);

		long start = System.currentTimeMillis();

		event.setJoinMessage(null);

		determineAdmin(player, start);

		Profile profile = Profile.getByUUID(player.getUniqueId());
		profile.setName(player.getName());
		profile.setInSpawn(ProfileProtection.isWithin(player.getLocation()));
		profile.setLastInside(Claim.getProminentClaimAt(player.getLocation()));

		SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByUuid(player.getUniqueId());
		if (offlinePlayer == null) 
		{
			profile.setSoulboundLives(2);
			profile.setGeneralLives(1);
			new SimpleOfflinePlayer(player);
		} 
		else if (!offlinePlayer.getName().equals(player.getName())) 
		{
			offlinePlayer.setName(player.getName());
		} 

		Claim claim = Claim.getProminentClaimAt(event.getPlayer().getLocation());
		if (claim != null)
			profile.setLastInside(claim); 
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) 
	{
		Player player = event.getPlayer();

		event.setQuitMessage(null);

		if(Profile.getByPlayer(player) != null)
		{
			new BukkitRunnable() 
			{

				@Override
				public void run() 
				{
					Profile profile = Profile.getByPlayer(player);
					Profile.unloadProfile(player);
					profile.save(options);
				}
			}.runTaskLater(plugin, 20L);
		}
	}
	
	@EventHandler
	public void onInventoryMoveItemEvent(InventoryMoveItemEvent event) 
	{
		event.getSource().remove(Claim.getWand());
		event.getDestination().remove(Claim.getWand());
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event) 
	{
		if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL && event.getCause() != PlayerTeleportEvent.TeleportCause.NETHER_PORTAL && event.getCause() != PlayerTeleportEvent.TeleportCause.END_PORTAL) 
		{
			Profile profile = Profile.getByUUID(event.getPlayer().getUniqueId());
			profile.setLastInside(Claim.getProminentClaimAt(event.getTo()));
		} 
	}

	@EventHandler
	public void onPlayerLeaveHouseEvent(PlayerLeaveHouseEvent event)
	{
		Profile profile = Profile.getByUUID(event.getPlayer().getUniqueId());
		if (profile.getClaimProfile() != null && profile.getClaimProfile().getHouse() == event.getHouse())
			profile.setClaimProfile(null); 
	}

	@EventHandler
	public void onHouseDisband(PlayerDisbandHouseEvent event) 
	{
		for (Player player : event.getHouse().getOnlinePlayers()) 
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			if (profile.getClaimProfile() != null && profile.getClaimProfile().getHouse() == event.getHouse())
				profile.setClaimProfile(null); 
		} 
	}

	@EventHandler(ignoreCancelled = true)
	public void onDamageByEntity(EntityDamageByEntityEvent event) 
	{
		if (event.getEntity() instanceof Player) 
		{
			Player damager, damaged = (Player)event.getEntity();
			if (event.getDamager() instanceof Player) 
			{
				damager = (Player)event.getDamager();
			} 
			else if (event.getDamager() instanceof Projectile) 
			{
				Projectile projectile = (Projectile)event.getDamager();
				if (projectile.getShooter() instanceof Player) 
				{
					damager = (Player)projectile.getShooter();
				} 
				else 
				{
					return;
				} 
			} 
			else 
			{
				return;
			} 

			if (damager == damaged)
				return; 
			House damagedHouse = Profile.getByUUID(damaged.getUniqueId()).getHouse();
			House damagerHouse = Profile.getByUUID(damager.getUniqueId()).getHouse();

			if (damagedHouse == null || damagerHouse == null)
				return; 

			if (damagedHouse == damagerHouse) 
			{
				damager.sendMessage(MessageTool.color("&eYou cannot hurt &a%PLAYER%&e.").replace("%PLAYER%", damaged.getName()));
				event.setCancelled(true);
				return;
			} 

			if (damagedHouse.getAllies().contains(damagerHouse) && !plugin.getMainConfig().getBoolean("ALLIES.DAMAGE_ALLIES")) 
			{
				damager.sendMessage(MessageTool.color("&eYou cannot hurt &a%PLAYER%&e.").replace("%PLAYER%", damaged.getName()));
				event.setCancelled(true);
			} 
		} 
	}

	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent event) 
	{
		if (event.getFrom().getBlockX() != event.getTo().getBlockX() || event.getFrom().getBlockY() != event.getTo().getBlockY() || event.getFrom().getBlockZ() != event.getTo().getBlockZ()) 
		{
			Player player = event.getPlayer();
			Profile profile = Profile.getByUUID(player.getUniqueId());
			if (profile.getTeleportWarmup() != null)
				if (profile.getTeleportWarmup().getEvent().getTeleportType() == ProfileTeleportType.HOME_TELEPORT)
				{
					profile.getTeleportWarmup().getEvent().setCancelled(true);
					Bukkit.getPluginManager().callEvent((Event)new PlayerCancelHouseTeleportEvent(player, null, ProfileTeleportType.HOME_TELEPORT));
					profile.setTeleportWarmup(null);
					player.sendMessage(MessageTool.color("&cTeleport cancelled!"));
				}
		} 
	}


	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerPreLoginEvent(AsyncPlayerPreLoginEvent event) 
	{
		if (event.getLoginResult() == AsyncPlayerPreLoginEvent.Result.ALLOWED) 
		{
			Profile profile = new Profile(event.getUniqueId());
			if (profile.getName() == null || !profile.getName().equals(event.getName())) 
			{
				profile.setName(event.getName());
				profile.save(options);
			} 
			ProfileDeathban deathban = profile.getDeathban();
			if (profile.getDeathban() != null) 
			{
				event.setLoginResult(AsyncPlayerPreLoginEvent.Result.KICK_OTHER);
				event.setKickMessage(ProfileDeathban.KICK_MESSAGE.replace("%TIME%", deathban.getTimeLeft()));
				Profile.getProfiles().remove(profile);
			} 
		} 
	}
	
	@SuppressWarnings("deprecation")
	private void determineAdmin(Player player, long start)
	{
		if(player.hasPermission("kingdoms.admin"))
		{
			if(!Admin.getAdminProfiles().containsKey(player.getUniqueId()))
			{
				Admin.loadAdminProfile(player);
				Admin admin = Admin.getByPlayer(player);
				admin.setAdmin(true);

				long end = System.currentTimeMillis();
				player.sendMessage(MessageTool.color("&9Administrator profile for " + player.getName() + " was loaded successfully (" + (end - start) + "ms)."));	
			}
		}
		else
		{
			if(!Profile.getActiveProfiles().containsKey(player.getUniqueId()))
			{
				Profile.loadProfile(player);

				for(Admin admin : Admin.getAdminProfiles().values())
				{
					player.hidePlayer(admin.getPlayer());
				}

				long end = System.currentTimeMillis();
				player.sendMessage(MessageTool.color("&9Profile " + player.getName() + " was loaded successfully (" + (end - start) + "ms)."));	
			}
		}
	}
}
