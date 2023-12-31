package com.rivensoftware.hardcoresmp.profile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;
import org.bukkit.scoreboard.Team.Option;
import org.bukkit.scoreboard.Team.OptionStatus;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.addons.claimwall.ClaimWall;
import com.rivensoftware.hardcoresmp.addons.deathlookup.DeathLookup;
import com.rivensoftware.hardcoresmp.event.procedure.CapturePointCreateProcedure;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.house.claim.ClaimPillar;
import com.rivensoftware.hardcoresmp.house.claim.ClaimProfile;
import com.rivensoftware.hardcoresmp.profile.admin.Admin;
import com.rivensoftware.hardcoresmp.profile.chat.ProfileChatType;
import com.rivensoftware.hardcoresmp.profile.cooldowns.ProfileCooldown;
import com.rivensoftware.hardcoresmp.profile.cooldowns.ProfileCooldownType;
import com.rivensoftware.hardcoresmp.profile.deathban.ProfileDeathban;
import com.rivensoftware.hardcoresmp.profile.fight.ProfileFight;
import com.rivensoftware.hardcoresmp.profile.fight.ProfileFightEffect;
import com.rivensoftware.hardcoresmp.profile.fight.killer.ProfileFightKiller;
import com.rivensoftware.hardcoresmp.profile.fight.killer.type.ProfileFightEnvironmentKiller;
import com.rivensoftware.hardcoresmp.profile.fight.killer.type.ProfileFightPlayerKiller;
import com.rivensoftware.hardcoresmp.profile.protection.ProfileProtection;
import com.rivensoftware.hardcoresmp.profile.teleport.ProfileTeleportTask;
import com.rivensoftware.hardcoresmp.tools.InventorySerializer;
import com.rivensoftware.hardcoresmp.tools.LocationSerialization;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.PlayerTool;

import lombok.Getter;
import lombok.Setter;

@SuppressWarnings("deprecation")
@Getter 
public class Profile 
{
	private static HardcoreSMP plugin = HardcoreSMP.getInstance();

	@Setter private boolean loggedIn;
	@Setter private UUID uuid;
	@Setter private String name;
	@Setter private Player player;
	@Setter private boolean loaded;
	@Setter private boolean isAdmin;
	@Setter private boolean inSpawn;
	@Setter private boolean isWanted;
	@Setter private boolean isCriminal;

	@Getter private Map<Location, ClaimWall> walls;
	@Setter private Set<ClaimPillar> mapPillars;
	@Setter private ClaimProfile claimProfile;
	@Setter private Claim lastInside;
	@Setter private boolean viewingMap;

	@Setter private List<ProfileCooldown> cooldowns;
	@Setter private Location pearlLocation;
	@Setter private boolean respawning;

	@Setter private List<ProfileFight> fights;
	@Setter private LinkedHashMap<UUID, Boolean> previousFights;
	@Getter @Setter private DeathLookup deathLookup;

	@Setter private double generalLives = 1;
	@Setter private double soulboundLives = 2;
	@Setter private boolean leftSpawn;
	@Setter private boolean combatLogged;
	@Setter private boolean safeLogout;
	@Setter private ProfileDeathban deathban;
	@Setter private ProfileProtection protection;
	@Setter private ProfileTeleportTask teleportWarmup;
	@Setter private CapturePointCreateProcedure capturePointCreateProcedure;

	@Setter private House house;
	@Setter private ProfileChatType chatType;

	@Getter private static MongoCollection<Document> collection = plugin.getKingdomsDatabase().getProfiles();
	@Getter private static Map<UUID, Profile> activeProfiles = new HashMap<UUID, Profile>();

	public Profile(UUID uuid)
	{
		this.uuid = uuid;
		this.player = Bukkit.getPlayer(uuid);
		this.chatType = ProfileChatType.PUBLIC;
		this.cooldowns = new ArrayList<ProfileCooldown>();
		this.mapPillars = new HashSet<ClaimPillar>();
		this.walls = new HashMap<Location, ClaimWall>();
		this.fights = new ArrayList<ProfileFight>();
		this.previousFights = new LinkedHashMap<UUID, Boolean>();
		this.safeLogout = true;
		this.deathban = null;

		for (House house : House.getHouses().values()) 
		{
			if (house.getAllPlayerUuids().contains(uuid))
				this.house = house; 
		} 

		activeProfiles.put(uuid, this);
		load();
		this.loaded = true;
	}

