package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("house|h")
public class HouseUninviteCommand extends HouseCommand
{
	@Subcommand("uninvite")
	public void uninviteCommand(CommandSender sender, String playerName) 
	{
		UUID uuid;
		String name;
		Player player = (Player) sender;
		if (getOrigArgs().length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h uninvite &3<player>"));
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
				player.sendMessage(MessageTool.color("&cPlayer '%PLAYER%' has never joined the server!").replace("%PLAYER%", getOrigArgs()[0]));
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
			player.sendMessage(MessageTool.color("&cNo pending invite for '%PLAYER%'").replace("%PLAYER%", name));
			return;
		} 
		house.getInvitedPlayers().remove(uuid);
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has cancelled &d%UNINVITED_PLAYER%&e's invititation to the house").replace("%PLAYER%", player.getName()).replace("%UNINVITED_PLAYER%", name));
	}

}
