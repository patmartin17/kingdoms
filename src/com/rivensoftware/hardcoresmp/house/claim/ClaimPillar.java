package com.rivensoftware.hardcoresmp.house.claim;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ClaimPillar 
{
	@Getter private static HardcoreSMP plugin = HardcoreSMP.getInstance();
	@Getter @Setter private Player player;
	@Getter @Setter private Location location;
	@Getter @Setter private Location originalLocation;

	public ClaimPillar(Player player, Location location)
	{
		setLocation(location);
		setOriginalLocation(location.clone());
		getLocation().setY(0.0D);
		setPlayer(player);
	}

	@SuppressWarnings("deprecation")
	public ClaimPillar show(Material material, int data) 
	{
		Location loc = new Location(this.originalLocation.getWorld(), this.originalLocation.getX(), 0.0D, this.originalLocation.getZ());
		int pos = 0;
		for (int i = 0; i < (loc.getWorld().getMaxHeight() - 150); i++) 
		{
			if (pos == 5) 
			{
				if (loc.getBlock().isEmpty())
					this.player.sendBlockChange(loc, material, (byte)data); 
				pos = 0;
			} 
			else if (loc.getBlock().isEmpty()) 
			{
				this.player.sendBlockChange(loc, Material.GLASS, (byte)0);
			} 
			pos++;
			loc.add(0.0D, 1.0D, 0.0D);
		} 
		return this;
	}

	@SuppressWarnings("deprecation")
	public ClaimPillar remove() 
	{
		Location loc = new Location(this.originalLocation.getWorld(), this.originalLocation.getX(), 0.0D, this.originalLocation.getZ());
		for (int i = 0; i < loc.getWorld().getMaxHeight(); i++) 
		{
			if (loc.getBlock().isEmpty())
				this.player.sendBlockChange(loc, Material.AIR, (byte)0); 
			loc.add(0.0D, 1.0D, 0.0D);
		} 
		return this;
	}
}
