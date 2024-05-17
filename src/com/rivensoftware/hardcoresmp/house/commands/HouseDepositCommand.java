package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import co.aikar.commands.annotation.Syntax;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import net.milkbowl.vault.economy.Economy;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("house|h")
public class HouseDepositCommand extends HouseCommand 
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	private Economy economy = plugin.getEconomy();

	@Subcommand("deposit|d")
	@Syntax("<amount>")
	public void createHouseCommand(CommandSender sender, String input) 
	{
		Player player = (Player)sender;
		Profile profile = Profile.getByUUID(player.getUniqueId());
		String[] args = getOrigArgs();
		if (profile.getHouse() == null) 
		{
			player.sendMessage(MessageTool.color("&cYou must have a house to deposit money."));
			return;
		} 

		if (args.length == 0) 
		{
			player.sendMessage(MessageTool.color("&cToo few arguments. &eUse like this: &b/h deposit &3<amount/all>"));
			return;
		} 

		int amount = 0;

		if (input.equalsIgnoreCase("all") || input.equalsIgnoreCase("a")) 
		{
			amount = (int)Math.floor(this.economy.getBalance((OfflinePlayer)player));
		} 
		else 
		{
			if (!NumberUtils.isNumber(input)) 
			{
				player.sendMessage(MessageTool.color("&c'%STRING%' is not a valid number!").replace("%STRING%", args[0]));
				return;
			} 

			amount = (int)Math.floor(Double.valueOf(input).doubleValue());

			if (amount > this.economy.getBalance((OfflinePlayer)player)) 
			{
				player.sendMessage(MessageTool.color("&cYou do not have enough money to do this!"));
				return;
			} 
		} 

		if (amount <= 0) 
		{
			player.sendMessage(MessageTool.color("&cYou cannot deposit 0g (or less)!"));
			return;
		} 
		this.economy.withdrawPlayer((OfflinePlayer)player, amount);
		House house = profile.getHouse();
		house.setBalance(house.getBalance() + amount);
		house.sendMessage(MessageTool.color("&ePlayer &a%PLAYER%&e has deposited &6%AMOUNT%g&e into the house balance!").replace("%PLAYER%", player.getName()).replace("%AMOUNT%", "" + amount));
	}
}
