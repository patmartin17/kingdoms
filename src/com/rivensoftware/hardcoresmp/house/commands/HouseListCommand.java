package com.rivensoftware.hardcoresmp.house.commands;

import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.Subcommand;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.command.CommandSender;

import java.util.*;

@CommandAlias("house|h")
public class HouseListCommand extends HouseCommand
{
	private HardcoreSMP plugin = HardcoreSMP.getInstance();
	
	@Subcommand("list")
	public void listCommand(CommandSender sender) 
	{
		String[] args = getOrigArgs();
		HashMap<House, Integer> houses = new HashMap<House, Integer>();
		int page = 1;
		for (House houseList : House.getHouses().values()) 
		{
			if (houseList.getOnlinePlayers().size() > 0)
				houses.put(houseList, Integer.valueOf(houseList.getOnlinePlayers().size())); 
		} 

		List<House> sortedList = new ArrayList<House>(houses.keySet());

		Collections.sort(sortedList, new Comparator<House>() 
		{
			@Override
			public int compare(House firstHouse, House secondHouse) 
			{
				return ((Integer)houses.get(firstHouse)).compareTo((Integer)houses.get(secondHouse));
			}
		});

		if (args.length == 2 && args[0].equalsIgnoreCase("list") && NumberUtils.isNumber(args[1]))
			page = (int)Double.parseDouble(args[1]); 

		if (sortedList.isEmpty())
		{
			sender.sendMessage(MessageTool.color("&cThere are currently no houses to list!"));
			return;
		} 

		int listSize = Math.round((sortedList.size() / 10));
		if (listSize == 0)
			listSize = 1; 

		if (page > listSize)
			page = listSize; 
		
		for (String msg : plugin.getHouseConfig().getStringList("HOUSE_LIST")) 
		{
			if (msg.contains("%HOUSE%")) 
			{
				for (int i = page * 10 - 10; i < page * 10; i++) 
				{
					if (sortedList.size() > i) 
					{
						House house = sortedList.get(i);
						sender.sendMessage(MessageTool.color(msg.replace("%HOUSE%", house.getHouseName()).replace("%ONLINE_COUNT%", house.getOnlinePlayers().size() + "").replace("%MAX_COUNT%", house.getAllPlayerUuids().size() + "").replace("%POSITION%", (i + 1) + "")));
					} 
				} 
				continue;
			} 
			sender.sendMessage(MessageTool.color(msg.replace("%PAGE%", page + "").replace("%TOTAL_PAGES%", listSize + "")));
		} 
	}
}