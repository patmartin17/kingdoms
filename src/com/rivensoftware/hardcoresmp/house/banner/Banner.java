package com.rivensoftware.hardcoresmp.house.banner;

import com.jeff_media.customblockdata.CustomBlockData;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.menu.BannerWallMaterial;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.ItemStackBuilder;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@Getter
public class Banner 
{
	@Setter private BannerType type;
	@Setter private BannerEffect effect;
	@Setter private Location location;

	@Getter public static Map<Location, Banner> activeBanners = new HashMap<Location, Banner>();
	@Getter private static NamespacedKey bannerNamespaceKey = new NamespacedKey(HardcoreSMP.getInstance(), "secondary-banner");

	public Banner(Location location, BannerType type, BannerEffect effect)
	{
		setType(type);
		setEffect(effect);
		setLocation(location);

		activeBanners.put(location, this);
	}

	public static ItemStack getBannerItem(BannerType type, House house, String effect)
	{	
		List<String> lore = new ArrayList<String>();
		for(String string : Arrays.asList(type.getLore()))
		{
			lore.add(string);
		}

		String replaceEffect = lore.get(1).replace("%EFFECT%", MessageTool.color("&e" + effect + "&f"));
		lore.set(1, replaceEffect);

		ItemStack item = Banner.deserializeBannerEntry(house.getPrimaryBanner());

		String nameCapital = type.name().substring(0, 1).toUpperCase();
		String name = nameCapital + type.name().toLowerCase().substring(1);
		String effectCapital = effect.toString().substring(0, 1).toUpperCase();
		String effectName = effectCapital + effect.toString().substring(1);


		ItemStack banner = new ItemStackBuilder(item.getType())
				.bannerMeta((BannerMeta)item.getItemMeta())
				.amount(1)
				.name(MessageTool.color("&e&l" + house.getHouseName() + " &7Banner (" + name + ")" + " (" + effectName + ")"))
				.lore(lore)
				.build();
		return banner;
	}

	public static Banner getByLocation(Location location)
	{
		for(Location bannerLocation : activeBanners.keySet())
		{
			if(location.equals(bannerLocation))
			{
				return activeBanners.get(location);
			}
		}
		return null;
	}

	public static void replaceHouseBanners(Player player)
	{
		Profile profile = Profile.getByPlayer(player);
		
		if(profile.getHouse().getBannerList().isEmpty())
		{
			return;
		}
		
		Map<Location, String> bannerLocations = new HashMap<Location, String>();
		
		for(Banner banner : profile.getHouse().getBannerList())
		{
			String blockData[] = banner.getLocation().getBlock().getBlockData().getAsString().split("facing=");
			String blockFaceString = blockData[1].replace("]", "").replace("=", "");
			
			bannerLocations.put(banner.getLocation(), blockFaceString);
		}
		
		removeHouseBanners(player);
		
		new BukkitRunnable() 
		{
			
			@Override
			public void run() 
			{
				for(Location location : bannerLocations.keySet())
				{
					if(profile.getHouse().isCustomBanner())
						setCustomHouseBannerBlock(player, location.getBlock(), bannerLocations.get(location));	
					else
						setDefaultHouseBannerBlock(player, location.getBlock(), bannerLocations.get(location));
				}
				
			}
			
		}.runTaskLater(HardcoreSMP.getInstance(), 1L);
		
		
	}

	public static void removeHouseBanners(Player player)
	{
		Profile profile = Profile.getByPlayer(player);
		
		for(Banner banner : profile.getHouse().getBannerList())
		{
			if(banner.getLocation().getBlock().getType().name().contains("BANNER"))
			{
				String blockData[] = banner.getLocation().getBlock().getBlockData().getAsString().split("facing=");
				String blockFaceString = blockData[1].replace("]", "").replace("=", "");
				
				removeHouseBannerBlock(player, banner.getLocation().getBlock(), blockFaceString);	
			}
		}
	}

