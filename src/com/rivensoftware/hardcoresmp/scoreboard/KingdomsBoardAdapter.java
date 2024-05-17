package com.rivensoftware.hardcoresmp.scoreboard;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.cooldown.BoardCooldown;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.event.Event;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.event.capturepoint.CapturePointEvent;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.cooldowns.ProfileCooldown;
import com.rivensoftware.hardcoresmp.profile.cooldowns.ProfileCooldownType;
import com.thebigdolphin1.tagspawnprotection.combat.tag.TagManager;

import lombok.Getter;
import lombok.Setter;

public class KingdomsBoardAdapter implements BoardAdapter 
{
	public static final DecimalFormat SECONDS_FORMATTER = new DecimalFormat("#0.0");

	@Getter @Setter private static HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Getter public static File scoreboardFile = new File(plugin.getDataFolder(), "/scoreboard.yml");
	@Getter public static YamlConfiguration scoreboardConfiguration = YamlConfiguration.loadConfiguration(scoreboardFile);

	public String getTitle(Player player)
	{
		return getScoreboardConfiguration().getString("TITLE");
	}

	public List<String> getScoreboard(Player player, Board board, Set<BoardCooldown> set) 
	{
		List<String> toReturn = new ArrayList<>();
		Profile profile = Profile.getByPlayer(player);
		for (String line : getScoreboardConfiguration().getStringList("LINES")) 
		{
			if (line.contains("%CAPTURE_POINT%")) 
			{
				for (Event event : EventManager.getInstance().getEvents()) 
				{
					if (event instanceof CapturePointEvent && event.isActive())
						toReturn.addAll(event.getScoreboardText()); 
				} 
				continue;
			} 
			if (line.contains("%ENDER_PEARL%")) 
			{
				ProfileCooldown cooldown = profile.getCooldownByType(ProfileCooldownType.ENDER_PEARL);
				if (cooldown != null)
					toReturn.add(line.replace("%ENDER_PEARL%", cooldown.getTimeLeft())); 
				continue;
			} 
			if (line.contains("%PVP_PROTECTION%")) 
			{
				if (profile.getProtection() != null && profile.isLeftSpawn())
					toReturn.add(line.replace("%PVP_PROTECTION%", profile.getProtection().getTimeLeft())); 
				continue;
			} 
			if (line.contains("%COMBAT_TAG%")) 
			{
				double time = TagManager.getTagTime(profile.getUuid()) / 1000;
				
				if (TagManager.isInCombat(profile.getUuid()))
					toReturn.add(line.replace("%COMBAT_TAG%", time + "")); 
				continue;
			} 
			toReturn.add(line);
		} 
		if (toReturn.size() <= 2)
			return null; 
		return toReturn;
	}
}
