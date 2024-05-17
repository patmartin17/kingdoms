package com.rivensoftware.hardcoresmp.house.banner.menu.shop;

import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenuType;
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

public class BannerShopMenu 
{	
	public static Menu bannerShopMenu() 
	{
	    return ChestMenu.builder(2)
	            .title(MessageTool.color("&7House Banner Shop"))
	            .redraw(true)
	            .build();
	}
	
	public static void displayBannerShopMenu(Player player) 
	{
	    Menu menu = bannerShopMenu();
	    
	    Slot customization = menu.getSlot(2);
	    customization.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.CREEPER_BANNER_PATTERN);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&a&lPurchase Banner Customization Items"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color(""));
	        lore.add(MessageTool.color("&7Purchase items that can be used"));
	        lore.add(MessageTool.color("&7to &ecustomize &7your house banner"));
	        lore.add(MessageTool.color("&7such as &fdyes, colored banners, etc."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot effects = menu.getSlot(6);
	    effects.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.WHITE_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&9&lPurchase House Banners"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Purchase banners that can be"));
	        lore.add(MessageTool.color("&7used by your house for an advantage"));
	        lore.add(MessageTool.color("&7&ein battle &7and in &eyour claims."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    BannerMenuType.BANNER_CUSTOMIZATION_SHOP_MENU.openMenu(customization);
	    BannerMenuType.BANNER_EFFECT_SHOP_MENU.openMenu(effects);
	    
	    BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_MENU);
	    
	    menu.open(player);
	    
	}
}
