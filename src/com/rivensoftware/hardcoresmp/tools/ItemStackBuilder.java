package com.rivensoftware.hardcoresmp.tools;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecation")
public class ItemStackBuilder implements Listener 
{
	private final ItemStack is;

	public ItemStackBuilder(Material mat) 
	{
		this.is = new ItemStack(mat);
	}

	public ItemStackBuilder(ItemStack is) 
	{
		this.is = is;
	}

	public ItemStackBuilder amount(int amount) 
	{
		this.is.setAmount(amount);
		return this;
	}

	public ItemStackBuilder name(String name) 
	{
		ItemMeta meta = this.is.getItemMeta();
		meta.setDisplayName(name);
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder lore(String name) 
	{
		ItemMeta meta = this.is.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<>(); 
		lore.add(name);
		meta.setLore(lore);
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder lore(List<String> lore) 
	{
		List<String> toSet = new ArrayList<>();
		ItemMeta meta = this.is.getItemMeta();
		for (String string : lore)
			toSet.add(ChatColor.translateAlternateColorCodes('&', string)); 
		meta.setLore(toSet);
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder durability(int durability) 
	{
		this.is.setDurability((short)durability);
		return this;
	}

	public ItemStackBuilder data(int data) 
	{
		this.is.setData(new MaterialData(this.is.getType(), (byte)data));
		return this;
	}

	public ItemStackBuilder enchantment(Enchantment enchantment, int level) 
	{
		this.is.addUnsafeEnchantment(enchantment, level);
		return this;
	}

	public ItemStackBuilder enchantment(Enchantment enchantment) 
	{
		this.is.addUnsafeEnchantment(enchantment, 1);
		return this;
	}

	public ItemStackBuilder type(Material material) 
	{
		this.is.setType(material);
		return this;
	}

	public ItemStackBuilder clearLore() 
	{
		ItemMeta meta = this.is.getItemMeta();
		meta.setLore(new ArrayList<String>());
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder clearEnchantments() 
	{
		for (Enchantment e : this.is.getEnchantments().keySet())
			this.is.removeEnchantment(e); 
		return this;
	}
	
	public ItemStackBuilder bannerMeta(BannerMeta meta)
	{
		this.is.setItemMeta(meta);
		return this;
	}

	public ItemStackBuilder color(Color color) 
	{
		if (this.is.getType() == Material.LEATHER_BOOTS || this.is.getType() == Material.LEATHER_CHESTPLATE || this.is.getType() == Material.LEATHER_HELMET || 
				this.is.getType() == Material.LEATHER_LEGGINGS) {
			LeatherArmorMeta meta = (LeatherArmorMeta)this.is.getItemMeta();
			meta.setColor(color);
			this.is.setItemMeta((ItemMeta)meta);
			return this;
		} 
		throw new IllegalArgumentException("color() only applicable for leather armor!");
	}

	public ItemStackBuilder owner(String owner) 
	{
		SkullMeta meta = (SkullMeta)this.is.getItemMeta();
		meta.setOwner(owner);
		this.is.setItemMeta((ItemMeta)meta);
		return this;
	}

	public ItemStack build() 
	{
		return this.is;
	}
}
