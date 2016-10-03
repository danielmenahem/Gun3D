package database;

import java.time.LocalDateTime;

import Utilities.Event;

public class Record {
	
	private String playerID;
	private int gameID;
	private int score;
	private Event event;
	private LocalDateTime timeStamp;

	public Record() {
	}

	public Record(String playerID, int gameID, int score, Event event, LocalDateTime timeStamp) {
		this.playerID = playerID;
		this.gameID = gameID;
		this.score = score;
		this.event = event;
		this.timeStamp = timeStamp;
	}

	public int getScore() {
		return score;
	}

	public void setGameScore(int score) {
		this.score = score;
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

	public Event getEvent() {
		return event;
	}

	public void setEventType(Event event) {
		this.event = event;
	}

	public LocalDateTime getTimeStamp() {
		return timeStamp;
	}

	public void setTimeStamp(LocalDateTime timeStamp) {
		this.timeStamp = timeStamp;
	}

}
