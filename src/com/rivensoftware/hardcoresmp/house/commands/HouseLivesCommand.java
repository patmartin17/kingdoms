package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.lives.LifeTransaction;
import com.rivensoftware.hardcoresmp.profile.lives.LifeType;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("house|h")
public class HouseLivesCommand extends HouseCommand
{
	@Subcommand("lives")
	@Syntax("<amount>")
	public void livesCommand(CommandSender sender, double amount) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByPlayer(player);
		
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color("&cYou must be a member of a house to deposit lives."));
			return;
		}
		
		if(profile.getHouse().isRegenning()) 
		{
			player.sendMessage(MessageTool.color("&cYou cannot deposit lives while your house is regenerating."));
			return;
		}
		
		LifeTransaction transaction = new LifeTransaction(profile);
		if(transaction.removeLives(amount, LifeType.GENERAL))
		{
			if(profile.getHouse().isPowerless() && (profile.getHouse().getLives() + amount) > 0)
				profile.getHouse().setPowerless(false);
			profile.getHouse().setLives(profile.getHouse().getLives() + amount);
			player.sendMessage(MessageTool.color("&aYou have successfully deposited &f" + amount + " &alives into your house life balance."));
			return;
		}
		else
		{
			player.sendMessage(MessageTool.color("&cYou do not have enough general lives to do this."));
			return;
		}
	}
}
