package com.rivensoftware.hardcoresmp.house.commands.captain;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("house|h")
public class HouseAnnouncementCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Subcommand("announcement")
	public void announcementCommand(CommandSender sender) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		if (args.length == 0)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ANNOUNCEMENT.USAGE")));
			return;
		} 
		
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		
		if(!house.getLord().equals(player.getUniqueId()))
		{
			
		}
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < (getOrigArgs()).length; i++)
			sb.append(getOrigArgs()[i]).append(" "); 
		String message = sb.toString().trim();
		
		house.setAnnouncement(message);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.ANNOUNCEMENT.PLAYER_CHANGED_ANNOUNCEMENT")).replace("%PLAYER%", player.getName()).replace("%MESSAGE%", message).replace("%HOUSE%", house.getHouseName()));
	}

}
