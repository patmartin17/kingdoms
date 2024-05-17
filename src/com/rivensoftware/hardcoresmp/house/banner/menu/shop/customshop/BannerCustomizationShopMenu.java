package com.rivensoftware.hardcoresmp.house.banner.menu.shop.customshop;

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

public class BannerCustomizationShopMenu 
{	
	public static Menu bannerCustomizationShopMenu() 
	{
		return ChestMenu.builder(6)
				.title(MessageTool.color("&7Banner Customization Shop"))
				.redraw(true)
				.build();
	}

	public static void displayBannerShopMenu(Player player) 
	{
		Menu menu = bannerCustomizationShopMenu();

		int i = 0;

		for(BannerCustomizationShopItems items : BannerCustomizationShopItems.values())
		{
			Slot entry = menu.getSlot(i);
			entry.setItemTemplate(p -> 
			{
				ItemStack item = new ItemStack(items.getMaterial());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(MessageTool.color("&f&l" + MessageTool.getPrettyName(items.name())));

				List<String> lore = new ArrayList<String>();
				lore.add(MessageTool.color("&7Cost: &6" + items.getPrice() + "g"));
				itemMeta.setLore(lore);

				item.setItemMeta(itemMeta);
				
				return item;
			});	
			
			BannerMenu.handleBuy(menu.getSlot(i), new ItemStack(BannerCustomizationShopItems.values()[i].getMaterial()), BannerCustomizationShopItems.values()[i].getPrice());
			
			i++;
		}

		Slot loom = menu.getSlot(49);
		loom.setItemTemplate(p -> 
		{
			ItemStack item = new ItemStack(Material.LOOM);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(MessageTool.color("&e&lBuy a Loom"));

			List<String> lore = new ArrayList<String>();
			lore.add(MessageTool.color("&7Cost: &60.1g"));
			itemMeta.setLore(lore);

			item.setItemMeta(itemMeta);
			return item;
		});
		
		BannerMenu.handleBuy(menu.getSlot(49), new ItemStack(Material.LOOM), 0.1);

		BannerMenu.setReturnButton(menu, 45, BannerMenuType.BANNER_MENU);

		menu.open(player);
	}
}
