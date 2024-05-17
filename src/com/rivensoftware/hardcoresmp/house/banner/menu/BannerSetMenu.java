package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;

public class BannerSetMenu
{
	public static Menu bannerSetMenu() 
	{
	    return ChestMenu.builder(2)
	            .title(MessageTool.color("&7Set Banner"))
	            .build();
	}
	
	public static void displayBannerSetMenu(Player player) 
	{
		Profile profile = Profile.getByPlayer(player);
		House house = House.getByPlayer(player);
		
		if(!house.getLord().equals(profile.getUuid()))
		{
			player.sendMessage(MessageTool.color("&cYou must be the lord the set the house banner."));
			return;
		}
		
	    Menu menu = bannerSetMenu();
	    
	    Slot defaultBanner = menu.getSlot(2);
	    defaultBanner.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.WHITE_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&7&lDefault Banner"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Set a &eprimary banner &7and a"));
	        lore.add(MessageTool.color("&esecondary banner &7that &fwill expand &7when"));
	        lore.add(MessageTool.color("&7placed somewhere in your territory."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot customBanner = menu.getSlot(6);
	    customBanner.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.BLUE_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&9&lCustom Banner"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Set up to &e9 banners &7in your"));
	        lore.add(MessageTool.color("&7desired order that &fwill expand when"));
	        lore.add(MessageTool.color("&7placed somewhere in your territory."));
	        lore.add(MessageTool.color(""));
	        lore.add(MessageTool.color("&7(Middle click the banner you want to be your main banner)"));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_MENU);
	    
	    BannerMenuType.BANNER_DEFAULT_MENU.openMenu(defaultBanner);
	    BannerMenuType.BANNER_CUSTOM_MENU.openMenu(customBanner);
	    
	    menu.open(player);
	}
}
