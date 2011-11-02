package com.tictactoe.server.message;

public class TurnMessageBean extends MessageBean {
	
	public enum Turn {
		WAITING, YOUR_TURN
	}
	
	private final String type = "turn";
	private Turn turn;

	public TurnMessageBean(Turn t) {
		turn = t;
	}

	public String getType() {
		return type;
	}
	
	public Turn getTurn() {
		return turn;
	}	
	
}
