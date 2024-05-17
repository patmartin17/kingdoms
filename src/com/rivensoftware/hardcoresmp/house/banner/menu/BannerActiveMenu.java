package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;

public class BannerActiveMenu
{
	public static Menu bannerActiveMenu() 
	{
	    return ChestMenu.builder(6)
	            .title(MessageTool.color("&7View Active Banners"))
	            .redraw(true)
	            .build();
	}
	
	public static void displayBannerActiveMenu(Player player) 
	{
	    Menu menu = bannerActiveMenu();
	    
	    Profile profile = Profile.getByPlayer(player);
	    House house = profile.getHouse();
	    
	    int i = 0;
	    for(Banner banner : house.getBannerList())
	    {
			Slot bannerEntry = menu.getSlot(i);
			bannerEntry.setItemTemplate(p -> 
			{
				ItemStack item = Banner.deserializeBannerEntry(house.getPrimaryBanner());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(MessageTool.color("&e" + banner.getEffect().getEffectName() + " &7Banner"));

				List<String> lore = new ArrayList<String>();
				lore.add("");
				lore.add(MessageTool.color("&7Located at&f: x: " + banner.getLocation().getBlockX() + ", y: " + banner.getLocation().getBlockY() + ", z: " + banner.getLocation().getBlockZ()));
				itemMeta.setLore(lore);

				item.setItemMeta(itemMeta);
				return item;
			});
			i++;
	    }
	    
	    BannerMenu.setReturnButton(menu, 45, BannerMenuType.BANNER_MENU);
	    
	    menu.open(player);
	    
	}
}