	public Profile(Player player)
	{
		this(player.getUniqueId());
	}

	public static Profile getExistingByUUID(UUID uuid)
	{
		if(activeProfiles.containsKey(uuid))
		{
			return activeProfiles.get(uuid);
		}
		return null;
	}

	public static Profile getByUUID(UUID uuid)
	{
		if(activeProfiles.containsKey(uuid))
		{
			return activeProfiles.get(uuid);
		}
		return new Profile(uuid);
	}

	public static Profile getByPlayer(Player player)
	{

		if(activeProfiles.containsKey(player.getUniqueId()))
		{
			return activeProfiles.get(player.getUniqueId());
		}
		return null;
	}

	public static Profile getByName(String name) 
	{
		for (Profile profile : activeProfiles.values()) 
		{
			if (profile.getName() != null && profile.getName().equals(name))
				return profile; 
		} 

		Document document = (Document)collection.find(Filters.eq("recentNameLowercase", name.toLowerCase())).first();

		if (document != null)
			return new Profile(UUID.fromString(document.getString("uuid"))); 

		return null;
	}

	public static Set<Profile> getProfiles()
	{
		return new HashSet<Profile>(activeProfiles.values());
	}

	public ProfileFight getLatestFight() 
	{
		if (!this.fights.isEmpty())
			return this.fights.get(this.fights.size() - 1); 
		return null;
	}

	public ProfileCooldown getCooldownByType(ProfileCooldownType type) 
	{
		Iterator<ProfileCooldown> iterator = getCooldowns().iterator();
		while (iterator.hasNext()) 
		{
			ProfileCooldown cooldown = iterator.next();
			if (cooldown.getType() == type) 
			{
				if (cooldown.isFinished()) 
				{
					iterator.remove();
					continue;
				} 
				return cooldown;
			} 
		} 
		return null;
	}

	public ProfileDeathban getDeathban() 
	{
		if (this.deathban != null && this.deathban.getCreatedAt() + this.deathban.getDuration() <= System.currentTimeMillis())
			this.deathban = null; 
		return this.deathban;
	}


	public static void loadProfile(Player player)
	{
		Profile profile = Profile.getByUUID(player.getUniqueId());

		if(profile == null)
		{
			Bukkit.getConsoleSender().sendMessage(MessageTool.color(player.getName() + "&c's profile was not loaded correctly"));
		}
	}

	public static void unloadProfile(Player player)
	{
		Profile profile = Profile.getExistingByUUID(player.getUniqueId());

		if (profile != null) 
		{
			Profile.getProfiles().remove(profile);
			return;
		} 
		Bukkit.getConsoleSender().sendMessage(MessageTool.color("&c" + player.getName() + "'s profile was not correctly saved."));
	}

