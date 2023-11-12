package com.rivensoftware.hardcoresmp.profile.protection.commands.subcommands;

import org.apache.commons.lang3.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("pvp")
public class ProfileProtectionTimeCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("time")
	public void timeCommand(CommandSender sender)
	{
		Profile profile;
		String[] args = getOrigArgs();
		if (args.length != 0 && !sender.hasPermission("kingdoms.admin")) 
		{
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.PROTECTION.TIME.USAGE")));
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
				sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.COMMON.MUST_BE_PLAYER")));
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
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.PROTECTION.ENABLE.NO_PLAYER_FOUND")).replace("%INPUT%", StringUtils.join((Object[])args)));
			return;
		} 
		if (profile.getProtection() == null) 
		{
			if (args.length == 0) 
			{
				sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.PROTECTION.ENABLE.NO_PROTECTION")));
			} 
			else 
			{
				sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.PROTECTION.ENABLE.PLAYER_NO_PROTECTION")).replace("%PLAYER%", profile.getName()));
			} 
		} 
		else if (args.length == 0) 
		{
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.PROTECTION.ENABLE.CURRENT_TIME")).replace("%TIME%", profile.getProtection().getTimeLeft()));
		} 
		else 
		{
			sender.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.PROTECTION.ENABLE.PLAYER_CURRENT_TIME")).replace("%PLAYER%", profile.getName()).replace("%TIME%", profile.getProtection().getTimeLeft()));
		} 
		
		if (Bukkit.getPlayer(profile.getUuid()) == null)
			Profile.getProfiles().remove(profile); 
	}
}
