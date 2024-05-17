package com.rivensoftware.hardcoresmp.house.banner.effects;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum HeldBannerNegativeEffects 
{
	WITHER("Wither", Material.WITHER_SKELETON_SKULL) 
	{
		@Override
		public void applyEffect(Player player) 
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 200, 1));
		}
	};
	
	@Getter public String effectName;
	@Getter public Material displayMaterial;
	
	private HeldBannerNegativeEffects(String effectName, Material displayMaterial) 
	{
		this.effectName = effectName;
		this.displayMaterial = displayMaterial;
	}
	
	public abstract void applyEffect(Player player);
}
