package com.rivensoftware.hardcoresmp.addons.inventory;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

@CommandAlias("cloneinventory|cloneinv|copyinv|cinv")
public class CloneInventoryCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Default
	public void cloneInventoryCommand(CommandSender sender, String target) 
	{
		Player player = (Player) sender;

        Player toClone;
        if (getOrigArgs().length == 0) 
        {
            player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.CLONE_INVENTORY.USAGE")));
            return;
        } 
        else 
        {
            toClone = Bukkit.getPlayer(target);
            if (toClone == null) 
            {
                player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.CLONE_INVENTORY.NO_PLAYER_FOUND")).replace("%TARGET%", target));
                return;
            }
        }

        player.getInventory().setContents(toClone.getInventory().getContents());
        player.getInventory().setArmorContents(toClone.getInventory().getArmorContents());
        player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.CLONE_INVENTORY.INVENTORY_CLONED")));
	}
}
