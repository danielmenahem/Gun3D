package GameObjects;
import Utilities.EventType;

public class GameEvent {
	private String name;
	private int gameID;
	private EventType event;
	private int gameScore;
	
	public GameEvent(String name, int gameID, EventType event, int gameScore) {
		this.name = name;
		this.gameID = gameID;
		this.event = event;
		this.gameScore = gameScore;
	}
	
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getGameID() {
		return gameID;
	}
	
	public void setGameID(int gameID) {
		this.gameID = gameID;
	}
	
	public EventType getEvent() {
		return this.event;
	}
	
	public void setEvent(EventType event) {
		this.event = event;
	}
	
	public int getGameScore() {
		return gameScore;
	}
	
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
