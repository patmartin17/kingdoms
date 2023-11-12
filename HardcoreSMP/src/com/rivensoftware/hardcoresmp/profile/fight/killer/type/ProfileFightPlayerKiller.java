package com.rivensoftware.hardcoresmp.profile.fight.killer.type;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import com.rivensoftware.hardcoresmp.profile.fight.ProfileFightEffect;
import com.rivensoftware.hardcoresmp.profile.fight.killer.ProfileFightKiller;

import lombok.Getter;

public class ProfileFightPlayerKiller extends ProfileFightKiller 
{

    @Getter private final UUID uuid;
    @Getter private final PlayerInventory inventory;
    @Getter private final double health, hunger;
    @Getter private final int ping;
    @Getter private final List<ProfileFightEffect> effects;

    public ProfileFightPlayerKiller(Player player) 
    {
        this(player.getName(), player.getUniqueId(), player.getPing(), player.getInventory(), player.getHealth(), player.getFoodLevel(), new ArrayList<>());

        for (PotionEffect effect : player.getActivePotionEffects()) 
        {
            effects.add(new ProfileFightEffect(effect));
        }
    }

    public ProfileFightPlayerKiller(String name, UUID uuid, int ping, PlayerInventory inventory, double health, double hunger, List<ProfileFightEffect> effects)
    {
        super(EntityType.PLAYER, name);

        this.ping = ping;
        this.uuid = uuid;
        this.inventory = inventory;
        this.health = health;
        this.hunger = hunger;
        this.effects = effects;
    }

}
