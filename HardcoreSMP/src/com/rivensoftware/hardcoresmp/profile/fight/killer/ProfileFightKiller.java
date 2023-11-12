package com.rivensoftware.hardcoresmp.profile.fight.killer;

import org.bukkit.entity.EntityType;

import com.rivensoftware.hardcoresmp.tools.MessageTool;

import lombok.Getter;

public class ProfileFightKiller 
{

    @Getter private final EntityType entityType;
    @Getter private final String name;

    public ProfileFightKiller(EntityType entityType, String name) 
    {
        this.entityType = entityType;
        this.name = name;
    }

    public ProfileFightKiller(EntityType entityType) 
    {
        this(entityType, MessageTool.getPrettyName(entityType.name()));
    }
}
