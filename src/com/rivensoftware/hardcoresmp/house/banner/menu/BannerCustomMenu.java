package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.type.ChestMenu;

public class BannerCustomMenu 
{
	public static Menu bannerCustomSetting() 
	{
		return ChestMenu.builder(2)
				.title(MessageTool.color("&7Set Custom Banner"))
				.redraw(false)
				.build();
	}

	public static void displayBannerCustomMenu(Player player) 
	{
		Menu menu = bannerCustomSetting();
		Profile profile = Profile.getByPlayer(player);

		if(profile.getHouse().isCustomBanner())
		{
			for(int i = 0; i < profile.getHouse().getCustomBannerList().size(); i++)
			{

				if(profile.getHouse().getCustomBannerList().size() != 0)
				{
					if(profile.getHouse().getCustomBannerList().get(i) != null)
					{
						if(i == profile.getHouse().getPrimaryBannerIndex())
						{
							ItemStack item = Banner.deserializeBannerEntry(profile.getHouse().getCustomBannerList().get(i));
							ItemMeta meta = item.getItemMeta();
							meta.setDisplayName(MessageTool.color("&a&lPrimary Banner"));
							item.setItemMeta(meta);
							menu.getSlot(i).setItem(item);
						}
						else
						{
							menu.getSlot(i).setItem(Banner.deserializeBannerEntry(profile.getHouse().getCustomBannerList().get(i)));
						}

					}	
				}
			}

			for(int i = 0; i <= 8; i++)
			{
				BannerMenu.allowPlacement(menu.getSlot(i));
			}

			for(int i = 9; i <= 17; i++)
			{
				menu.getSlot(i).setItem(new ItemStack(Material.RED_STAINED_GLASS_PANE));
			}
			
    	    BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_SET_MENU);

		}
		else
		{
			BannerConfirmMenu.displayBannerConfirmMenu(player, true);
			return;
		}

		menu.open(player);

	}
}
