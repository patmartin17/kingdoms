package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

public class BannerViewMenu 
{
	public static Menu bannerViewMenu(String houseName) 
	{
		return ChestMenu.builder(1)
				.title(MessageTool.color("&9" + houseName + "&7's Banner"))
				.redraw(false)
				.build();
	}

	public static void displayBannerViewMenu(Player player, House house) 
	{
		Menu menu = bannerViewMenu(house.getHouseName());

		if(house.isCustomBanner())
		{
			for(int i = 0; i < house.getCustomBannerList().size(); i++)
			{

				if(house.getCustomBannerList().size() != 0)
				{
					if(house.getCustomBannerList().get(i) != null)
					{
						if(i == house.getPrimaryBannerIndex())
						{
							ItemStack item = Banner.deserializeBannerEntry(house.getCustomBannerList().get(i));
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(MessageTool.color("&a&lPrimary Banner"));
							item.setItemMeta(meta);
							menu.getSlot(i).setItem(item);
						}
						else
						{
							menu.getSlot(i).setItem(Banner.deserializeBannerEntry(house.getCustomBannerList().get(i)));
						}
					}	
				}
			}

		}
		else
		{
			ItemStack secondary = null;
			if(house.getSecondaryBanner() == null)
			{
				secondary = new ItemStack(Banner.deserializeBannerEntry(house.getPrimaryBanner()).getType());	
			}
			else
			{
				secondary = new ItemStack(Banner.deserializeBannerEntry(house.getSecondaryBanner()).getType());	
			}
			
			ItemStack item = Banner.deserializeBannerEntry(house.getPrimaryBanner());
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(MessageTool.color("&a&lPrimary Banner"));
			item.setItemMeta(meta);
			
			menu.getSlot(0).setItem(item);
			
			for(int i = 1; i <= 8; i++)
			{
				menu.getSlot(i).setItem(secondary);
			}
			
		}
		menu.open(player);
	}
}
