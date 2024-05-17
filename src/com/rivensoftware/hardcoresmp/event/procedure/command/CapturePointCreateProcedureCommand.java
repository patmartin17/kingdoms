package com.rivensoftware.hardcoresmp.event.procedure.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.event.procedure.CapturePointCreateProcedure;
import com.rivensoftware.hardcoresmp.event.procedure.CapturePointCreateProcedureStage;
import com.rivensoftware.hardcoresmp.profile.Profile;
import org.apache.commons.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("capturepoint|cp")
public class CapturePointCreateProcedureCommand extends BaseCommand
{
	@Subcommand("create|createkoth|newkoth")
	@Syntax("<name>")
	public void capturePointStartCommand(CommandSender sender, String chatType) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByPlayer(player);
		String[] args = getOrigArgs();
		if (profile.getCapturePointCreateProcedure() != null) 
		{
			player.sendMessage(" ");
			player.sendMessage(ChatColor.RED + "You're already in the process of creating a capture point.");
			player.sendMessage(ChatColor.RED + "Type 'cancel' in chat to stop the procedure.");
			player.sendMessage(" ");
			return;
		} 
		
		if (args.length == 0) 
		{
			profile.setCapturePointCreateProcedure((new CapturePointCreateProcedure()).stage(CapturePointCreateProcedureStage.NAME_SELECTION));
			player.sendMessage(" ");
			player.sendMessage(ChatColor.YELLOW + "Please type a name for this capture point in chat.");
			player.sendMessage(" ");
		} 
		else 
		{
			if (EventManager.getInstance().getByName(StringUtils.join((Object[])args)) != null) {
				player.sendMessage(" ");
				player.sendMessage(ChatColor.RED + "An event with that name already exists.");
				player.sendMessage(" ");
				return;
			} 
			
			profile.setCapturePointCreateProcedure((new CapturePointCreateProcedure()).stage(CapturePointCreateProcedureStage.ZONE_SELECTION).name(StringUtils.join((Object[])args)));
			player.sendMessage(" ");
			player.sendMessage(ChatColor.YELLOW + "You have received the zone wand.");
			player.sendMessage(" ");
			player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
			player.getInventory().addItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
		} 
	}
}
