package com.rivensoftware.hardcoresmp.house.events.player;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseEvent;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class PlayerLeaveHouseEvent extends HouseEvent 
{
	
    private House house;
    private Player player;

    public PlayerLeaveHouseEvent(Player player, House house) 
    {
        this.player = player;
        this.house = house;
    }

}
