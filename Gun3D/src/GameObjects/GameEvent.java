package GameObjects;

import java.io.Serializable;

import Utilities.EventType;

/**
 * A game event class
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 * 
 *          JavaDoc made under the assumption that the HTML generated doc will
 *          include private fields. Under different circumstances, the private
 *          attributes would be documented in the public getters, and the rest
 *          would link there. Private attributes documentation was made so
 *          everything will be documented.
 * 
 */
public class GameEvent implements Serializable {
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
	private EventType eventType;

	/**
	 * current game score at the time of the event.
	 */
	private int gameScore;

	/**
	 * Public constructor to create a game event
	 * 
	 * @param playerName
	 *            {@link GameEvent#playerName}
	 * @param gameID
	 *            {@link GameEvent#gameID}
	 * @param eventType
	 *            {@link GameEvent#eventType}
	 * @param gameScore
	 *            {@link GameEvent#gameScore}
	 */
	public GameEvent(String playerName, int gameID, EventType eventType, int gameScore) {
		this.playerName = playerName;
		this.gameID = gameID;
		this.eventType = eventType;
		this.gameScore = gameScore;
	}

	/**
	 * @return {@link GameEvent#playerName}
	 */
	public String getPlayerName() {
		return playerName;
	}

	/**
	 * @param name
	 *            {@link GameEvent#playerName}
	 */
	public void setPlayerName(String name) {
		this.playerName = name;
	}

	/**
	 * @return {@link GameEvent#gameID}
	 */
	public int getGameID() {
		return gameID;
	}

	/**
	 * @param gameID
	 *            {@link GameEvent#gameID}
	 */
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	/**
	 * @return {@link GameEvent#eventType}
	 */
	public EventType getEvent() {
		return this.eventType;
	}

	/**
	 * @param event
	 *            {@link GameEvent#eventType}
	 */
	public void setEvent(EventType event) {
		this.eventType = event;
	}

	/**
	 * @return {@link GameEvent#gameScore}
	 */
	public int getGameScore() {
		return gameScore;
	}

	/**
	 * @param gameScore
	 *            {@link GameEvent#gameScore}
	 */
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
