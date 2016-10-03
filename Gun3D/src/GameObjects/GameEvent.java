package GameObjects;
import Utilities.Event;

public class GameEvent {
	private String name;
	private int gameID;
	private Event event;
	private int gameScore;
	
	public GameEvent(String name, int gameID, Event event, int gameScore) {
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
	
	public Event getEvent() {
		return this.event;
	}
	
	public void setEvent(Event event) {
		this.event = event;
	}
	
	public int getGameScore() {
		return gameScore;
	}
	
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
