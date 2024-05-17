package com.rivensoftware.hardcoresmp.house.banner;

import com.jeff_media.customblockdata.CustomBlockData;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.BannerTool;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;

public class BannerListeners implements Listener
{
	@EventHandler
	public void onHeadClick(InventoryClickEvent event)
	{
		//check of putting on and taking off.
		//if it is a head banner
		Player player = (Player) event.getWhoClicked();
		Profile profile = Profile.getByPlayer(player);
		if(profile.getHouse() != null)
		{
			//if they shift click this item it puts them into an active map of players with banners on their heads for looping through.
		}
	}

	@EventHandler
	public void onBannerPlace(BlockPlaceEvent event)
	{
		//you can place banners on spawn if you are the royal house but when you lose the house they all disappear
		//you cant break enemy banners even if they are raidable. This makes the incentive to put them in obvious places.
		if(!event.getBlockPlaced().getType().toString().contains("BANNER"))
		{
			return;
		}
		if(!event.getItemInHand().getItemMeta().getDisplayName().contains("(Wall)"))
		{
			return;
		}
		if(event.getItemInHand().getItemMeta().getLore() == null || event.getItemInHand().getItemMeta().getLore().isEmpty())
		{
			return;
		}

		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		House house = House.getByPlayer(player);

		BannerMeta bannerMeta = (BannerMeta) event.getItemInHand().getItemMeta();

		if(house.getPrimaryBanner() == null)
		{
			player.sendMessage(MessageTool.color("&cYour house does not have a banner set."));
			event.setCancelled(true);
			return;
		}

		ItemStack primaryItem = Banner.deserializeBannerEntry(house.getPrimaryBanner());
		BannerMeta primaryMeta = (BannerMeta) primaryItem.getItemMeta();

		if(!bannerMeta.getPatterns().equals(primaryMeta.getPatterns()))
		{
			player.sendMessage(MessageTool.color("&cYou can only place your teams banner."));
			event.setCancelled(true);
			return;
		}
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color("&cYou must belong to a house to place a wall banner."));
			event.setCancelled(true);
			return;
		}
		if(!profile.getHouse().getLord().equals(player.getUniqueId()) && !profile.getHouse().getCaptains().contains(player.getUniqueId()))
		{
			player.sendMessage(MessageTool.color("&cYou must be a lord or a captain to set the house banner."));
			event.setCancelled(true);
			return;
		}

		Claim claim = Claim.getProminentClaimAt(event.getBlockPlaced().getLocation());

		if(claim == null || !claim.getHouse().equals(profile.getHouse()))
		{
			player.sendMessage(MessageTool.color("&cYou must place the banner inside your own claim."));
			event.setCancelled(true);
			return;
		}


		if(house.isCustomBanner())
		{
			if(!BannerTool.checkUnderBanner(event.getBlockPlaced().getLocation(), house.getCustomBannerList().size()))
			{
				player.sendMessage(MessageTool.color("&cYou must place the banner &e" + house.getCustomBannerList().size() + " blocks above the ground with no interference below."));
				event.setCancelled(true);
				return;
			}

			String blockData[] = event.getBlock().getBlockData().getAsString().split("facing=");
			String blockFaceString = blockData[1].replace("]", "").replace("=", "");

			Banner.setCustomHouseBannerBlock(player, event.getBlockPlaced(), blockFaceString);
		}
		else
		{
			if(!BannerTool.checkUnderBanner(event.getBlockPlaced().getLocation(), 6))
			{
				player.sendMessage(MessageTool.color("&cYou must place the banner &e7 blocks above the ground with no interference below."));
				event.setCancelled(true);
				return;
			}

			String blockData[] = event.getBlock().getBlockData().getAsString().split("facing=");
			String blockFaceString = blockData[1].replace("]", "").replace("=", "");
			
			Banner.setDefaultHouseBannerBlock(player, event.getBlockPlaced(), blockFaceString);
		}

		String stripEffect = ChatColor.stripColor(event.getItemInHand().getItemMeta().getLore().get(1));
		String[] bannerInfo = stripEffect.split("apply ");
		String effect = bannerInfo[1].replace(" to all", "");

		Banner banner = new Banner(event.getBlockPlaced().getLocation(), BannerType.WALL, BannerEffect.getBannerEffect(effect));
		player.sendMessage(MessageTool.color("&aYou have placed a &e" + banner.getEffect().getEffectName() + " &abanner at&f: x: " + banner.getLocation().getBlockX() + ", y: " + banner.getLocation().getBlockY() + ", z: " + banner.getLocation().getBlockZ()));
		profile.getHouse().getBannerList().add(banner);

	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBannerBreak(BlockBreakEvent event)
	{
		if(!event.getBlock().getType().toString().contains("BANNER"))
		{
			return;
		}
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);

		Claim claim = Claim.getProminentClaimAt(event.getBlock().getLocation());
		if(claim == null)
		{
			return;
		}
		
		if(!claim.getHouse().equals(profile.getHouse()) && !player.hasPermission("kingdoms.admin"))
		{
			player.sendMessage(MessageTool.color("&cYou can't break other house banners"));
			event.setCancelled(true);
			return;
		}

		Banner banner = Banner.getByLocation(event.getBlock().getLocation());	
		if(banner == null)
		{
			return;
		}
		
		if(!profile.getHouse().getBannerList().contains(banner))
		{
			return;
		}

		String blockData[] = event.getBlock().getBlockData().getAsString().split("facing=");
		String blockFaceString = blockData[1].replace("]", "").replace("=", "");

		ItemStack bannerItem = Banner.getBannerItem(banner.getType(), profile.getHouse(), banner.getEffect().getEffectName());
		
		player.getWorld().dropItemNaturally(event.getBlock().getLocation(), bannerItem);
		profile.getHouse().getBannerList().remove(banner);
		Banner.activeBanners.remove(banner.getLocation());
		Banner.removeHouseBannerBlock(player, event.getBlock(), blockFaceString);
		
		player.sendMessage(MessageTool.color("&aYou have removed a team banner"));
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void breakConnectingBannerOrBlock(BlockBreakEvent event)
	{
		Claim claim = Claim.getProminentClaimAt(event.getBlock().getLocation());
		if(claim == null)
		{
			return;
		}
		
		Banner banner = Banner.getByLocation(event.getBlock().getLocation());
		if(CustomBlockData.isProtected(event.getBlock(), HardcoreSMP.getInstance()) && banner == null)
		{
			Player player = event.getPlayer();
			player.sendMessage(MessageTool.color("&cYou cannot break a block that is connected to a house banner."));
			event.setCancelled(true);
			return;
		}
		
	}
	
	@EventHandler
	public void blockMove(BlockPistonExtendEvent event)
	{
		for(Block block : event.getBlocks())
		{
			if(CustomBlockData.isProtected(block, HardcoreSMP.getInstance()))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
	
	@EventHandler
	public void blockRetract(BlockPistonRetractEvent event)
	{
		for(Block block : event.getBlocks())
		{
			if(CustomBlockData.isProtected(block, HardcoreSMP.getInstance()))
			{
				event.setCancelled(true);
				return;
			}
		}
	}
}
