package com.rivensoftware.hardcoresmp.house.banner.effects;

import lombok.Getter;
import org.bukkit.Material;

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
