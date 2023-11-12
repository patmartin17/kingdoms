package com.rivensoftware.hardcoresmp.house.commands.captain;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseUninviteCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("uninvite")
	public void uninviteCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.UNINVITE.USAGE")));
			return;
		} 
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		Player toInvite = Bukkit.getPlayer(getOrigArgs()[0]);
		if (toInvite == null) 
		{
			SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByName(getOrigArgs()[0]);
			if (offlinePlayer != null) 
			{
				uuid = offlinePlayer.getUuid();
				name = offlinePlayer.getName();
			} 
			else 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_NEVER_JOINED")).replace("%PLAYER%", getOrigArgs()[0]));
				return;
			} 
		} 
		else 
		{
			uuid = toInvite.getUniqueId();
			name = toInvite.getName();
		} 
		
		if (!house.getInvitedPlayers().containsKey(uuid)) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.UNINVITE.NO_PENDING_INVITES")).replace("%PLAYER%", name));
			return;
		} 
		house.getInvitedPlayers().remove(uuid);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.COMMON.PLAYER_CANCELLED_INVITE")).replace("%PLAYER%", player.getName()).replace("%UNINVITED_PLAYER%", name));
	}

}
