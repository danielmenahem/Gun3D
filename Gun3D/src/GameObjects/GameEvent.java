package GameObjects;

import java.io.Serializable;

import Utilities.EventType;

/**
 * A game event class
 * 
 * @author Micha
 * @author Daniel
 */
public class GameEvent implements Serializable{
	/**
	 * The value of static final long is {@value}.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Player's ID that is the player's name associated to this event
	 */
	private String playerName;

	/*
	 * Game's ID associated to this event.
	 */
	private int gameID;

	/**
	 * Event type of the event associated to this record. {@link EventType}
	 */
	private EventType event;
	private int gameScore;

	/**
	 * Public constructor to create a game event
	 * 
	 * @param playerName
	 *            player's name and ID
	 * @param gameID
	 *            game ID
	 * @param event
	 *            event type ({@link EventType} options: HIT, MISS, END_GAME)
	 * @param gameScore
	 *            game's current score
	 */
	public GameEvent(String playerName, int gameID, EventType event, int gameScore) {
		this.playerName = playerName;
		this.gameID = gameID;
		this.event = event;
		this.gameScore = gameScore;
	}

	/**
	 * Gets the event's player ID
	 * 
	 * @return the player's name
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * Sets the event's player ID
	 * 
	 * @param name
	 *            the event's new player's name
	 */
	public void setPlayerName(String name) {
		this.playerName = name;
	}

	/**
	 * Gets the event's game ID
	 * 
	 * @return game's ID
	 */
	public int getGameID() {
		return gameID;
	}

	/**
	 * Sets the event's game ID
	 * 
	 * @param gameID
	 *            the event's new game ID
	 */
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	/**
	 * Gets the event type
	 * 
	 * @return {@link EventType} options: MISS, HIT, END_GAME
	 */
	public EventType getEvent() {
		return this.event;
	}

	/**
	 * Sets the event's event type
	 * 
	 * @param event
	 *            new event type, {@link EventType} options: MISS, HIT, END_GAME
	 */
	public void setEvent(EventType event) {
		this.event = event;
	}

	/**
	 * Gets the event's game score
	 * 
	 * @return the game's score
	 */
	public int getGameScore() {
		return gameScore;
	}

	/**
	 * Sets the event's game score
	 * 
	 * @param gameScore
	 *            new event game score
	 */
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