	public void updateTeams() 
	{
		new BukkitRunnable() 
		{

			@Override
			public void run() 
			{
				Player player = Bukkit.getPlayer(uuid);

				if (player != null) 
				{
					Scoreboard scoreboard;
					boolean newScoreboard = false;

					if (!player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) 
					{
						scoreboard = player.getScoreboard();
					} 
					else 
					{
						scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
						newScoreboard = true;
					}

					Team self = getExistingOrCreateNewTeam("self", scoreboard, ChatColor.GREEN);
					Team admin = getExistingOrCreateNewTeam("admin", scoreboard, ChatColor.DARK_GREEN);
					Team wanted = getExistingOrCreateNewTeam("wanted", scoreboard, ChatColor.DARK_RED);
					Team criminal = getExistingOrCreateNewTeam("criminal", scoreboard, ChatColor.DARK_RED);
					Team friendly = getExistingOrCreateNewTeam("friendly", scoreboard, ChatColor.DARK_GREEN);
					Team ally = getExistingOrCreateNewTeam("ally", scoreboard, ChatColor.BLUE);
					Team neutral = getExistingOrCreateNewTeam("neutral", scoreboard, ChatColor.YELLOW);
					Team enemy = getExistingOrCreateNewTeam("enemy", scoreboard, ChatColor.RED);
					
					for(Admin admins : Admin.getAdminProfiles().values())
					{
						if(!admin.hasEntry(admins.getName()))
							admin.addEntry(admins.getName());
					}
					
					if (house != null) 
					{
						for (Player friendlyPlayer : house.getOnlinePlayers())
						{
							if(friendlyPlayer.equals(player)) continue;
							if(Profile.getByPlayer(friendlyPlayer).isAdmin()) continue;
							if (!(friendly.hasEntry(friendlyPlayer.getName()))) 
							{
								friendly.addEntry(friendlyPlayer.getName());
							}
						}

						for (House allyHouse : house.getAllies()) 
						{
							for (Player allyPlayer : allyHouse.getOnlinePlayers()) 
							{
								if(Profile.getByPlayer(allyPlayer).isAdmin()) continue;
								if (!(ally.hasEntry(allyPlayer.getName()))) 
								{
									ally.addEntry(allyPlayer.getName());
								}
							}
						}

						for (House enemyHouse : house.getAllEnemies()) 
						{
							for (Player enemyPlayer : enemyHouse.getOnlinePlayers()) 
							{
								if(Profile.getByPlayer(enemyPlayer).isAdmin()) continue;
								if (!(enemy.hasEntry(enemyPlayer.getName()))) 
								{
									enemy.addEntry(enemyPlayer.getName());
								}
							}
						}
						
					}

					for (Player neutralPlayer : PlayerTool.getOnlinePlayers()) 
					{
						if (!(neutralPlayer.getName().equals(player.getName()))) 
						{
							if(Profile.getByPlayer(neutralPlayer).isAdmin()) continue;
							
							if (friendly.hasEntry(neutralPlayer.getName()) && (house == null || !house.getOnlinePlayers().contains(neutralPlayer))) 
							{
								friendly.removeEntry(neutralPlayer.getName());
							}

							if (ally.hasEntry(neutralPlayer.getName())) 
							{
								Profile neutralProfile = Profile.getByUUID(neutralPlayer.getUniqueId());
								House neutralHouse = neutralProfile.getHouse();
								if (neutralHouse == null || house == null || !house.getAllies().contains(neutralHouse)) 
								{
									ally.removeEntry(neutralPlayer.getName());
								}
							}
							
							if (enemy.hasEntry(neutralPlayer.getName())) 
							{
								Profile enemyProfile = Profile.getByUUID(neutralPlayer.getUniqueId());
								House enemyHouse = enemyProfile.getHouse();
								if (enemyHouse == null || house == null || !house.getAllEnemies().contains(enemyHouse)) 
								{
									enemy.removeEntry(enemyProfile.getName());
								}
							}

							if (!(friendly.hasEntry(neutralPlayer.getName())) && (!(ally.hasEntry(neutralPlayer.getName())) && (!(enemy.hasEntry(neutralPlayer.getName()))))) 
							{
								neutral.addEntry(neutralPlayer.getName());
							}
						}
					}

					if (!(self.hasEntry(player.getName()))) 
					{
						self.addEntry(player.getName());
					}

					if (newScoreboard) 
					{
						player.setScoreboard(scoreboard);
					}
				}

			}
		}.runTaskLater(plugin, 5L);
	}

