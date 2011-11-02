package com.tictactoe.server.message;

public class WinnerMessageBean extends MessageBean {
	private final String type = "you_win";
	
	public String getType() {
		return type;
	}
}
