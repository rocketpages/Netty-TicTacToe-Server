package com.tictactoe.server.message;

public class GameOverMessageBean extends MessageBean {
	public enum Result {
		YOU_WIN, TIED
	}
	
	private final String type = "game_over";
	private Result result;
	
	public GameOverMessageBean(Result r) {
		result = r;
	}
	
	public String getType() {
		return type;
	}
	
	public Result getResult() {
		return result;
	}
}
