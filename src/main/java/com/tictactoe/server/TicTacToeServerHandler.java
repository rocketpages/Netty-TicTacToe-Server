package com.tictactoe.server;

import static org.jboss.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.CONNECTION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.ORIGIN;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SEC_WEBSOCKET_KEY1;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SEC_WEBSOCKET_KEY2;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SEC_WEBSOCKET_LOCATION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SEC_WEBSOCKET_ORIGIN;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.SEC_WEBSOCKET_PROTOCOL;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.WEBSOCKET_LOCATION;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.WEBSOCKET_ORIGIN;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Names.WEBSOCKET_PROTOCOL;
import static org.jboss.netty.handler.codec.http.HttpHeaders.Values.WEBSOCKET;
import static org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1;
import static com.tictactoe.server.message.GameOverMessageBean.Result.YOU_WIN;
import static com.tictactoe.server.message.GameOverMessageBean.Result.TIED;
import static com.tictactoe.server.message.TurnMessageBean.Turn.YOUR_TURN;
import static com.tictactoe.server.message.TurnMessageBean.Turn.WAITING;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.DefaultHttpResponse;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpHeaders.Names;
import org.jboss.netty.handler.codec.http.HttpHeaders.Values;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.handler.codec.http.websocket.DefaultWebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrame;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameDecoder;
import org.jboss.netty.handler.codec.http.websocket.WebSocketFrameEncoder;
import org.jboss.netty.util.CharsetUtil;

import com.google.gson.Gson;
import com.tictactoe.game.Board;
import com.tictactoe.game.Game;
import com.tictactoe.game.Player;
import com.tictactoe.server.message.GameOverMessageBean;
import com.tictactoe.server.message.HandshakeMessageBean;
import com.tictactoe.server.message.IncomingMessageBean;
import com.tictactoe.server.message.OutgoingMessageBean;
import com.tictactoe.server.message.TurnMessageBean;

/**
 * Handles a server-side channel for a multiplayer game of Tic Tac Toe.
 * 
 * @author Kevin Webber
 * 
 */
public class TicTacToeServerHandler extends SimpleChannelUpstreamHandler {

	static Map<Integer, Game> games = new HashMap<Integer, Game>();

	private static final String WEBSOCKET_PATH = "/websocket";

