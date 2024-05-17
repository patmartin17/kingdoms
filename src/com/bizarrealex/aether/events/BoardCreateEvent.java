package com.bizarrealex.aether.events;

import com.bizarrealex.aether.scoreboard.Board;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BoardCreateEvent extends Event 
{
  private static final HandlerList handlers = new HandlerList();
  
  private final Board board;
  
  private final Player player;
  
  public Board getBoard() 
  {
    return this.board;
  }
  
  public Player getPlayer() 
  {
    return this.player;
  }
  
  public BoardCreateEvent(Board board, Player player) 
  {
    this.board = board;
    this.player = player;
  }
  
  public HandlerList getHandlers() 
  {
    return handlers;
  }
  
  public static HandlerList getHandlerList()
  {
    return handlers;
  }
}
