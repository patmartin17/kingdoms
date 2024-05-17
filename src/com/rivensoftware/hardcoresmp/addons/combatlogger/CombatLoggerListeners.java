package com.rivensoftware.hardcoresmp.addons.combatlogger;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.deathban.ProfileDeathban;
import com.rivensoftware.hardcoresmp.profile.fight.ProfileFight;
import com.rivensoftware.hardcoresmp.profile.fight.ProfileFightEnvironment;
import com.rivensoftware.hardcoresmp.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import com.rivensoftware.hardcoresmp.profile.fight.killer.type.ProfileFightPlayerKiller;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.thebigdolphin1.tagspawnprotection.combat.tag.TagManager;

public class CombatLoggerListeners implements Listener 
{

	private HardcoreSMP plugin;

	public CombatLoggerListeners(HardcoreSMP plugin) 
	{
		this.plugin = plugin;
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				for(Player players : Bukkit.getOnlinePlayers())
				{
					Profile profile = Profile.getByPlayer(players);
					if(TagManager.isInCombat(players.getUniqueId()))
					{
						profile.setSafeLogout(false);
					}
					else
					{
						profile.setSafeLogout(true);
					}
				}

				Iterator<Map.Entry<CombatLogger, Long>> iterator = CombatLogger.getLoggersMap().entrySet().iterator();
				while (iterator.hasNext()) 
				{
					Map.Entry<CombatLogger, Long> entry = iterator.next();
					CombatLogger logger = entry.getKey();
					long time = entry.getValue();

					if (System.currentTimeMillis() - time > (plugin.getMainConfig().getInt("COMBAT_LOGGER.DESPAWN_TIME") * 1000)) 
					{
						logger.getEntity().remove();
						iterator.remove();
					}
				}
			}
		}.runTaskTimer(plugin, 20L, 20L);
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent event) 
	{
		Player player = event.getPlayer();

		if (player.getGameMode() == GameMode.CREATIVE) 
		{
			return;
		}

		Profile profile = Profile.getByPlayer(player);
		if (profile.getProtection() != null) 
		{
			return;
		}

		if (profile.isSafeLogout()) 
		{
			return;
		}

		new CombatLogger(player);
	}

	@EventHandler
	public void onChunkLoadEvent(ChunkLoadEvent event) 
	{
		for (Entity entity : event.getChunk().getEntities()) 
		{
			if (entity.getType() == CombatLogger.ENTITY_TYPE) 
			{
				if (entity instanceof LivingEntity) 
				{
					if (((LivingEntity) entity).getCustomName() != null) 
					{
						entity.remove();
					}
				}
			}
		}
	}

	@EventHandler
	public void onChunkUnloadEvent(ChunkUnloadEvent event) 
	{
		for (Entity entity : event.getChunk().getEntities()) 
		{
			if (entity instanceof LivingEntity) {
				CombatLogger logger = CombatLogger.getByEntity((LivingEntity) entity);

				if (logger != null) 
				{
					entity.remove();
					CombatLogger.getLoggers().remove(logger);
				}

			}
		}
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		CombatLogger logger = CombatLogger.getByPlayer(player);

		if (logger != null) 
		{
			if (!(profile.isCombatLogged())) 
			{
				event.getPlayer().setHealth(logger.getEntity().getHealth() / 5);
			}
			event.getPlayer().teleport(logger.getEntity().getLocation());
			logger.getEntity().remove();
			CombatLogger.getLoggers().remove(logger);
		}

		if (profile.isCombatLogged()) 
		{
			player.getInventory().clear();
			player.getInventory().setArmorContents(null);
			player.setExp(0);
			player.setHealth(0);
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) 
	{
		if (event.getRightClicked() instanceof LivingEntity) 
		{
			CombatLogger logger = CombatLogger.getByEntity((LivingEntity) event.getRightClicked());
			if (logger != null) 
			{
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onEntityDeathEvent(EntityDeathEvent event) 
	{
		LivingEntity entity = event.getEntity();
		CombatLogger logger = CombatLogger.getByEntity(entity);
		if (logger != null) 
		{
			Profile profile = new Profile(logger.getUuid());

			for (ItemStack itemStack : logger.getArmor()) 
			{
				if (itemStack != null && itemStack.getType() != Material.AIR) 
				{
					entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
				}
			}

			for (ItemStack itemStack : logger.getContents()) 
			{
				if (itemStack != null && itemStack.getType() != Material.AIR) 
				{
					entity.getWorld().dropItemNaturally(entity.getLocation(), itemStack);
				}
			}

			EntityDamageEvent damageEvent = entity.getLastDamageCause();

			entity.getWorld().strikeLightningEffect(entity.getLocation());
			ProfileDeathban.handleCombatLoggerDeath(profile, logger);

			if (entity.getKiller() != null) 
			{
				ProfileFight fight = new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getHunger(), logger.getEffects(), new ProfileFightPlayerKiller(entity.getKiller()), entity.getLocation());
				profile.getFights().add(fight);

				Profile.getByPlayer(entity.getKiller()).getFights().add(fight);

				//Player killer = entity.getKiller();
				//Profile killerProfile = Profile.getByPlayer(killer);
				//new ProfileDeathMessage(ProfileDeathMessageTemplate.PLAYER, profile, killerProfile, weapon);

				profile.save(plugin.getOptions());
				Profile.getProfiles().remove(profile);
				return;
			}

			if (damageEvent == null) 
			{
				profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getHunger(), logger.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM), entity.getLocation()));


				//new ProfileDeathMessage(ProfileDeathMessageTemplate.CUSTOM, profile);

				profile.save(plugin.getOptions());
				Profile.getProfiles().remove(profile);
				return;
			}

			EntityDamageEvent.DamageCause cause = damageEvent.getCause();

			if (cause == EntityDamageEvent.DamageCause.PROJECTILE || cause == EntityDamageEvent.DamageCause.ENTITY_ATTACK || cause == EntityDamageEvent.DamageCause.POISON || cause == EntityDamageEvent.DamageCause.MAGIC || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) 
			{
				return;
			}

			try
			{
				profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getHunger(), logger.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase())), entity.getLocation()));
			} 
			catch (Exception ignored)
			{
				profile.getFights().add(new ProfileFight(UUID.randomUUID(), -1, System.currentTimeMillis(), logger.getContents(), logger.getHunger(), logger.getEffects(), new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM), entity.getLocation()));
			}

			//ProfileDeathMessageTemplate template;
			//try 
			//{
			//	template = ProfileDeathMessageTemplate.valueOf(cause.name());
			//}
			//catch (Exception exception)
			//{
			//	return;
			//}

			//new ProfileDeathMessage(template, profile);

			profile.save(plugin.getOptions());
			Profile.getProfiles().remove(profile);
		}
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) 
	{
		if (event.getEntity() instanceof LivingEntity) 
		{

			Player damager;
			if (event.getDamager() instanceof Player) 
			{
				damager = (Player) event.getDamager();
			} 
			else if (event.getDamager() instanceof Projectile) 
			{
				Projectile projectile = (Projectile) event.getDamager();
				if (projectile.getShooter() instanceof Player) 
				{
					damager = (Player) projectile.getShooter();
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

			CombatLogger logger = CombatLogger.getByEntity((LivingEntity) event.getEntity());

			if (logger != null) 
			{
				House damagedHouse = House.getByPlayerName(logger.getName());
				House damagerHouse = House.getByPlayerName(damager.getName());

				if (damagedHouse == null || damagerHouse == null) 
				{
					CombatLogger.getLoggersMap().put(logger, System.currentTimeMillis());
					return;
				}

				if (damagedHouse == damagerHouse) 
				{
					damager.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("ADDONS.COMBAT_LOGGER.CANT_HURT_PLAYER")).replace("%PLAYER%", logger.getName()));
					event.setCancelled(true);
					return;
				}

				if (damagedHouse.getAllies().contains(damagerHouse) && !HardcoreSMP.getInstance().getMainConfig().getBoolean("ALLIES.DAMAGE_FRIENDLY")) 
				{
					damager.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("ADDONS.COMBAT_LOGGER.CANT_HURT_PLAYER")).replace("%PLAYER%", logger.getName()));
					event.setCancelled(true);
				}
			}

		}
	}

	@EventHandler
	public void onEntityDamageEvent(EntityDamageEvent event) 
	{
		if (event.getEntity() instanceof LivingEntity) 
		{
			CombatLogger logger = CombatLogger.getByEntity((LivingEntity) event.getEntity());
			if (logger != null) 
			{
				CombatLogger.getLoggersMap().put(logger, System.currentTimeMillis());
				new BukkitRunnable() 
				{
					@Override
					public void run() 
					{
						event.getEntity().setVelocity(new Vector());
					}

				}.runTaskLaterAsynchronously(plugin, 1L);
				if (event.getCause() == EntityDamageEvent.DamageCause.FALL) 
				{
					event.setDamage(event.getFinalDamage() * 5);
				}
			}
		}
	}
}