	/* (non-Javadoc)
	 * @see org.jboss.netty.channel.SimpleChannelUpstreamHandler#messageReceived(org.jboss.netty.channel.ChannelHandlerContext, org.jboss.netty.channel.MessageEvent)
	 * 
	 * An incoming message (event). Invoked when either a:
	 * 
	 * - A player navigates to the page. The initial page load triggers an HttpRequest. We perform the WebSocket handshake 
	 * 		and assign them to a particular game.
	 * 
	 * - OR A player clicks on a tic tac toe square. The message contains who clicked 
	 * 		on which square (1 thru 9) and which game they're playing.
	 */
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e)
			throws Exception {
		Object msg = e.getMessage();
		if (msg instanceof HttpRequest) {
			handleHttpRequest(ctx, (HttpRequest) msg);
		} else if (msg instanceof WebSocketFrame) {
			handleWebSocketFrame(ctx, (WebSocketFrame) msg);
		}
	}

	/**
	 * Handles all HttpRequests. Must be a GET. Performs the WebSocket handshake 
	 * and assigns a player to a game.
	 * 
	 * @param ctx
	 * @param req
	 * @throws Exception
	 */
	private void handleHttpRequest(ChannelHandlerContext ctx, HttpRequest req)
			throws Exception {

		// Allow only GET methods.
		if (req.getMethod() != HttpMethod.GET) {
			sendHttpResponse(ctx, req, new DefaultHttpResponse(
					HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
			return;
		}

		// Serve the WebSocket handshake request.
		if (req.getUri().equals(WEBSOCKET_PATH)
				&& Values.UPGRADE.equalsIgnoreCase(req.getHeader(CONNECTION))
				&& WEBSOCKET.equalsIgnoreCase(req.getHeader(Names.UPGRADE))) {

			// Create the WebSocket handshake response.
			HttpResponse res = new DefaultHttpResponse(
					HTTP_1_1,
					new HttpResponseStatus(101, "Web Socket Protocol Handshake"));
			res.addHeader(Names.UPGRADE, WEBSOCKET);
			res.addHeader(CONNECTION, Values.UPGRADE);

			// Fill in the headers and contents depending on handshake method.
			// New handshake specification has a challenge.
			if (req.containsHeader(SEC_WEBSOCKET_KEY1)
					&& req.containsHeader(SEC_WEBSOCKET_KEY2)) {
				
				// New handshake method with challenge
				res.addHeader(SEC_WEBSOCKET_ORIGIN, req.getHeader(ORIGIN));
				res.addHeader(SEC_WEBSOCKET_LOCATION, getWebSocketLocation(req));
				String protocol = req.getHeader(SEC_WEBSOCKET_PROTOCOL);
				if (protocol != null) {
					res.addHeader(SEC_WEBSOCKET_PROTOCOL, protocol);
				}

				// Calculate the answer of the challenge.
				String key1 = req.getHeader(SEC_WEBSOCKET_KEY1);
				String key2 = req.getHeader(SEC_WEBSOCKET_KEY2);
				int a = (int) (Long.parseLong(key1.replaceAll("[^0-9]", "")) / key1
						.replaceAll("[^ ]", "").length());
				int b = (int) (Long.parseLong(key2.replaceAll("[^0-9]", "")) / key2
						.replaceAll("[^ ]", "").length());
				long c = req.getContent().readLong();
				ChannelBuffer input = ChannelBuffers.buffer(16);
				input.writeInt(a);
				input.writeInt(b);
				input.writeLong(c);
				ChannelBuffer output = ChannelBuffers
						.wrappedBuffer(MessageDigest.getInstance("MD5").digest(
								input.array()));
				res.setContent(output);
			} else {
				// Old handshake method with no challenge:
				res.addHeader(WEBSOCKET_ORIGIN, req.getHeader(ORIGIN));
				res.addHeader(WEBSOCKET_LOCATION, getWebSocketLocation(req));
				String protocol = req.getHeader(WEBSOCKET_PROTOCOL);
				if (protocol != null) {
					res.addHeader(WEBSOCKET_PROTOCOL, protocol);
				}
			}

			// Upgrade the connection and send the handshake response.
			ChannelPipeline p = ctx.getChannel().getPipeline();
			p.remove("aggregator");
			p.replace("decoder", "wsdecoder", new WebSocketFrameDecoder());

			// Write handshake response to the channel
			ctx.getChannel().write(res);
			
			// Upgrade encoder to WebSocketFrameEncoder
			p.replace("encoder", "wsencoder", new WebSocketFrameEncoder());
			
			// Initialize the game. Assign players to a game and assign them a letter (X or O)
			initGame(ctx);

			return;
		}

		// Send an error page otherwise.
		sendHttpResponse(ctx, req, new DefaultHttpResponse(
				HttpVersion.HTTP_1_1, HttpResponseStatus.FORBIDDEN));
	}
	
	/**
	 * Initializes a game. Finds an open game for a player (if another player is already waiting) or creates a new game.
	 * 
	 * @param ctx
	 */
	private void initGame(ChannelHandlerContext ctx) {
		// Try to find a game waiting for a player. If one doesn't exist, create a new one.
		Game game = findGame();
		
		// Create a new instance of player and assign their channel for WebSocket communications.
		Player player = new Player(ctx.getChannel());
		
		// Add the player to the game.
		Game.Letter letter = game.addPlayer(player);
		
		// Add the game to the collection of games.
		games.put(game.getId(), game);
		
		// Send confirmation message to player with game ID and their assigned letter (X or O) 
		ctx.getChannel().write(new DefaultWebSocketFrame(new HandshakeMessageBean(game.getId(), letter.toString()).toJson()));
		
		// If the game has begun we need to inform the players. Send them a "turn" message (either "waiting" or "your_turn")
		if (game.getStatus() == Game.Status.IN_PROGRESS) {			
			game.getPlayer("X").getChannel().write(new DefaultWebSocketFrame(new TurnMessageBean(YOUR_TURN).toJson()));
			game.getPlayer("O").getChannel().write(new DefaultWebSocketFrame(new TurnMessageBean(WAITING).toJson()));
		}
	}
	
	/**
	 * Finds an open game for a player (if another player is waiting) or creates a new game.
	 *
	 * @return Game
	 */
	private Game findGame() {		
		// Find an existing game and return it
		for (Game g : games.values()) {
			if (g.getStatus().equals(Game.Status.WAITING)) {
				return g;
			}
		}
		
		// Or return a new game
		return new Game();
	}

	/**
	 * Process turn data from players. Message contains which square they clicked on. Sends turn data to their
	 * opponent.
	 * 
	 * @param ctx
	 * @param frame
	 */
	private void handleWebSocketFrame(ChannelHandlerContext ctx,
			WebSocketFrame frame) {
		
		System.out.println(frame.getTextData());
		
		Gson gson = new Gson();
		IncomingMessageBean message = gson.fromJson(frame.getTextData(), IncomingMessageBean.class);
		
		Game game = games.get(message.getGameId());
		Player opponent = game.getOpponent(message.getPlayer());
		Player player = game.getPlayer(message.getPlayer());
		Board board = game.getBoard();
		
		// Mark the cell the player selected.
		board.markCell(message.getGridIdAsInt(), player.getLetter());
		
		// Check to see if the player just won or tied the game.		
		boolean winner = board.isWinner(player.getLetter());
		boolean tied = false;
		
		if (!winner && board.isTied()) {
			tied = true;
		}
		
		// Respond to the opponent in order to update their screen.
		String responseToOpponent = new OutgoingMessageBean(player.getLetter().toString(), message.getGridId(), winner, tied).toJson();		
		opponent.getChannel().write(new DefaultWebSocketFrame(responseToOpponent));
		
		// Respond to the player to let them know they won.
		if (winner) {
			player.getChannel().write(new DefaultWebSocketFrame(new GameOverMessageBean(YOU_WIN).toJson()));
		} else if (tied) {
			player.getChannel().write(new DefaultWebSocketFrame(new GameOverMessageBean(TIED).toJson()));
		}
		
		System.out.println(responseToOpponent);
	}

	private void sendHttpResponse(ChannelHandlerContext ctx, HttpRequest req,
			HttpResponse res) {
		// Generate an error page if response status code is not OK (200).
		if (res.getStatus().getCode() != 200) {
			res.setContent(ChannelBuffers.copiedBuffer(res.getStatus()
					.toString(), CharsetUtil.UTF_8));
		}

		// Send the response and close the connection if necessary.
		ChannelFuture f = ctx.getChannel().write(res);
		if (!isKeepAlive(req) || res.getStatus().getCode() != 200) {
			f.addListener(ChannelFutureListener.CLOSE);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e)
			throws Exception {
		e.getCause().printStackTrace();
		e.getChannel().close();
	}

	private String getWebSocketLocation(HttpRequest req) {
		return "ws://" + req.getHeader(HttpHeaders.Names.HOST) + WEBSOCKET_PATH;
	}
}