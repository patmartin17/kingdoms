package com.rivensoftware.hardcoresmp;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.ipvp.canvas.MenuFunctionListener;

import com.bizarrealex.aether.Aether;
import com.bizarrealex.aether.AetherOptions;
import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.mongodb.MongoClient;
import com.mongodb.client.model.UpdateOptions;
import com.rivensoftware.hardcoresmp.addons.AddonsManager;
import com.rivensoftware.hardcoresmp.addons.combatlogger.CombatLoggerListeners;
import com.rivensoftware.hardcoresmp.addons.deathlookup.DeathLookupCommand;
import com.rivensoftware.hardcoresmp.addons.deathlookup.DeathLookupListeners;
import com.rivensoftware.hardcoresmp.addons.inventory.CloneInventoryCommand;
import com.rivensoftware.hardcoresmp.addons.inventory.GiveInventoryCommand;
import com.rivensoftware.hardcoresmp.addons.inventory.LastInventoryCommand;
import com.rivensoftware.hardcoresmp.addons.mobstack.MobStackListeners;
import com.rivensoftware.hardcoresmp.commands.DepositCommand;
import com.rivensoftware.hardcoresmp.commands.WithdrawCommand;
import com.rivensoftware.hardcoresmp.event.capturepoint.CapturePointListeners;
import com.rivensoftware.hardcoresmp.event.capturepoint.commands.CapturePointCommand;
import com.rivensoftware.hardcoresmp.event.capturepoint.commands.CapturePointStartCommand;
import com.rivensoftware.hardcoresmp.event.capturepoint.commands.CapturePointStopCommand;
import com.rivensoftware.hardcoresmp.event.procedure.CapturePointCreateProcedureListeners;
import com.rivensoftware.hardcoresmp.event.procedure.command.CapturePointCreateProcedureCommand;
import com.rivensoftware.hardcoresmp.house.House;
import com.rivensoftware.hardcoresmp.house.banner.BannerCreationListeners;
import com.rivensoftware.hardcoresmp.house.banner.BannerEffect;
import com.rivensoftware.hardcoresmp.house.banner.BannerListeners;
import com.rivensoftware.hardcoresmp.house.banner.BannerType;
import com.rivensoftware.hardcoresmp.house.banner.BattleListeners;
import com.rivensoftware.hardcoresmp.house.banner.effects.HeldBannerNegativeEffects;
import com.rivensoftware.hardcoresmp.house.banner.effects.HeldBannerPositiveEffects;
import com.rivensoftware.hardcoresmp.house.banner.effects.WallBannerNegativeEffects;
import com.rivensoftware.hardcoresmp.house.banner.effects.WallBannerPositiveEffects;
import com.rivensoftware.hardcoresmp.house.claim.ClaimListeners;
import com.rivensoftware.hardcoresmp.house.claim.ClaimPillar;
import com.rivensoftware.hardcoresmp.house.commands.HouseChatCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseCreateCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseDepositCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseHomeCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseInvitesCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseJoinCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseLeaveCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseListCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseLivesCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseMapCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseMessageCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseShowCommand;
import com.rivensoftware.hardcoresmp.house.commands.HouseVersionCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseAllyCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseAnnouncementCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseBannerCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseClaimCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseEnemyCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseInviteCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseKickCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseNeutralCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseRenameCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseSetHomeCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseUnclaimCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseUninviteCommand;
import com.rivensoftware.hardcoresmp.house.commands.captain.HouseWithdrawCommand;
import com.rivensoftware.hardcoresmp.house.commands.lord.HouseDisbandCommand;
import com.rivensoftware.hardcoresmp.house.commands.lord.HouseLordCommand;
import com.rivensoftware.hardcoresmp.house.commands.lord.HousePromoteCommand;
import com.rivensoftware.hardcoresmp.house.listeners.ChatListeners;
import com.rivensoftware.hardcoresmp.house.listeners.ScoreboardListeners;
import com.rivensoftware.hardcoresmp.house.taxes.TaxTask;
import com.rivensoftware.hardcoresmp.messages.MessageHandler;
import com.rivensoftware.hardcoresmp.profile.Profile;
import com.rivensoftware.hardcoresmp.profile.ProfileListeners;
import com.rivensoftware.hardcoresmp.profile.admin.Admin;
import com.rivensoftware.hardcoresmp.profile.admin.AdminListeners;
import com.rivensoftware.hardcoresmp.profile.admin.commands.AdminCommand;
import com.rivensoftware.hardcoresmp.profile.admin.commands.VanishCommand;
import com.rivensoftware.hardcoresmp.profile.cooldowns.ProfileCooldownListeners;
import com.rivensoftware.hardcoresmp.profile.fight.ProfileFightListeners;
import com.rivensoftware.hardcoresmp.profile.lives.commands.LifeCommands;
import com.rivensoftware.hardcoresmp.profile.protection.ProfileProtection;
import com.rivensoftware.hardcoresmp.profile.protection.ProfileProtectionListeners;
import com.rivensoftware.hardcoresmp.profile.protection.commands.ProfileProtectionCommand;
import com.rivensoftware.hardcoresmp.profile.protection.commands.subcommands.ProfileProtectionEnableCommand;
import com.rivensoftware.hardcoresmp.profile.protection.commands.subcommands.ProfileProtectionGiveCommand;
import com.rivensoftware.hardcoresmp.profile.protection.commands.subcommands.ProfileProtectionTimeCommand;
import com.rivensoftware.hardcoresmp.scoreboard.KingdomsBoardAdapter;
import com.rivensoftware.hardcoresmp.storage.database.KingdomsDatabase;
import com.rivensoftware.hardcoresmp.storage.file.ConfigFile;
import com.rivensoftware.hardcoresmp.tools.MessageTool;
import com.rivensoftware.hardcoresmp.tools.player.PlayerTool;
import com.rivensoftware.hardcoresmp.tools.player.SimpleOfflinePlayer;

