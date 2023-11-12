package com.rivensoftware.hardcoresmp.profile.lives;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import lombok.Getter;

public class LifeTransaction
{
	/*
	 * This needs to work with offline players by connecting to the profile database setting their lives correctly and saving
	 */
	//for saving transactions later if needed
	@Getter public static HashMap<UUID, LifeTransaction> transactions = new HashMap<UUID, LifeTransaction>();
	@Getter private LifeTransactionType type;
	@Getter private Profile profile;
	@Getter private Player target;
	@Getter private double lives;
	
	public LifeTransaction(Profile profile)
	{
		this.profile = profile;
	}
	
	public boolean removeLives(double lives, LifeType lifeType)
	{
		if(lifeType == null)
		{
			for(LifeType lifeTypes : LifeType.values())
			{
				if(lifeTypes.getLives(profile) >= lives)
				{
					lifeType = lifeTypes;
				}
			}
			if(lifeType == null)
			{
				return false;
			}
		}
		if(lifeType.getLives(profile) >= lives)
		{
			this.type = LifeTransactionType.REMOVE;
			this.lives = lives;
			double total = lifeType.getLives(profile) - 1;
	
			lifeType.setLives(total, profile);
			transactions.put(UUID.randomUUID(), this);
			return true;
		}
		return false;
	}
	
	public boolean addLives(double lives, LifeType lifeType)
	{
		if(lifeType.getLives(profile) >= lives)
		{
			this.type = LifeTransactionType.ADD;
			this.lives = lives;
			double total = lifeType.getLives(profile) + lives;
			lifeType.setLives(total, profile);
			transactions.put(UUID.randomUUID(), this);
			return true;
		}
		return false;
	}
	
	public boolean sendLives(double lives, Player target)
	{
		if(target.isOnline())
		{
			Profile targetProfile = Profile.getByPlayer(target);
			if(profile.getGeneralLives() >= lives)
			{
				this.lives = lives;
				this.target = target;
				this.type = LifeTransactionType.SEND;
				profile.setGeneralLives(profile.getGeneralLives() - lives);
				targetProfile.setGeneralLives(targetProfile.getGeneralLives() + lives);
				transactions.put(UUID.randomUUID(), this);
				return true;
			}
			return false;
		}
		else
		{
			profile.getPlayer().sendMessage(MessageTool.color("&cPlayer must be online to send lives."));
		}
		return false;
	}
}
