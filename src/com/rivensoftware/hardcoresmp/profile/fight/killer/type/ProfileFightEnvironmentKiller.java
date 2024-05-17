package com.rivensoftware.hardcoresmp.profile.fight.killer.type;

import com.rivensoftware.hardcoresmp.profile.fight.ProfileFightEnvironment;
import com.rivensoftware.hardcoresmp.profile.fight.killer.ProfileFightKiller;

import lombok.Getter;

public class ProfileFightEnvironmentKiller extends ProfileFightKiller 
{

    @Getter private final ProfileFightEnvironment type;

    public ProfileFightEnvironmentKiller(ProfileFightEnvironment type) 
    {
        super(null, null);
        this.type = type;
    }
}
