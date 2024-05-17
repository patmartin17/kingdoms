package com.rivensoftware.hardcoresmp.house.banner.menu.shop.customshop;

import lombok.Getter;
import org.bukkit.Material;

public enum BannerCustomizationShopItems 
{
	FLOWER_CHARGE(Material.FLOWER_BANNER_PATTERN, 0.5),
	CREEPER_CHARGE(Material.CREEPER_BANNER_PATTERN, 0.5),
	SKULL_CHARGE(Material.SKULL_BANNER_PATTERN, 0.5),
	THING(Material.MOJANG_BANNER_PATTERN, 0.5),
	GLOBE(Material.GLOBE_BANNER_PATTERN, 0.5),
	SNOUT(Material.PIGLIN_BANNER_PATTERN, 0.5),
	WHITE_DYE(Material.WHITE_DYE, 0.1),
	ORANGE_DYE(Material.ORANGE_DYE, 0.1),
	MAGENTA_DYE(Material.MAGENTA_DYE, 0.1),
	LIGHT_BLUE_DYE(Material.LIGHT_BLUE_DYE, 0.1),
	YELLOW_DYE(Material.YELLOW_DYE, 0.1),
	LIME_DYE(Material.LIME_DYE, 0.1),
	PINK_DYE(Material.PINK_DYE, 0.1),
	GRAY_DYE(Material.GRAY_DYE, 0.1),
	LIGHT_GRAY_DYE(Material.LIGHT_GRAY_DYE, 0.1),
	CYAN_DYE(Material.CYAN_DYE, 0.1),
	PURPLE_DYE(Material.PURPLE_DYE, 0.1),
	BLUE_DYE(Material.BLUE_DYE, 0.1),
	BROWN_DYE(Material.BROWN_DYE, 0.1),
	GREEN_DYE(Material.GREEN_DYE, 0.1),
	RED_DYE(Material.RED_DYE, 0.1),
	BLACK_DYE(Material.BLACK_DYE, 0.1),
	WHITE_BANNER(Material.WHITE_BANNER, 1.0),
	ORANGE_BANNER(Material.ORANGE_BANNER, 1.0),
	MAGENTA_BANNER(Material.MAGENTA_BANNER, 1.0),
	LIGHT_BLUE_BANNER(Material.LIGHT_BLUE_BANNER, 1.0),
	YELLOW_BANNER(Material.YELLOW_BANNER, 1.0),
	LIME_BANNER(Material.LIME_BANNER, 1.0),
	PINK_BANNER(Material.PINK_BANNER, 1.0),
	GRAY_BANNER(Material.GRAY_BANNER, 1.0),
	LIGHT_GRAY_BANNER(Material.LIGHT_GRAY_BANNER, 1.0),
	CYAN_BANNER(Material.CYAN_BANNER, 1.0),
	PURPLE_BANNER(Material.PURPLE_BANNER, 1.0),
	BLUE_BANNER(Material.BLUE_BANNER, 1.0),
	BROWN_BANNER(Material.BROWN_BANNER, 1.0),
	GREEN_BANNER(Material.GREEN_BANNER, 1.0),
	RED_BANNER(Material.RED_BANNER, 1.0),
	BLACK_BANNER(Material.BLACK_BANNER, 1.0);
	
	@Getter private Material material;
	@Getter private double price;
	
	BannerCustomizationShopItems(Material material, double price)
	{
		this.material = material;
		this.price = price;
	}
}
