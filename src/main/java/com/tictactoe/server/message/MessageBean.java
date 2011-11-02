package com.tictactoe.server.message;

import com.google.gson.Gson;

public class MessageBean {

	public String toJson() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}	
	
}
