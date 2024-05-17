package com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop;

import com.rivensoftware.hardcoresmp.house.banner.effects.HeldBannerNegativeEffects;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenuType;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;

public class HeldBannerNegativeEffectsShop 
{
	public static Menu bannerHeldNegativeEffectsShopMenu() 
	{
	    return ChestMenu.builder(2)
	            .title(MessageTool.color("&c&lBannerman Debuff Banners"))
	            .redraw(true)
	            .build();
	}
	
	public static void displayHeldBannerNegativeEffectsShopMenu(Player player) 
	{
	    Menu menu = bannerHeldNegativeEffectsShopMenu();
	    
	    int i = 0;
	    for(HeldBannerNegativeEffects effects : HeldBannerNegativeEffects.values())
	    {
		    Slot entry = menu.getSlot(i);
		    entry.setItemTemplate(p -> 
		    {
		        ItemStack item = new ItemStack(effects.getDisplayMaterial());
		        ItemMeta itemMeta = item.getItemMeta();
		        itemMeta.setDisplayName(MessageTool.color("&e" + effects.getEffectName() + " &fEffect"));
		        
		        List<String> lore = new ArrayList<String>();
		        lore.add(MessageTool.color("&7This &ebanner &7will apply &e" + effects.getEffectName() + " &7effect"));
		        lore.add(MessageTool.color("&7to &cenemies &7within a 30 block radius"));
		        lore.add(MessageTool.color("&7by &fwearing this banner &7and using"));
		        lore.add(MessageTool.color("&7a &fgoats horn &7to activate."));
		        itemMeta.setLore(lore);
		        
		        item.setItemMeta(itemMeta);
		        return item;
		    });	
	    	i++;
	    }
	    
	    
	    BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_SHOP_MENU);
	    
	    menu.open(player);
	    
	}
}
