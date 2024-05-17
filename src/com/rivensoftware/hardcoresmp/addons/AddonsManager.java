package com.rivensoftware.hardcoresmp.addons;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.addons.claimwall.ClaimWallListeners;

public class AddonsManager 
{
	public AddonsManager(HardcoreSMP plugin)
	{
		plugin.getPluginManager().registerEvents(new ClaimWallListeners(plugin), plugin);
	}
}
