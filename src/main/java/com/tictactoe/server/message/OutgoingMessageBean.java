package com.tictactoe.server.message;

public class OutgoingMessageBean extends MessageBean {
	private final String type = "response";
	private String opponent;
	private String gridId;
	private boolean winner;
	
	public OutgoingMessageBean(String opponent, String grid, boolean winner) {
		this.opponent = opponent;
		this.gridId = grid;
		this.winner = winner;
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
}
