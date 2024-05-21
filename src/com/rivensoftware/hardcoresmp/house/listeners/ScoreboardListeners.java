package com.rivensoftware.hardcoresmp.house.listeners;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareAllyEvent;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareNeutralEvent;
import com.rivensoftware.hardcoresmp.house.events.HouseDeclareWarEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerDisbandHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerJoinHouseEvent;
import com.rivensoftware.hardcoresmp.house.events.player.PlayerLeaveHouseEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.HashSet;
import java.util.Set;

public class ScoreboardListeners implements Listener {

	@EventHandler
	public void onJoinHouse(PlayerJoinHouseEvent event) {
	    for (Player player : event.getHouse().getOnlinePlayers()) {
	        Profile profile = Profile.getByUUID(player.getUniqueId());
	        profile.updateTeams();
	        Bukkit.getLogger().info("Updated teams for " + player.getName() + " (reason: " + event.getPlayer().getName() + " joined house " + event.getHouse().getHouseName() + ")");
	    }
	    for (House ally : event.getHouse().getAllies()) {
	        for (Player allyPlayer : ally.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
	            profile.updateTeams();
	            Bukkit.getLogger().info("Updated teams for ally player " + allyPlayer.getName() + " (reason: " + event.getPlayer().getName() + " joined house " + event.getHouse().getHouseName() + ")");
	        }
	    }
	}

	@EventHandler
	public void onLeaveHouse(PlayerLeaveHouseEvent event) {
	    Set<Player> toLoop = new HashSet<>(event.getHouse().getOnlinePlayers());
	    toLoop.add(event.getPlayer());
	    for (Player player : toLoop) {
	        Profile profile = Profile.getByUUID(player.getUniqueId());
	        profile.updateTeams();
	        Bukkit.getLogger().info("Updated teams for " + player.getName() + " (reason: " + event.getPlayer().getName() + " left house " + event.getHouse().getHouseName() + ")");
	    }
	    for (House ally : event.getHouse().getAllies()) {
	        for (Player allyPlayer : ally.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
	            profile.updateTeams();
	            Bukkit.getLogger().info("Updated teams for ally player " + allyPlayer.getName() + " (reason: " + event.getPlayer().getName() + " left house " + event.getHouse().getHouseName() + ")");
	        }
	    }
	    for (House enemyHouse : event.getHouse().getAllEnemies()) {
	        for (Player enemyPlayer : enemyHouse.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(enemyPlayer.getUniqueId());
	            profile.updateTeams();
	            Bukkit.getLogger().info("Updated teams for enemy player " + enemyPlayer.getName() + " (reason: " + event.getPlayer().getName() + " left house " + event.getHouse().getHouseName() + ")");
	        }
	    }
	}

	@EventHandler
	public void onDisbandHouse(PlayerDisbandHouseEvent event) {
	    for (Player player : event.getHouse().getOnlinePlayers()) {
	        Profile profile = Profile.getByUUID(player.getUniqueId());
	        profile.updateTeams();
	        Bukkit.getLogger().info("Updated teams for " + player.getName() + " (reason: house " + event.getHouse().getHouseName() + " disbanded by " + event.getPlayer().getName() + ")");
	    }
	    for (House ally : event.getHouse().getAllies()) {
	        for (Player allyPlayer : ally.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(allyPlayer.getUniqueId());
	            profile.updateTeams();
	            Bukkit.getLogger().info("Updated teams for ally player " + allyPlayer.getName() + " (reason: house " + event.getHouse().getHouseName() + " disbanded by " + event.getPlayer().getName() + ")");
	        }
	    }
	    for (House enemyHouse : event.getHouse().getAllEnemies()) {
	        for (Player enemyPlayer : enemyHouse.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(enemyPlayer.getUniqueId());
	            profile.updateTeams();
	            Bukkit.getLogger().info("Updated teams for enemy player " + enemyPlayer.getName() + " (reason: house " + event.getHouse().getHouseName() + " disbanded by " + event.getPlayer().getName() + ")");
	        }
	    }
	}

	@EventHandler
	public void onAllyHouse(HouseDeclareAllyEvent event) {
	    for (House house : event.getHouses()) {
	        for (Player player : house.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(player.getUniqueId());
	            profile.updateTeams();
	            String otherHouseName = "";
	            for (House otherHouse : event.getHouses()) {
	                if (!otherHouse.equals(house)) {
	                    otherHouseName = otherHouse.getHouseName();
	                    break;
	                }
	            }
	            Bukkit.getLogger().info("Updated teams for " + player.getName() + " (reason: house " + house.getHouseName() + " allied with house " + otherHouseName + ")");
	        }
	    }
	}

	@EventHandler
	public void onNeutralHouse(HouseDeclareNeutralEvent event) {
	    for (House house : event.getHouses()) {
	        for (Player player : house.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(player.getUniqueId());
	            profile.updateTeams();
	            String otherHouseName = "";
	            for (House otherHouse : event.getHouses()) {
	                if (!otherHouse.equals(house)) {
	                    otherHouseName = otherHouse.getHouseName();
	                    break;
	                }
	            }
	            Bukkit.getLogger().info("Updated teams for " + player.getName() + " (reason: house " + house.getHouseName() + " declared neutral with house " + otherHouseName + ")");
	        }
	    }
	}

	@EventHandler
	public void onEnemyHouse(HouseDeclareWarEvent event) {
	    for (House house : event.getHouses()) {
	        for (Player player : house.getOnlinePlayers()) {
	            Profile profile = Profile.getByUUID(player.getUniqueId());
	            profile.updateTeams();
	            String otherHouseName = "";
	            for (House otherHouse : event.getHouses()) {
	                if (!otherHouse.equals(house)) {
	                    otherHouseName = otherHouse.getHouseName();
	                    break;
	                }
	            }
	            Bukkit.getLogger().info("Updated teams for " + player.getName() + " (reason: house " + house.getHouseName() + " declared war on house " + otherHouseName + ")");
	        }
	    }
	}
}