package com.rivensoftware.hardcoresmp.event.capturepoint;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Filters;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.event.Event;
import com.rivensoftware.hardcoresmp.event.EventManager;
import com.rivensoftware.hardcoresmp.event.EventZone;
import com.rivensoftware.hardcoresmp.scoreboard.KingdomsBoardAdapter;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.time.DurationFormatUtils;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CapturePointEvent implements Event
{
	private static EventManager manager = EventManager.getInstance();

	@Getter @Setter private UUID uuid;

	@Getter @Setter private String name;

	@Getter @Setter private EventZone zone;

	@Getter @Setter private long time;

	@Getter @Setter private long capTime;

	@Getter @Setter private long grace;

	@Getter @Setter private boolean active;

	@Getter private Player cappingPlayer;
	
	private static HardcoreSMP plugin = HardcoreSMP.getInstance();

	public CapturePointEvent(UUID uuid, String name, EventZone zone) 
	{
		this.uuid = uuid;
		this.name = name;
		this.zone = zone;
		manager.getEvents().add(this);
	}

	public CapturePointEvent(String name, EventZone zone) 
	{
		this(UUID.randomUUID(), name, zone);
	}

	public void start(long capTime) 
	{
		this.capTime = capTime;
		this.active = true;
		Bukkit.broadcastMessage(MessageTool.color("&9[&7Capture&9] %CAPTURE_POINT%&7 can now be contested&9.").replace("%CAPTURE_POINT%", this.name).replace("%TIME%", getTimeLeft()));
	}

	public void stop(boolean force) 
	{
		if (force || this.cappingPlayer == null) 
		{
			Bukkit.broadcastMessage(MessageTool.color("&9[&7Capture&9] %CAPTURE_POINT%&7 can no longer be contested&9.").replace("%CAPTURE_POINT%", this.name).replace("%TIME%", getTimeLeft()));
		} 
		else 
		{
			Bukkit.broadcastMessage(MessageTool.color("&9[&7Capture&9] %CAPTURE_POINT%&7 has been captured by &f%PLAYER%&9.").replace("%CAPTURE_POINT%", this.name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", this.cappingPlayer.getName()));
		} 

		this.active = false;
		this.capTime = 0L;
		this.time = 0L;
		this.cappingPlayer = null;
	}

	public boolean isGrace() 
	{
		return (this.time + this.grace > System.currentTimeMillis());
	}

	public boolean isFinished() 
	{
		return (this.active && this.time + this.capTime - System.currentTimeMillis() <= 0L && this.cappingPlayer != null);
	}

	public void setCappingPlayer(Player player) 
	{
		if (player == null) 
		{
			Bukkit.broadcastMessage(MessageTool.color("&9[&7Capture&9] &7Control of &9%CAPTURE_POINT%&7 lost. &9(%TIME%)").replace("%CAPTURE_POINT%", this.name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", this.cappingPlayer.getName()));
			this.grace = 5000L;
			this.time = System.currentTimeMillis();
		} 
		else 
		{
			Bukkit.broadcastMessage(MessageTool.color("&9[&7Capture&9] &9%CAPTURE_POINT%&7 is being contested. &9(%TIME%)").replace("%CAPTURE_POINT%", this.name).replace("%TIME%", getTimeLeft()).replace("%PLAYER%", player.getName()));
		} 

		this.cappingPlayer = player;
		this.time = System.currentTimeMillis();
	}

	public long getDeciSecondsLeft() 
	{
		if (this.cappingPlayer == null)
			return this.capTime / 100L; 

		return (this.time + this.capTime - System.currentTimeMillis()) / 100L;
	}

	public String getTimeLeft() 
	{
		if (this.cappingPlayer == null)
			return DurationFormatUtils.formatDuration(this.capTime, "mm:ss"); 
		if((this.time + this.capTime - System.currentTimeMillis()) <= 0)
			return "00:00";
		return DurationFormatUtils.formatDuration(this.time + this.capTime - System.currentTimeMillis(), "mm:ss");
	}

	public List<String> getScoreboardText() 
	{
		List<String> toReturn = new ArrayList<>();
		for (String line : KingdomsBoardAdapter.getScoreboardConfiguration().getStringList("PLACE_HOLDER.CAPTURE_POINT")) 
		{
			line = line.replace("%CAPTURE_POINT%", this.name);
			line = line.replace("%TIME%", getTimeLeft());
			toReturn.add(line);
		} 
		return toReturn;
	}

	@SuppressWarnings("deprecation")
	public static void load() 
	{
		MongoCollection<Document> collection = plugin.getKingdomsDatabase().getCapturePoints();
		MongoCursor<Document> cursor = collection.find().iterator();
		while (cursor.hasNext()) 
		{
			Document document = (Document)cursor.next();
			UUID uuid = UUID.fromString(document.getString("uuid"));
			String name = document.getString("name");
			JsonArray zoneArray = (new JsonParser()).parse(document.getString("zone")).getAsJsonArray();
			Location firstLocation = LocationSerialization.deserializeLocation(zoneArray.get(0).getAsString());
			Location secondLocation = LocationSerialization.deserializeLocation(zoneArray.get(1).getAsString());
			new CapturePointEvent(uuid, name, new EventZone(firstLocation, secondLocation));
		} 
	}

	@SuppressWarnings("deprecation")
	public void save() 
	{
		MongoCollection<Document> collection = plugin.getKingdomsDatabase().getCapturePoints();
		Document document = new Document();
		document.put("uuid", this.uuid.toString());
		document.put("name", this.name);
		JsonArray zoneArray = new JsonArray();
		zoneArray.add((JsonElement)new JsonPrimitive(LocationSerialization.serializeLocation(this.zone.getFirstLocation())));
		zoneArray.add((JsonElement)new JsonPrimitive(LocationSerialization.serializeLocation(this.zone.getSecondLocation())));
		document.put("zone", zoneArray.toString());
		collection.replaceOne(Filters.eq("uuid", this.uuid.toString()), document, plugin.getOptions());
	}
}
