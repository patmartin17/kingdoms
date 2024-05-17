package com.rivensoftware.hardcoresmp.profile.protection.commands.subcommands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("pvp")
public class ProfileProtectionTimeCommand extends BaseCommand
{

	@Subcommand("time")
	public void timeCommand(CommandSender sender)
	{
		Profile profile;
		String[] args = getOrigArgs();
		if (args.length != 0 && !sender.hasPermission("pvp.time.others")) 
		{
			sender.sendMessage(MessageTool.color("&cUsage: /pvp time"));
			return;
		} 
		if (args.length == 0) 
		{
			if (sender instanceof Player) 
			{
				profile = Profile.getByPlayer((Player)sender);
			}
			else 
			{
				sender.sendMessage(ChatColor.RED + "You're console dumbass.");
				return;
			} 
		} else 
		{
			Player player = Bukkit.getPlayer(StringUtils.join((Object[])args));
			if (player != null) 
			{
				profile = Profile.getByPlayer(player);
			} 
			else 
			{
				profile = Profile.getByName(StringUtils.join((Object[])args));
			} 
		} 
		if (profile == null) 
		{
			sender.sendMessage(ChatColor.RED + "No player with name '" + StringUtils.join((Object[])args) + "' found.");
			return;
		} 
		if (profile.getProtection() == null) 
		{
			if (args.length == 0) 
			{
				sender.sendMessage(MessageTool.color("&cYou do not have PvP Protection."));
			} 
			else 
			{
				sender.sendMessage(MessageTool.color("&c%PLAYER% does not have PvP Protection.").replace("%PLAYER%", profile.getName()));
			} 
		} 
		else if (args.length == 0) 
		{
			sender.sendMessage(MessageTool.color("&cYou currently have &lPvP Protection&c for %TIME% minutes.").replace("%TIME%", profile.getProtection().getTimeLeft()));
		} 
		else 
		{
			sender.sendMessage(MessageTool.color("&c%PLAYER% currently has &lPvP Protection&c for %TIME% minutes.").replace("%PLAYER%", profile.getName()).replace("%TIME%", profile.getProtection().getTimeLeft()));
		} 
		
		if (Bukkit.getPlayer(profile.getUuid()) == null)
			Profile.getProfiles().remove(profile); 
	}
}
