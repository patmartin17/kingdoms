package com.rivensoftware.hardcoresmp.house.events.player;

import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.events.HouseEvent;

import lombok.Getter;

@Getter
public class PlayerCreateHouseEvent extends HouseEvent 
{

	private House house;
	private Player player;

	public PlayerCreateHouseEvent(Player player, House house) 
	{
		this.player = player;
		this.house = house;
	}

}
