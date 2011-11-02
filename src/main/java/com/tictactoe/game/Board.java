package com.tictactoe.game;

import com.tictactoe.game.Game.Letter;

/**
 * A board for a game of Tic Tac Toe. Represents the current state of the game and which cells have been selected by whom. 
 * 
 * @author Kevin Webber
 */
public class Board {
	
	// The winning combinations of Tic Tac Toe are small, so we'll keep it simple and do "brute force" matching.
	public static final int[][] WINNING = { {1,2,3}, {4,5,6}, {7,8,9}, {1,4,7}, {2,5,8}, {3,6,9}, {1,5,9}, {3,5,7} };
	
	/*
	 * Represents a flattened game board for Tic Tac Toe. Below is the index value for each game cell.
	 * 
	 *    1 | 2 | 3
	 *    4 | 5 | 6
	 *    7 | 8 | 9
	 */
	Letter[] cells = new Letter[9];
	
	/**
	 * Mark a cell with the player's selection.
	 * 
	 * @param gridId
	 * @param player
	 */
	public void markCell(int gridId, Letter player) {
		cells[gridId-1] = player;
	}
	
	/**
	 * Compare the current state of the game board with the possible winning combinations to determine a win condition.
	 * This should be checked at the end of each turn.
	 * 
	 * @param player
	 * @return
	 */
	public boolean isWinner(Letter player) {
		for (int i = 0; i < WINNING.length; i++) {
			int[] possibleWinningCombo = WINNING[i];			
			if (cells[possibleWinningCombo[0]-1] == player && cells[possibleWinningCombo[1]-1] == player && cells[possibleWinningCombo[2]-1] == player) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determines if the game is tied. The game is considered tied if there is no winner and all cells have been selected.
	 */
	public boolean isTied() {
		boolean boardFull = true;
		boolean tied = false;
		
		for (int i = 0; i < 9; i++) {
			Letter letter = cells[i];
			if (letter == null) {
				boardFull = false;
			}
		}
		
		if (boardFull && (!isWinner(Letter.X) || !isWinner(Letter.O))) {
			tied = true;
		}
		
		return tied;
	}
}
