package com.tictactoe.server.message;

public class OutgoingMessageBean extends MessageBean {
	private final String type = "response";
	private String opponent;
	private String gridId;
	private boolean winner;
	private boolean tied;
	
	public OutgoingMessageBean(String opponent, String grid, boolean winner, boolean tied) {
		this.opponent = opponent;
		this.gridId = grid;
		this.winner = winner;
		this.tied = tied;
	}
	
	public String getType() {
		return type;
	}
	
	public String getOpponent() {
		return opponent;
	}

	public String getGrid() {
		return gridId;
	}
	
	public boolean getWinner() {
		return winner;
	}
	
	public boolean getTied() {
		return tied;
	}
}
