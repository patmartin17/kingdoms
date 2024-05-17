package com.rivensoftware.hardcoresmp.house.events;

import com.rivensoftware.hardcoresmp.house.House;
import lombok.Getter;

@Getter
public class HouseDeclareNeutralEvent extends HouseEvent 
{

    private House[] houses;

    public HouseDeclareNeutralEvent(House[] houses) 
    {
        this.houses = houses;
    }

}
