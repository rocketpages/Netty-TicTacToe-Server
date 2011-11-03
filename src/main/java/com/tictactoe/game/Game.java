package com.tictactoe.game;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents a game of Tic Tac Toe. Contains players and a game board.
 * 
 * @author Kevin Webber
 */
public class Game {
	
	private static int GAME_COUNT = 0;
	
	public enum Status {
	    WAITING, IN_PROGRESS, WON, TIED
	}
	
	public enum PlayerLetter { 
		X, O 
	}
	
	// The game ID. The server increments this count with each new game initiated.
	private final int id;
	
	// Status of the current game (WAITING, IN_PROGRESS, FINISHED)
	private Status status;
	
	private final Board board;	
	private Map<PlayerLetter, Player> players;
	private PlayerLetter winner;

	public Game() {
		this.id = GAME_COUNT++;
		this.board = new Board();
		status = Status.WAITING;
		players = new HashMap<PlayerLetter, Player>();
	}
	
	/**
	 * Adds a player to this game. Changes status of game from WAITING to IN_PROGRESS if the game fills up.
	 * 
	 * @param p
	 * @return
	 * @throws RuntimeException if there are already 2 or more players assigned to this game. 
	 */
	public PlayerLetter addPlayer(Player p) {
		if (players.size() >= 2) {
			throw new RuntimeException("Too many players. Cannot add more than 1 player to a game.");
		}
		
		PlayerLetter l = (players.containsKey(PlayerLetter.X)) ? PlayerLetter.O : PlayerLetter.X;
		p.setLetter(l);
		players.put(l, p);
		
		if (players.size() == 2) {
			status = Status.IN_PROGRESS;
		}
		
		return l;
	}
	
	/**
	 * Marks the selected cell of the user and updates the game's status.
	 * 
	 * @param gridId
	 * @param playerLetter
	 */
	public void markCell(int gridId, PlayerLetter playerLetter) {
		board.markCell(gridId, playerLetter);
		setStatus(playerLetter);
	}
	
	/**
	 * Updates the status of the game. Invoked after each player's turn.
	 * 
	 * @param playerLetter
	 */
	private void setStatus(PlayerLetter playerLetter) {		
		// Checks first to see if the board has a winner.
		if (board.isWinner(playerLetter)) {
			status = Status.WON;
			
			if (playerLetter == PlayerLetter.X) {
				winner = PlayerLetter.X;
			} else {
				winner = PlayerLetter.O;
			}
		// Next check to see if the game has been tied.	
		} else if (board.isTied()) {
			status = Status.TIED;
		}
	}
	
	public int getId() {
		return id;
	}
	
	public Collection<Player> getPlayers() {
		return players.values();
	}
	
	public Player getPlayer(PlayerLetter playerLetter) {
		return players.get(playerLetter);
	}
	
	/**
	 * Returns the opponent given a player letter.
	 */
	public Player getOpponent(String currentPlayer) {
		PlayerLetter currentPlayerLetter = PlayerLetter.valueOf(currentPlayer);
		PlayerLetter opponentPlayerLetter = currentPlayerLetter.equals(PlayerLetter.X) ? PlayerLetter.O : PlayerLetter.X;
		return players.get(opponentPlayerLetter);
	}	
	
	public Board getBoard() {
		return board;
	}
	
	public Status getStatus() {
		return status;
	}	
	
	public PlayerLetter getWinner() {
		return winner;
	}
	
	/**
	 * Convenience method to determine if a specific player is the winner.
	 */
	public boolean isPlayerWinner(PlayerLetter playerLetter) {
		if (status == Status.WON && winner == playerLetter) {
			return true;
		}
		
		return false;
	}

	/**
	 * Convenience method to determine if the game has been tied.
	 */	
	public boolean isTied() {
		if (status == Status.TIED) {
			return true;
		}
		
		return false;
	}
}
