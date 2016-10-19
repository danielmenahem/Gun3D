package Database;

import java.time.LocalDateTime;

import GameObjects.EventType;

/**
 * Database record
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
public class Record {
	/**
	 * Player's ID that is the player's name of the event associated to this
	 * record.
	 */
	private String playerID;
	/**
	 * Game's ID of the event associated to this record.
	 */
	private int gameID;
	/**
	 * Score at the time of the event associated to this record.
	 */
	private int score;
	/**
	 * {@link EventType} of the event associated to this record.
	 * 
	 */
	private EventType event;
	/**
	 * Generated time stamp of the event associated to this record.
	 */
	private LocalDateTime timeStamp;

	/**
	 * Default empty constructor.
	 */
	public Record() {
	}

	/**
	 * A public constructor for a database record.
	 * 
	 * @param playerID
	 *            {@link Record#playerID}
	 * @param gameID
	 *            {@link Record#gameID}
	 * @param score
	 *            {@link Record#score}
	 * @param event
	 *            {@link Record#event}
	 * @param timeStamp
	 *            {@link Record#timeStamp}
	 */
	public Record(String playerID, int gameID, int score, EventType event, LocalDateTime timeStamp) {
		this.playerID = playerID;
		this.gameID = gameID;
		this.score = score;
		this.event = event;
		this.timeStamp = timeStamp;
	}

	/**
	 * @return {@link Record#score}
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @param score
	 *            {@link Record#score}
	 */
	public void setGameScore(int score) {
		this.score = score;
	}

	/**
	 * @return {@link Record#playerID}
	 */
	public String getPlayerID() {
		return playerID;
	}

	/**
	 * @param playerID
	 *            {@link Record#playerID}
	 */
	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	/**
	 * @return {@link Record#gameID}
	 */
	public int getGameID() {
		return gameID;
	}

	/**
	 * @param gameID
	 *            {@link Record#gameID}
	 */
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	/**
	 * @return {@link Record#event}
	 */
	public EventType getEvent() {
		return event;
	}

	/**
	 * @param event
	 *            {@link Record#event}
	 */
	public void setEventType(EventType event) {
		this.event = event;
	}

	/**
	 * @return {@link Record#timeStamp}
	 */
	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp
	 *            {@link Record#timeStamp}
	 */
	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

}
