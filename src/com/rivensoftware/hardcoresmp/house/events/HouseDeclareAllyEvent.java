package com.rivensoftware.hardcoresmp.house.events;

import com.rivensoftware.hardcoresmp.house.House;
import lombok.Getter;

@Getter
public class HouseDeclareAllyEvent extends HouseEvent 
{

    private House[] houses;

    public HouseDeclareAllyEvent(House[] houses) 
    {
        this.houses = houses;
    }

}
