package com.rivensoftware.hardcoresmp.house;

import com.mongodb.Block;
import com.mongodb.MongoException;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import com.rivensoftware.hardcoresmp.HardcoreSMP;
import com.rivensoftware.hardcoresmp.house.banner.Banner;
import com.rivensoftware.hardcoresmp.house.banner.BannerEffect;
import com.rivensoftware.hardcoresmp.house.banner.BannerType;
import com.rivensoftware.hardcoresmp.house.claim.Claim;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.tools.player.PlayerTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

@Getter
public class House 
{
	@Setter private String houseName;
	@Setter private String kingdomCapital;
	@Setter private String message;
	@Setter private UUID uuid;
	@Setter private UUID lord;
	@Setter private String announcement;
	@Setter private int balance;

	@Setter private boolean royal = false;
	@Setter private boolean autoPay = false;
	@Setter private boolean paid = false;
	@Setter private int taxRate = 10;

	@Setter private Set<Claim> claims;
	@Setter private Set<House> allies;

	@Setter private Set<House> enemies;
	@Setter private Set<House> invoulentaryEnemies;
	@Setter private Set<House> allEnemies;

	@Setter private Set<UUID> captains;
	@Setter private Set<UUID> members;
	@Setter private Set<UUID> requestedAllies;
	@Setter private Map<UUID, UUID> invitedPlayers;

	@Setter private boolean isPowerless;
	@Setter private double lives;
	@Setter private boolean regenning;

	@Setter private boolean customBanner = false;
	@Setter private String primaryBanner;
	@Setter private String secondaryBanner;
	@Setter private List<String> customBannerList;
	@Setter private int primaryBannerIndex;
	@Setter private Set<Banner> bannerList;

	@Getter public static HardcoreSMP plugin = HardcoreSMP.getInstance();
	@Getter public static Map<UUID, House> houses = new HashMap<UUID, House>();

	public House(String name, UUID lord, UUID uuid)
	{
		this.uuid = uuid;
		this.houseName = name;
		this.lord = lord;
		this.claims = new HashSet<Claim>();
		this.captains = new HashSet<UUID>();
		this.members = new HashSet<UUID>();
		this.allies = new HashSet<House>();
		this.enemies = new HashSet<House>();
		this.invoulentaryEnemies = new HashSet<House>();
		this.allEnemies = new HashSet<House>();

		this.requestedAllies = new HashSet<UUID>();
		this.invitedPlayers = new HashMap<UUID, UUID>();
		this.bannerList = new HashSet<Banner>();
		this.customBannerList = new ArrayList<String>();

		houses.put(uuid, this);
	}

	/*
	 * Return a house object by finding its name
	 */
	public static House getByName(String name)
	{
		for(House house : getHouses().values())
		{
			if(house.getHouseName().replace(" ", "").equalsIgnoreCase(name.replace(" ", "")))
				return house;
		}
		return null;
	}

	/*
	 * Return a house object by finding its stored UUID.
	 */
	public static House getByUUID(UUID uuid)
	{
		for(UUID houseUUID : getHouses().keySet())
		{
			if(houseUUID.equals(uuid))
			{
				return getHouses().get(houseUUID);
			}
		}
		return null;
	}

	public static Set<House> getAllByString(String string) 
	{
		Set<House> toReturn = new HashSet<House>();
		for (House house : houses.values()) 
		{
			if (!toReturn.contains(house)) 
			{
				if (house.getHouseName().replace(" ", "").equalsIgnoreCase(string))
					toReturn.add(house); 

				for (UUID uuid : house.getAllPlayerUuids()) 
				{
					SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByUuid(uuid);
					if (offlinePlayer != null && offlinePlayer.getName().equalsIgnoreCase(string))
						toReturn.add(house); 
				} 
			} 
		} 
		return toReturn;
	}

	public double getTaxPrice()
	{
		double total = 0;
		for(UUID uuid : members)
		{
			double playerBalance = plugin.getInternalEconomy().getOfflineBalance(uuid);
			total = total + playerBalance;
		}
		total = total + balance;

		total = total / (members.size() + captains.size() + 2);

		total = total * taxRate;

		return total;
	}

