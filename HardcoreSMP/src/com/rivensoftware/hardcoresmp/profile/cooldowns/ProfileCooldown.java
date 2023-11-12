package com.rivensoftware.hardcoresmp.profile.cooldowns;

import java.text.DecimalFormat;

import org.apache.commons.lang3.time.DurationFormatUtils;

public class ProfileCooldown 
{
	private static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

	private final ProfileCooldownType type;

	private final long duration;

	private final long createdAt;

	public ProfileCooldownType getType() 
	{
		return this.type;
	}

	public ProfileCooldown(ProfileCooldownType type, long duration) 
	{
		this.type = type;
		this.duration = duration * 1000L;
		this.createdAt = System.currentTimeMillis();
	}

	public boolean isFinished() 
	{
		return (this.createdAt + this.duration - System.currentTimeMillis() <= 0L);
	}

	public String getTimeLeft() 
	{
		if (this.createdAt + this.duration - System.currentTimeMillis() >= 60000L)
			return DurationFormatUtils.formatDuration(this.createdAt + this.duration - System.currentTimeMillis(), "mm:ss"); 
		return SECONDS_FORMATTER.format(((float)(this.createdAt + this.duration - System.currentTimeMillis()) / 1000.0F)) + "s";
	}
}
