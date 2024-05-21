package com.rivensoftware.hardcoresmp.profile;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.NameTagVisibility;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

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
import com.rivensoftware.hardcoresmp.economy.MySQLManager;
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
@Setter
public class Profile {
	private static HardcoreSMP plugin = HardcoreSMP.getInstance();

	private boolean loggedIn;

	private UUID uuid;

	private String name;

	private Player player;

	private boolean loaded;

	private boolean isAdmin;

	private boolean inSpawn;

	private boolean isWanted;

	private boolean isCriminal;

	private Map<Location, ClaimWall> walls;

	private Set<ClaimPillar> mapPillars;

	private ClaimProfile claimProfile;

	private Claim lastInside;

	private boolean viewingMap;

	private List<ProfileCooldown> cooldowns;

	private Location pearlLocation;

	private boolean respawning;

	private List<ProfileFight> fights;

	private LinkedHashMap<UUID, Boolean> previousFights;

	private DeathLookup deathLookup;

	private double generalLives = 0.0D;
	
	private double soulboundLives = 2.0D;

	private boolean leftSpawn;

	private boolean combatLogged;

	private boolean safeLogout;

	private ProfileDeathban deathban;

	private ProfileProtection protection;

	private ProfileTeleportTask teleportWarmup;

	private CapturePointCreateProcedure capturePointCreateProcedure;

	private House house;

	private ProfileChatType chatType;
	
	private double balance;
	
    public static MySQLManager mySQLManager = HardcoreSMP.getInstance().getMySQLManager();

	private static MongoCollection<Document> collection = plugin.getKingdomsDatabase().getProfiles();

	@Getter private static Map<UUID, Profile> activeProfiles = new HashMap<>();

	public Profile(UUID uuid) {
		this.uuid = uuid;
		this.player = Bukkit.getPlayer(uuid);
		this.chatType = ProfileChatType.PUBLIC;
		this.cooldowns = new ArrayList<>();
		this.mapPillars = new HashSet<>();
		this.walls = new HashMap<>();
		this.fights = new ArrayList<>();
		this.previousFights = new LinkedHashMap<>();
		this.safeLogout = true;
		this.deathban = null;
        this.balance = loadBalance();
		for (House house : House.getHouses().values()) {
			if (house.getAllPlayerUuids().contains(uuid))
				this.house = house;
		}
		activeProfiles.put(uuid, this);
		load();
		this.loaded = true;
	}

	public Profile(Player player) {
		this(player.getUniqueId());
	}

	public static Profile getExistingByUUID(UUID uuid) {
		if (activeProfiles.containsKey(uuid))
			return activeProfiles.get(uuid);
		return null;
	}

	public static Profile getByUUID(UUID uuid) {
	    if (Admin.getAdminProfiles().containsKey(uuid)) {
	        return Admin.getByUUID(uuid);
	    }
	    if (activeProfiles.containsKey(uuid)) {
	        return activeProfiles.get(uuid);
	    }
	    return new Profile(uuid);
	}

	public static Profile getByPlayer(Player player) {
	    UUID uuid = player.getUniqueId();
	    if (Admin.getAdminProfiles().containsKey(uuid)) {
	        return Admin.getByUUID(uuid);
	    }
	    if (activeProfiles.containsKey(uuid)) {
	        return activeProfiles.get(uuid);
	    }
	    return null;
	}

	public static Profile getByName(String name) {
		for (Profile profile : activeProfiles.values()) {
			if (profile.getName() != null && profile.getName().equals(name))
				return profile;
		}
		Document document = (Document)collection.find(Filters.eq("recentNameLowercase", name.toLowerCase())).first();
		if (document != null)
			return new Profile(UUID.fromString(document.getString("uuid")));
		return null;
	}

	public static Set<Profile> getProfiles() {
		return new HashSet<>(activeProfiles.values());
	}

	public ProfileFight getLatestFight() {
		if (!this.fights.isEmpty())
			return this.fights.get(this.fights.size() - 1);
		return null;
	}

