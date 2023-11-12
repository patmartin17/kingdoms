package com.rivensoftware.hardcoresmp.house.banner.effects;

import org.bukkit.Material;

import lombok.Getter;

public enum WallBannerPositiveEffects 
{
	SPEED("Speed", Material.SUGAR, 2);

	@Getter public String effectName;
	@Getter public Material displayMaterial;
	@Getter public int maxLevel;

	private WallBannerPositiveEffects(String effectName, Material displayMaterial, int maxLevel) 
	{
		this.effectName = effectName;
		this.displayMaterial = displayMaterial;
		this.maxLevel = maxLevel;
	}
}