import co.aikar.commands.PaperCommandManager;
import lombok.Getter;
import lombok.Setter;
import net.milkbowl.vault.economy.Economy;

public class HardcoreSMP extends JavaPlugin
{
	@Getter @Setter private static boolean loaded;
	@Getter public static HardcoreSMP instance;

	@Getter private PaperCommandManager commandManager;
	@Getter private MongoClient client;
    @Getter private KingdomsDatabase kingdomsDatabase;
	@Getter private ConfigFile mainConfig;
	@Getter private ConfigFile houseConfig;
	@Getter private ConfigFile messageConfig;
	@Getter private ConfigFile languageConfig;
	@Getter private ConfigFile scoreboardConfig;
	@Getter private TaxTask taxTask;
	@Getter private PluginManager pluginManager;
	@Getter private Economy economy;
	@Getter private UpdateOptions options;

	@Override
	public void onEnable()
	{
		/*
		 * Instantiate variables and plugin instance.
		 */
		instance = this;
		this.pluginManager = Bukkit.getPluginManager();
		this.commandManager = new PaperCommandManager(this);
		this.kingdomsDatabase = new KingdomsDatabase(this);
		this.taxTask = new TaxTask();
	    this.mainConfig = new ConfigFile(this, "config");
	    this.houseConfig = new ConfigFile(this, "houses");
	    this.messageConfig = new ConfigFile(this, "messages");
	    this.languageConfig = new ConfigFile(this, "en_US");
	    this.scoreboardConfig = new ConfigFile(this, "scoreboard");
		this.economy = (Economy)Bukkit.getServer().getServicesManager().getRegistration(Economy.class).getProvider();
		this.options = new UpdateOptions().upsert(true);
		new Aether(this, (BoardAdapter)new KingdomsBoardAdapter(), (new AetherOptions()).hook(true));

		/*
		 * Load all simple offline players.
		 */
		SimpleOfflinePlayer.load(this);
		
		/*
		 * Executes registry methods for commands, listeners, profiles, and available managers
		 */
		registerProfileListeners();
		registerHouseListeners();
		registerHouseCommands();
		registerMenuFunction();
		registerListeners();
		registerCommands();

		/*
		 * Loads all player profiles that are currently online when onEnable runs
		 */
		loadProfiles();
		loadAdminProfileSettings();
		loadBannerEffects();
		
		/*
		 * Loads final house runnables and needed information.
		 */
		House.load();
		ProfileProtection.run(this);
		new AddonsManager(this);
		new MessageHandler(this);
		BannerEffect.run();
	}

