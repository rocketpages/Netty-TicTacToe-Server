package com.tictactoe.game;

import org.jboss.netty.channel.Channel;

import com.tictactoe.game.Game.PlayerLetter;

/**
 * Represents a player for a game of Tic Tac Toe.
 * 
 * @author Kevin Webber
 */
public class Player {
	
	// The player's websocket channel. Used for communications.
	private Channel channel; 
	
	// The player's currently assigned letter.
	private PlayerLetter letter;
	
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
