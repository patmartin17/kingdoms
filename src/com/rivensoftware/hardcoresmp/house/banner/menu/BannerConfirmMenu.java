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

public class BannerConfirmMenu 
{

	public static Menu bannerConfirmMenu(boolean setCustom) 
	{
		if(setCustom)
		{
			return ChestMenu.builder(1)
					.title(MessageTool.color("&7Confirm Custom Banner"))
					.redraw(true)
					.build();	
		}
		return ChestMenu.builder(1)
				.title(MessageTool.color("&7Confirm Default Banner"))
				.redraw(true)
				.build();	
	}	

	public static void displayBannerConfirmMenu(Player player, boolean setCustom) 
	{
		Menu menu = bannerConfirmMenu(setCustom);

		Slot confirm = menu.getSlot(2);
		confirm.setItemTemplate(p -> 
		{
			ItemStack item = new ItemStack(Material.LIME_WOOL);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(MessageTool.color("&a&lConfirm"));

			List<String> lore = new ArrayList<String>();
			lore.add(MessageTool.color("&7Confirm setting your house banner to"));
			if(setCustom)
			{
				lore.add(MessageTool.color("&ecustom &7which requires you to choose"));
				lore.add(MessageTool.color("&f1-9 banners &7and select your main banner."));
				lore.add(MessageTool.color("&7(You can switch back to default any time)"));
			}
			else
			{
				lore.add(MessageTool.color("&edefault &7which requires you to choose"));
				lore.add(MessageTool.color("&f2 banners&7, a primary and a secondary"));
				lore.add(MessageTool.color("&7(You can switch back to custom any time)"));
			}
			itemMeta.setLore(lore);

			item.setItemMeta(itemMeta);
			return item;
		});

		Slot deny = menu.getSlot(6);
		deny.setItemTemplate(p -> 
		{
			ItemStack item = new ItemStack(Material.RED_WOOL);
			ItemMeta itemMeta = item.getItemMeta();
			itemMeta.setDisplayName(MessageTool.color("&c&lCancel"));

			List<String> lore = new ArrayList<String>();
			lore.add(MessageTool.color("&7Exit the confirmation process."));
			itemMeta.setLore(lore);

			item.setItemMeta(itemMeta);
			return item;
		});

		setSetting(confirm, setCustom);

		deny.setClickHandler((p, info) -> 
		{
			p.closeInventory();
		});

		menu.open(player);
	}

	public static void setSetting(Slot slot, boolean setCustom) 
	{
		slot.setClickHandler((player, info) -> 
		{
			Profile profile = Profile.getByPlayer(player);
			House house = profile.getHouse();
			house.setCustomBanner(setCustom);

			if(setCustom)
				BannerCustomMenu.displayBannerCustomMenu(player);
			else
				BannerDefaultMenu.displaySetDefaultBannersMenu(player);
		});
	}
}
