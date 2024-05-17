package com.rivensoftware.hardcoresmp.house.banner.effects;

import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public enum HeldBannerPositiveEffects 
{
	SPEED("Speed", Material.SUGAR) 
	{
		@Override
		public void applyEffect(Player player) 
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 200, 1));
		}
	},
	STRENGTH("Strength", Material.BLAZE_POWDER) 
	{
		@Override
		public void applyEffect(Player player) 
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 200, 1));
		}
	},
	REGEN("Regen", Material.GHAST_TEAR) 
	{
		@Override
		public void applyEffect(Player player) 
		{
			player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 200, 1));
		}
	},
	JESUS("Jesus", Material.WATER_BUCKET) 
	{
		@Override
		public void applyEffect(Player player) 
		{
			
		}
	};
	
	@Getter public String effectName;
	@Getter public Material displayMaterial;
	
	private HeldBannerPositiveEffects(String effectName, Material displayMaterial) 
	{
		this.effectName = effectName;
		this.displayMaterial = displayMaterial;
	}
	
	public abstract void applyEffect(Player player);
}