	@Override
	public void onDisable()
	{	
		House.save(options);

		for(Player players : Bukkit.getOnlinePlayers())
		{
			Board board = Board.getByPlayer(players);
			if (board != null)
			{
				board.getObjective().unregister();
				Board.getBoards().remove(board); 	
			}
		}
		
	    for (Player player : PlayerTool.getOnlinePlayers()) 
	    {
	        Profile profile = Profile.getByUUID(player.getUniqueId());
	        profile.save(options);
	        
	        if (profile.getClaimProfile() != null)
	          profile.getClaimProfile().removePillars(); 
	        
	        for (ClaimPillar claimPillar : profile.getMapPillars())
	          claimPillar.remove(); 
	        
	        player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
	      } 

		try 
		{
			SimpleOfflinePlayer.save(this);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
		kingdomsDatabase.getClient().close();
	}
	
	/*
	 * Register any miscellaneous listeners as well as calls any other register listener method (Main listener registrator)
	 */
	private void registerListeners()
	{	
		pluginManager.registerEvents(new ClaimListeners(), this);

		pluginManager.registerEvents(new ChatListeners(), this);
		pluginManager.registerEvents(new ScoreboardListeners(), this);
		pluginManager.registerEvents(new DeathLookupListeners(), this);
		pluginManager.registerEvents(new MobStackListeners(), this);
		pluginManager.registerEvents(new CombatLoggerListeners(this), this);
		
		pluginManager.registerEvents(new BattleListeners(), this);
		pluginManager.registerEvents(new BannerListeners(), this);
		pluginManager.registerEvents(new BannerCreationListeners(), this);

		pluginManager.registerEvents(new CapturePointListeners(), this);
		pluginManager.registerEvents(new CapturePointCreateProcedureListeners(), this);
	}

	/*
	 * Registers the command framework and all necessary commands
	 */
	private void registerCommands()
	{	
		commandManager.registerCommand(new AdminCommand());
		commandManager.registerCommand(new VanishCommand());
	}

	private void registerHouseCommands()
	{
		commandManager.registerCommand(new HouseCommand());
		commandManager.registerCommand(new HouseChatCommand());
		commandManager.registerCommand(new HouseDepositCommand());
		commandManager.registerCommand(new HouseCreateCommand());
		commandManager.registerCommand(new HouseClaimCommand());
		commandManager.registerCommand(new HouseMapCommand());
		commandManager.registerCommand(new HouseHomeCommand());
		commandManager.registerCommand(new HouseSetHomeCommand());
		commandManager.registerCommand(new HouseInviteCommand());
		commandManager.registerCommand(new HouseInvitesCommand());
		commandManager.registerCommand(new HouseJoinCommand());
		commandManager.registerCommand(new HouseLeaveCommand());
		commandManager.registerCommand(new HouseListCommand());
		commandManager.registerCommand(new HouseMessageCommand());
		commandManager.registerCommand(new HouseShowCommand());
		commandManager.registerCommand(new HouseVersionCommand());
		commandManager.registerCommand(new HouseAllyCommand());
		commandManager.registerCommand(new HouseAnnouncementCommand());
		commandManager.registerCommand(new HouseEnemyCommand());
		commandManager.registerCommand(new HouseNeutralCommand());
		commandManager.registerCommand(new HouseKickCommand());
		commandManager.registerCommand(new HouseRenameCommand());
		commandManager.registerCommand(new HouseUnclaimCommand());
		commandManager.registerCommand(new HouseUninviteCommand());
		commandManager.registerCommand(new HouseWithdrawCommand());
		commandManager.registerCommand(new HouseDisbandCommand());
		commandManager.registerCommand(new HouseLordCommand());
		commandManager.registerCommand(new HousePromoteCommand());
		commandManager.registerCommand(new HouseBannerCommand());
		commandManager.registerCommand(new HouseLivesCommand());

		commandManager.registerCommand(new CapturePointCommand());
		commandManager.registerCommand(new CapturePointStartCommand());
		commandManager.registerCommand(new CapturePointStopCommand());
		commandManager.registerCommand(new CapturePointCreateProcedureCommand());
		
		commandManager.registerCommand(new ProfileProtectionCommand());
		commandManager.registerCommand(new ProfileProtectionEnableCommand());
		commandManager.registerCommand(new ProfileProtectionTimeCommand());
		commandManager.registerCommand(new ProfileProtectionGiveCommand());
		
		commandManager.registerCommand(new LastInventoryCommand());
		commandManager.registerCommand(new GiveInventoryCommand());
		commandManager.registerCommand(new CloneInventoryCommand());
		commandManager.registerCommand(new DeathLookupCommand());
		
		commandManager.registerCommand(new LifeCommands());
		
		commandManager.registerCommand(new DepositCommand());
		commandManager.registerCommand(new WithdrawCommand());
	}

	/*
	 * Registers all listeners pertaining to the Profile class.
	 */
	private void registerProfileListeners()
	{	
		pluginManager.registerEvents(new ProfileListeners(), this);
		pluginManager.registerEvents(new ProfileCooldownListeners(), this);
		pluginManager.registerEvents(new ProfileProtectionListeners(), this);
		pluginManager.registerEvents(new ProfileFightListeners(), this);

		pluginManager.registerEvents(new AdminListeners(), this);
	}

	private void registerHouseListeners()
	{
		pluginManager.registerEvents(new HouseChatCommand(), this);
	}

	/*
	 * Load all banner effects from enums and creates BannerEffect object for further searching of effects.
	 */
	private void loadBannerEffects()
	{
		for(HeldBannerNegativeEffects effects : HeldBannerNegativeEffects.values())
		{
			new BannerEffect(effects.getEffectName(), BannerType.HELD, true);
		}
		for(HeldBannerPositiveEffects effects : HeldBannerPositiveEffects.values())
		{
			new BannerEffect(effects.getEffectName(), BannerType.HELD, false);
		}
		for(WallBannerNegativeEffects effects : WallBannerNegativeEffects.values())
		{
			new BannerEffect(effects.getEffectName(), BannerType.WALL, true);
		}
		for(WallBannerPositiveEffects effects : WallBannerPositiveEffects.values())
		{
			new BannerEffect(effects.getEffectName(), BannerType.WALL, false);
		}
	}
	
	/*
	 * Loads player and administrator profiles into existence when the server loads up.
	 */
	private void loadProfiles()
	{
		for(Player players : Bukkit.getOnlinePlayers())
		{
			if(players.hasPermission("admin"))
			{
				Admin.loadAdminProfile(players);
				players.sendMessage(MessageTool.color("&9Administrator profile for " + players.getName() + " was loaded successfully."));
			}
			else
			{
				Profile.loadProfile(players);
				players.sendMessage(MessageTool.color("&aProfile was successfully loaded."));
			}
		}
	}

	/*
	 * Method to load admin mode and previous settings from file or from mongodb.
	 */
	private void loadAdminProfileSettings()
	{
		for(Player players : Bukkit.getOnlinePlayers())
		{
			Admin admin = Admin.getByPlayer(players);

			if(admin != null)
			{
				/*
				 * 				boolean adminMode = getAdminConfig().getBoolean(players.getUniqueId().toString() + ".admin_mode");
				boolean vanishMode = getAdminConfig().getBoolean(players.getUniqueId().toString() + ".vanish_mode");

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

	}

	/*
	 * Registers the iPvP Menu Function Listener
	 */
	private void registerMenuFunction()
	{
		Bukkit.getPluginManager().registerEvents(new MenuFunctionListener(), this);
	}
}