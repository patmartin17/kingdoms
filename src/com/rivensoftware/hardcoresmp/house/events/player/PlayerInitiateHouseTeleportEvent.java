package com.rivensoftware.hardcoresmp.house.events.player;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseEvent;
import com.rivensoftware.hardcoresmp.profile.teleport.ProfileTeleportType;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
public class PlayerInitiateHouseTeleportEvent extends HouseEvent
{

    private House house;
    private Player player;
    private ProfileTeleportType teleportType;
    private Location initialLocation;
    private long init;
    @Setter private double time;
    @Setter private Location location;
    @Setter private boolean cancelled;

    public PlayerInitiateHouseTeleportEvent(Player player, House house, ProfileTeleportType teleportType, double time, Location location, Location initialLocation)
    {
        this.player = player;
        this.house = house;
        this.teleportType = teleportType;
        this.time = time;
        this.init = System.currentTimeMillis();
        this.location = location;
        this.initialLocation = initialLocation;
    }


}
