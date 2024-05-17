package com.rivensoftware.hardcoresmp.addons.inventory;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.mongodb.client.FindIterable;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Sorts;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.fight.ProfileFight;
import com.rivensoftware.hardcoresmp.tools.InventorySerializer;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.util.UUID;

@CommandAlias("lastinventory|lastinv|linv")
public class LastInventoryCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Default
	public void giveInventoryCommand(CommandSender sender, String target) 
	{
		Player player = (Player) sender;

		Profile toRestore;
		if (getOrigArgs().length == 0) 
		{
            player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.LAST_INVENTORY.USAGE")));
			return;
		} else 
		{
			Player toRestorePlayer = Bukkit.getPlayer(getOrigArgs()[0]);
			if (toRestorePlayer != null) 
			{
				toRestore = Profile.getByPlayer(toRestorePlayer);
			} 
			else 
			{
				toRestore = Profile.getByName(getOrigArgs()[0]);
			}
		}

		if (toRestore == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.LAST_INVENTORY.NO_PLAYER_FOUND")).replace("%TARGET%", target));
			return;
		}

		UUID uuid = null;
		if (getOrigArgs().length > 1) 
		{
			try 
			{
				uuid = UUID.fromString(getOrigArgs()[1]);
			} 
			catch (Exception ex) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.LAST_INVENTORY.INVALID_QUERY")));
				return;
			}
		}

		ItemStack[] inventory = null;
		if (!toRestore.getFights().isEmpty()) 
		{
			for (ProfileFight oneFight : toRestore.getFights()) 
			{
				if (oneFight.getKiller() != null && oneFight.getKiller().getName() != null && oneFight.getKiller().getName().equals(toRestore.getName())) continue;
				if (uuid != null && !oneFight.getUuid().equals(uuid)) continue;
				inventory = oneFight.getInventoryContents();
			}
		} 
		else 
		{	
			Document fightDocument;
			FindIterable<Document> iterable = plugin.getKingdomsDatabase().getFights().find(Filters.eq("killed", toRestore.getUuid().toString())).sort(Sorts.descending("occurred_at"));
			if (uuid == null) 
			{
				fightDocument = (Document) iterable.first();
			} 
			else 
			{
				fightDocument = (Document) iterable.filter(Filters.eq("uuid", uuid.toString())).first();
			}

			if (fightDocument == null) 
			{
				player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.LAST_INVENTORY.INVENTORY_RECEIVED")).replace("%PLAYER%", toRestore.getName()));
				return;
			}

			try 
			{
				inventory = InventorySerializer.deserialize(fightDocument.getString("killed_inventory"));
			} 
			catch (IllegalStateException e)
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}

		if (inventory == null) 
		{
			player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.LAST_INVENTORY.ERROR_GETTING_INVENTORY")));
			return;
		}

		player.getInventory().setContents(inventory);
        player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.LAST_INVENTORY.INVENTORY_RECEIVED")));
	}
}
