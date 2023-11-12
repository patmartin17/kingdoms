package com.rivensoftware.hardcoresmp.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Syntax;
import net.milkbowl.vault.economy.Economy;

@CommandAlias("withdraw")
public class WithdrawCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	private Economy economy = plugin.getEconomy();

	double goldNuggetValue = DepositCommand.GOLD_NUGGET_VALUE;
	double goldIngotValue = DepositCommand.GOLD_INGOT_VALUE;
	double goldBlockValue = DepositCommand.GOLD_BLOCK_VALUE;

	@Default
	@Syntax("<amount>")
	public void withdrawCommand(CommandSender sender, double amount) 
	{
		Player player = (Player) sender;

		if(this.economy.getBalance(player) >= amount)
		{
			int spacesNeeded = getWithdrawalItems(player, amount).size();
			int totalSlots = availableSlots(player.getInventory());

			if(totalSlots >= spacesNeeded)
			{
				this.economy.withdrawPlayer((OfflinePlayer)player, amount);

				for(ItemStack item : getWithdrawalItems(player, amount))
				{
					player.getInventory().addItem(item);
				}
				
				player.sendMessage(MessageTool.color("&aSuccessfully withdrew &f" + amount + " &agold into your inventory."));
				return;
			}
			else
			{
				player.sendMessage(MessageTool.color("&cYou need at least &f" + spacesNeeded + " &cempty slots to withdraw this much."));
				return;
			}	
		}
		else
		{
			player.sendMessage(MessageTool.color("&cYou do not have enough in your balance to withdraw this much."));
			return;
		}
	}

	private int availableSlots(PlayerInventory inventory)
	{
		int total = 0;
		for(ItemStack item : inventory.getStorageContents())
		{
			if(item == null || item.getType().equals(Material.AIR))
			{
				total++;
			}
		}
		return total;
	}

	private List<ItemStack> getWithdrawalItems(Player player, double amount)
	{
		List<ItemStack> items = new ArrayList<ItemStack>();

		double goldBlocks = Math.floor(amount / 9);
		if((goldBlocks / 64) > 1)
		{
			double goldBlockStacks = Math.floor(goldBlocks / 64);
			double goldBlockRemiander = Math.floor(goldBlocks % 64);

			for(int i = 0; i <= goldBlockStacks - 1; i++)
			{
				ItemStack goldBlockItems = new ItemStack(Material.GOLD_BLOCK);
				goldBlockItems.setAmount(64);

				items.add(goldBlockItems);
			}

			if(goldBlockRemiander >= 1)
			{
				ItemStack remainderGoldBlockItems = new ItemStack(Material.GOLD_BLOCK);
				remainderGoldBlockItems.setAmount((int)goldBlockRemiander);

				items.add(remainderGoldBlockItems);	
			}
		}
		else
		{
			ItemStack goldBlockItems = new ItemStack(Material.GOLD_BLOCK);
			goldBlockItems.setAmount((int)goldBlocks);

			items.add(goldBlockItems);
		}

		if((amount % 9) >= 1.0)
		{
			double goldIngots = Math.floor(amount % 9);
			double remainderAmount = (amount % 9) - Math.floor(amount % 9);

			if(goldIngots >= 1)
			{
				ItemStack goldIngotItems = new ItemStack(Material.GOLD_INGOT);
				goldIngotItems.setAmount((int)goldIngots);

				items.add(goldIngotItems);	
			}

			if(remainderAmount >= goldNuggetValue)
			{
				double goldNuggets = Math.floor(remainderAmount / goldNuggetValue);
				double returnRemainder = (remainderAmount % goldNuggetValue) - Math.floor(remainderAmount % goldNuggetValue);

				if(goldNuggets >= 1)
				{
					ItemStack goldNuggetItems = new ItemStack(Material.GOLD_NUGGET);
					goldNuggetItems.setAmount((int)goldNuggets);

					items.add(goldNuggetItems);	
				}

				this.economy.depositPlayer((OfflinePlayer)player, (returnRemainder * 0.5));
			}
		}
		else
		{
			double goldNuggets = Math.floor((amount % 9) / goldNuggetValue);
			double returnRemainder = (amount % 9) % goldNuggetValue;

			if(goldNuggets >= 1)
			{
				ItemStack goldNuggetItems = new ItemStack(Material.GOLD_NUGGET);
				goldNuggetItems.setAmount((int)goldNuggets);

				items.add(goldNuggetItems);
			}

			this.economy.depositPlayer((OfflinePlayer)player, (returnRemainder * 0.5));
		}

		return items;
	}
}
