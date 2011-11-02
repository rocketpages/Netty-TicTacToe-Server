package com.tictactoe.game;

import org.jboss.netty.channel.Channel;

import com.tictactoe.game.Game.PlayerLetter;

public class Player {
	
	Channel channel;
	PlayerLetter letter;
	
	public Player(Channel c) {
		channel = c;
	}
	
	public Channel getChannel() {
		return channel;
	}
	
	public void setLetter(PlayerLetter l) {
		letter = l;
	}
	
	public PlayerLetter getLetter() {
		return letter;
	}
}
