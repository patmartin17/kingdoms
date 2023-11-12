package com.rivensoftware.hardcoresmp.house.events.player;

import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseEvent;
import com.rivensoftware.hardcoresmp.profile.teleport.ProfileTeleportType;

import lombok.Getter;

@Getter
public class PlayerCancelHouseTeleportEvent extends HouseEvent
{

    private House house;
    private Player player;
    private ProfileTeleportType teleportType;

    public PlayerCancelHouseTeleportEvent(Player player, House house, ProfileTeleportType teleportType) 
    {
        this.player = player;
        this.house = house;
        this.teleportType = teleportType;
    }


}
