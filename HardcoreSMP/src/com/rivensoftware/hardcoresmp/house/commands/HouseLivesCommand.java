package com.rivensoftware.hardcoresmp.house.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.lives.LifeTransaction;
import com.rivensoftware.hardcoresmp.profile.lives.LifeType;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;

@CommandAlias("house|h")
public class HouseLivesCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("lives")
	@Syntax("<amount>")
	public void livesCommand(CommandSender sender, double amount) 
	{
		Player player = (Player) sender;
		Profile profile = Profile.getByPlayer(player);
		
		if(profile.getHouse() == null)
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LIVES.MUST_BE_MEMBER")));
			return;
		}
		
		if(profile.getHouse().isRegenning()) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LIVES.HOUSE_IS_REGENERATING")));
			return;
		}
		
		LifeTransaction transaction = new LifeTransaction(profile);
		if(transaction.removeLives(amount, LifeType.GENERAL))
		{
			if(profile.getHouse().isPowerless() && (profile.getHouse().getLives() + amount) > 0)
				profile.getHouse().setPowerless(false);
			profile.getHouse().setLives(profile.getHouse().getLives() + amount);
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LIVES.SUCCESSFULLY_DEPOSITED")).replace("%AMOUNT%", String.valueOf(amount)));
			return;
		}
		else
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.LIVES.NOT_ENOUGH_LIVES")));
			return;
		}
	}
}
