package com.rivensoftware.hardcoresmp.addons.claimwall;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import org.bukkit.Material;

public enum ClaimWallType 
{

	PVP_PROTECTION;

	private static HardcoreSMP plugin = HardcoreSMP.getInstance();

	public Material getBlockType() 
	{
		return Material.valueOf(plugin.getMainConfig().getString("CLAIM_WALL." + name() + ".BLOCK"));
	}

	public int getSize() 
	{
		return plugin.getMainConfig().getInt("CLAIM_WALL." + name() + ".SIZE");
	}

	public int getRange() 
	{
		return plugin.getMainConfig().getInt("CLAIM_WALL." + name() + ".RANGE");
	}

	public boolean isValid(Claim claim) 
	{
		if (claim == null) 
		{
			return false;
		}

		if(claim.getFirstX() <= 1000 || claim.getFirstZ() <= 1000 || claim.getSecondX() <= 1000 || claim.getSecondZ() <= 1000)
		{
			return false;
		}

		return true;
	}


}
