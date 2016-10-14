package database;

import java.sql.SQLException;
import java.util.ArrayList;

import Utilities.EventType;

public interface DBcontrollerInterface {
	
	// General queries
	
	void insertEvent(String playerID, int gameID, int gameScore, EventType event) throws SQLException;
	ArrayList<Record> getEventsByPlayerID(String playerID) throws SQLException;
	ArrayList<Record> getEventsByGameID(int gameID) throws SQLException;
	ArrayList<Record> getAllEvents() throws SQLException;
	ArrayList<Record> getAllGamesByPlayerID(String playerID) throws SQLException;
	ArrayList<Record> getPlayers() throws SQLException;
	int getCurrentNumberOfGames() throws SQLException;
	void deletePlayer(String playerID) throws SQLException;
	void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException;
	void closeConnection();
	
	// Specific requested queries
	
	ArrayList<Record> getAllEventsByNameAndThenByTimeDescending() throws SQLException;
	ArrayList<Record> getAllEventsByNameAndThenByScoreDescending() throws SQLException;
	ArrayList<Record> getAllEventsByGameScoreDescending() throws SQLException;
	ArrayList<Record> getAverageScoresOfPlayersWithXGamesOrMoreDescending(int minGames) throws SQLException;
	ArrayList<Record> getAllGamesByMostEventsDescending(EventType event) throws SQLException;
	
}
