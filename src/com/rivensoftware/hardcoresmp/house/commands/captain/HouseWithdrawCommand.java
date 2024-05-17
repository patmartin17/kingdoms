package com.rivensoftware.hardcoresmp.house.commands.captain;

import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.economy.InternalEconomy;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;

@CommandAlias("house|h")
public class HouseWithdrawCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	private InternalEconomy economy = plugin.getInternalEconomy();
	
	@Subcommand("withdraw")
	public void withdrawCommand(CommandSender sender, int amount) 
	{
		Player player = (Player) sender;
		String[] args = getOrigArgs();
		if (args.length == 0) 
		{
			player.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has withdrawn &9$%AMOUNT%&e from the house balance!"));
			return;
		} 
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
				player.sendMessage(MessageTool.color("&c'%STRING%' is not a valid number!").replace("%STRING%", args[0]));
				return;
			} 
			amount = (int)Math.floor(Double.valueOf(args[0]).doubleValue());
			if (amount > house.getBalance()) 
			{
				player.sendMessage(MessageTool.color("&cYour house does not have enough money to do this!"));
				return;
			} 
		} 
		if (amount <= 0) 
		{
			player.sendMessage(MessageTool.color("&cYou cannot withdraw 0g (or less)!"));
			return;
		} 
		this.economy.addBalance(((OfflinePlayer)player).getUniqueId(), amount);
		house.setBalance(house.getBalance() - amount);
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has withdrawn &6%AMOUNT%g&e from the house balance!").replace("%PLAYER%", player.getName()).replace("%AMOUNT%", amount + ""));
	}

}
