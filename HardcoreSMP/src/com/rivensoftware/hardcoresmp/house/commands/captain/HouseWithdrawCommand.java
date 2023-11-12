package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import net.milkbowl.vault.economy.Economy;

@CommandAlias("house|h")
public class HouseWithdrawCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	private Economy economy = plugin.getEconomy();
	
	@Subcommand("withdraw")
	public void withdrawCommand(CommandSender sender, int amount) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		Profile profile = Profile.getByUUID(player.getUniqueId());
		House house = profile.getHouse();
		if (args[0].equalsIgnoreCase("all") || args[0].equalsIgnoreCase("a")) 
		{
			amount = house.getBalance();
		} 
		else 
		{
			if (!NumberUtils.isNumber(args[0])) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.WITHDRAW.INVALID_NUMBER")).replace("%STRING%", args[0]));
				return;
			} 
			amount = (int)Math.floor(Double.valueOf(args[0]).doubleValue());
			if (amount > house.getBalance()) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.WITHDRAW.HOUSE_INSUFFICIENT_FUNDS")));
				return;
			} 
		} 
		if (amount <= 0) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.WITHDRAW.CANT_WITHDRAW_ZERO")));
			return;
		} 
		this.economy.depositPlayer((OfflinePlayer)player, amount);
		house.setBalance(house.getBalance() - amount);
		house.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.HOUSE.WITHDRAW.PLAYER_WITHDREW")).replace("%PLAYER%", player.getName()).replace("%AMOUNT%", amount + ""));
	}

}
