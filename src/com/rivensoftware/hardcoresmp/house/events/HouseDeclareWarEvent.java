package com.rivensoftware.hardcoresmp.house.events;

import com.rivensoftware.hardcoresmp.house.House;
import lombok.Getter;

@Getter
public class HouseDeclareWarEvent extends HouseEvent 
{

    private House[] houses;

    public HouseDeclareWarEvent(House[] houses) 
    {
        this.houses = houses;
    }

}
