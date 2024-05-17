package com.bizarrealex.aether;

import com.bizarrealex.aether.events.BoardCreateEvent;
import com.bizarrealex.aether.scoreboard.Board;
import com.bizarrealex.aether.scoreboard.BoardAdapter;
import com.bizarrealex.aether.scoreboard.BoardEntry;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class Aether implements Listener 
{
	@Getter @Setter private JavaPlugin plugin;

	@Getter @Setter private AetherOptions options;

	@Getter BoardAdapter adapter;

	public Aether(JavaPlugin plugin, BoardAdapter adapter, AetherOptions options) {
		this.options = options;
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, (Plugin)plugin);
		setAdapter(adapter);
		run();
	}

	public Aether(JavaPlugin plugin, BoardAdapter adapter) 
	{
		this(plugin, adapter, AetherOptions.defaultOptions());
	}

	public Aether(JavaPlugin plugin) 
	{
		this(plugin, null, AetherOptions.defaultOptions());
	}

	private void run() 
	{
		(new BukkitRunnable() 
		{
			public void run() 
			{
				if (Aether.this.adapter == null)
					return; 
				for (Player player : Bukkit.getOnlinePlayers()) 
				{
					Board board = Board.getByPlayer(player);
					if (board != null) 
					{
						List<String> scores = Aether.this.adapter.getScoreboard(player, board, board.getCooldowns());
						List<String> translatedScores = new ArrayList<>();
						if (scores == null) 
						{
							if (!board.getEntries().isEmpty()) 
							{
								for (BoardEntry boardEntry : board.getEntries())
									boardEntry.remove(); 
								board.getEntries().clear();
							} 
							continue;
						} 
						for (String line : scores)
							translatedScores.add(ChatColor.translateAlternateColorCodes('&', line)); 
						if (!Aether.this.options.scoreDirectionDown())
							Collections.reverse(scores); 
						Scoreboard scoreboard = board.getScoreboard();
						Objective objective = board.getObjective();
						if (!objective.getDisplayName().equals(Aether.this.adapter.getTitle(player)))
							objective.setDisplayName(ChatColor.translateAlternateColorCodes('&', Aether.this.adapter.getTitle(player))); 
						int i;
						label74: for (i = 0; i < scores.size(); i++) 
						{
							int position;
							String text = scores.get(i);
							if (Aether.this.options.scoreDirectionDown()) 
							{
								position = 47 - i;
							}
							else 
							{
								position = i + 1;
							} 
							Iterator<BoardEntry> iterator = (new ArrayList<>(board.getEntries())).iterator();
							while (iterator.hasNext()) 
							{
								BoardEntry boardEntry = iterator.next();
								Score score = objective.getScore(boardEntry.getKey());
								if (score != null && boardEntry.getText().equals(ChatColor.translateAlternateColorCodes('&', text)) && 
										score.getScore() == position)
									continue label74; 
							} 
							int positionToSearch = Aether.this.options.scoreDirectionDown() ? (47 - position) : (position - 1);
							iterator = board.getEntries().iterator();
							while (iterator.hasNext()) 
							{
								BoardEntry boardEntry = iterator.next();
								int entryPosition = scoreboard.getObjective(DisplaySlot.SIDEBAR).getScore(boardEntry.getKey()).getScore();
								if (!Aether.this.options.scoreDirectionDown() && 
										entryPosition > scores.size()) 
								{
									iterator.remove();
									boardEntry.remove();
								} 
							} 
							BoardEntry entry = board.getByPosition(positionToSearch);
							if (entry == null) 
							{
								(new BoardEntry(board, text)).send(position);
							} 
							else 
							{
								entry.setText(text).setup().send(position);
							} 
							if (board.getEntries().size() > scores.size()) 
							{
								iterator = board.getEntries().iterator();
								while (iterator.hasNext()) 
								{
									BoardEntry boardEntry = iterator.next();
									if (!translatedScores.contains(boardEntry.getText()) || Collections.frequency(board.getBoardEntriesFormatted(), boardEntry.getText()) > 1) 
									{
										iterator.remove();
										boardEntry.remove();
									} 
								} 
							} 
						} 
						player.setScoreboard(scoreboard);
					} 
				} 
			}
		}).runTaskTimerAsynchronously((Plugin)this.plugin, 20L, 2L);
	}

	public void setAdapter(BoardAdapter adapter) 
	{
		this.adapter = adapter;
		for (Player player : Bukkit.getOnlinePlayers()) 
		{
			Board board = Board.getByPlayer(player);
			if (board != null)
				Board.getBoards().remove(board); 
			Bukkit.getPluginManager().callEvent((Event)new BoardCreateEvent(new Board(player, this, this.options), player));
		} 
	}

	@EventHandler
	public void onPlayerJoinEvent(PlayerJoinEvent event) 
	{
		if (Board.getByPlayer(event.getPlayer()) == null)
			Bukkit.getPluginManager().callEvent((Event)new BoardCreateEvent(new Board(event.getPlayer(), this, this.options), event.getPlayer())); 
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerQuitEvent(PlayerQuitEvent event) 
	{
		Board board = Board.getByPlayer(event.getPlayer());
		if (board != null)
			Board.getBoards().remove(board); 
	}
}
