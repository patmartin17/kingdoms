package com.rivensoftware.hardcoresmp.house.claim;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.ItemStackBuilder;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Claim 
{
	@Getter @Setter private static Set<Claim> claims = new HashSet<>();
	@Getter @Setter private static Map<String, HashSet<Claim>> chunkClaimMap = new HashMap<String, HashSet<Claim>>();

	@Getter @Setter private House house;
	@Getter @Setter private String worldName;

	@Getter @Setter private int[] coordinates;
	@Getter @Setter private Location[] corners;

	@Getter @Setter private ArrayList<Location> border;

	public Claim(House house, int[] coordinates, String worldName)
	{
		setHouse(house);
		setCoordinates(new int[] {Math.min(coordinates[0], coordinates[1]), Math.min(coordinates[2], coordinates[3]), Math.max(coordinates[0], coordinates[1]), Math.max(coordinates[2], coordinates[3])});
		setWorldName(worldName);

		Location firstCorner = new Location(Bukkit.getWorld(worldName), getFirstX(), 0.0D, getFirstZ());
		Location secondCorner = new Location(Bukkit.getWorld(worldName), getFirstX(), 0.0D, getSecondZ());
		Location thirdCorner = new Location(Bukkit.getWorld(worldName), getSecondX(), 0.0D, getFirstZ());
		Location fourthCorner = new Location(Bukkit.getWorld(worldName), getSecondX(), 0.0D, getSecondZ());
		setCorners(new Location[] {firstCorner, secondCorner, thirdCorner, fourthCorner});

		index();
		setupBorder();

		house.getClaims().add(this);
		claims.add(this);
	}

	public static ItemStack getWand()
	{
		List<String> lore = new ArrayList<String>();
		lore.add("&aLeft click the ground&7 to set the &afirst&7 point.");
		lore.add("&aRight click the ground&7 to set the &asecond&7 point.");
		lore.add("&aSneak and left click the air&7 to claim once both points set.");
		lore.add("&aRight click the air twice&7 to clear your selection.");

		ItemStack claimWand = new ItemStackBuilder(Material.WOODEN_HOE)
				.amount(1)
				.name(MessageTool.color("&aClaiming Wand"))
				.lore(lore)
				.data(0)
				.build();



		return claimWand;
	}

	private void index() 
	{
		for (int x = getFirstX(); x < getSecondX() + 1; x++) 
		{
			for (int z = getFirstZ(); z < getSecondZ() + 1; z++) 
			{
				int toSetX = x >> 4;
			int toSetZ = z >> 4;
			if (!chunkClaimMap.containsKey(toSetX + ":" + toSetZ))
				chunkClaimMap.put(toSetX + ":" + toSetZ, new HashSet<>()); 
			((HashSet<Claim>)chunkClaimMap.get(toSetX + ":" + toSetZ)).add(this);
			} 
		} 
	}

	private void setupBorder() 
	{
		ArrayList<Location> toReturn = new ArrayList<>();
		World world = Bukkit.getWorld(this.worldName);
		int i;
		for (i = Math.min(this.corners[0].getBlockZ(), this.corners[1].getBlockZ()); i < Math.max(this.corners[0].getBlockZ(), this.corners[1].getBlockZ()); i++)
			toReturn.add(new Location(world, this.corners[0].getBlockX(), 0.0D, i)); 
		for (i = Math.min(this.corners[2].getBlockZ(), this.corners[3].getBlockZ()); i < Math.max(this.corners[2].getBlockZ(), this.corners[3].getBlockZ()); i++)
			toReturn.add(new Location(world, this.corners[3].getBlockX(), 0.0D, i)); 
		for (i = Math.min(this.corners[1].getBlockX(), this.corners[3].getBlockX()); i < Math.max(this.corners[1].getBlockX(), this.corners[3].getBlockX()) + 1; i++)
			toReturn.add(new Location(world, i, 0.0D, this.corners[1].getBlockZ())); 
		for (i = Math.min(this.corners[0].getBlockX(), this.corners[2].getBlockX()); i < Math.max(this.corners[0].getBlockX(), this.corners[2].getBlockX()); i++)
			toReturn.add(new Location(world, i, 0.0D, this.corners[0].getBlockZ())); 
		this.border = toReturn;
	}

	public static ArrayList<Material> getMapBlocks() 
	{
		ArrayList<Material> toReturn = new ArrayList<>();
		for (Material material : Material.values()) {
			if (material.isBlock() && material.isOccluding() && material.isSolid() && !material.name().contains("STAIRS") && !material.name().contains("SLAB") && !material.name().contains("MUSHROOM"))
				toReturn.add(material); 
		} 
		return toReturn;
	}

	public boolean overlaps(double x1, double z1, double x2, double z2) 
	{
		double[] dim = new double[2];
		dim[0] = x1;
		dim[1] = x2;
		Arrays.sort(dim);
		if (getFirstX() > dim[1] || getSecondX() < dim[0])
			return false; 
		dim[0] = z1;
		dim[1] = z2;
		Arrays.sort(dim);
		if (getFirstZ() > dim[1] || getSecondZ() < dim[0])
			return false; 
		return true;
	}
	
	public static boolean overlapsRoyal(double x1, double z1, double x2, double z2) 
	{
		double[] dim = new double[2];
		dim[0] = x1;
		dim[1] = x2;
		Arrays.sort(dim);
		if (1000 > dim[1] || -1000 < dim[0])
			return false; 
		dim[0] = z1;
		dim[1] = z2;
		Arrays.sort(dim);
		if (1000 > dim[1] || -1000 < dim[0])
			return false; 
		return true;
	}
	
	public static boolean intersectsRestrictedArea(Location location)
	{
		if(Math.abs(location.getBlockX()) <= 1000 && Math.abs(location.getBlockZ()) <= 1000)
		{
			return true;
		}
		return false;
	}

	public boolean isGreaterThan(Claim claim) 
	{
		//if (Ekko.getInstance().getMainConfig().getStringList("FACTION_GENERAL.BYPASS_INSIDE_CHECK").contains(this.faction.getName()))
		//return false; 
		//if (Ekko.getInstance().getMainConfig().getStringList("FACTION_GENERAL.BYPASS_INSIDE_CHECK").contains(claim.getFaction().getName()))
		//return true; 
		int distance1 = (int)claim.getCorners()[0].distance(claim.getCorners()[3]);
		int distance2 = (int)getCorners()[0].distance(getCorners()[3]);
		return (distance2 > distance1);
	}

	public void remove() 
	{
        claims.remove(this);

        for (String key : Claim.getChunkClaimMap().keySet())
        {
            HashSet<Claim> keySet = chunkClaimMap.get(key);
            if (keySet.contains(this)) 
            {
                keySet.remove(this);
            }
        }

        for (String key : new ArrayList<>(Claim.getChunkClaimMap().keySet()))
        {
            if (Claim.getChunkClaimMap().get(key).isEmpty()) 
            {
                chunkClaimMap.remove(key);
            }
        }

        for (Profile profile : Profile.getProfiles()) 
        {
            if (!(profile.getMapPillars().isEmpty())) 
            {
                for (ClaimPillar claimPillar : new HashSet<>(profile.getMapPillars())) 
                {
                    if (isInside(claimPillar.getLocation())) 
                    {
                        claimPillar.remove();
                        profile.getMapPillars().remove(claimPillar);
                    }
                }
                if (profile.getMapPillars().isEmpty()) 
                {
                    profile.setViewingMap(false);
                }
            }
        }

		Location cornerThree = new Location(Bukkit.getWorld(this.worldName), getFirstX(), 0.0D, getSecondZ());
		int width = (int)cornerThree.distance(this.corners[0]) + 1;
		int length = (int)cornerThree.distance(this.corners[3]) + 1;
		int value = (int)((width * length) * 1);
		getHouse().setBalance(house.getBalance() + value);
		getHouse().getClaims().remove(this);
	}

	public boolean isNearby(Location l, int buffer) 
	{
		if (Bukkit.getWorld(this.worldName) == (new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).getWorld())
		{
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, buffer)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(buffer, 0.0D, 0.0D)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, -buffer)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-buffer, 0.0D, 0.0D)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-buffer, 0.0D, buffer)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(buffer, 0.0D, -buffer)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-buffer, 0.0D, -buffer)))
				return true; 
			if (isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(buffer, 0.0D, buffer)))
				return true; 
		} 
		return false;
	}

	public boolean isInside(Location location)
	{
		return location.getWorld().getName().equalsIgnoreCase(getWorldName()) && location.getBlockX() >= getFirstX() && location.getBlockX() <= getSecondX() && location.getBlockZ() >= getFirstZ() && location.getBlockZ() <= getSecondZ();
	}

	public static Claim getProminentClaimAt(Location location)
	{
		List<Claim> possibleClaims = getClaimsAt(location);
		if (possibleClaims != null) 
		{
			Claim currentClaim = null;
			for (Claim claim : possibleClaims) 
			{
				if (claim.isInside(location)) 
				{
					if (currentClaim != null) 
					{
						if (currentClaim.isGreaterThan(claim))
							currentClaim = claim; 
						continue;
					} 
					currentClaim = claim;
				} 
			} 
			return currentClaim;
		} 
		return null;
	}

	public static ArrayList<Claim> getClaimsAt(Location location) 
	{
		if (chunkClaimMap.containsKey((location.getBlockX() >> 4) + ":" + (location.getBlockZ() >> 4)))
			return new ArrayList<>(chunkClaimMap.get((location.getBlockX() >> 4) + ":" + (location.getBlockZ() >> 4))); 
		return null;
	}
    
	public static Set<Claim> getNearbyClaimsAt(Location location, int radius) 
	{
		Set<Claim> toReturn = new HashSet<>();
		int[] pos = new int[]{location.getBlockX(), location.getBlockZ()};

		Location currentLocation = new Location(location.getWorld(), pos[0], 0, pos[1]);
		for (int x = pos[0] - radius; x < pos[0] + radius; x++) 
		{
			for (int z = pos[1] - radius; z < pos[1] + radius; z++) 
			{
				currentLocation.setX(x);
				currentLocation.setZ(z);
				Claim claim = getProminentClaimAt(currentLocation);
				
				if (claim != null && !toReturn.contains(claim)) 
				{
					toReturn.add(claim);
				}
			}
		}

		return toReturn;
	}

	public int getWidth() 
	{
		return (int)this.corners[2].distance(this.corners[0]) + 1;
	}

	public int getLength()
	{
		return (int)this.corners[2].distance(this.corners[1]) + 1;
	}

	public int getFirstX() 
	{
		return this.coordinates[0];
	}

	public int getSecondX() 
	{
		return this.coordinates[2];
	}

	public int getFirstZ() 
	{
		return this.coordinates[1];
	}

	public int getSecondZ() 
	{
		return this.coordinates[3];
	}

}