	public void sendMessage(String message) 
	{
		for (Player player : getOnlinePlayers())
			player.sendMessage(message); 
	}

	public Set<Player> getOnlinePlayers() 
	{
		Set<Player> toReturn = new HashSet<Player>();
		for (UUID uuid : getAllPlayerUuids()) 
		{
			Player player = Bukkit.getPlayer(uuid);
			if (player != null)
				toReturn.add(player); 
		} 
		return toReturn;
	}

	public List<UUID> getAllPlayerUuids() 
	{
		List<UUID> toReturn = new ArrayList<UUID>();
		toReturn.add(getLord());
		toReturn.addAll(getCaptains());
		toReturn.addAll(getMembers());
		return toReturn;
	}

	public static House getByPlayer(Player player) 
	{
		Profile profile = Profile.getByUUID(player.getUniqueId());
		return profile.getHouse();
	}

	public static House getByPlayerName(String name) 
	{
		for (House houses : getHouses().values()) 
		{
			for (UUID uuid : houses.getAllPlayerUuids()) 
			{
				SimpleOfflinePlayer offlinePlayer = SimpleOfflinePlayer.getByUuid(uuid);
				if (offlinePlayer != null && offlinePlayer.getName().equalsIgnoreCase(name))
				{
					return houses; 
				}	
			} 
		} 
		return null;
	}

	public static House getAnyByString(String houseName)
	{
		House house = House.getByName(houseName);
		if (house == null) 
		{
			house = getByPlayerName(houseName);
			if (house == null)
				return null; 
		} 
		return house;
	}


