package com.rivensoftware.hardcoresmp.house.banner.menu;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.slot.ClickOptions;
import org.ipvp.canvas.slot.Slot;
import org.ipvp.canvas.type.ChestMenu;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BannerMenu implements Listener
{
	private static HardcoreSMP plugin = HardcoreSMP.getInstance();
	private static Economy economy = plugin.getEconomy();
	
	public static Menu bannerMenu() 
	{
	    return ChestMenu.builder(1)
	            .title(MessageTool.color("&7Banner Settings"))
	            .build();
	}	
	
	public static void displayBannerMenu(Player player) 
	{
	    Menu menu = bannerMenu();
	    
	    Slot shop = menu.getSlot(1);
	    shop.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.GOLD_INGOT);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&6&lBanner Shop"));
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot setBanner = menu.getSlot(3);
	    setBanner.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.WHITE_BANNER);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&9&lSet Banner"));
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot activeBanners = menu.getSlot(5);
	    activeBanners.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.BEACON);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&a&lActive Banners"));
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    Slot loom = menu.getSlot(7);
	    loom.setItemTemplate(p -> 
	    {
	        ItemStack item = new ItemStack(Material.LOOM);
	        ItemMeta itemMeta = item.getItemMeta();
	        itemMeta.setDisplayName(MessageTool.color("&e&lBuy a Loom"));
	        
	        List<String> lore = new ArrayList<String>();
	        lore.add(MessageTool.color("&7Cost: &60.1g"));
	        itemMeta.setLore(lore);
	        
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    BannerMenuType.BANNER_SHOP_MENU.openMenu(shop);
	    BannerMenuType.BANNER_SET_MENU.openMenu(setBanner);
	    BannerMenuType.BANNER_ACTIVE_MENU.openMenu(activeBanners);
	    
		BannerMenu.handleBuy(menu.getSlot(7), new ItemStack(Material.LOOM), 0.1);
	    
	    menu.open(player);
	}
	
	public static void allowPlacement(Slot slot)
	{
		ClickOptions options = ClickOptions.builder()
				.allActions()
				.allClickTypes()
				.build();
		slot.setClickOptions(options);
	}
	
	public static void setReturnButton(Menu menu, int slot, BannerMenuType type)
	{
	    Slot returnButton = menu.getSlot(slot);
	    returnButton.setItemTemplate(p -> 
	    {
	    	String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmQ2OWUwNmU1ZGFkZmQ4NGU1ZjNkMWMyMTA2M2YyNTUzYjJmYTk0NWVlMWQ0ZDcxNTJmZGM1NDI1YmMxMmE5In19fQ==";
	    	
	        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
	        SkullMeta itemMeta = getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
	        itemMeta.setDisplayName(MessageTool.color("&c&lReturn"));
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    type.openMenu(returnButton);
	}
	
	public static void setPlaceHereButton(Menu menu, int slot, BannerMenuType type, String display)
	{
	    Slot returnButton = menu.getSlot(slot);
	    returnButton.setItemTemplate(p -> 
	    {
	    	String texture = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmEzYjhmNjgxZGFhZDhiZjQzNmNhZThkYTNmZTgxMzFmNjJhMTYyYWI4MWFmNjM5YzNlMDY0NGFhNmFiYWMyZiJ9fX0=";
	    	
	        ItemStack item = new ItemStack(Material.PLAYER_HEAD);
	        SkullMeta itemMeta = getSkullMetaTextureByB64(((SkullMeta) item.getItemMeta()), texture);
	        itemMeta.setDisplayName(display);
	        item.setItemMeta(itemMeta);
	        return item;
	    });
	    
	    type.openMenu(returnButton);
	}
	
	public static void handleBuy(Slot slot, ItemStack item, double price) 
	{
		DecimalFormat df = new DecimalFormat("0.00");
		df.setGroupingUsed(true);
		df.setGroupingSize(3);
		
		slot.setClickHandler((player, info) -> 
		{
			EconomyResponse response = economy.withdrawPlayer((OfflinePlayer)player, price);
			if(response.transactionSuccess())
			{
				if(player.getInventory().firstEmpty() == -1)
				{
					player.sendMessage(MessageTool.color("&cYour inventory is full!"));
				}
				else
				{
					player.getInventory().addItem(item);
					player.sendMessage(MessageTool.color("&aSuccessfully purchased &fx" + item.getAmount() + " " + MessageTool.getPrettyName(item.getType().name())));	
					player.sendMessage(MessageTool.color("&6Gold Balance&7: &6" + df.format(economy.getBalance(player)) + "g"));
				}
			}
			else
			{
				player.sendMessage(MessageTool.color("&cYou can not afford this item."));
			}
		});
	}
	
    public static SkullMeta getSkullMetaTextureByB64(SkullMeta meta, String texture) 
    {
        GameProfile profile = new GameProfile(UUID.randomUUID(), "");
        profile.getProperties().put("textures", new Property("textures", texture));
        try 
        {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        }
        catch (Exception e) 
        {
            e.printStackTrace();
        }
        return meta;
    }
}
