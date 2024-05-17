package com.rivensoftware.hardcoresmp.addons.inventory;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@CommandAlias("giveinventory|giveinv|ginv")
public class GiveInventoryCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Default
	public void giveInventoryCommand(CommandSender sender, String target) 
	{
		Player player = (Player) sender;
		
        Player toSend;
        if (getOrigArgs().length == 0) 
        {
            player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.GIVE_INVENTORY.USAGE")));
            return;
        }
        else 
        {
        	toSend = Bukkit.getPlayer(target);
            if (toSend == null) 
            {
                player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.GIVE_INVENTORY.NO_PLAYER_FOUND")).replace("%TARGET%", target));
                return;
            }
        }

        toSend.getInventory().setContents(player.getInventory().getContents());
        toSend.getInventory().setArmorContents(player.getInventory().getArmorContents());
        player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.GIVE_INVENTORY.INVENTORY_GIVEN")));
	}
}
