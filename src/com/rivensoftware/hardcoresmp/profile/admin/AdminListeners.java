package com.rivensoftware.hardcoresmp.profile.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.ItemStackBuilder;
import com.rivensoftware.hardcoresmp.tools.MessageTool;

public class AdminListeners implements Listener
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@EventHandler
	public void onAdminJoin(PlayerJoinEvent event)
	{
		Player player = event.getPlayer();
		Admin admin = Admin.getByPlayer(player);
		
		if(admin != null)
		{
			/*
			 * 			boolean adminMode = plugin.getAdminConfig().getBoolean(player.getUniqueId().toString() + ".admin_mode");
			boolean vanishMode = plugin.getAdminConfig().getBoolean(player.getUniqueId().toString() + ".vanish_mode");
		
			if(adminMode)
			{
				admin.loadSavedInventoryFromFile();
				admin.giveAdminInventory();
				admin.setInAdminMode(true);	
			}
			
			if(vanishMode)
			{
				admin.enterVanishMode();	
			}
			 */
		}
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent event)
	{
		Player player = event.getPlayer();
		Admin admin = Admin.getByPlayer(player);
		
		if(admin != null)
		{
			//plugin.getAdminConfig().getConfiguration().set(player.getUniqueId().toString() + ".admin_mode", admin.isInAdminMode());
			//plugin.getAdminConfig().getConfiguration().set(player.getUniqueId().toString() + ".vanish_mode", admin.isInVanish());
			
			//String base64 = BukkitSerialization.itemStackArrayToBase64(admin.getSavedInventory());
			
			//plugin.getAdminConfig().getConfiguration().set(admin.getUuid().toString() + ".saved_inventory", "base64 here");
			
			//plugin.getAdminConfig().save();
		}
		
		Admin.unloadAdminProfile(player);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onToolUse(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Admin admin = Admin.getByPlayer(player);

		if(admin == null)
			return;
		
		if(admin.isInAdminMode())
		{
			if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR))
			{
				if(!event.getHand().equals(EquipmentSlot.HAND))
					return;
				
				ItemStack item = player.getInventory().getItemInMainHand();
				if(item.getType().equals(Material.AIR) || item == null)
					return;
				
				if(item.getItemMeta().getDisplayName().contains("Random Teleport"))
				{
					event.setCancelled(true);
					Player random = Bukkit.getOnlinePlayers().stream().findAny().get();
					player.teleport(random);
				}
				else if(item.getItemMeta().getDisplayName().contains("Inspect Player"))
				{
					displayMenu(player, player);
				}
				else if(item.getItemMeta().getDisplayName().contains("Online Players"))
				{

				}
				else if(item.getItemMeta().getDisplayName().contains("Unvanished"))
				{
					if(!admin.isInVanish())
					{
						admin.enterVanishMode();	
					}
					
					giveAlternateItem(player, getVanishItem());
				}
				else if(item.getItemMeta().getDisplayName().contains("Vanished"))
				{
					if(admin.isInVanish())
					{
						admin.exitVanishMode();	
					}
					
					giveAlternateItem(player, getUnvanishItem());
				}
			}
		}
	}
	
	private void giveAlternateItem(Player player, ItemStack item)
	{
		new BukkitRunnable() 
		{
			@Override
			public void run() 
			{
				player.getInventory().setItemInMainHand(item);
				player.updateInventory();
			}
		}.runTaskLater(plugin, 1);
	}
	
	
	public Menu createInspectMenu(Player inspectedPlayer) 
	{
	    return ChestMenu.builder(6)
	            .title(MessageTool.color("&7Inspecting: &c" + inspectedPlayer.getName()))
	            .build();
	}
	
	public void displayMenu(Player player, Player inspectedPlayer) 
	{
	    Menu menu = createInspectMenu(inspectedPlayer);
	    
	    Slot slot = menu.getSlot(0);
	    
	    slot.setItemTemplate(p -> {
			ItemStack item = new ItemStack(Material.BOOK);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName("IP: " + p.getAddress().getHostString());
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    menu.open(player);
	}
	
	private ItemStack getVanishItem()
	{
		ItemStack vanishOn = new ItemStackBuilder(Material.LIME_DYE)
				.amount(1)
				.name(MessageTool.color("&aVanished"))
				.lore(MessageTool.color("&7(Right click to exit vanish mode)"))
				.build();
		return vanishOn;
	}
	
	private ItemStack getUnvanishItem()
	{
		ItemStack vanishOff = new ItemStackBuilder(Material.GRAY_DYE)
				.amount(1)
				.name(MessageTool.color("&7Unvanished"))
				.lore(MessageTool.color("&7(Right click to enter vanish mode)"))
				.build();
		return vanishOff;
	}
}
