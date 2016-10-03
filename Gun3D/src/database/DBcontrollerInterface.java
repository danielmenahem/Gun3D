package database;

import java.sql.SQLException;
import java.util.ArrayList;

import Utilities.Event;

public interface DBcontrollerInterface {
	void insertEvent(String playerID, int gameID, int gameScore, Event event) throws SQLException;
	ArrayList<DBRecord> getEventsByPlayerID(String playerID) throws SQLException;
	ArrayList<DBRecord> getEventsByGameID(int gameID) throws SQLException;
	ArrayList<DBRecord> getAllEvents() throws SQLException;
	int getCurrentNumberOfGames();
	void deletePlayer(String playerID) throws SQLException;
	void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException;
}
