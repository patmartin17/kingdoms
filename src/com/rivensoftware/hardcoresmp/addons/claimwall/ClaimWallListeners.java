package com.rivensoftware.hardcoresmp.addons.claimwall;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Set;

public class ClaimWallListeners implements Listener 
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	

    public ClaimWallListeners(HardcoreSMP plugin) 
    {
        new BukkitRunnable() 
        {
            @Override
            public void run() 
            {
                for (Player player : Bukkit.getOnlinePlayers()) 
                {
                    Profile profile = Profile.getByPlayer(player);

                    for (ClaimWallType type : ClaimWallType.values()) 
                    {

                        if ((profile.getProtection() == null && type == ClaimWallType.PVP_PROTECTION)) 
                        {

                            if (profile.getProtection() == null && !profile.getWalls().isEmpty())
                            {
                                int min = player.getLocation().getBlockY() - (type.getRange() / 2);
                                int max = player.getLocation().getBlockY() + (type.getRange() / 2);
                                Set<Claim> nearbyClaims = Claim.getNearbyClaimsAt(player.getLocation(),type.getRange() * 2);

                                
                                if (!(nearbyClaims.isEmpty())) 
                                {
                                    for (Claim claim : nearbyClaims) 
                                    {
                                    	
                                        if (!(type.isValid(claim))) 
                                        {
                                            continue;
                                        }

                                        if (claim.getBorder() == null) 
                                        {
                                            continue;
                                        }

                                        for (Location location : claim.getBorder()) 
                                        {
                                            for (int i = min - (20); i < max + (20); i++) 
                                            {
                                                location.setY(i);
                                                if (location.getBlock().isEmpty()) 
                                                {
                                                    if (profile.getWalls().containsKey(location)) 
                                                    {
                                                        profile.getWalls().get(location).hide(player);
                                                        profile.getWalls().remove(location);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            continue;
                        }

                        int min = player.getLocation().getBlockY() - (type.getRange() / 2);
                        int max = player.getLocation().getBlockY() + (type.getRange() / 2);
                        Set<Claim> nearbyClaims = Claim.getNearbyClaimsAt(player.getLocation(),type.getRange() * 2);
                        
                        if (!(nearbyClaims.isEmpty())) 
                        {
                            for (Claim claim : nearbyClaims) 
                            {
                            	
                                if (!(type.isValid(claim))) 
                                {
                                    continue;
                                }

                                if (claim.getBorder() == null) 
                                {
                                    continue;
                                }

                                for (Location location : claim.getBorder()) 
                                {
                                    for (int i = min - (20); i < max + (20); i++)
                                    {
                                        location.setY(i);
                                        if (location.getBlock().isEmpty()) 
                                        {
                                            if (location.distance(player.getLocation()) <= type.getRange() && !claim.isInside(player.getLocation()) && i < max && i > min) 
                                            {
                                                if (profile.getProtection() == null && !profile.getWalls().isEmpty()) 
                                                {
                                                    if (profile.getWalls().containsKey(location)) 
                                                    {
                                                        profile.getWalls().get(location).hide(player);
                                                        profile.getWalls().remove(location);
                                                    }
                                                } 
                                                else 
                                                {
                                                    profile.getWalls().put(location, new ClaimWall(type, location).show(player));
                                                }
                                            } 
                                            else 
                                            {
                                                if (profile.getWalls().containsKey(location)) 
                                                {
                                                    profile.getWalls().get(location).hide(player);
                                                    profile.getWalls().remove(location);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } 
                        else 
                        {
                            if (!(profile.getWalls().isEmpty())) 
                            {
                                for (Location location : profile.getWalls().keySet()) 
                                {
                                	if(Claim.getProminentClaimAt(location) == null)
                                	{
                                        profile.getWalls().clear();
                                        return;
                                	}
                                    profile.getWalls().get(location).hide(player);
                                }
                                profile.getWalls().clear();
                            }
                        }

                    }

                }
            }
        }.runTaskTimerAsynchronously(plugin, 2L, 2L);
    }

    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent event)
    {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.getProtection() != null) 
        {
            Claim entering = Claim.getProminentClaimAt(event.getTo());
            Claim leaving = Claim.getProminentClaimAt(event.getFrom());

            if (entering != null && (leaving == null || !leaving.equals(entering))) 
            {
                if (ClaimWallType.PVP_PROTECTION.isValid(entering)) 
                {
                    event.setCancelled(true);

                    if (player.getVehicle() != null) 
                    {
                        player.getVehicle().eject();
                    }

                    player.setSprinting(false);
                    player.setVelocity(new Vector());
                }

            }
        }
    }

    @EventHandler
    public void onPlayerTeleportEvent(PlayerTeleportEvent event) 
    {
        Player player = event.getPlayer();
        Profile profile = Profile.getByPlayer(player);

        if (profile.getProtection() != null) 
        {
            Claim entering = Claim.getProminentClaimAt(event.getTo());
            Claim leaving = Claim.getProminentClaimAt(event.getFrom());

            if (entering != null && (leaving == null || !leaving.equals(entering))) 
            {
                if (ClaimWallType.PVP_PROTECTION.isValid(entering)) 
                {
                    event.setCancelled(true);
                    player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("ADDONS.CLAIM_WALL.CANT_ENTER_PVP_PROTECTION")).replace("%HOUSE%", entering.getHouse().getHouseName()).replace("%TIME%", profile.getProtection().getTimeLeft()));
                }
            }
        }
    }

}
