package com.rivensoftware.hardcoresmp.house.banner;

import lombok.Getter;

public enum BannerType
{
	HELD(new String[]{"&7(Right click to wear this banner)", "&fThis banner applies %EFFECT% to", "&fhouse members within a 30 block range."}),
	WALL(new String[]{"&7(Place this banner in your kingdoms claims)", "&fThis banner will apply %EFFECT% to all", "&fhouse members in your claim."});
	
	@Getter public String[] lore;
	
	BannerType(String[] lore)
	{
		this.lore = lore;
	}
}