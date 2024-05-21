package com.rivensoftware.hardcoresmp.house.claim;

import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.admin.Admin;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.block.data.Openable;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.*;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.*;
import org.bukkit.material.Redstone;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimListeners implements Listener
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();

	@EventHandler
	public void onMove(PlayerMoveEvent e) 
	{
		if (e.getTo().getX() != e.getFrom().getX() || e.getTo().getZ() != e.getFrom().getZ()) 
		{
			Player p = e.getPlayer();
			Profile profile = Profile.getByUUID(p.getUniqueId());
			Claim claim = Claim.getProminentClaimAt(e.getTo());
			if (claim != null) 
			{
				if (claim.isInside(e.getTo())) 
				{
					if (profile.getLastInside() == null) 
					{
						profile.setLastInside(claim);
						p.sendMessage(MessageTool.color(getEnteringMessage(profile, claim)));
						return;
					}

					if (profile.getLastInside().getHouse() != claim.getHouse()) 
					{
						if (profile.getLastInside().isInside(e.getTo()) && !profile.getLastInside().isGreaterThan(claim)) 
						{
							return;
						}
						p.sendMessage(MessageTool.color(getEnteringMessage(profile, claim)));
					}

					if(claim.getHouse().getHouseName().contains(" KOTH"))
					{
						p.sendMessage(MessageTool.color("Koth code here"));
					}

					profile.setLastInside(claim);
				} 
				else 
				{
					if (profile.getLastInside() != null && profile.getLastInside() == claim) 
					{
						new BukkitRunnable() 
						{
							@Override
							public void run() 
							{
								if (profile.getLastInside() != null && profile.getLastInside() == claim) 
								{
									p.sendMessage(MessageTool.color("&eNow entering&7: Unclaimed Land"));
									profile.setLastInside(null);
								}
							}
						}.runTaskLater(plugin, 1L);
					}
				}
			} 
			else 
			{
				if (profile.getLastInside() != null) 
				{
					p.sendMessage(MessageTool.color("&eNow entering&7: Unclaimed Land"));
					profile.setLastInside(profile.getLastInside());
					profile.setLastInside(null);
				}
			}

		}
	}


	/*
	 * 	private String getLeavingMessage(Profile profile, Claim inside) 
	{
		if (profile.getHouse() != null && profile.getHouse() == inside.getHouse()) 
		{
			return "&eNow leaving: &2" + inside.getHouse().getHouseName();
		} 
		else if (profile.getHouse() != null && profile.getHouse().getAllies().contains(inside.getHouse())) 
		{
			return "&eNow leaving: &9" + inside.getHouse().getHouseName();
		} 
		else 
		{
			return "&eNow leaving: &c" + inside.getHouse().getHouseName();
		}
	}
	 */

	private String getEnteringMessage(Profile profile, Claim inside) 
	{
		if (profile.getHouse() != null && profile.getHouse() == inside.getHouse()) 
		{
			return "&eNow entering&7: &2" + inside.getHouse().getHouseName();
		} 
		else if (profile.getHouse() != null && profile.getHouse().getAllies().contains(inside.getHouse())) 
		{
			return "&eNow entering&7: &9" + inside.getHouse().getHouseName();
		} 
		else 
		{
			return "&eNow entering&7: &c" + inside.getHouse().getHouseName();
		}
	}

	@EventHandler
	public void onPlayerInteractClaim(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		if (event.getItem() != null && event.getItem().equals(Claim.getWand())) 
		{
			event.setCancelled(true);

			Profile profile = Profile.getByUUID(player.getUniqueId());
			ClaimProfile claimProfile = profile.getClaimProfile();

			if (claimProfile == null) 
			{
				if (profile.getHouse() != null) 
				{
					claimProfile = new ClaimProfile(player, profile.getHouse());
				} 
				else 
				{
					return;
				}
			}

			House house = claimProfile.getHouse();
			if (house.getLord() != player.getUniqueId() && !house.getCaptains().contains(player.getUniqueId()) && !player.hasPermission("admin")) 
			{
				player.getInventory().removeItem(Claim.getWand());
				claimProfile.removePillars();
				return;
			}

			if (event.getAction().name().contains("BLOCK")) 
			{
				Material material = Material.EMERALD_BLOCK;
				Location location = event.getClickedBlock().getLocation();
				claimProfile.setResetClicked(false);

				if(Claim.intersectsRestrictedArea(location))
				{
					if(!player.isOp())
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim royal house territory! (1000x1000 from spawn)"));
						return;	
					}
				}

				int toDisplay = 0;
				if (event.getAction() == Action.RIGHT_CLICK_BLOCK) 
				{
					toDisplay = 1;
				} 
				else 
				{
					if (!house.getClaims().isEmpty() && !house.isNearBorder(location)) 
					{
						player.sendMessage(MessageTool.color("&cYou must claim closer to your existing claims!"));
						return;
					}
				}

				ClaimPillar pillar = claimProfile.getPillars()[toDisplay];
				String[] message = {""};

				if(toDisplay == 1)
				{
					message[0] = MessageTool.color("&eYou have set the &9first&e point to &7(X: %X_POS%, Z: %Z_POS%)&e.").replace("%X_POS%", location.getBlockX() + "").replace("%Z_POS%", location.getBlockZ() + "");
				}
				else
				{
					message[0] = MessageTool.color("&eYou have set the &9second&e point to &7(X: %X_POS%, Z: %Z_POS%)&e.").replace("%X_POS%", location.getBlockX() + "").replace("%Z_POS%", location.getBlockZ() + "");
				}

				for (Claim claim : Claim.getClaims()) 
				{
					if (claim.isInside(location)) 
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim here!"));
						return;
					}
					if (claim.isNearby(location, 10) && claim.getHouse() != house) 
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim within 10 blocks of another house's territory!"));
						return;
					}
				}

				if (toDisplay == 1) 
				{
					final ClaimProfile finalClaimProfile = claimProfile;
					new BukkitRunnable() 
					{
						@Override
						public void run() 
						{
							ClaimPillar secondPillar = finalClaimProfile.getPillars()[0];
							ClaimPillar firstPillar = new ClaimPillar(player, location);

							if (secondPillar != null) {
								Location cornerOne = firstPillar.getLocation();
								Location cornerTwo = secondPillar.getLocation();
								Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());

								int width = (int) cornerThree.distance(cornerOne) + 1;
								int length = (int) cornerThree.distance(cornerTwo) + 1;

								if (width < 5 || length < 5) 
								{
									player.sendMessage(MessageTool.color("&cYour selection must be at least 5x5 blocks!"));
									return;
								}

								player.sendMessage(MessageTool.color(message[0]));
								player.sendMessage(MessageTool.color("&eClaim cost: &6" + calculateCosts(firstPillar, secondPillar) + "g&e, Current size: &7(" + width + ", " + length + ")&e, &9" + length * width + "&e blocks."));
							}
							else 
							{
								player.sendMessage(MessageTool.color(message[0]));
							}

							if (pillar != null) 
							{
								if (pillar.getLocation().equals(location)) 
								{
									return;
								} 
								else 
								{
									pillar.remove();
								}
							}

							finalClaimProfile.getPillars()[1] = firstPillar.show(material, 0);
						}
					}.runTaskLaterAsynchronously(plugin, 1L);
				} 
				else 
				{
					ClaimPillar secondPillar = claimProfile.getPillars()[1];
					ClaimPillar firstPillar = new ClaimPillar(player, location);

					if (secondPillar != null) 
					{
						Location cornerOne = firstPillar.getLocation();
						Location cornerTwo = secondPillar.getLocation();
						Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());
						int width = (int) cornerThree.distance(cornerOne) + 1;
						int length = (int) cornerThree.distance(cornerTwo) + 1;

						if (width < 5 || length < 5)
						{
							player.sendMessage(MessageTool.color("&cYour selection must be at least 5x5 blocks!"));
							return;
						}

						player.sendMessage(MessageTool.color(message[0]));
						player.sendMessage(MessageTool.color("&eClaim cost: &6" + calculateCosts(firstPillar, secondPillar) + "g&e, Current size: &7(" + width + ", " + length + ")&e, &9" + length * width + "&e blocks."));
					} 
					else 
					{
						player.sendMessage(MessageTool.color(message[0]));
					}

					if (pillar != null) 
					{
						if (pillar.getLocation().equals(location)) 
						{
							return;
						} 
						else 
						{
							pillar.remove();
						}
					}

					claimProfile.getPillars()[0] = firstPillar.show(material, 0);
				}
				return;
			}


			if (event.getAction() == Action.LEFT_CLICK_AIR && player.isSneaking())
			{

				if (claimProfile.getPillars()[0] == null || claimProfile.getPillars()[1] == null)
				{
					player.sendMessage(MessageTool.color("&cYou do not have a valid selection!"));
					return;
				}

				ClaimPillar firstPillar = claimProfile.getPillars()[0];
				ClaimPillar secondPillar = claimProfile.getPillars()[1];
				Location cornerOne = firstPillar.getLocation();
				Location cornerTwo = secondPillar.getLocation();
				Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());
				Location cornerFour = new Location(cornerOne.getWorld(), cornerTwo.getBlockX(), 0, cornerOne.getBlockZ());


				if(Claim.overlapsRoyal(firstPillar.getLocation().getBlockX(), firstPillar.getLocation().getBlockZ(), secondPillar.getLocation().getBlockX(), secondPillar.getLocation().getBlockZ()))
				{
					player.sendMessage(MessageTool.color("&cYou cannot overclaim the royal house!"));
					return;
				}

				for (Claim claim : Claim.getClaims()) 
				{
					if (claim.overlaps(firstPillar.getLocation().getBlockX(), firstPillar.getLocation().getBlockZ(), secondPillar.getLocation().getBlockX(), secondPillar.getLocation().getBlockZ()))
					{
						player.sendMessage(MessageTool.color("&cYou cannot overclaim existing claims!"));
						return;
					}
					if (claim.isNearby(cornerOne, 10) && claim.getHouse() != house)
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim within 10 blocks of another house's territory!"));
						return;
					}
					if (claim.isNearby(cornerTwo, 10) && claim.getHouse() != house) 
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim within 10 blocks of another house's territory!"));
						return;
					}
					if (claim.isNearby(cornerThree, 10) && claim.getHouse() != house) 
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim within 10 blocks of another house's territory!"));
						return;
					}
					if (claim.isNearby(cornerFour, 10) && claim.getHouse() != house) 
					{
						player.sendMessage(MessageTool.color("&cYou cannot claim within 10 blocks of another house's territory!"));
						return;
					}
				}

				int price = calculateCosts(firstPillar, secondPillar);

				if (house.getBalance() < price && !player.hasPermission("admin")) 
				{
					player.sendMessage(MessageTool.color("&cYour house does not have enough money to do this!"));
					return;
				}

				house.setBalance(house.getBalance() - price);
				house.sendMessage(MessageTool.color("&ePlayer &a" + player.getName() + "&e has claimed land for your house!"));


				new Claim(house, new int[]{firstPillar.getLocation().getBlockX(), secondPillar.getLocation().getBlockX(), firstPillar.getLocation().getBlockZ(), secondPillar.getLocation().getBlockZ()}, firstPillar.getLocation().getWorld().getName());
				claimProfile.removePillars();
				player.getInventory().remove(Claim.getWand());
			}

			if (event.getAction() == Action.RIGHT_CLICK_AIR) 
			{
				if (claimProfile.getPillars()[0] == null && claimProfile.getPillars()[1] == null) 
				{
					player.sendMessage(MessageTool.color("&cYou do not have a valid selection!"));
					return;
				}
				if (claimProfile.isResetClicked()) 
				{
					player.sendMessage(MessageTool.color("&7Your claim selection has been reset!"));
					claimProfile.setResetClicked(false);
					claimProfile.removePillars();
				}
				else 
				{
					claimProfile.setResetClicked(true);
					player.sendMessage(MessageTool.color("&7Right-click the air again to reset your selection!"));
				}
			}
		}
	}

	private int calculateCosts(ClaimPillar firstPillar, ClaimPillar secondPillar) 
	{
		Location cornerOne = firstPillar.getLocation();
		Location cornerTwo = secondPillar.getLocation();
		Location cornerThree = new Location(cornerOne.getWorld(), cornerOne.getBlockX(), 0, cornerTwo.getBlockZ());
		int width = (int) cornerThree.distance(cornerOne) + 1;
		int length = (int) cornerThree.distance(cornerTwo) + 1;
		return (int) ((width * length * 1.8) / 10);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) 
	{
		event.getPlayer().getInventory().removeItem(Claim.getWand());
	}

	@EventHandler
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) 
	{
		if (event.getDamager() instanceof Player && event.getEntity().getType() == EntityType.ITEM_FRAME) 
		{
			Player player = (Player) event.getDamager();
			Profile profile = Profile.getByUUID(player.getUniqueId());

			if (profile.isAdmin()) 
			{
				Admin admin = Admin.getByUUID(player.getUniqueId());
				if(admin.isInAdminMode())
				{
					return;	
				}
			}

			Claim claim = Claim.getProminentClaimAt(event.getEntity().getLocation());
			if (claim != null) {
				House house = claim.getHouse();

				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() == null || !house.equals(profile.getHouse())) 
				{
					if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
					{
						player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
					} 
					else 
					{
						player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
					}
					event.setCancelled(true);
				}
			}
		}
	}


	@EventHandler
	public void onHangingPlaceEvent(HangingPlaceEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		Claim claim = Claim.getProminentClaimAt(event.getEntity().getLocation());
		if (claim != null) {
			House house = claim.getHouse();

			if (house.isPowerless()) 
			{
				return;
			}

			if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
			{
				player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
			} 
			else 
			{
				player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
			}
		}
	}

	@EventHandler
	public void onBlockPistonExtendEvent(BlockPistonExtendEvent event) 
	{
		Block block = event.getBlock();
		if (Claim.getProminentClaimAt(block.getLocation()) == null) 
		{
			for (Block other : event.getBlocks()) 
			{
				if (Claim.getProminentClaimAt(other.getLocation()) != null) 
				{
					event.setCancelled(true);
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onBlockPistonRetractEvent(BlockPistonRetractEvent event) 
	{
		Block block = event.getBlock();
		if (Claim.getProminentClaimAt(block.getLocation()) == null) 
		{
			if (Claim.getProminentClaimAt(event.getRetractLocation()) != null) 
			{
				event.setCancelled(true);
			}
		}
	}
	@EventHandler
	public void onHangingBreakByEntityEvent(HangingBreakByEntityEvent event) 
	{
		if (event.getRemover() instanceof Player) 
		{
			Player player = (Player) event.getRemover();
			Profile profile = Profile.getByUUID(player.getUniqueId());

			if (profile.isAdmin()) 
			{
				Admin admin = Admin.getByUUID(player.getUniqueId());
				if(admin.isInAdminMode())
				{
					return;	
				}
			}

			Claim claim = Claim.getProminentClaimAt(event.getEntity().getLocation());
			if (claim != null) {
				House house = claim.getHouse();

				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
				} 
				else 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
				}
			}
		}
	}

	@EventHandler
	public void onDropItem(PlayerDropItemEvent event) 
	{
		if (event.getItemDrop().getItemStack().getItemMeta().hasDisplayName()) 
		{
			if (event.getItemDrop().getItemStack().getItemMeta().getDisplayName().equals(Claim.getWand().getItemMeta().getDisplayName())) {
				event.getItemDrop().remove();

				ClaimProfile profile = Profile.getByUUID(event.getPlayer().getUniqueId()).getClaimProfile();
				if (profile != null) {
					for (ClaimPillar pillar : profile.getPillars()) {
						if (pillar != null) {
							pillar.remove();
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onExplodeEvent(EntityExplodeEvent event) 
	{
		event.setCancelled(true);
	}

	@EventHandler
	public void onBreak(BlockBreakEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		Claim claim = Claim.getProminentClaimAt(event.getBlock().getLocation());
		if (claim != null) {
			House house = claim.getHouse();

			if (house.isPowerless()) 
			{
				return;
			}

			if (!house.equals(profile.getHouse())) 
			{
				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
				} 
				else 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		Claim claim = Claim.getProminentClaimAt(event.getBlock().getLocation());
		if (claim != null) 
		{
			House house = claim.getHouse();

			if (house.isPowerless()) 
			{
				return;
			}

			if (!house.equals(profile.getHouse())) 
			{
				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
				} 
				else 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		Claim claim = Claim.getProminentClaimAt(event.getBlockClicked().getLocation());
		if (claim != null) 
		{
			House house = claim.getHouse();

			if (house.isPowerless()) {
				return;
			}

			if (!house.equals(profile.getHouse())) 
			{
				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
				} 
				else 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		Claim claim = Claim.getProminentClaimAt(event.getBlockClicked().getLocation());
		if (claim != null) 
		{
			House house = claim.getHouse();

			if (house.isPowerless()) 
			{
				return;
			}

			if (!house.equals(profile.getHouse())) 
			{
				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
				} 
				else 
				{
					player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
				}
				event.setCancelled(true);
			}
		}
	}

	@EventHandler
	public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());
		if (event.getRightClicked() instanceof Hanging) 
		{
			Entity entity = event.getRightClicked();

			Claim claim = Claim.getProminentClaimAt(entity.getLocation());
			if (claim != null) 
			{
				House house = claim.getHouse();

				if (profile.isAdmin()) 
				{
					Admin admin = Admin.getByUUID(player.getUniqueId());
					if(admin.isInAdminMode())
					{
						return;	
					}
				}

				if (house.isPowerless()) 
				{
					return;
				}

				if (profile.getHouse() == null || !house.equals(profile.getHouse())) 
				{
					if (house.isPowerless()) 
					{
						return;
					}

					if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
					{
						player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
					} 
					else 
					{
						player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
					}
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) 
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		if (event.getAction() == Action.RIGHT_CLICK_BLOCK)
		{
			Block block = event.getClickedBlock();
			if (block.getState() instanceof Container || block.getState().getData() instanceof Redstone || block.getState().getData() instanceof Openable) {
				Claim claim = Claim.getProminentClaimAt(block.getLocation());
				if (claim != null) 
				{
					House house = claim.getHouse();

					if (house.isPowerless()) 
					{
						return;
					}

					if (!house.equals(profile.getHouse())) 
					{
						if (house.isPowerless()) 
						{
							return;
						}

						if (profile.getHouse() != null && profile.getHouse().getAllies().contains(house)) 
						{
							player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &9" + house.getHouseName() + "&e."));
						} 
						else 
						{
							player.sendMessage(MessageTool.color("&eYou cannot build in the territory of &c" + house.getHouseName() + "&e."));
						}
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPhsycialInteract(PlayerInteractEvent event)
	{
		Player player = event.getPlayer();
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if (profile.isAdmin()) 
		{
			Admin admin = Admin.getByUUID(player.getUniqueId());
			if(admin.isInAdminMode())
			{
				return;	
			}
		}

		if (event.getAction() == Action.PHYSICAL) 
		{
			Block block = event.getClickedBlock();
			Claim claim = Claim.getProminentClaimAt(block.getLocation());
			if (claim != null) 
			{
				House house = claim.getHouse();

				if (house.isPowerless()) 
				{
					return;
				}

				if (!house.equals(profile.getHouse())) 
				{
					if (block.getType() == Material.LEGACY_SOIL) 
					{
						event.setCancelled(true);
					}
				}
			}
		}
	}

	@EventHandler
	public void onInventoryInreact(InventoryMoveItemEvent event) {
		if (event.getItem() == Claim.getWand()) 
		{
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onInventoryInreact(InventoryClickEvent event) 
	{
		if (event.getInventory().contains(Claim.getWand())) 
		{
			event.getInventory().remove(Claim.getWand());
			ClaimProfile profile = Profile.getByUUID(event.getWhoClicked().getUniqueId()).getClaimProfile();
			if (profile != null) 
			{
				for (ClaimPillar pillar : profile.getPillars()) 
				{
					if (pillar != null) 
					{
						pillar.remove();
					}
				}
			}
		}
		if (event.getClickedInventory() != null) 
		{
			if (event.getClickedInventory().getHolder() instanceof Player) 
			{
				Player player = (Player) event.getClickedInventory().getHolder();
				ClaimProfile profile = Profile.getByUUID(player.getUniqueId()).getClaimProfile();
				if (profile != null) 
				{
					for (ClaimPillar pillar : profile.getPillars()) 
					{
						if (pillar != null) 
						{
							pillar.remove();
						}
					}
				}
			}
			event.getClickedInventory().remove(Claim.getWand());
		}
	}

	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent event) 
	{
		if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.NATURAL && event.getEntity() instanceof Monster) 
		{
			Claim claim = Claim.getProminentClaimAt(event.getLocation());
			if (claim != null) 
			{
				event.setCancelled(true);
			}
		}
	}
}
