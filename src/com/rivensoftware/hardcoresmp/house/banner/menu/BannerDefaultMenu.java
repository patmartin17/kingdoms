package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;

public class BannerDefaultMenu 
{
	public static Menu bannerDefaultSetting() 
	{
	    return ChestMenu.builder(2)
	            .title(MessageTool.color("&7Set Default Banner"))
	            .redraw(false)
	            .build();
	}
	
	public static void displaySetDefaultBannersMenu(Player player) 
	{
	    Menu menu = bannerDefaultSetting();
	    Profile profile = Profile.getByPlayer(player);
	    
    	if(!profile.getHouse().isCustomBanner())
    	{
    	    for(int i = 0; i <= 8; i++)
    	    {
    	    	Slot slot = menu.getSlot(i);
    	    	slot.setItem(new ItemStack(Material.BLUE_STAINED_GLASS_PANE));
    	    }
    	    
    	    Slot arrowPrimary = menu.getSlot(1);
    	    arrowPrimary.setItemTemplate(p -> 
    	    {
    	        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    	    	String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzYjhmNjgxZGFhZDhiZjQzNmNhZThkYTNmZTgxMzFmNjJhMTYyYWI4MWFmNjM5YzNlMDY0NGFhNmFiYWMyZiJ9fX0=";
    	    	
    	        SkullMeta itemMeta = BannerMenu.getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
    	        itemMeta.addEnchant(Enchantment.DURABILITY, 0, false);
    	        itemMeta.setDisplayName(MessageTool.color("&c&lPlace your &eprimary banner &c&lto the right"));
    	        
    	        List<String> lore = new ArrayList<String>();
    	        lore.add(MessageTool.color("&7Place your desired &eprimary banner &7to the right."));
    	        lore.add(MessageTool.color("&7This banner will be used by your &ekingdom's base"));
    	        lore.add(MessageTool.color("&7and by your &ehouse's bannermen &7during battle"));
    	        itemMeta.setLore(lore);
    	        
    	        item.setItemMeta(itemMeta);
    	        return item;
    	    });
    	    
    	    Slot arrowSecondary = menu.getSlot(5);
    	    arrowSecondary.setItemTemplate(p -> 
    	    {
    	        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
    	    	String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzYjhmNjgxZGFhZDhiZjQzNmNhZThkYTNmZTgxMzFmNjJhMTYyYWI4MWFmNjM5YzNlMDY0NGFhNmFiYWMyZiJ9fX0=";
    	    	
    	        SkullMeta itemMeta = BannerMenu.getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
    	        itemMeta.addEnchant(Enchantment.DURABILITY, 0, false);
    	        itemMeta.setDisplayName(MessageTool.color("&c&lPlace your &esecondary banner &c&lto the right"));
    	        
    	        List<String> lore = new ArrayList<String>();
    	        lore.add(MessageTool.color("&7Place your desired &esecondary banner &7to the right."));
    	        lore.add(MessageTool.color("&7This banner will be used to enhance your primary"));
    	        lore.add(MessageTool.color("&7banners design when displayed on your &ekingdom's base&7."));
    	        itemMeta.setLore(lore);
    	        
    	        item.setItemMeta(itemMeta);
    	        return item;
    	    });
    	    
    	    for(int i = 9; i <= 17; i++)
    	    {
    	    	menu.getSlot(i).setItem(new ItemStack(Material.RED_STAINED_GLASS_PANE));
    	    }
    	    
    	    if(profile.getHouse().getPrimaryBanner() != null && !profile.getHouse().getPrimaryBanner().equals(""))
    	    	menu.getSlot(2).setItem(Banner.deserializeBannerEntry(profile.getHouse().getPrimaryBanner()));
    	    else
    	    	menu.getSlot(2).setItem(new ItemStack(Material.AIR));
    	    if(profile.getHouse().getSecondaryBanner() != null && !profile.getHouse().getSecondaryBanner().equals(""))
    	    	menu.getSlot(6).setItem(Banner.deserializeBannerEntry(profile.getHouse().getSecondaryBanner()));
    	    else
    	    	menu.getSlot(6).setItem(new ItemStack(Material.AIR));
    	    
    	    BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_SET_MENU);
    	    
    	    BannerMenu.allowPlacement(menu.getSlot(2));
    	    BannerMenu.allowPlacement(menu.getSlot(6));
    	    
    	    menu.open(player);    		
    	}
    	else
    	{
    		BannerConfirmMenu.displayBannerConfirmMenu(player, false);
    	}
	}
}
