package com.rivensoftware.hardcoresmp.profile.fight;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.deathban.ProfileDeathban;
import com.rivensoftware.hardcoresmp.profile.fight.killer.ProfileFightKiller;
import com.rivensoftware.hardcoresmp.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import com.rivensoftware.hardcoresmp.profile.fight.killer.type.ProfileFightPlayerKiller;

public class ProfileFightListeners implements Listener 
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
    @SuppressWarnings("deprecation")
	@EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) 
    {
        if (event.getEntity() instanceof Player) 
        {
            Player player = (Player) event.getEntity();

            new BukkitRunnable() 
            {
                @Override
                public void run() 
                {
                    player.setNoDamageTicks(19);
                }
                
            }.runTaskLater(plugin, 1L);

            if (player.getHealth() - event.getFinalDamage() <= 0) 
            {
                Profile profile = Profile.getByPlayer(player);

                LivingEntity damager;
                if (event.getDamager() instanceof LivingEntity) 
                {
                    damager = (LivingEntity) event.getDamager();
                } 
                else if (event.getDamager() instanceof Projectile) 
                {
                    if (((Projectile) event.getDamager()).getShooter() != null) 
                    {
                        damager = (LivingEntity) ((Projectile) event.getDamager()).getShooter();
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

                if (damager == null) 
                {
                    return;
                }

                if (profile.isCombatLogged()) 
                {
                    return;
                }

                if (!(damager instanceof Player)) 
                {
                    profile.getFights().add(new ProfileFight(player, new ProfileFightKiller(damager.getType(), damager.getType().getName())));
                    new BukkitRunnable() 
                    {
                        @Override
                        public void run() 
                        {
                            profile.save(plugin.getOptions());
                        }
                    }.runTaskAsynchronously(plugin);
                }

            }
        }
    }

    @EventHandler
    public void onPlayerDeathEvent(PlayerDeathEvent event) 
    {
        Player player = event.getEntity();
        Profile profile = Profile.getByPlayer(player);
        EntityDamageEvent damageEvent = player.getLastDamageCause();

		ProfileDeathban.handleDeath(profile);
        
        if (profile.isCombatLogged())
        {
            profile.setCombatLogged(false);
            event.setDeathMessage(null);
            return;
        }

        player.getWorld().strikeLightningEffect(player.getLocation());

        if (player.getKiller() != null) 
        {
            ProfileFight fight = new ProfileFight(player, new ProfileFightPlayerKiller(player.getKiller()));
            profile.getFights().add(fight);

            Profile.getByPlayer(player.getKiller()).getFights().add(fight);

            new BukkitRunnable() 
            {
                @Override
                public void run() 
                {
                    profile.save(plugin.getOptions());
                }
                
            }.runTaskAsynchronously(plugin);
            return;
        }

        if (damageEvent == null) 
        {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() 
            {
                @Override
                public void run() 
                {
                    profile.save(plugin.getOptions());
                }
            }.runTaskAsynchronously(plugin);
            return;
        }

        DamageCause cause = damageEvent.getCause();

        if (cause == DamageCause.PROJECTILE || cause == DamageCause.ENTITY_ATTACK || cause == DamageCause.POISON || cause == DamageCause.MAGIC || cause == DamageCause.ENTITY_EXPLOSION) 
        {
            return;
        }

        try 
        {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.valueOf(cause.name().toUpperCase()))));
            new BukkitRunnable() 
            {
                @Override
                public void run() 
                {
                    profile.save(plugin.getOptions());
                }
                
            }.runTaskAsynchronously(plugin);
        } 
        catch (Exception ignored) 
        {
            profile.getFights().add(new ProfileFight(player, new ProfileFightEnvironmentKiller(ProfileFightEnvironment.CUSTOM)));
            new BukkitRunnable() 
            {
                @Override
                public void run()
                {
                    profile.save(plugin.getOptions());
                }
            }.runTaskAsynchronously(plugin);
        }
    }
}
