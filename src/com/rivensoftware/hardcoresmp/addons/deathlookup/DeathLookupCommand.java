package com.rivensoftware.hardcoresmp.addons.deathlookup;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Default;

@CommandAlias("deathlookup|dl")
public class DeathLookupCommand extends BaseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Default
	public void deathLookupCommand(CommandSender sender, String target) 
	{
		Player player = (Player) sender;
		
        Profile profile = Profile.getByPlayer(player);

        if (getOrigArgs().length == 0) 
        {
            player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.DEATH_LOOKUP.USAGE")));
            return;
        }

        Profile toLookupProfile;
        Player toLookup = Bukkit.getPlayer(target);
        if (toLookup != null) 
        {
            toLookupProfile = Profile.getByPlayer(toLookup);
        } 
        else 
        {
            toLookupProfile = Profile.getByName(target);
        }

        if (toLookupProfile == null) 
        {
            player.sendMessage(MessageTool.color(plugin.getLanguageConfig().getString("COMMANDS.ADDONS.DEATH_LOOKUP.NO_PLAYER_FOUND")).replace("%TARGET%", target));
            return;
        }

        profile.setDeathLookup(new DeathLookup(toLookupProfile));
        player.openInventory(profile.getDeathLookup().getDeathInventory(1));
	}
}