	public static void removeHouseBannerBlock(Player player, Block eventBlock, String blockFace)
	{
		Profile profile = Profile.getByPlayer(player);
		int size = 0;
		if(profile.getHouse().isCustomBanner())
			size = profile.getHouse().getCustomBannerList().size();
		else
			size = 6;
		
		for(int y = 0; y <= size; y++)
		{
			Location testLocation = new Location(player.getWorld(), 
					eventBlock.getLocation().getBlockX(), 
					eventBlock.getLocation().getBlockY() - y, 
					eventBlock.getLocation().getBlockZ());

			Block block = player.getWorld().getBlockAt(testLocation);

			CustomBlockData customBlockData = new CustomBlockData(block, HardcoreSMP.getInstance());
			customBlockData.setProtected(false);
			customBlockData.remove(bannerNamespaceKey);

			CustomBlockData behindBlockData = new CustomBlockData(getBehindBlock(BlockFace.valueOf(blockFace.toUpperCase()), block.getLocation()), HardcoreSMP.getInstance());
			behindBlockData.setProtected(false);
			behindBlockData.remove(bannerNamespaceKey);

			block.setType(Material.AIR);
		}
	}

	public static void setCustomHouseBannerBlock(Player player, Block eventBlock, String blockFace)
	{
		Profile profile = Profile.getByPlayer(player);
		House house = profile.getHouse();

		for(int y = 0; y <= house.getCustomBannerList().size() - 1; y++)	
		{
			ItemStack bannerItem = Banner.deserializeBannerEntry(profile.getHouse().getCustomBannerList().get(y));
			BannerMeta bannerItemMeta = (BannerMeta)bannerItem.getItemMeta();
			Material bannerName = BannerWallMaterial.valueOf(bannerItem.getType().name()).getMaterial();

			Location testLocation = new Location(player.getWorld(), 
					eventBlock.getLocation().getBlockX(), 
					eventBlock.getLocation().getBlockY() - y, 
					eventBlock.getLocation().getBlockZ());

			Block block = player.getWorld().getBlockAt(testLocation);
			block.setType(bannerName);

			CustomBlockData customBlockData = new CustomBlockData(block, HardcoreSMP.getInstance());
			customBlockData.set(bannerNamespaceKey, PersistentDataType.STRING, house.getHouseName());
			customBlockData.setProtected(true);

			CustomBlockData behindBlockData = new CustomBlockData(getBehindBlock(BlockFace.valueOf(blockFace.toUpperCase()), block.getLocation()), HardcoreSMP.getInstance());
			behindBlockData.set(bannerNamespaceKey, PersistentDataType.STRING, house.getHouseName());
			behindBlockData.setProtected(true);

			String customBannersData = "minecraft:" + bannerName.name().toLowerCase() + "[facing=" + blockFace + "]";

			org.bukkit.block.Banner customBannerBlock = (org.bukkit.block.Banner) block.getState();
			customBannerBlock.setBlockData(Bukkit.createBlockData(customBannersData));

			List<Pattern> patterns = bannerItemMeta.getPatterns();
			customBannerBlock.setPatterns(patterns);
			customBannerBlock.update();
		}	
	}
	