	public void updateTeams(Player toUpdate) 
	{
		new BukkitRunnable() 
		{

			@Override
			public void run() 
			{
				Player player = Bukkit.getPlayer(uuid);

				if (player != null) 
				{

					Scoreboard scoreboard;
					boolean newScoreboard = false;

					if (!player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())) 
					{
						scoreboard = player.getScoreboard();
					} 
					else 
					{
						scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
						newScoreboard = true;
					}

					Team self = getExistingOrCreateNewTeam("self", scoreboard, ChatColor.GREEN);
					Team admin = getExistingOrCreateNewTeam("admin", scoreboard, ChatColor.DARK_GREEN);
					Team wanted = getExistingOrCreateNewTeam("wanted", scoreboard, ChatColor.DARK_RED);
					Team criminal = getExistingOrCreateNewTeam("criminal", scoreboard, ChatColor.DARK_RED);
					Team friendly = getExistingOrCreateNewTeam("friendly", scoreboard, ChatColor.DARK_GREEN);
					Team ally = getExistingOrCreateNewTeam("ally", scoreboard, ChatColor.BLUE);
					Team neutral = getExistingOrCreateNewTeam("neutral", scoreboard, ChatColor.YELLOW);
					Team enemy = getExistingOrCreateNewTeam("enemy", scoreboard, ChatColor.RED);

					if(getPlayer() != null && getPlayer().equals(toUpdate))
					{
						if(!self.hasEntry(toUpdate.getName()))
							self.addEntry(toUpdate.getName());
						return;
					}

					if(Profile.getByPlayer(toUpdate).isAdmin)
					{
						if(!admin.hasEntry(toUpdate.getName()))
							admin.addEntry(toUpdate.getName());
						return;
					}

					if (house != null) 
					{
						if (house.getOnlinePlayers().contains(toUpdate) && !(friendly.hasEntry(toUpdate.getName()))) 
						{
							friendly.addEntry(toUpdate.getName());
						}

						for (House allyHouse : house.getAllies()) 
						{
							if (allyHouse.getOnlinePlayers().contains(toUpdate) && !(ally.hasEntry(toUpdate.getName()))) 
							{
								ally.addEntry(toUpdate.getName());
							}
						}
						
						for (House enemyHouse : house.getAllEnemies()) 
						{
							if (enemyHouse.getOnlinePlayers().contains(toUpdate) && !(enemy.hasEntry(toUpdate.getName()))) 
							{
								enemy.addEntry(toUpdate.getName());
							}
						}
					}

					if (friendly.hasEntry(toUpdate.getName()) && (house == null || !house.getOnlinePlayers().contains(toUpdate))) 
					{
						friendly.removeEntry(toUpdate.getName());
					}

					if (ally.hasEntry(toUpdate.getName())) 
					{
						Profile neutralProfile = Profile.getByUUID(toUpdate.getUniqueId());
						House neutralHouse = neutralProfile.getHouse();
						if (neutralHouse == null || house == null || !house.getAllies().contains(neutralHouse))
						{
							ally.removeEntry(toUpdate.getName());
						}
					}

					if (enemy.hasEntry(toUpdate.getName())) 
					{
						Profile neutralProfile = Profile.getByUUID(toUpdate.getUniqueId());
						House neutralHouse = neutralProfile.getHouse();
						if (neutralHouse == null || house == null || !house.getAllEnemies().contains(neutralHouse))
						{
							enemy.removeEntry(toUpdate.getName());
						}
					}
					
					if (!(friendly.hasEntry(toUpdate.getName())) && (!(ally.hasEntry(toUpdate.getName())) && (!(enemy.hasEntry(toUpdate.getName()))))) 
					{
						neutral.addEntry(toUpdate.getName());
					}

					if (newScoreboard) 
					{
						player.setScoreboard(scoreboard);
					}
				}

			}
		}.runTaskLater(plugin, 20L);
	}

	private Team getExistingOrCreateNewTeam(String string, Scoreboard scoreboard, ChatColor color) 
	{
		Team toReturn = scoreboard.getTeam(string);

		if (toReturn == null) 
		{
			toReturn = scoreboard.registerNewTeam(string);
			toReturn.setColor(color);
			toReturn.setCanSeeFriendlyInvisibles(true);
			toReturn.setOption(Option.NAME_TAG_VISIBILITY, OptionStatus.ALWAYS);
			toReturn.setNameTagVisibility(NameTagVisibility.ALWAYS);
			if(toReturn.getName().equals("admin"))
				toReturn.setPrefix(MessageTool.color("&5✶"));
		}

		return toReturn;
	}

	public static void sendGlobalTabUpdate() 
	{
		for (Player player : PlayerTool.getOnlinePlayers()) 
		{
			getByUUID(player.getUniqueId()).updateTeams();
		}
	}

	public static void sendPlayerTabUpdate(Player toUpdate) 
	{
		for (Player player : PlayerTool.getOnlinePlayers()) 
		{
			getByUUID(player.getUniqueId()).updateTeams(toUpdate);
		}
	}


	private void saveFights()
	{
		for (ProfileFight fight : this.fights)
		{
			if (!this.previousFights.containsKey(fight.getUuid()))
				this.previousFights.put(fight.getUuid(), Boolean.valueOf((fight.getKiller() instanceof ProfileFightPlayerKiller && ((ProfileFightPlayerKiller)fight.getKiller()).getUuid().equals(this.uuid))));

			if (fight.getKiller() instanceof ProfileFightPlayerKiller && ((ProfileFightPlayerKiller)fight.getKiller()).getUuid().equals(this.uuid))
				continue; 

			Document document = new Document();
			document.put("uuid", fight.getUuid().toString());
			document.put("killed", this.uuid.toString());
			document.put("ping", Integer.valueOf(fight.getPing()));
			document.put("occurred_at", Long.valueOf(fight.getOccurredAt()));
			document.put("health", Integer.valueOf(0));
			document.put("hunger", Double.valueOf(fight.getHunger()));
			document.put("location", LocationSerialization.serializeLocation(fight.getLocation()));
			JsonArray effects = new JsonArray();

			for (ProfileFightEffect effect : fight.getEffects()) 
			{
				JsonObject effectObject = new JsonObject();
				effectObject.addProperty("type", effect.getType().getName());
				effectObject.addProperty("level", Integer.valueOf(effect.getLevel()));
				effectObject.addProperty("duration", Integer.valueOf(effect.getDuration()));
				effects.add((JsonElement)effectObject);
			} 

			document.put("effects", effects.toString());
			Document killerDocument = new Document();

			if (fight.getKiller() instanceof ProfileFightPlayerKiller) 
			{
				ProfileFightPlayerKiller killer = (ProfileFightPlayerKiller)fight.getKiller();
				killerDocument.put("type", "PLAYER");
				killerDocument.put("name", killer.getName());
				killerDocument.put("uuid", killer.getUuid().toString());
				killerDocument.put("ping", Integer.valueOf(killer.getPing()));
				killerDocument.put("health", Double.valueOf(killer.getHealth()));
				killerDocument.put("hunger", Double.valueOf(killer.getHunger()));
				JsonArray killerEffects = new JsonArray();
				for (ProfileFightEffect effect : killer.getEffects()) 
				{
					JsonObject effectObject = new JsonObject();
					effectObject.addProperty("type", effect.getType().getName());
					effectObject.addProperty("level", Integer.valueOf(effect.getLevel()));
					effectObject.addProperty("duration", Integer.valueOf(effect.getDuration()));
					killerEffects.add((JsonElement)effectObject);
				} 
				killerDocument.put("effects", killerEffects.toString());

				String serialized = InventorySerializer.serialize(killer.getInventory().getContents());
				killerDocument.put("killer_inventory", serialized);

				document.put("killer_uuid", killer.getUuid().toString());
			} 
			else if (fight.getKiller() instanceof ProfileFightEnvironmentKiller) 
			{
				ProfileFightEnvironmentKiller killer = (ProfileFightEnvironmentKiller)fight.getKiller();
				killerDocument.put("type", killer.getType().name());
			} 
			else
			{
				ProfileFightKiller killer = fight.getKiller();
				killerDocument.put("type", "MOB");
				killerDocument.put("mob_type", killer.getEntityType().name());
				killerDocument.put("name", killer.getName());
			} 
			document.put("killer", killerDocument);

			String serialized = InventorySerializer.serialize(fight.getInventory());
			document.put("killed_inventory", serialized);

			plugin.getKingdomsDatabase().getFights().replaceOne(Filters.eq("uuid", fight.getUuid().toString()), document, plugin.getOptions());
		} 
	}


	public void save(UpdateOptions options) 
	{
		Document document = new Document();
		document.put("uuid", this.uuid.toString());
		if (this.name != null) 
		{
			document.put("recentName", this.name);
			document.put("recentNameLowercase", this.name.toLowerCase());
		} 
		if (this.protection != null)
			document.put("protection", Integer.valueOf(this.protection.getDurationLeft())); 

		if (this.respawning)
			document.put("respawning", Boolean.valueOf(true)); 

		if (this.combatLogged)
			document.put("combatLogged", Boolean.valueOf(true)); 	

		if (!this.leftSpawn)
			document.put("leftSpawn", Boolean.valueOf(false)); 

		//if (this.playTime != 0L)
		//	document.put("playTime", Long.valueOf(this.playTime)); 

		//document.put("kills", Integer.valueOf(getKillCount()));
		//document.put("deaths", Integer.valueOf(getDeathCount()));

		document.put("general_lives", this.generalLives); 
		document.put("soulbound_lives", this.soulboundLives); 

		if (this.deathban != null) 
		{
			Document deathbanDocument = new Document();
			deathbanDocument.put("createdAt", Long.valueOf(this.deathban.getCreatedAt()));
			deathbanDocument.put("duration", Long.valueOf(this.deathban.getDuration()));
			document.put("deathban", deathbanDocument);
		} 

		JsonArray fightsArray = new JsonArray();

		for (UUID fight : this.previousFights.keySet()) 
		{
			JsonObject object = new JsonObject();
			object.addProperty("uuid", fight.toString());
			object.addProperty("killer", this.previousFights.get(fight));
			fightsArray.add((JsonElement)object);
		} 

		for (ProfileFight fight : this.fights) 
		{
			JsonObject object = new JsonObject();
			object.addProperty("uuid", fight.getUuid().toString());
			object.addProperty("killer", Boolean.valueOf((fight.getKiller() instanceof ProfileFightPlayerKiller && fight.getKiller().getName().equals(this.name))));
			fightsArray.add((JsonElement)object);
		} 

		if (fightsArray.size() > 0)
			document.put("fights", fightsArray.toString()); 

		JsonArray achievementArray = new JsonArray();
		//for (ProfileAchievement achievement : this.achievements) 
		//{
		//	JsonObject jsonObject = new JsonObject();
		//	jsonObject.addProperty("createdAt", Long.valueOf(achievement.getCreatedAt()));
		//	jsonObject.addProperty("type", achievement.getType().name());
		//	jsonObject.addProperty("name", achievement.getType().getName());
		//	jsonObject.addProperty("icon", achievement.getType().getIcon());
		//	achievementArray.add((JsonElement)jsonObject);
		//} 

		if (achievementArray.size() > 0)
			document.put("achievements", achievementArray.toString()); 

		Document oresDocument = new Document();
		//for (ProfileOreType type : this.ores.keySet())
		//	oresDocument.put(type.name().toLowerCase(), this.ores.get(type)); 

		document.put("ores", oresDocument);
		saveFights();
		this.fights.clear();


		try
		{
			UpdateResult result = collection.replaceOne(Filters.eq("uuid", this.uuid.toString()), document, options);
			Bukkit.getLogger().log(Level.INFO, "[HardcoreSMP] Mongo has updated " + result.getModifiedCount() + " database entry.");
		}
		catch(MongoException exception)
		{
			Bukkit.getLogger().log(Level.SEVERE, "[HardcoreSMP] Mongo encountered an error whilst updating a database.");
		}
	}

	private void load() 
	{
		Document document = (Document)collection.find(Filters.eq("uuid", this.uuid.toString())).first();
		if (document != null) 
		{
			if (document.containsKey("recentName"))
				this.name = document.getString("recentName"); 

			if (document.containsKey("protection")) 
			{
				this.protection = new ProfileProtection(document.getInteger("protection").intValue());
				if (Bukkit.getPlayer(this.uuid) == null)
					this.protection.pause(); 
			} 

			if (document.containsKey("leftSpawn"))
				this.leftSpawn = false; 

			if (document.containsKey("combatLogged"))
				this.combatLogged = true; 

			if (document.containsKey("respawning"))
				this.respawning = true; 

			if (document.containsKey("deathban")) 
			{
				Document deathbanDocument = (Document)document.get("deathban");
				this.deathban = new ProfileDeathban(deathbanDocument.getLong("createdAt").longValue(), deathbanDocument.getLong("duration").longValue());
			} 

			if (document.containsKey("general_lives")) 
				this.generalLives = document.getDouble("general_lives"); 

			if (document.containsKey("soulbound_lives")) 
				this.soulboundLives = document.getDouble("soulbound_lives"); 

			//if (document.containsKey("ores")) 
			//{
			//	Document oresDocument = (Document)document.get("ores");
			//	for (String typeName : oresDocument.keySet())
			//		this.ores.put(ProfileOreType.valueOf(typeName.toUpperCase()), oresDocument.getInteger(typeName)); 
			//} 

			if (document.containsKey("fights") && document.get("fights") instanceof String) 
			{
				JsonArray fightsArray = (new JsonParser()).parse(document.getString("fights")).getAsJsonArray();
				for (JsonElement jsonElement : fightsArray) 
				{
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					try 
					{
						this.previousFights.put(UUID.fromString(jsonObject.get("uuid").getAsString()), Boolean.valueOf(jsonObject.get("killer").getAsBoolean()));
					} 
					catch (Exception ex) 
					{
						ex.printStackTrace();
					} 
				} 
			} 
		} 
		else 
		{
			this.protection = new ProfileProtection(ProfileProtection.DEFAULT_DURATION);
			this.protection.pause();
			this.leftSpawn = false;
		} 
	}
}
