package com.rivensoftware.hardcoresmp.profile.fight;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import com.rivensoftware.hardcoresmp.profile.fight.killer.ProfileFightKiller;

import lombok.Getter;

public class ProfileFight 
{
    @Getter private final UUID uuid;
    @Getter private final ProfileFightKiller killer;
    @Getter private final long occurredAt;
    @Getter private final ItemStack[] inventory;
    @Getter private final double hunger;
    @Getter private final List<ProfileFightEffect> effects;
    @Getter private final Location location;
    @Getter private final int ping;

    public ProfileFight(Player player, ProfileFightKiller killer) 
    {
        this(UUID.randomUUID(), player.getPing(), System.currentTimeMillis(), player.getInventory().getContents(), player.getFoodLevel(), new ArrayList<>(), killer, player.getLocation());

        for (PotionEffect effect : player.getActivePotionEffects()) 
        {
            effects.add(new ProfileFightEffect(effect));
        }
    }

    public ProfileFight(UUID uuid, int ping, long occurredAt, ItemStack[] inventory, double hunger, List<ProfileFightEffect> effects, ProfileFightKiller killer, Location location) 
    {
        this.uuid = uuid;

        this.occurredAt = occurredAt;
        this.inventory = inventory;
        this.hunger = hunger;
        this.killer = killer;
        this.effects = effects;
        this.location = location;
        this.ping = ping;
    }

    public boolean hasArmor() 
    {
        for (ItemStack itemStack : getArmorContents()) 
        {
            if (itemStack != null && itemStack.getType() != Material.AIR) 
            {
                return true;
            }
        }
        return false;
    }

    public int getInventorySize() 
    {
        int toReturn = 0;

        for (ItemStack itemStack : inventory) 
        {
            if (itemStack != null) 
            {
                toReturn++;
            }
        }

        return toReturn;
    }
    
    public ItemStack[] getInventoryContents()
    {
    	ItemStack[] items = new ItemStack[36];
    	for(int i = 0; i <= 35; i++)
    	{
    		items[i] = inventory[i];
    	}
    	return items;
    }
    
    public ItemStack[] getArmorContents()
    {
    	ItemStack[] items = new ItemStack[4];
    	for(int i = 0; i <= 3; i++)
    	{
    		items[i] = inventory[i + 36];
    	}
    	return items;
    }
    
    public ItemStack getOffHand()
    {
    	return inventory[40];
    }
}
