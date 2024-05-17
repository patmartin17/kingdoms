package com.rivensoftware.hardcoresmp.house.banner.menu.shop.effectshop;

import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.house.banner.BannerEffect;
import com.rivensoftware.hardcoresmp.house.banner.BannerType;
import com.rivensoftware.hardcoresmp.house.banner.effects.WallBannerPositiveEffects;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenu;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerMenuType;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.util.ArrayList;
import java.util.List;

public class WallBannerPositiveEffectsShop 
{
	public static Menu bannerWallPositiveEffectsShopMenu() 
	{
		return ChestMenu.builder(2)
				.title(MessageTool.color("&9&lBannerman Debuff Banners"))
				.redraw(true)
				.build();
	}

	public static void displayWallBannerPositiveEffectsShopMenu(Player player) 
	{
		Menu menu = bannerWallPositiveEffectsShopMenu();

		int i = 0;
		for(WallBannerPositiveEffects effects : WallBannerPositiveEffects.values())
		{
			Slot entry = menu.getSlot(i);
			entry.setItemTemplate(p -> 
			{
				ItemStack item = new ItemStack(effects.getDisplayMaterial());
				ItemMeta itemMeta = item.getItemMeta();
				itemMeta.setDisplayName(MessageTool.color("&e" + effects.getEffectName() + " &fEffect"));

				List<String> lore = new ArrayList<String>();
				lore.add(MessageTool.color("&7This &ebanner &7will apply &e" + effects.getEffectName() + " &7effect"));
				lore.add(MessageTool.color("&7to &ehouse members &7that remain in your"));
				lore.add(MessageTool.color("&7territory by &fplacing this banner &7somewhere"));
				lore.add(MessageTool.color("&7within your &fclaimed territory&7."));
				itemMeta.setLore(lore);

				item.setItemMeta(itemMeta);
				return item;
			});	

			entry.setClickHandler((p, info) -> 
			{
				if(player.getInventory().firstEmpty() != -1)
				{
					Profile profile = Profile.getByPlayer(p);
					if(profile.getHouse().getPrimaryBanner() != null)
					{
						ItemStack banner =  Banner.getBannerItem(BannerType.WALL, profile.getHouse(), BannerEffect.getBannerEffect(effects.name()).getEffectName());
						p.getInventory().addItem(banner);
						p.sendMessage(MessageTool.color("&cYou have been given a &cwall " + effects.getEffectName() + " banner"));	
					}
					else
					{
						p.sendMessage(MessageTool.color("&cYour house does not have a banner set."));	
					}
				}
				else
				{
					player.sendMessage(MessageTool.color("&cYour inventory is full!"));
					return;
				}
			});

			i++;
		}


		BannerMenu.setReturnButton(menu, 9, BannerMenuType.BANNER_SHOP_MENU);

		menu.open(player);

	}
}
