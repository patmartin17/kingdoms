package com.rivensoftware.hardcoresmp.event.procedure;

import com.rivensoftware.hardcoresmp.house.claim.ClaimPillar;
import com.rivensoftware.hardcoresmp.tools.ItemStackBuilder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CapturePointCreateProcedure 
{
	@Setter private CapturePointCreateProcedureStage stage;

	@Getter @Setter private String name;

	public CapturePointCreateProcedureStage stage()
	{
		return this.stage;
	}

	public CapturePointCreateProcedure stage(CapturePointCreateProcedureStage stage) 
	{
		this.stage = stage;
		return this;
	}

	public String name() 
	{
		return this.name;
	}

	public CapturePointCreateProcedure name(String name) 
	{
		this.name = name;
		return this;
	}

	public ClaimPillar[] pillars() 
	{
		return this.pillars;
	}

	public CapturePointCreateProcedure pillars(ClaimPillar[] pillars) 
	{
		this.pillars = pillars;
		return this;
	}

	public int clicks() 
	{
		return this.clicks;
	}

	public CapturePointCreateProcedure clicks(int clicks) 
	{
		this.clicks = clicks;
		return this;
	}

	private ClaimPillar[] pillars = new ClaimPillar[2];

	private int clicks;

	public static ItemStack getWand() 
	{
		return (new ItemStackBuilder(Material.DIAMOND_HOE)).name(ChatColor.GREEN + "Zone Selection").lore(Arrays.asList(new String[] { "&aLeft click the ground&7 to set the &afirst&7 point.", "&aRight click the ground&7 to set the &asecond&7 point.", "&aSneak and left click the air&7 to confirm zone once both points set.", "&aRight click the air twice&7 to clear your selection." })).build();
	}

	public static Inventory getConfirmationInventory(CapturePointCreateProcedure procedure) 
	{
		Inventory toReturn = Bukkit.createInventory(null, 18, ChatColor.RED + "Confirm capture point?");
		toReturn.setItem(0, (new ItemStackBuilder(Material.GRAY_CARPET)).durability(7).name(ChatColor.RED + "Cancel").build());
		toReturn.setItem(8, (new ItemStackBuilder(Material.GRAY_CARPET)).durability(7).name(ChatColor.RED + "Cancel").build());
		toReturn.setItem(4, (new ItemStackBuilder(Material.PAPER)).name(ChatColor.RED + "Procedure Information").lore(Arrays.asList(new String[] { ChatColor.YELLOW + "Name: " + ChatColor.RED + procedure
		.name(), ChatColor.YELLOW + "Corner 1: &cWorld: " + procedure.pillars[0]
		.getOriginalLocation().getWorld().getName().replace("_", " ") + ", &cX: " + procedure.pillars[0].getOriginalLocation().getBlockX() + ", Y: " + procedure.pillars[0].getOriginalLocation().getBlockY() + ", Z: " + procedure.pillars[0].getOriginalLocation().getBlockZ(), ChatColor.YELLOW + "Corner 2: &cWorld: " + procedure.pillars[1]
		.getOriginalLocation().getWorld().getName().replace("_", " ") + ", &cX: " + procedure.pillars[1].getOriginalLocation().getBlockX() + ", Y: " + procedure.pillars[1].getOriginalLocation().getBlockY() + ", Z: " + procedure.pillars[1].getOriginalLocation().getBlockZ() })).build());
		
		for (int i = 0; i < 9; i++)
			toReturn.setItem(9 + i, (new ItemStackBuilder(Material.GREEN_WOOL)).durability(5).name(ChatColor.GREEN + "" + ChatColor.BOLD + "Confirm capture point").build()); 
		return toReturn;
	}
}
