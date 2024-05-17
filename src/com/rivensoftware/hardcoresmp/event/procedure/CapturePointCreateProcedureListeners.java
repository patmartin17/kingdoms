package com.rivensoftware.hardcoresmp.event.procedure;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.event.EventZone;
import com.rivensoftware.hardcoresmp.event.capturepoint.CapturePointEvent;
import com.rivensoftware.hardcoresmp.house.claim.ClaimPillar;
import com.rivensoftware.hardcoresmp.profile.Profile;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class CapturePointCreateProcedureListeners implements Listener 
{
	@EventHandler
	public void onInventoryClickEvent(InventoryClickEvent event) 
	{
		Player player = (Player)event.getWhoClicked();
		Profile profile = Profile.getByPlayer(player);
		CapturePointCreateProcedure procedure = profile.getCapturePointCreateProcedure();
		String inventoryTitle = event.getView().getTitle();
		
		if (procedure != null && procedure.stage() == CapturePointCreateProcedureStage.CONFIRMATION && inventoryTitle.contains("Confirm capture point?")) {
			event.setCancelled(true);
			ItemStack itemStack = event.getCurrentItem();
			
			if (itemStack != null && itemStack.getType() != Material.AIR) 
			{
				String displayName = itemStack.getItemMeta().getDisplayName();
				if (displayName != null) 
				{
					if (displayName.contains("Cancel")) 
					{
						profile.setCapturePointCreateProcedure(null);
						player.sendMessage(" ");
						player.sendMessage(ChatColor.RED + "Capture point create procedure cancelled.");
						player.sendMessage(" ");
						player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
						player.closeInventory();
						return;
					} 
					if (displayName.contains("Confirm")) 
					{
						profile.setCapturePointCreateProcedure(null);
						player.sendMessage(" ");
						player.sendMessage(ChatColor.YELLOW + "Capture point successfully created.");
						player.sendMessage(" ");
						player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
						player.closeInventory();
						new CapturePointEvent(procedure.name(), new EventZone(procedure.pillars()[0].getOriginalLocation(), procedure.pillars()[1].getOriginalLocation()));
						return;
					} 
				} 
			} 
		} 
	}

	@EventHandler
	public void onInventoryCloseEvent(InventoryCloseEvent event) 
	{
		Player player = (Player)event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		CapturePointCreateProcedure procedure = profile.getCapturePointCreateProcedure();

		String inventoryTitle = event.getView().getTitle();
		
		if (procedure != null && 
				procedure.stage() == CapturePointCreateProcedureStage.CONFIRMATION && inventoryTitle.contains("Confirm capture point?"))

			(new BukkitRunnable() 
			{
				public void run() 
				{
					player.openInventory(CapturePointCreateProcedure.getConfirmationInventory(procedure));
				}
			}).runTaskLaterAsynchronously(HardcoreSMP.getInstance(), 2L); 
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onAsyncPlayerChatEvent(AsyncPlayerChatEvent event) {
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		CapturePointCreateProcedure procedure = profile.getCapturePointCreateProcedure();
		if (procedure != null) {
			event.setCancelled(true);
			if (event.getMessage().equalsIgnoreCase("cancel")) {
				profile.setCapturePointCreateProcedure(null);
				player.sendMessage(" ");
				player.sendMessage(ChatColor.RED + "Capture point create procedure cancelled.");
				player.sendMessage(" ");
				player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
				return;
			} 
			if (procedure.stage() == CapturePointCreateProcedureStage.NAME_SELECTION) 
			{
				String name = event.getMessage().replace(" ", "");
				if (EventManager.getInstance().getByName(name) != null) 
				{
					player.sendMessage(" ");
					player.sendMessage(ChatColor.RED + "An event with that name already exists.");
					player.sendMessage(" ");
					return;
				} 
				player.sendMessage(" ");
				player.sendMessage(ChatColor.YELLOW + "Capture point name set to '" + procedure.name(name).name() + "'.");
				player.sendMessage(ChatColor.YELLOW + "You have received the zone wand.");
				player.sendMessage(" ");
				player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
				player.getInventory().addItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
				procedure.stage(CapturePointCreateProcedureStage.ZONE_SELECTION);
			} 
		} 
	}

	@EventHandler
	public void onPlayerDropItemEvent(PlayerDropItemEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		CapturePointCreateProcedure procedure = profile.getCapturePointCreateProcedure();
		if (event.getItemDrop().getItemStack().isSimilar(CapturePointCreateProcedure.getWand())) 
		{
			event.getItemDrop().remove();
			if (procedure != null) {
				profile.setCapturePointCreateProcedure(null);
				player.sendMessage(" ");
				player.sendMessage(ChatColor.RED + "Capture point create procedure cancelled.");
				player.sendMessage(" ");
				player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
			} 
		} 
	}

	@EventHandler
	public void onPlayerInteractEvent(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByPlayer(player);
		CapturePointCreateProcedure procedure = profile.getCapturePointCreateProcedure();
		Action action = event.getAction();
		if (procedure == null && event.getItem() != null && event.getItem().isSimilar(CapturePointCreateProcedure.getWand())) 
		{
			player.getInventory().removeItem(new ItemStack[] { event.getItem() });
			return;
		} 
		if (procedure != null && event.getItem() != null && event.getItem().isSimilar(CapturePointCreateProcedure.getWand())) 
		{
			event.setCancelled(true);
			if (procedure.stage() == CapturePointCreateProcedureStage.ZONE_SELECTION) 
			{
				if (action == Action.LEFT_CLICK_BLOCK) 
				{
					ClaimPillar claimPillar = procedure.pillars()[0];
					if (claimPillar != null)
						claimPillar.remove(); 
					player.sendMessage(" ");
					player.sendMessage(ChatColor.YELLOW + "First position set.");
					player.sendMessage(" ");
					procedure.pillars()[0] = (new ClaimPillar(player, event.getClickedBlock().getLocation())).show(Material.LAPIS_BLOCK, 0);
					return;
				} 
				if (action == Action.RIGHT_CLICK_BLOCK) 
				{
					ClaimPillar claimPillar = procedure.pillars()[1];
					if (claimPillar != null)
						claimPillar.remove(); 
					player.sendMessage(" ");
					player.sendMessage(ChatColor.YELLOW + "Second position set.");
					player.sendMessage(" ");
					(new BukkitRunnable() 
					{
						public void run() 
						{
							procedure.pillars()[1] = (new ClaimPillar(player, event.getClickedBlock().getLocation())).show(Material.LAPIS_BLOCK, 0);
						}
					}).runTaskLaterAsynchronously(HardcoreSMP.getInstance(), 2L);
					return;
				} 
				if (action == Action.RIGHT_CLICK_AIR) 
				{
					int clicks = procedure.clicks();
					if (procedure.pillars()[0] == null && procedure.pillars()[1] == null) 
					{
						player.sendMessage(" ");
						player.sendMessage(ChatColor.RED + "Capture point pillars not defined.");
						player.sendMessage(" ");
					} 
					if (clicks == 0) {
						procedure.clicks(1);
						player.sendMessage(" ");
						player.sendMessage(ChatColor.YELLOW + "Click again to reset Capture point pillars.");
						player.sendMessage(" ");
						return;
					} 
					for (ClaimPillar claimPillar : procedure.pillars()) 
					{
						if (claimPillar != null)
							claimPillar.remove(); 
					} 
					procedure.clicks(0);
					procedure.pillars()[0] = null;
					procedure.pillars()[1] = null;
					player.sendMessage(" ");
					player.sendMessage(ChatColor.YELLOW + "Capture point zone pillars have been reset.");
					player.sendMessage(" ");
					return;
				} 
				if (action == Action.LEFT_CLICK_AIR && player.isSneaking()) 
				{
					for (ClaimPillar claimPillar : procedure.pillars()) 
					{
						if (claimPillar == null) 
						{
							player.sendMessage(" ");
							player.sendMessage(ChatColor.RED + "Capture point zone not defined.");
							player.sendMessage(" ");
							return;
						} 
					} 
					for (ClaimPillar claimPillar : procedure.pillars())
						claimPillar.remove(); 
					player.getInventory().removeItem(new ItemStack[] { CapturePointCreateProcedure.getWand() });
					player.openInventory(CapturePointCreateProcedure.getConfirmationInventory(procedure));
					procedure.stage(CapturePointCreateProcedureStage.CONFIRMATION);
				} 
			} 
		} 
	}
}