	public boolean isNearBorder(Location l) 
	{
		for (Claim claim : getClaims())
		{
			if (claim.getWorldName().equals(l.getWorld().getName()))
			{
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, 1.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, 0.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(0.0D, 0.0D, -1.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, 0.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, 1.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(-1.0D, 0.0D, -1.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, 1.0D)))
					return true; 
				if (claim.isInside((new Location(l.getWorld(), l.getX(), l.getY(), l.getZ())).add(1.0D, 0.0D, -1.0D)))
					return true; 
			} 
		} 
		return false;
	}

	public Set<UUID> getAllyUuids() 
	{
		Set<UUID> toReturn = new HashSet<>();
		for (House house : getAllies())
			toReturn.add(house.getUuid()); 
		return toReturn;
	}

	public Set<UUID> getEnemyUuids() 
	{
		Set<UUID> toReturn = new HashSet<>();
		for (House house : getEnemies())
			toReturn.add(house.getUuid()); 
		return toReturn;
	}

	public Set<UUID> getInvoulentaryEnemyUuids() 
	{
		Set<UUID> toReturn = new HashSet<>();
		for (House house : getInvoulentaryEnemies())
			toReturn.add(house.getUuid()); 
		return toReturn;
	}

	public Set<UUID> getAllEnemyUuids() 
	{
		Set<UUID> toReturn = new HashSet<>();
		for (House house : getAllEnemies())
			toReturn.add(house.getUuid()); 
		return toReturn;
	}

	@SuppressWarnings({ "deprecation", "unchecked" })
	public static void load() 
	{
		MongoCollection<Document> pCollection = plugin.getKingdomsDatabase().getHouses();

		Map<House, Set<UUID>> allyCache = new HashMap<>();
		Map<House, Set<UUID>> enemyCache = new HashMap<>();
		Map<House, Set<UUID>> invoulentaryEnemyCache = new HashMap<>();
		Map<House, Set<UUID>> allEnemyCache = new HashMap<>();
		pCollection.find().forEach((Block<Document>) obj -> 
		{
			Document dbo = (Document) obj;

			UUID uuid = UUID.fromString(dbo.getString("uuid"));
			UUID lord = UUID.fromString(dbo.getString("lord"));
			String name = dbo.getString("name");

			double lives = dbo.getDouble("lives");
			int balance = dbo.getInteger("balance");
			String capital = null;
			String announcement = null;
			boolean isCustomBanner = false;
			String primaryBanner = null;
			String secondaryBanner = null;
			int primaryBannerIndex = -1;

			Set<UUID> captains = new HashSet<>();
			Set<UUID> members = new HashSet<>();
			Map<UUID, UUID> invitedPlayers = new HashMap<>();

			if(dbo.containsKey("custom_banner"))
			{
				isCustomBanner = dbo.getBoolean("custom_banner");
			}

			if(dbo.containsKey("primary_index"))
			{
				primaryBannerIndex = dbo.getInteger("primary_index");
			}
			else
			{
				if(isCustomBanner)
					primaryBannerIndex = 0;
			}

			if(dbo.containsKey("primary_banner"))
			{
				primaryBanner = dbo.getString("primary_banner");
			}

			if(dbo.containsKey("secondary_banner"))
			{
				secondaryBanner = dbo.getString("secondary_banner");
			}

			if (dbo.containsKey("capital")) 
			{
				capital = dbo.getString("capital");
			}

			if (dbo.containsKey("announcement")) 
			{
				announcement = dbo.getString("announcement");
			}

			Document invitedPlayerMap = (Document) dbo.get("invitedPlayers");
			for (String key : invitedPlayerMap.keySet()) 
			{
				UUID invitedPlayer = UUID.fromString(key);
				UUID invitedBy = (UUID) invitedPlayerMap.get(key);
				invitedPlayers.put(invitedPlayer, invitedBy);
			}

			List<String> membersList = (List<String>) dbo.get("members");
			for (String member : membersList) {
				if (member.length() == uuid.toString().length()) 
				{
					members.add(UUID.fromString(member));
				}
			}

			List<String> captainList = (List<String>) dbo.get("captains");
			for (String captain : captainList) 
			{
				if (captain.length() == uuid.toString().length()) 
				{
					captains.add(UUID.fromString(captain));
				}
			}

			House house = new House(name, lord, uuid);
			house.setCaptains(captains);
			house.setMembers(members);
			house.setBalance(balance);
			house.setLives(lives);
			house.setKingdomCapital(capital);
			house.setInvitedPlayers(invitedPlayers);
			house.setAnnouncement(announcement);
			house.setCustomBanner(isCustomBanner);
			house.setPrimaryBanner(primaryBanner);
			house.setSecondaryBanner(secondaryBanner);
			house.setPrimaryBannerIndex(primaryBannerIndex);

			if(lives <= 0)
				house.setPowerless(true);
			else
				house.setPowerless(false);

			Set<Banner> bannerSet = new HashSet<Banner>();

			if(dbo.containsKey("banners"))
			{
				List<String> bannersList = (List<String>) dbo.get("banners");
				for(String bannersEntry : bannersList)
				{
					String[] bannerSplit = bannersEntry.split("@");
					String[] bannerLocationSplit = bannerSplit[0].split(":");
					Location bannerLocation = new Location(Bukkit.getWorld(bannerLocationSplit[0]), 
							Integer.parseInt(bannerLocationSplit[1]), 
							Integer.parseInt(bannerLocationSplit[2]), 
							Integer.parseInt(bannerLocationSplit[3]));

					Banner banner = new Banner(bannerLocation, BannerType.WALL, BannerEffect.getBannerEffect(bannerSplit[1]));
					bannerSet.add(banner);
				}
			}

			if(house.isCustomBanner())
			{
				if(dbo.containsKey("custom_banners"))
				{
					List<String> customBannerList = (List<String>) dbo.get("custom_banners");
					if(customBannerList.size() != 0)
					{
						if(primaryBannerIndex != -1)
							house.setPrimaryBanner(customBannerList.get(primaryBannerIndex));
						house.setCustomBannerList(customBannerList);	
					}
				}	
			}

			house.setBannerList(bannerSet);

			List<String> claims = (List<String>) dbo.get("claims");
			for (String claim : claims) 
			{
				if (claim.length() >= 5) 
				{
					String[] claimParts = (claim).split(";");
					final String worldName = claimParts[0];
					final int x1 = Integer.parseInt(claimParts[1]);
					final int z1 = Integer.parseInt(claimParts[2]);
					final int x2 = Integer.parseInt(claimParts[3]);
					final int z2 = Integer.parseInt(claimParts[4]);
					new Claim(house, new int[]{x1, x2, z1, z2}, worldName);
				}
			}

			if(dbo.containsKey("allies"))
			{
				List<String> allies = (List<String>) dbo.get("allies");
				for (String ally : allies) 
				{
					if (ally.length() == uuid.toString().length()) 
					{
						if (allyCache.containsKey(house)) 
						{
							allyCache.get(house).add(UUID.fromString(ally));
						} 
						else 
						{
							allyCache.put(house, new HashSet<>(Arrays.asList(UUID.fromString(ally))));
						}
					}
				}
			}

			if(dbo.containsKey("enemies"))
			{
				List<String> enemies = (List<String>) dbo.get("enemies");
				for (String enemy : enemies) 
				{
					if (enemy.length() == uuid.toString().length()) 
					{
						if (enemyCache.containsKey(house)) 
						{
							enemyCache.get(house).add(UUID.fromString(enemy));
						} 
						else 
						{
							enemyCache.put(house, new HashSet<>(Arrays.asList(UUID.fromString(enemy))));
						}
					}
				}
			}

			if(dbo.containsKey("invoulentaryEnemies"))
			{
				List<String> invoulentaryEnemies = (List<String>) dbo.get("invoulentaryEnemies");
				for (String enemy : invoulentaryEnemies) 
				{
					if (enemy.length() == uuid.toString().length()) 
					{
						if (invoulentaryEnemyCache.containsKey(house)) 
						{
							invoulentaryEnemyCache.get(house).add(UUID.fromString(enemy));
						} 
						else 
						{
							invoulentaryEnemyCache.put(house, new HashSet<>(Arrays.asList(UUID.fromString(enemy))));
						}
					}
				}
			}

			if(dbo.containsKey("allEnemies"))
			{
				List<String> allEnemies = (List<String>) dbo.get("allEnemies");
				for (String enemy : allEnemies) 
				{
					if (enemy.length() == uuid.toString().length()) 
					{
						if (allEnemyCache.containsKey(house)) 
						{
							allEnemyCache.get(house).add(UUID.fromString(enemy));
						} 
						else 
						{
							allEnemyCache.put(house, new HashSet<>(Arrays.asList(UUID.fromString(enemy))));
						}
					}
				}
			}
		});

		for (House key : allyCache.keySet()) 
		{
			for (UUID allyUuid : allyCache.get(key)) 
			{
				House allyHouse = House.getByUUID(allyUuid);

				key.getAllies().add(allyHouse);
			}
		}

		for (House key : enemyCache.keySet()) 
		{
			for (UUID enemyUuid : enemyCache.get(key)) 
			{
				House enemyHouse = House.getByUUID(enemyUuid);

				key.getEnemies().add(enemyHouse);
			}
		}

		for (House key : invoulentaryEnemyCache.keySet()) 
		{
			for (UUID enemyUuid : invoulentaryEnemyCache.get(key)) 
			{
				House enemyHouse = House.getByUUID(enemyUuid);

				key.getInvoulentaryEnemies().add(enemyHouse);
			}
		}

		for (House key : allEnemyCache.keySet()) 
		{
			for (UUID enemyUuid : allEnemyCache.get(key)) 
			{
				House enemyHouse = House.getByUUID(enemyUuid);

				key.getAllEnemies().add(enemyHouse);
			}
		}

		for (Player player : PlayerTool.getOnlinePlayers())
		{
			Profile profile = Profile.getByUUID(player.getUniqueId());
			Claim claim = Claim.getProminentClaimAt(player.getLocation());
			if (claim != null) 
			{
				profile.setLastInside(claim);
			}
			House house = House.getByPlayerName(player.getName());
			if (profile.getHouse() == null && house != null) 
			{
				profile.setHouse(house);
			}
		}

		Profile.sendGlobalTabUpdate();
		HardcoreSMP.setLoaded(true);
	}

	@SuppressWarnings("deprecation")
	public static void save(UpdateOptions options) 
	{
		if (!(getHouses().isEmpty())) 
		{
			System.out.println("Preparing to save " + getHouses().size() + " houses.");
			MongoCollection<Document> pCollection = plugin.getKingdomsDatabase().getHouses();
			for (House house : getHouses().values()) 
			{

				Document dbo = new Document();

				dbo.put("uuid", house.getUuid().toString());
				dbo.put("lord", house.getLord().toString());
				dbo.put("name", house.getHouseName());
				dbo.put("name_lower", house.getHouseName().toLowerCase());
				dbo.put("lives", house.getLives());
				dbo.put("balance", house.getBalance());
				dbo.put("custom_banner", house.isCustomBanner());

				if(house.getPrimaryBannerIndex() != -1)
				{
					dbo.put("primary_index", house.getPrimaryBannerIndex());
				}

				if(house.getPrimaryBanner() != null)
				{
					dbo.put("primary_banner", house.getPrimaryBanner());
				}

				if(house.getSecondaryBanner() != null)
				{
					dbo.put("secondary_banner", house.getSecondaryBanner());
				}

				if (house.getKingdomCapital() != null) 
				{
					dbo.put("capital", house.getKingdomCapital());
				}

				if (house.getAnnouncement() != null) 
				{
					dbo.put("announcement", house.getAnnouncement());
				}

				List<String> captains = new ArrayList<>();
				List<String> members = new ArrayList<>();
				List<String> allies = new ArrayList<>();
				List<String> enemies = new ArrayList<>();
				List<String> invoulentaryEnemies = new ArrayList<>();
				List<String> allEnemies = new ArrayList<>();
				List<String> requestedAllies = new ArrayList<>();
				Document invitedPlayers = new Document();
				List<String> claims = new ArrayList<>();
				List<String> banners = new ArrayList<>();
				List<String> customBanners = new ArrayList<>();

				captains.addAll(PlayerTool.getConvertedUuidSet(house.getCaptains()));
				members.addAll(PlayerTool.getConvertedUuidSet(house.getMembers()));
				allies.addAll(PlayerTool.getConvertedUuidSet(house.getAllyUuids()));
				enemies.addAll(PlayerTool.getConvertedUuidSet(house.getEnemyUuids()));
				invoulentaryEnemies.addAll(PlayerTool.getConvertedUuidSet(house.getInvoulentaryEnemyUuids()));
				allEnemies.addAll(PlayerTool.getConvertedUuidSet(house.getAllEnemyUuids()));
				requestedAllies.addAll(PlayerTool.getConvertedUuidSet(house.getRequestedAllies()));

				for (UUID invitedPlayer : house.getInvitedPlayers().keySet()) 
				{
					invitedPlayers.put(invitedPlayer.toString(), house.getInvitedPlayers().get(invitedPlayer));
				}

				for (Claim claim : house.getClaims()) 
				{
					claims.add(claim.getWorldName() + ";" + claim.getFirstX() + ";" + claim.getFirstZ() + ";" + claim.getSecondX() + ";" + claim.getSecondZ());
				}

				for(Banner banner : house.getBannerList())
				{
					banners.add(banner.serializeBanner());
				}

				for(String string : house.getCustomBannerList())
				{
					customBanners.add(string);
				}

				dbo.put("captains", captains);
				dbo.put("members", members);
				dbo.put("allies", allies);
				dbo.put("enemies", enemies);
				dbo.put("invoulentaryEnemies", invoulentaryEnemies);
				dbo.put("allEnemies", allEnemies);
				dbo.put("requestedAllies", requestedAllies);
				dbo.put("invitedPlayers", invitedPlayers);
				dbo.put("claims", claims);
				dbo.put("banners", banners);
				dbo.put("custom_banners", customBanners);

				try
				{
					UpdateResult result = pCollection.replaceOne(Filters.eq("uuid", house.getUuid().toString()), dbo, options);
					Bukkit.getLogger().log(Level.INFO, "[HardcoreSMP] Mongo has updated " + result.getModifiedCount() + " database entry.");
				}
				catch(MongoException exception)
				{
					Bukkit.getLogger().log(Level.SEVERE, "[HardcoreSMP] Mongo encountered an error whilst updating a database.");
				}
			}
		}
	}
}
