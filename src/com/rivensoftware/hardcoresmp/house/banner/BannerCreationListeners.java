package com.rivensoftware.hardcoresmp.house.banner;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.BannerTool;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class BannerCreationListeners implements Listener
{
	@EventHandler
	public void onSetBanner(InventoryCloseEvent event)
	{
		Player player = (Player) event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		House house = profile.getHouse();

		if(event.getView().getTitle().contains("Set Custom Banner"))
		{
			if(!checkFisrtRow(event.getInventory()) && !house.getBannerList().isEmpty())
			{
				player.sendMessage(MessageTool.color("&cYou must have at least one banner set. Changes were not saved."));
				return;
			}
			
			List<String> newBanners = new ArrayList<String>();
			for(int i = 0; i <= 8; i++)
			{
				if(event.getInventory().getItem(i) == null || event.getInventory().getItem(i).getType().equals(Material.AIR))
				{
					continue;
				}

				if(event.getInventory().getItem(i).getType().name().contains("BANNER"))
				{
					newBanners.add(Banner.serializeBannerEntry(event.getInventory().getItem(i)));
					if(event.getInventory().getItem(i).getAmount() > 1)
					{
						ItemStack item = new ItemStack(event.getInventory().getItem(i).getType());
						ItemMeta meta = event.getInventory().getItem(i).getItemMeta();
						item.setAmount(event.getInventory().getItem(i).getAmount() - 1);
						item.setItemMeta(meta);
						BannerTool.addOrDropIncorrectItems(player, item);
					}
				}
				else
				{
					ItemStack item = new ItemStack(event.getInventory().getItem(i).getType());
					ItemMeta meta = event.getInventory().getItem(i).getItemMeta();
					item.setAmount(event.getInventory().getItem(i).getAmount());
					item.setItemMeta(meta);

					BannerTool.addOrDropIncorrectItems(player, item);
				}
			}

			if(BannerTool.areBannersEqual(profile.getHouse().getCustomBannerList(), newBanners)) 
			{
				return;
			}

			if(newBanners.size() > 0)
			{
				house.setPrimaryBanner(newBanners.get(0));
				house.setPrimaryBannerIndex(0);	
			}
			else
			{
				house.setPrimaryBanner(null);
				house.setPrimaryBannerIndex(-1);
			}
			house.setCustomBannerList(newBanners);
			
			if(!house.getBannerList().isEmpty())
				Banner.replaceHouseBanners(player);
			
			house.sendMessage(MessageTool.color("&a" + Bukkit.getPlayer(house.getLord()).getName() + " &fhas updated the house banner."));

		}
		else if(event.getView().getTitle().contains("Set Default Banner"))
		{
			if(event.getInventory().getItem(2) == null && !house.getBannerList().isEmpty())
			{
				player.sendMessage(MessageTool.color("&cYou must have a banner set. Changes were not saved."));
				return;
			}
			
			if(event.getInventory().getItem(2) != null && event.getInventory().getItem(2).getType().name().contains("BANNER"))
			{
				if(house.getPrimaryBanner() != null)
				{
					if(!BannerTool.isBannerEqual(house.getPrimaryBanner(), Banner.serializeBannerEntry(event.getInventory().getItem(2))))
					{
						house.setPrimaryBanner(Banner.serializeBannerEntry(event.getInventory().getItem(2)));
						if(!house.getBannerList().isEmpty())
							Banner.replaceHouseBanners(player);
						house.sendMessage(MessageTool.color("&a" + Bukkit.getPlayer(house.getLord()).getName() + " &fhas updated the house banner."));

						//drop extra primary banners
						BannerTool.dropIndexedItems(event.getInventory(), 2, player);
					}
				}
				else
				{
					house.setPrimaryBanner(Banner.serializeBannerEntry(event.getInventory().getItem(2)));
					if(!house.getBannerList().isEmpty())
						Banner.replaceHouseBanners(player);
					house.sendMessage(MessageTool.color("&a" + Bukkit.getPlayer(house.getLord()).getName() + " &fhas updated the house banner."));

					BannerTool.dropIndexedItems(event.getInventory(), 2, player);
				}
			}
			else
			{
				if(house.getPrimaryBanner() != null)
				{
					if(!house.getBannerList().isEmpty())
						Banner.replaceHouseBanners(player);
					house.sendMessage(MessageTool.color("&a" + Bukkit.getPlayer(house.getLord()).getName() + " &fhas updated the house banner."));
				}
				house.setPrimaryBanner(null);
			}

			//Secondary banner check
			if(event.getInventory().getItem(6) != null && event.getInventory().getItem(6).getType().name().contains("BANNER"))
			{
				if(house.getSecondaryBanner() != null)
				{
					if(!BannerTool.isBannerEqual(house.getSecondaryBanner(), Banner.serializeBannerEntry(event.getInventory().getItem(6))))
					{
						house.setSecondaryBanner(Banner.serializeBannerEntry(event.getInventory().getItem(6)));

						//drop extra primary banners
						BannerTool.dropIndexedItems(event.getInventory(), 6, player);
					}
				}
				else
				{
					house.setSecondaryBanner(Banner.serializeBannerEntry(event.getInventory().getItem(6)));
					BannerTool.dropIndexedItems(event.getInventory(), 6, player);
				}
			}
			else
			{
				house.setSecondaryBanner(null);
			}
		}
	}


	@EventHandler
	public void onMainBannerSelect(InventoryClickEvent event)
	{
		if(!event.getView().getTitle().contains("Custom Banner"))
		{
			return;
		}
		if(!event.getClick().equals(ClickType.MIDDLE))
		{
			return;
		}
		if(!event.getCurrentItem().getType().name().contains("BANNER"))
		{
			return;
		}
		event.setCancelled(true);

		Player player = (Player) event.getWhoClicked();
		Profile profile = Profile.getByPlayer(player);

		int currentIndex = profile.getHouse().getPrimaryBannerIndex();
		ItemStack currentIndexItem = event.getInventory().getItem(currentIndex);
		ItemMeta currentIndexMeta = currentIndexItem.getItemMeta();
		currentIndexMeta.setDisplayName(MessageTool.getPrettyName(currentIndexItem.getType().name()));
		currentIndexItem.setItemMeta(currentIndexMeta);
		event.getInventory().setItem(currentIndex, currentIndexItem);

		ItemStack item = event.getCurrentItem();
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(MessageTool.color("&a&lPrimary Banner"));
		item.setItemMeta(meta);
		event.getInventory().setItem(event.getSlot(), item);

		profile.getHouse().setPrimaryBanner(Banner.serializeBannerEntry(item));
		profile.getHouse().setPrimaryBannerIndex(event.getSlot());

	}
	
	private boolean checkFisrtRow(Inventory inventory)
	{
		for(int i = 0; i <= 8; i++)
		{
			if(inventory.getItem(i) != null && !inventory.getItem(i).getType().equals(Material.AIR))
			{
				return true;
			}
		}
		return false;
	}
}
