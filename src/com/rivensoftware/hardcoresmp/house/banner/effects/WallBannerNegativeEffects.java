package com.rivensoftware.hardcoresmp.house.banner.effects;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum WallBannerNegativeEffects 
{
	//LIMIT IS +1 FOR ACTUAL LEVEL
	WITHER("Wither", Material.WITHER_SKELETON_SKULL, 1) 
	{
		@Override
		public void applyEffect(Player player, int level) 
		{
			int checkLevel = level - 1;
			if(checkLevel > maxLevel)
				checkLevel = maxLevel;
			
			player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 120, checkLevel));
		}
	};
	
	@Getter public String effectName;
	@Getter public Material displayMaterial;
	@Getter public int maxLevel;
	
	private WallBannerNegativeEffects(String effectName, Material displayMaterial, int maxLevel) 
	{
		this.effectName = effectName;
		this.displayMaterial = displayMaterial;
		this.maxLevel = maxLevel;
	}
	
	public abstract void applyEffect(Player player, int level);
}
