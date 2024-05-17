package com.rivensoftware.hardcoresmp.commands;

import java.text.DecimalFormat;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.economy.InternalEconomy;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import lombok.Getter;

@CommandAlias("deposit")
public class DepositCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	private InternalEconomy economy = plugin.getInternalEconomy();

	@Getter public static final double GOLD_NUGGET_VALUE = 0.11;
	@Getter public static final double GOLD_INGOT_VALUE = 1.0;
	@Getter public static final double GOLD_BLOCK_VALUE = 9.0;
	private DecimalFormat format = new DecimalFormat("0.00");

	@Default
	public void depositCommand(CommandSender sender) 
	{
		Player player = (Player) sender;

		if(player.getInventory().getItemInMainHand().getType().equals(Material.GOLD_NUGGET) 
				|| player.getInventory().getItemInMainHand().getType().equals(Material.GOLD_INGOT)
				|| player.getInventory().getItemInMainHand().getType().equals(Material.GOLD_BLOCK))
		{
			double total = getHandGoldAmount(player.getInventory().getItemInMainHand());

			this.economy.addBalance(((OfflinePlayer)player).getUniqueId(), total);

			player.sendMessage(MessageTool.color("&7You have successfully deposited &6" + format.format(total) + "g &7to your balance."));

			player.updateInventory();	
		}
		else
		{
			player.sendMessage(MessageTool.color("&cYou must hold gold in your main hand to deposit to your balance."));
			return;
		}

	}

	@Subcommand("all")
	public void depositAll(CommandSender sender) 
	{
		Player player = (Player) sender;

		if(hasGold(player.getInventory()))
		{
			double total = getAllGoldAmount(player.getInventory());

			this.economy.addBalance(((OfflinePlayer)player).getUniqueId(), total);

			player.sendMessage(MessageTool.color("&7You have successfully deposited &6" + format.format(total) + "g &7to your balance."));

			player.updateInventory();
		}	
		else
		{
			player.sendMessage(MessageTool.color("&cYou have no gold in your inventory to deposit."));
			return;
		}
	}

	private boolean hasGold(PlayerInventory inventory)
	{
		for(ItemStack item : inventory.getContents())
		{
			if(item != null && !item.getType().equals(Material.AIR))
				if(item.getType().equals(Material.GOLD_NUGGET) || item.getType().equals(Material.GOLD_INGOT) || item.getType().equals(Material.GOLD_BLOCK))
				{
					return true;
				}
		}
		return false;
	}

	private double getAllGoldAmount(PlayerInventory inventory)
	{	
		double value = 0.0;
		for(ItemStack item : inventory.getContents())
		{
			double count = getHandGoldAmount(item);
			value = value + count;
		}
		return value;
	}

	private double getHandGoldAmount(ItemStack item)
	{
		double value = 0.0;

		if(item != null && !item.getType().equals(Material.AIR))
		{
			double count = 0.0;

			if(item.getType().equals(Material.GOLD_NUGGET))
			{
				count = item.getAmount() * GOLD_NUGGET_VALUE;
				item.setAmount(0);
			}
			if(item.getType().equals(Material.GOLD_INGOT))
			{
				count = item.getAmount() * GOLD_INGOT_VALUE;
				item.setAmount(0);
			}
			if(item.getType().equals(Material.GOLD_BLOCK))
			{
				count = item.getAmount() * GOLD_BLOCK_VALUE;
				item.setAmount(0);
			}

			value = value + count;
		}
		return value;
	}
}