	public ProfileCooldown getCooldownByType(ProfileCooldownType type) {
		Iterator<ProfileCooldown> iterator = getCooldowns().iterator();
		while (iterator.hasNext()) {
			ProfileCooldown cooldown = iterator.next();
			if (cooldown.getType() == type) {
				if (cooldown.isFinished()) {
					iterator.remove();
					continue;
				}
				return cooldown;
			}
		}
		return null;
	}

	public ProfileDeathban getDeathban() {
		if (this.deathban != null && this.deathban.getCreatedAt() + this.deathban.getDuration() <= System.currentTimeMillis())
			this.deathban = null;
		return this.deathban;
	}

	public static void loadProfile(Player player) {
	    if (player.hasPermission("admin")) {
	        Admin.loadAdminProfile(player);
	    } else {
	        Profile profile = getByUUID(player.getUniqueId());
	        if (profile == null) {
	            Bukkit.getConsoleSender().sendMessage(MessageTool.color(player.getName() + "&c's profile was not loaded correctly"));
	        }
	    }
	}

	public static void unloadProfile(Player player) {
		Profile profile = getExistingByUUID(player.getUniqueId());
		if (profile != null) {
			getProfiles().remove(profile);
			return;
		}
		Bukkit.getConsoleSender().sendMessage(MessageTool.color("&c" + player.getName() + "'s profile was not correctly saved."));
	}

