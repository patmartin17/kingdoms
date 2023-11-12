package com.rivensoftware.hardcoresmp.profile.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.ItemStackBuilder;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

import lombok.Getter;
import lombok.Setter;

public class Admin extends Profile
{
	public static HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Getter public static Map<UUID, Admin> adminProfiles = new HashMap<UUID, Admin>();

	@Getter @Setter public boolean inAdminMode = false;
	@Getter @Setter public boolean inVanish = false;

	@Getter @Setter public ItemStack[] savedInventory;

	public Admin(UUID uuid) 
	{
		super(uuid);

		adminProfiles.put(uuid, this);
	}

	public Admin(Player player)
	{
		this(player.getUniqueId());
	}

	/*
	 * Finds existing specificed admin object by uuid.
	 */
	public static Admin getByUUID(UUID uuid)
	{
		if(adminProfiles.containsKey(uuid))
		{
			return adminProfiles.get(uuid);
		}
		return null;
	}

	/*
	 * Finds existing specificed admin object by player object.
	 */
	public static Admin getByPlayer(Player player)
	{

		if(adminProfiles.containsKey(player.getUniqueId()))
		{
			return adminProfiles.get(player.getUniqueId());
		}
		return null;
	}

	/*
	 * Returns the player object of an existing admin.
	 */
	public Player getPlayer(Admin admin)
	{
		if(admin != null)
		{
			return Bukkit.getPlayer(admin.getUuid());	
		}
		return null;
	}

	/*
	 * Removes admin object from adminProfiles map and sets object to null.
	 */
	public static void unloadAdminProfile(Player player)
	{
		Admin admin = Admin.getByUUID(player.getUniqueId());

		if (admin != null) 
		{
			Admin.getAdminProfiles().remove(admin.getUuid());
			admin = null;
			return;
		} 
		Bukkit.getConsoleSender().sendMessage(MessageTool.color("&c" + player.getName() + "'s admin profile was not correctly saved."));
	}

	/*
	 * Loads admin object using player object by creating new instance. 
	 */
	public static void loadAdminProfile(Player player)
	{
		new Admin(player.getUniqueId());
	}

	/*
	 * Places the admin in admin mode by saving current inventory to file and then setting boolean to true.
	 */
	public void enterAdminMode()
	{
		setSavedInventory(getPlayer().getInventory().getContents());

		setInAdminMode(true);
	}

	/*
	 * Removes admin from admin mode by simply setting boolean to false. (Should be changed)
	 */
	public void exitAdminMode()
	{
		setInAdminMode(false);
	}

	/*
	 * Clears admin inventory and replaces it with admin inventory.
	 */
	public void giveAdminInventory()
	{
		getPlayer().getInventory().clear();

		ItemStack compass = new ItemStackBuilder(Material.COMPASS)
				.amount(1)
				.name(MessageTool.color("&cRandom Teleport"))
				.lore(MessageTool.color("&7(Randomly teleport to an online player)"))
				.build();

		ItemStack inspect = new ItemStackBuilder(Material.BOOK)
				.amount(1)
				.name(MessageTool.color("&aInspect Player"))
				.lore(MessageTool.color("&7(Right click to inspect a players inventory and info)"))
				.build();

		ItemStack wand = new ItemStackBuilder(Material.WOODEN_AXE)
				.amount(1)
				.build();

		ItemStack carpet = new ItemStackBuilder(Material.WHITE_CARPET)
				.amount(1)
				.name(MessageTool.color(" "))
				.build();

		ItemStack players = new ItemStackBuilder(Material.PLAYER_HEAD)
				.amount(1)
				.name(MessageTool.color("&eOnline Players"))
				.lore(MessageTool.color("&7(Right click to view a list of online players)"))
				.build();

		ItemStack vanishOff = new ItemStackBuilder(Material.GRAY_DYE)
				.amount(1)
				.name(MessageTool.color("&7Unvanished"))
				.lore(MessageTool.color("&7(Right click to enter vanish mode)"))
				.build();

		getPlayer().getInventory().setItem(0, compass);
		getPlayer().getInventory().setItem(1, inspect);
		getPlayer().getInventory().setItem(2, wand);
		getPlayer().getInventory().setItem(3, carpet);
		getPlayer().getInventory().setItem(7, players);
		getPlayer().getInventory().setItem(8, vanishOff);

		getPlayer().updateInventory();
	}

	/*
	 * Uses the current savedInventory which should always be loaded if existing and gives it to the admin.
	 */
	public void giveSavedInventory()
	{
		getPlayer().getInventory().clear();
		
		int i = 0;
		for(ItemStack item : getSavedInventory())
		{
			if(item != null)
			{
				getPlayer().getInventory().setItem(i, item);
			}
			else
			{
				getPlayer().getInventory().setItem(i, new ItemStack(Material.AIR));
			}
			i++;
		}
		
		getPlayer().updateInventory();

	}
	
	/*
	 * Loads an ItemStack[] by using base64 to translate file information. Only used on join and on reload.
	 */
	public void loadSavedInventoryFromFile()
	{
		ItemStack[] inventory = null;
	
		setSavedInventory(inventory);
	}

	/*
	 * Hides the admin from other online players and sets them in vanish mode for future joining players.
	 */
	@SuppressWarnings("deprecation")
	public void enterVanishMode()
	{
		for(Player players : Bukkit.getOnlinePlayers())
		{
			if(!players.hasPermission("admin"))
			{
				players.hidePlayer(getPlayer());	
			}
		}
		getPlayer().sendMessage(MessageTool.color("&aYou are now invisible."));
		setInVanish(true);
	}

	/*
	 * Unhides the admin and removes them from "vanish mode."
	 */
	@SuppressWarnings("deprecation")
	public void exitVanishMode()
	{
		for(Player players : Bukkit.getOnlinePlayers())
		{
			if(!players.hasPermission("admin"))
			{
				players.showPlayer(getPlayer());	
			}
		}
		getPlayer().sendMessage(MessageTool.color("&7You are now visable."));
		setInVanish(false);
	}

}
