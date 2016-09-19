package database;

import java.time.LocalDateTime;

public class DBRecord {
	private String playerID;
	private String eventType;
	private LocalDateTime timeStamp;
	
	public DBRecord() {
	}

	public DBRecord(String playerID, String eventType, LocalDateTime timeStamp) {
		this.playerID = playerID;
		this.eventType = eventType;
		this.timeStamp = timeStamp;
	}

	public String getPlayerID() {
		return playerID;
	}

	public void setPlayerID(String playerID) {
		this.playerID = playerID;
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
		return "DBRecord [playerID=" + playerID + ", eventType=" + eventType + ", timeStamp=" + timeStamp + "]";
	}
	
	
	
}
