package com.rivensoftware.hardcoresmp.house.banner.menu;

import lombok.Getter;
import org.bukkit.Material;

public enum BannerWallMaterial 
{
	WHITE_BANNER(Material.WHITE_WALL_BANNER),
	ORANGE_BANNER(Material.ORANGE_WALL_BANNER),
	MAGENTA_BANNER(Material.MAGENTA_WALL_BANNER),
	LIGHT_BLUE_BANNER(Material.LIGHT_BLUE_WALL_BANNER),
	YELLOW_BANNER(Material.YELLOW_WALL_BANNER),
	LIME_BANNER(Material.LIME_WALL_BANNER),
	PINK_BANNER(Material.PINK_WALL_BANNER),
	GRAY_BANNER(Material.GRAY_WALL_BANNER),
	LIGHT_GRAY_BANNER(Material.LIGHT_GRAY_WALL_BANNER),
	CYAN_BANNER(Material.CYAN_WALL_BANNER),
	PURPLE_BANNER(Material.PURPLE_WALL_BANNER),
	BLUE_BANNER(Material.BLUE_WALL_BANNER),
	BROWN_BANNER(Material.BROWN_WALL_BANNER),
	GREEN_BANNER(Material.GREEN_WALL_BANNER),
	RED_BANNER(Material.RED_WALL_BANNER),
	BLACK_BANNER(Material.BLACK_WALL_BANNER);
	
	@Getter private Material material;
	
	BannerWallMaterial(Material material)
	{
		this.material = material;
	}
}
