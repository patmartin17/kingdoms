package com.rivensoftware.hardcoresmp.profile.protection.commands.subcommands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.protection.ProfileProtection;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("pvp")
public class ProfileProtectionGiveCommand extends BaseCommand
{
	@CommandPermission("kingdoms.admin")
	@Subcommand("give")
	@Syntax("<player> <time>")
	public void giveCommand(CommandSender sender, String playerName, int amount)
	{
		Player player = Bukkit.getPlayer(playerName);
		if(player != null)
		{
			Profile profile = Profile.getByPlayer(player);

			profile.setProtection(new ProfileProtection(amount));
			profile.setRespawning(false);
		}
	}
}
