package com.rivensoftware.hardcoresmp.profile.lives;

import com.rivensoftware.hardcoresmp.profile.Profile;

public enum LifeType
{
	GENERAL 
	{
		@Override
		public double getLives(Profile profile) 
		{
			return profile.getGeneralLives();
		}

		@Override
		public void setLives(double lives, Profile profile) 
		{
			profile.setGeneralLives(lives);
		}
	},
	SOULBOUND 
	{
		@Override
		public double getLives(Profile profile) 
		{
			return profile.getSoulboundLives();
		}

		@Override
		public void setLives(double lives, Profile profile) 
		{
			profile.setSoulboundLives(lives);
		}
	};
	
	public abstract double getLives(Profile profile);
	public abstract void setLives(double lives, Profile profile);
}
