package com.tictactoe.game;

import org.jboss.netty.channel.Channel;

import com.tictactoe.game.Game.Letter;

public class Player {
	
	Channel channel;
	Letter letter;
	
	public Player(Channel c) {
		channel = c;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public void setLetter(Letter l) {
		letter = l;
	}
	
	public Letter getLetter() {
		return letter;
	}
}
