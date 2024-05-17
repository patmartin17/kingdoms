package com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop;

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

public class BannerEffectShopMenu 
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
	    
	    Slot heldPositive = menu.getSlot(1);
	    heldPositive.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.LIME_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&a&lBannerman Buff Banners"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Purchase &ebuffs &7that can be"));
	        lore.add(MessageTool.color("&7activated by &fa bannerman &7with"));
	        lore.add(MessageTool.color("&fa goats horn &7during battle."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot heldNegative = menu.getSlot(3);
	    heldNegative.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.RED_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&c&lBannerman Debuff Banners"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Purchase &cdebuffs &7that can be"));
	        lore.add(MessageTool.color("&7sent to enemies by &fa bannerman &7with"));
	        lore.add(MessageTool.color("&fa goats horn &7during battle."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot wallPositive = menu.getSlot(5);
	    wallPositive.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.BLUE_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&9&lKingdom Buff Banners"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Purchase &ebuffs &7that can be"));
	        lore.add(MessageTool.color("&7applied constantly inside &fyour claim"));
	        lore.add(MessageTool.color("&7by placing the banner &fwithin your territory."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot wallNegative = menu.getSlot(7);
	    wallNegative.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.BLACK_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&8&lKingdom Debuff Banners"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Purchase &cdebuffs &7that can be"));
	        lore.add(MessageTool.color("&7sent to enemies constantly inside &fyour claim"));
	        lore.add(MessageTool.color("&7by placing the banner &fwithin your territory."));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    BannerMenuType.HELD_BANNER_NEGATIVE_EFFECTS_SHOP_MENU.openMenu(heldNegative);
	    BannerMenuType.WALL_BANNER_NEGATIVE_EFFECTS_SHOP_MENU.openMenu(wallNegative);
	    BannerMenuType.WALL_BANNER_POSITIVE_EFFECTS_SHOP_MENU.openMenu(wallPositive);
	    
	    BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_MENU);
	    
	    menu.open(player);
	    
	}
}
