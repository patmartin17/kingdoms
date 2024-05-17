package com.rivensoftware.hardcoresmp.house.banner;

import com.rivensoftware.hardcoresmp.profile.Profile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class BattleListeners implements Listener
{
	@EventHandler
	public void onHornBlow(PlayerInteractEvent event)
	{
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			Player player = event.getPlayer();
			Profile profile = Profile.getByPlayer(player);
			if(!player.getInventory().getItemInMainHand().getType().equals(Material.GOAT_HORN))
			{
				return;
			}
			if(player.getInventory().getHelmet() != null && !player.getInventory().getHelmet().getType().equals(Material.AIR))
			{
				return;
			}
			if(!player.getInventory().getHelmet().getType().name().contains("BANNER"))
			{
				return;
			}
			String[] splitEffect = ChatColor.stripColor(player.getInventory().getHelmet().getItemMeta().getDisplayName()).split("HELD");
			String effect = splitEffect[1].replace(")", "").replace(" (", "");

			for(Entity entity : player.getNearbyEntities(30.0, 30.0, 30.0))
			{
				if(entity instanceof Player)
				{
					if(BannerEffect.getBannerEffect(effect).isNegative())
					{
						if(!profile.getHouse().getAllPlayerUuids().contains(((Player)entity).getUniqueId()))
						{
							//BannerEffect.getBannerEffect(effect).sendEffect((Player)entity, 1);
						}
					}
					else
					{
						if(profile.getHouse().getAllPlayerUuids().contains(((Player)entity).getUniqueId()))
						{
							//BannerEffect.getBannerEffect(effect).sendEffect((Player)entity, 1);
						}
					}
				}
			}
			if(!BannerEffect.getBannerEffect(effect).isNegative())
			{
				//BannerEffect.getBannerEffect(effect).sendEffect(player, 1);
			}
		}
	}
}