	public static void setDefaultHouseBannerBlock(Player player, Block eventBlock, String blockFaceString)
	{
		Profile profile = Profile.getByPlayer(player);
		House house = profile.getHouse();
		
		ItemStack bannerItem = null;
		if(profile.getHouse().getSecondaryBanner() != null)
		{
			bannerItem = Banner.deserializeBannerEntry(profile.getHouse().getSecondaryBanner());	
		}
		else
		{
			ItemStack primaryBanner = Banner.deserializeBannerEntry(house.getPrimaryBanner());
			bannerItem = new ItemStack(Material.getMaterial(primaryBanner.getType().name()));
		}
		BannerMeta bannerItemMeta = (BannerMeta)bannerItem.getItemMeta();
		Material bannerName = BannerWallMaterial.valueOf(bannerItem.getType().name()).getMaterial();
		
		for(int y = 1; y <= 6; y++)
		{
			Location testLocation = new Location(player.getWorld(), 
					eventBlock.getLocation().getBlockX(), 
					eventBlock.getLocation().getBlockY() - y, 
					eventBlock.getLocation().getBlockZ());

			Block block = player.getWorld().getBlockAt(testLocation);
			block.setType(bannerName);

			CustomBlockData customBlockData = new CustomBlockData(block, HardcoreSMP.getInstance());
			customBlockData.set(bannerNamespaceKey, PersistentDataType.STRING, house.getHouseName());
			customBlockData.setProtected(true);
			
			CustomBlockData behindBlockData = new CustomBlockData(getBehindBlock(BlockFace.valueOf(blockFaceString.toUpperCase()), block.getLocation()), HardcoreSMP.getInstance());
			behindBlockData.set(bannerNamespaceKey, PersistentDataType.STRING, house.getHouseName());
			behindBlockData.setProtected(true);

			if(y == 0)
			{	
				String primaryBlockData = "minecraft:" + bannerName.name().toLowerCase() + "[facing=" + blockFaceString + "]";

				org.bukkit.block.Banner primaryBanner = (org.bukkit.block.Banner) block.getState();
				primaryBanner.setBlockData(Bukkit.createBlockData(primaryBlockData));

				List<Pattern> patterns = ((BannerMeta)bannerItem.getItemMeta()).getPatterns();
				primaryBanner.setPatterns(patterns);
				primaryBanner.update();
			}
			else
			{
				String secondaryBlockData = "minecraft:" + bannerName.name().toLowerCase() + "[facing=" + blockFaceString + "]";

				org.bukkit.block.Banner secondaryBanner = (org.bukkit.block.Banner) block.getState();
				secondaryBanner.setBlockData(Bukkit.createBlockData(secondaryBlockData));

				List<Pattern> patterns = bannerItemMeta.getPatterns();
				secondaryBanner.setPatterns(patterns);
				secondaryBanner.update();	
			}
		}
	}

	public static String serializeBannerEntry(ItemStack item)
	{
		if(item.getType().name().contains("BANNER"))
		{
			String material = item.getType().name();

			ItemMeta meta = item.getItemMeta();

			List<Pattern> patterns = ((BannerMeta)meta).getPatterns();
			if(patterns.size() != 0)
			{
				String patternList = "@";
				for(Pattern pattern : patterns)
				{
					patternList = patternList + pattern.getColor().toString() + ":" + pattern.getPattern().name() + "%";
				}

				String serialize = material + patternList;

				if(serialize.charAt(serialize.length() - 1) == '%')
					return serialize.substring(0, serialize.length() - 1);	
			}

			return material;

		}
		return null;
	}

	public static ItemStack deserializeBannerEntry(String string)
	{
		if(string.contains("@"))
		{
			String[] splitMaterial = string.split("@");
			Material material = Material.getMaterial(splitMaterial[0]);
			String[] splitPatterns = splitMaterial[1].split("%");

			List<Pattern> patterns = new ArrayList<Pattern>();
			for(String pattern : splitPatterns)
			{
				String[] splitFinal = pattern.split(":");
				String color = splitFinal[0];
				String patternType = splitFinal[1];

				patterns.add(new Pattern(DyeColor.valueOf(color), PatternType.valueOf(patternType)));
			}

			ItemStack finalItem = new ItemStack(material);
			BannerMeta bannerMeta = (BannerMeta) finalItem.getItemMeta();
			bannerMeta.setPatterns(patterns);

			finalItem.setItemMeta(bannerMeta);
			return finalItem;	
		}
		return new ItemStack(Material.getMaterial(string));
	}

	public String serializeBanner()
	{
		String banner = location.getWorld().getName() + ":" + location.getBlockX() + ":" + location.getBlockY() + ":" + location.getBlockZ() + "@" + effect.getEffectName();
		return banner;
	}

	private static Block getBehindBlock(BlockFace blockFace, Location location)
	{	
		switch(blockFace)
		{
		case NORTH:
			location.add(0, 0, +1);
			return location.getBlock();
		case SOUTH:
			location.add(0, 0, -1);
			return location.getBlock();
		case EAST:
			location.add(-1, 0, 0);
			return location.getBlock();
		case WEST:
			location.add(+1, 0, 0);
			return location.getBlock();
		default:
			break;
		}
		return null;
	}
}