    public void updateTeams() {
        Player player = Bukkit.getPlayer(this.uuid);
        if (player != null) {
            Scoreboard scoreboard = player.getScoreboard();
            Team self = getExistingOrCreateNewTeam("self", scoreboard, ChatColor.DARK_GREEN);
            Team friendly = getExistingOrCreateNewTeam("friendly", scoreboard, ChatColor.GREEN);
            Team ally = getExistingOrCreateNewTeam("ally", scoreboard, ChatColor.BLUE);
            Team neutral = getExistingOrCreateNewTeam("neutral", scoreboard, ChatColor.YELLOW);
            Team enemy = getExistingOrCreateNewTeam("enemy", scoreboard, ChatColor.RED);

            // Set the player's own name tag color to dark green
            if (!self.hasEntry(player.getName())) {
                self.addEntry(player.getName());
                Bukkit.getLogger().info("Setting " + player.getName() + "'s own name tag color to " + ChatColor.DARK_GREEN + " (reason: own player)");
            }

            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if (!onlinePlayer.equals(player)) {
                    Profile onlineProfile = Profile.getByUUID(onlinePlayer.getUniqueId());
                    House onlineHouse = onlineProfile != null ? onlineProfile.getHouse() : null;

                    if (onlineHouse == null) {
                        if (!neutral.hasEntry(onlinePlayer.getName())) {
                            neutral.addEntry(onlinePlayer.getName());
                            Bukkit.getLogger().info("Setting " + onlinePlayer.getName() + "'s name tag color to " + ChatColor.YELLOW + " for " + player.getName() + " (reason: not in any house)");
                        }
                    } else {
                        if (this.house != null) {
                            if (this.house.equals(onlineHouse)) {
                                if (!friendly.hasEntry(onlinePlayer.getName())) {
                                    friendly.addEntry(onlinePlayer.getName());
                                    Bukkit.getLogger().info("Setting " + onlinePlayer.getName() + "'s name tag color to " + ChatColor.GREEN + " for " + player.getName() + " (reason: in the same house)");
                                }
                            } else if (this.house.getAllies() != null && this.house.getAllies().contains(onlineHouse)) {
                                if (!ally.hasEntry(onlinePlayer.getName())) {
                                    ally.addEntry(onlinePlayer.getName());
                                    Bukkit.getLogger().info("Setting " + onlinePlayer.getName() + "'s name tag color to " + ChatColor.BLUE + " for " + player.getName() + " (reason: in an allied house)");
                                }
                            } else if (this.house.getAllEnemies() != null && this.house.getAllEnemies().contains(onlineHouse)) {
                                if (!enemy.hasEntry(onlinePlayer.getName())) {
                                    enemy.addEntry(onlinePlayer.getName());
                                    Bukkit.getLogger().info("Setting " + onlinePlayer.getName() + "'s name tag color to " + ChatColor.RED + " for " + player.getName() + " (reason: in an enemy house)");
                                }
                            } else {
                                if (!neutral.hasEntry(onlinePlayer.getName())) {
                                    neutral.addEntry(onlinePlayer.getName());
                                    Bukkit.getLogger().info("Setting " + onlinePlayer.getName() + "'s name tag color to " + ChatColor.YELLOW + " for " + player.getName() + " (reason: not in any related house)");
                                }
                            }
                        } else {
                            if (!neutral.hasEntry(onlinePlayer.getName())) {
                                neutral.addEntry(onlinePlayer.getName());
                                Bukkit.getLogger().info("Setting " + onlinePlayer.getName() + "'s name tag color to " + ChatColor.YELLOW + " for " + player.getName() + " (reason: not in any related house)");
                            }
                        }
                    }
                }
            }
        }
    }

    private Team getExistingOrCreateNewTeam(String name, Scoreboard scoreboard, ChatColor color) {
        Team team = scoreboard.getTeam(name);
        if (team == null) {
            team = scoreboard.registerNewTeam(name);
            team.setColor(color);
            team.setCanSeeFriendlyInvisibles(true);
            team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.ALWAYS);
            team.setNameTagVisibility(NameTagVisibility.ALWAYS);
        }
        return team;
    }

	private void saveFights() {
		for (ProfileFight fight : this.fights) {
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
			for (ProfileFightEffect effect : fight.getEffects()) {
				JsonObject effectObject = new JsonObject();
				effectObject.addProperty("type", effect.getType().getName());
				effectObject.addProperty("level", Integer.valueOf(effect.getLevel()));
				effectObject.addProperty("duration", Integer.valueOf(effect.getDuration()));
				effects.add((JsonElement)effectObject);
			}
			document.put("effects", effects.toString());
			Document killerDocument = new Document();
			if (fight.getKiller() instanceof ProfileFightPlayerKiller) {
				ProfileFightPlayerKiller killer = (ProfileFightPlayerKiller)fight.getKiller();
				killerDocument.put("type", "PLAYER");
				killerDocument.put("name", killer.getName());
				killerDocument.put("uuid", killer.getUuid().toString());
				killerDocument.put("ping", Integer.valueOf(killer.getPing()));
				killerDocument.put("health", Double.valueOf(killer.getHealth()));
				killerDocument.put("hunger", Double.valueOf(killer.getHunger()));
				JsonArray killerEffects = new JsonArray();
				for (ProfileFightEffect effect : killer.getEffects()) {
					JsonObject effectObject = new JsonObject();
					effectObject.addProperty("type", effect.getType().getName());
					effectObject.addProperty("level", Integer.valueOf(effect.getLevel()));
					effectObject.addProperty("duration", Integer.valueOf(effect.getDuration()));
					killerEffects.add((JsonElement)effectObject);
				}
				killerDocument.put("effects", killerEffects.toString());
				String str = InventorySerializer.serialize(killer.getInventory().getContents());
				killerDocument.put("killer_inventory", str);
				document.put("killer_uuid", killer.getUuid().toString());
			} else if (fight.getKiller() instanceof ProfileFightEnvironmentKiller) {
				ProfileFightEnvironmentKiller killer = (ProfileFightEnvironmentKiller)fight.getKiller();
				killerDocument.put("type", killer.getType().name());
			} else {
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

	public void save(UpdateOptions options) {
		Document document = new Document();
		document.put("uuid", this.uuid.toString());
		if (this.name != null) {
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
		document.put("general_lives", Double.valueOf(this.generalLives));
		document.put("soulbound_lives", Double.valueOf(this.soulboundLives));
		//if (this.deathban != null) {
		//	Document deathbanDocument = new Document();
		//	deathbanDocument.put("createdAt", Long.valueOf(this.deathban.getCreatedAt()));
		//	deathbanDocument.put("duration", Long.valueOf(this.deathban.getDuration()));
		//	document.put("deathban", deathbanDocument);
		//}
		JsonArray fightsArray = new JsonArray();
		for (UUID fight : this.previousFights.keySet()) {
			JsonObject object = new JsonObject();
			object.addProperty("uuid", fight.toString());
			object.addProperty("killer", this.previousFights.get(fight));
			fightsArray.add((JsonElement)object);
		}
		for (ProfileFight fight : this.fights) {
			JsonObject object = new JsonObject();
			object.addProperty("uuid", fight.getUuid().toString());
			object.addProperty("killer", Boolean.valueOf((fight.getKiller() instanceof ProfileFightPlayerKiller && fight.getKiller().getName().equals(this.name))));
			fightsArray.add((JsonElement)object);
		}
		if (fightsArray.size() > 0)
			document.put("fights", fightsArray.toString());
		JsonArray achievementArray = new JsonArray();
		if (achievementArray.size() > 0)
			document.put("achievements", achievementArray.toString());
		Document oresDocument = new Document();
		document.put("ores", oresDocument);
		saveFights();
		this.fights.clear();
		try {
			UpdateResult result = collection.replaceOne(Filters.eq("uuid", this.uuid.toString()), document, options);
			Bukkit.getLogger().log(Level.INFO, "[HardcoreSMP] Mongo has updated " + result.getModifiedCount() + " database entry.");
		} catch (MongoException exception) {
			Bukkit.getLogger().log(Level.SEVERE, "[HardcoreSMP] Mongo encountered an error whilst updating a database.");
		}
	}

	private void load() {
		Document document = (Document)collection.find(Filters.eq("uuid", this.uuid.toString())).first();
		if (document != null) {
			if (document.containsKey("recentName"))
				this.name = document.getString("recentName");
			if (document.containsKey("protection")) {
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
			if (document.containsKey("deathban")) {
				Document deathbanDocument = (Document)document.get("deathban");
				this.deathban = new ProfileDeathban();
			}
			if (document.containsKey("general_lives"))
				this.generalLives = document.getDouble("general_lives").doubleValue();
			if (document.containsKey("soulbound_lives"))
				this.soulboundLives = document.getDouble("soulbound_lives").doubleValue();
			if (document.containsKey("fights") && document.get("fights") instanceof String) {
				JsonArray fightsArray = (new JsonParser()).parse(document.getString("fights")).getAsJsonArray();
				for (JsonElement jsonElement : fightsArray) {
					JsonObject jsonObject = jsonElement.getAsJsonObject();
					try {
						this.previousFights.put(UUID.fromString(jsonObject.get("uuid").getAsString()), Boolean.valueOf(jsonObject.get("killer").getAsBoolean()));
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} else {
			this.protection = new ProfileProtection(3600L);
			this.protection.pause();
			this.leftSpawn = false;
		}
	}
	
    public void setBalance(double balance) {
        this.balance = balance;
        saveBalance(); // Save balance to database
    }

    public void addBalance(double amount) {
        this.balance += amount;
        saveBalance(); // Save updated balance to database
    }

    public void removeBalance(double amount) {
        this.balance -= amount;
        saveBalance(); // Save updated balance to database
    }

    private double loadBalance() {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT balance FROM player_balances WHERE player_uuid = ?")) {
            statement.setString(1, this.uuid.toString());
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                createPlayerBalance(0.0); // Create a new record with zero balance if not exists
                return 0.0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    private void saveBalance() {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE player_balances SET balance = ? WHERE player_uuid = ?")) {
            statement.setDouble(1, balance);
            statement.setString(2, this.uuid.toString());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createPlayerBalance(double initialBalance) {
        try (Connection connection = mySQLManager.getConnection();
             PreparedStatement statement = connection.prepareStatement("INSERT INTO player_balances (player_uuid, balance) VALUES (?, ?)")) {
            statement.setString(1, this.uuid.toString());
            statement.setDouble(2, initialBalance);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}