package com.rivensoftware.hardcoresmp.tools;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class BannerTool 
{
	public static void dropIndexedItems(Inventory inventory, int i, Player player)
	{
		if(inventory.getItem(i).getAmount() > 0)
		{
			ItemStack dropItem = new ItemStack(inventory.getItem(i).getType());
			ItemMeta meta = inventory.getItem(i).getItemMeta();
			dropItem.setAmount(inventory.getItem(i).getAmount() - 1);
			dropItem.setItemMeta(meta);
			BannerTool.addOrDropIncorrectItems(player, dropItem);
		}	
	}
	
	public static void addOrDropIncorrectItems(Player player, ItemStack item)
	{
		if(player.getInventory().firstEmpty() == -1)
		{
			player.getWorld().dropItem(player.getLocation(), item);
		}
		else
		{
			player.getInventory().addItem(item);
		}
	}
	
	public static boolean isBannerEqual(String originalBanner, String newBanner)
	{
		if(originalBanner.equals(newBanner))
		{
			return true;
		}
		return false;
	}
	
	public static boolean areBannersEqual(List<String> originalBanners, List<String> newBanners)
	{
		if(originalBanners.size() == 0 && newBanners.size() == 0)
		{
			return true;
		}
		
		if(originalBanners.size() != newBanners.size())
		{
			return false;
		}
		
		for(int i = 0; i <= originalBanners.size() - 1; i++)
		{
			if(!originalBanners.get(i).equals(newBanners.get(i)))
				return false;
		}
		
		return true;
	}
	
	public static boolean checkUnderBanner(Location location, int size)
	{
		for(int y = 1; y <= size; y++)
		{
			Location testLocation = new Location(location.getWorld(), location.getBlockX(), location.getBlockY() - y, location.getBlockZ());
			if(!testLocation.getBlock().getType().equals(Material.AIR))
			{
				return false;
			}
		}
		return true;
	}
}
