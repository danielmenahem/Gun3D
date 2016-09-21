package database;

import java.time.LocalDateTime;

public class DBRecord {
	private String playerID;
	private int gameID;
	private int gameScore;
	private String eventType;
	private LocalDateTime timeStamp;

	public DBRecord() {
	}

	public DBRecord(String playerID, int gameID, int gameScore, String eventType, LocalDateTime timeStamp) {
		this.playerID = playerID;
		this.gameID = gameID;
		this.gameScore = gameScore;
		this.eventType = eventType;
		this.timeStamp = timeStamp;
	}

	public int getGameScore() {
		return gameScore;
	}

	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
	}

	public int getGameID() {
		return gameID;
	}

	public void setGameID(int gameID) {
		this.gameID = gameID;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

	@Override
	public String toString() {
		return "DBRecord [playerID=" + playerID + ", gameID=" + gameID + ", gameScore=" + gameScore + ", eventType="
				+ eventType + ", timeStamp=" + timeStamp + "]";
	}


}
