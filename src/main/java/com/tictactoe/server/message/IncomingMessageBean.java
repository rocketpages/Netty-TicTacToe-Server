package com.tictactoe.server.message;

public class IncomingMessageBean {
	private int gameId;
	private String player;
	private String gridId;
	
	public int getGameId() {
		return gameId;
	}
	public void setGameId(int gameId) {
		this.gameId = gameId;
	}
	public String getPlayer() {
		return player;
	}
	public void setPlayer(String player) {
		this.player = player;
	}
	public String getGridId() {
		return gridId;
	}
	public void setGridId(String gridId) {
		this.gridId = gridId;
	}
	public int getGridIdAsInt() {
		return Integer.valueOf(gridId.substring(gridId.length() - 1));
	}
}
