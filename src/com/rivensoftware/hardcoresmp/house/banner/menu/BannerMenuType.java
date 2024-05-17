package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.rivensoftware.hardcoresmp.house.banner.menu.shop.BannerShopMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.shop.customshop.BannerCustomizationShopMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop.BannerEffectShopMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop.HeldBannerNegativeEffectsShop;
import com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop.WallBannerNegativeEffectsShop;
import com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop.WallBannerPositiveEffectsShop;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.ipvp.canvas.slot.Slot;

public enum BannerMenuType 
{
	BANNER_ACTIVE_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	BannerActiveMenu.displayBannerActiveMenu(player);
		    });
		}
	},
	BANNER_CUSTOM_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	Profile profile = Profile.getByPlayer(player);
		    	if(profile.getHouse().getLord().equals(profile.getUuid()))
		    		BannerCustomMenu.displayBannerCustomMenu(player);
		    	else
		    		player.sendMessage(MessageTool.color("&cYou must be the lord to set your house's banner."));
		    });
		}
	},
	BANNER_DEFAULT_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	Profile profile = Profile.getByPlayer(player);
		    	if(profile.getHouse().getLord().equals(profile.getUuid()))
		    		BannerDefaultMenu.displaySetDefaultBannersMenu(player);
		    	else
		    		player.sendMessage(MessageTool.color("&cYou must be the lord to set your house's banner."));
		    });
		}
	},
	BANNER_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
				BannerMenu.displayBannerMenu(player);
		    });
		}
	},
	BANNER_SET_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	BannerSetMenu.displayBannerSetMenu(player);
		    });
		}
	},
	BANNER_SHOP_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	BannerShopMenu.displayBannerShopMenu(player);
		    });
		}
	}, 
	BANNER_EFFECT_SHOP_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	BannerEffectShopMenu.displayBannerShopMenu(player);
		    });
		}
	}, 
	BANNER_CUSTOMIZATION_SHOP_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	BannerCustomizationShopMenu.displayBannerShopMenu(player);
		    });
		}
	}, 
	HELD_BANNER_NEGATIVE_EFFECTS_SHOP_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	HeldBannerNegativeEffectsShop.displayHeldBannerNegativeEffectsShopMenu(player);
		    });
		}
	},
	WALL_BANNER_POSITIVE_EFFECTS_SHOP_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	WallBannerPositiveEffectsShop.displayWallBannerPositiveEffectsShopMenu(player);
		    });
		}
	},
	WALL_BANNER_NEGATIVE_EFFECTS_SHOP_MENU 
	{
		@Override
		public void openMenu(Slot slot) 
		{
		    slot.setClickHandler((player, info) -> 
		    {
		    	WallBannerNegativeEffectsShop.displayWallBannerNegativeEffectsShopMenu(player);
		    });
		}
	};
	
	public abstract void openMenu(Slot slot);
}
