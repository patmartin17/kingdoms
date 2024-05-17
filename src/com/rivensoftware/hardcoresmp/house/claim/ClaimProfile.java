package com.rivensoftware.hardcoresmp.house.claim;

import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class ClaimProfile 
{
	@Getter @Setter private Player player;
	@Getter @Setter private Profile profile;
	@Getter @Setter private House house;
	@Getter @Setter private boolean resetClicked;
	@Getter @Setter private ClaimPillar[] pillars;

	public ClaimProfile(Player player, House house)
	{
		setPlayer(player);
		setHouse(house);
		setPillars(new ClaimPillar[2]);
		setProfile(Profile.getByUUID(player.getUniqueId()));
		getProfile().setClaimProfile(this);
	}

	public void removePillars() 
	{
		for (ClaimPillar claimPillar : this.pillars) 
		{
			if (claimPillar != null)
				claimPillar.remove(); 
		} 
		this.pillars = new ClaimPillar[2];
	}
}
