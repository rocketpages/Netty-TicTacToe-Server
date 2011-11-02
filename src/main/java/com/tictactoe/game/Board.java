package com.tictactoe.game;

import com.tictactoe.game.Game.Letter;

public class Board {
	
	public static final int[][] WINNING = { {1,2,3}, {4,5,6}, {7,8,9}, {1,4,7}, {2,5,8}, {3,6,9}, {1,5,9}, {3,5,7} };
	
	Letter[] cells = new Letter[9];
	
	public void markCell(int gridId, Letter player) {
		cells[gridId-1] = player;
	}
	
	public boolean isWinner(Letter player) {
		for (int i = 0; i < WINNING.length; i++) {
			int[] possibleWinningCombo = WINNING[i];			
			if (cells[possibleWinningCombo[0]-1] == player && cells[possibleWinningCombo[1]-1] == player && cells[possibleWinningCombo[2]-1] == player) {
				return true;
			}
		}
		
		return false;
	}
}
