package com.tictactoe.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Game {
	
	private static int GAME_COUNT = 0;
	
	public enum Status {
	    WAITING, IN_PROGRESS, FINISHED
	}
	
	public enum Letter { 
		X, O 
	}
	
	private final int id;
	private final Board board;
	private Status status;
	private Map<Letter, Player> players;	
	private Letter lastTurnBy;

	public Game() {
		this.id = GAME_COUNT++;
		this.board = new Board();
		status = Status.WAITING;
		players = new HashMap<Letter, Player>();
	}
	
	public int getId() {
		return id;
	}
	
	public Collection<Player> getPlayers() {
		return players.values();
	}
	
	public Player getPlayer(String letter) {
		Letter currentPlayerLetter = Letter.valueOf(letter);
		return players.get(currentPlayerLetter);
	}
	
	public Player getOpponent(String currentPlayer) {
		Letter currentPlayerLetter = Letter.valueOf(currentPlayer);
		Letter opponentPlayerLetter = currentPlayerLetter.equals(Letter.X) ? Letter.O : Letter.X;
		return players.get(opponentPlayerLetter);
	}
	
	/**
	 * Adds a player to this game. Changes status of game from WAITING to IN_PROGRESS if the game fills up.
	 * 
	 * @param p
	 * @return
	 * @throws RuntimeException if there are already 2 or more players assigned to this game. 
	 */
	public Letter addPlayer(Player p) {
		if (players.size() >= 2) {
			throw new RuntimeException("Too many players. Cannot add more than 1 player to a game.");
		}
		
		Letter l = (players.containsKey(Letter.X)) ? Letter.O : Letter.X;
		p.setLetter(l);
		players.put(l, p);
		
		if (players.size() == 2) {
			status = Status.IN_PROGRESS;
		}
		
		return l;
	}
	
	public Board getBoard() {
		return board;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public void setStatus(Status s) {
		status = s;
	}
	
	public Letter getLastTurnBy() {
		return lastTurnBy;
	}

	public void setLastTurnBy(Letter lastTurnBy) {
		this.lastTurnBy = lastTurnBy;
	}	
	
}
