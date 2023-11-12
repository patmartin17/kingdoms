package com.rivensoftware.hardcoresmp.profile.cooldowns;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

public enum ProfileCooldownType 
{
	ENDER_PEARL, HORN_COOLDOWN;

	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	public int getDuration() 
	{
		return plugin.getMainConfig().getInt("COOLDOWN." + name());
	}

	public String getMessage() 
	{
		return MessageTool.color(plugin.getMainConfig().getString("COOLDOWN_MESSAGE." + name()));
	}
}
