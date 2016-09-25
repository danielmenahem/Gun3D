package GameObjects;

public class GameEvent {
	private String name;
	private int gameID;
	private String event;
	private int gameScore;
	
	public GameEvent(String name, int gameID, String event, int gameScore) {
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
	
	public String getEvent() {
		return this.event;
	}
	
	public void setEvent(String event) {
		this.event = event;
	}
	
	public int getGameScore() {
		return gameScore;
	}
	
	public void setGameScore(int gameScore) {
		this.gameScore = gameScore;
	}
}
