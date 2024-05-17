package com.rivensoftware.hardcoresmp.house.banner;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.effects.HeldBannerNegativeEffects;
import com.rivensoftware.hardcoresmp.house.banner.effects.HeldBannerPositiveEffects;
import com.rivensoftware.hardcoresmp.house.banner.effects.WallBannerNegativeEffects;
import com.rivensoftware.hardcoresmp.house.banner.effects.WallBannerPositiveEffects;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;

@Getter
public class BannerEffect 
{
	@Setter private String effectName;
	@Setter private BannerType bannerType;
	@Setter private boolean isNegative;

	public static Set<BannerEffect> activeEffects = new HashSet<BannerEffect>();

	public BannerEffect(String name, BannerType bannerType, boolean isNegative)
	{
		setEffectName(name);
		setBannerType(bannerType);
		setNegative(isNegative);

		activeEffects.add(this);
	}

	public static void run()
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				for(Player players : Bukkit.getOnlinePlayers())
				{
					Profile profile = Profile.getByPlayer(players);
					if(profile.getHouse() != null)
					{
						House house = profile.getHouse();
						if(!house.getBannerList().isEmpty())
						{
							Claim claim = Claim.getProminentClaimAt(players.getLocation());
							if(claim != null && claim.getHouse().equals(house))
							{
								for(Banner banner : house.getBannerList())
								{
									if(!banner.getEffect().isNegative())
									{
										int level = Integer.valueOf(countWallLevel(WallBannerPositiveEffects.values(), banner.getEffect().getEffectName(), house)) - 1;
										
										if(level > WallBannerPositiveEffects.valueOf(banner.getEffect().getEffectName().toString().toUpperCase()).getMaxLevel())
											level = WallBannerPositiveEffects.valueOf(banner.getEffect().getEffectName().toString().toUpperCase()).getMaxLevel();

										BannerEffect.sendEffect(players, banner.getEffect(), level);
									}
								}
							}
						}
						
						if(!house.getAllEnemies().isEmpty())
						{
							for(House enemies : house.getAllEnemies())
							{
								Claim claim = Claim.getProminentClaimAt(players.getLocation());
								if(claim != null && claim.getHouse().equals(enemies))
								{
									for(Banner banner : enemies.getBannerList())
									{
										if(banner.getEffect().isNegative())
										{
											int level = Integer.valueOf(countWallLevel(WallBannerNegativeEffects.values(), banner.getEffect().getEffectName(), house)) - 1;
											
											if(level > WallBannerNegativeEffects.valueOf(banner.getEffect().getEffectName().toString().toUpperCase()).getMaxLevel())
												level = WallBannerNegativeEffects.valueOf(banner.getEffect().getEffectName().toString().toUpperCase()).getMaxLevel();
											
											BannerEffect.sendEffect(players, banner.getEffect(), level);	
										}
									}
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(HardcoreSMP.getInstance(), 0L, 60L);
	}

	public static boolean isEffect(String name)
	{
		for(BannerEffect effects : activeEffects)
		{
			if(effects.getEffectName().equalsIgnoreCase(name))
			{
				return true;
			}
		}
		return false;
	}

	public static BannerEffect getBannerEffect(String name)
	{
		for(BannerEffect effects : activeEffects)
		{
			if(effects.getEffectName().equalsIgnoreCase(name))
			{
				return effects;
			}
		}
		return null;
	}

	public static void sendEffect(Player player, BannerEffect effect, int level)
	{
		if(effect.isNegative)
		{
			if(effect.getBannerType().equals(BannerType.HELD))
			{
				HeldBannerNegativeEffects.valueOf(effect.getEffectName().toString().toUpperCase()).applyEffect(player);
			}
			else
			{
				new PotionEffect(PotionEffectType.getByName(effect.getEffectName().toUpperCase()), 120, level, true).apply(player);
			}
		}
		else
		{
			if(effect.getBannerType().equals(BannerType.HELD))
			{
				HeldBannerPositiveEffects.valueOf(effect.getEffectName().toString().toUpperCase()).applyEffect(player);
			}
			else
			{
				new PotionEffect(PotionEffectType.getByName(effect.getEffectName().toUpperCase()), 120, level, true).apply(player);
			}
		}
	}

	public static int countWallLevel(Object[] enumValues, String effectName, House house)
	{
		if(enumContains(enumValues, effectName))
		{
			int total = 0;
			for(Banner banner : house.getBannerList())
			{
				if(banner.getEffect().getEffectName().equals(effectName))
				{
					total++;
				}
			}
			return total;
		}
		return 0;
	}
	
	private static boolean enumContains(Object[] enumValues, String name)
	{
		for(Object o : enumValues)
		{
			if(o.toString().equals(name.toUpperCase()))
			{
				return true;
			}
		}
		return false;
	}
}
