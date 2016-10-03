package database;

import java.sql.SQLException;
import java.util.ArrayList;

import Utilities.Event;

public interface DBcontrollerInterface {
	void insertEvent(String playerID, int gameID, int gameScore, Event event) throws SQLException;
	ArrayList<Record> getEventsByPlayerID(String playerID) throws SQLException;
	ArrayList<Record> getEventsByGameID(int gameID) throws SQLException;
	ArrayList<Record> getAllEvents() throws SQLException;
	int getCurrentNumberOfGames();
	void deletePlayer(String playerID) throws SQLException;
	void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException;
}
