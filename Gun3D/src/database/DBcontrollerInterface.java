package database;

import java.sql.SQLException;
import java.util.ArrayList;

import Utilities.EventType;

/**
 * Interface to declare the methods required , and their signatures, for the project's DB view
 * @author Micha
 * @author Daniel
 */
public interface DBcontrollerInterface {
	
	/*
	 * General queries
	 */
	
	/**
	 * Insert an event to the DB
	 * @param playerID		player's name, string inserted by the user
	 * @param gameID		game's ID. To insert, get current number of games and increment it by 1 to avoid collisions 
	 * @param gameScore		game's score at the moment of the event
	 * @param event			event type ({@link EventType} options: HIT, MISS, END_GAME)
	 * @throws SQLException	DB SQL exceptions
	 */
	void insertEvent(String playerID, int gameID, int gameScore, EventType event) throws SQLException;
	
	/**
	 * Select all events by the player's name
	 * @param playerID		player's name, string inserted by the user
	 * @return 				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getEventsByPlayerID(String playerID) throws SQLException;
	
	/**
	 * Select all events by the game's ID
	 * @param gameID 		game's ID 
	 * @return 				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getEventsByGameID(int gameID) throws SQLException;
	
	/**
	 * Select all events in the DB
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAllEvents() throws SQLException;
	
	/**
	 * Select all <b>games</b> by player's name
	 * @param playerID		player's name, string inserted by the user
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAllGamesByPlayerID(String playerID) throws SQLException;
	
	/**
	 * Get all players' names
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException	DB SQL exceptions
	 */
	ArrayList<Record> getPlayers() throws SQLException;
	
	/**
	 * Get current number of games
	 * @return				number of current games in the DB. New game's ID should be +1 of this number.
	 * @throws SQLException	DB SQL exceptions
	 */
	int getCurrentNumberOfGames() throws SQLException;
	
	/**
	 * Delete a player by his name
	 * @param playerID		player's name, string inserted by the user
	 * @throws SQLException DB SQL exceptions
	 */
	void deletePlayer(String playerID) throws SQLException;
	
	/**
	 * Change a player's name
	 * @param oldPlayerID	old player's name
	 * @param newPlayerID	new player's name
	 * @throws SQLException DB SQL exceptions
	 */
	void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException;
	
	/**
	 * Close connection
	 */
	void closeConnection();
	
	/*
	 *  Specific requested queries
	 */
	
	/**
	 * Select all the events, sorted by name and then by time
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAllEventsByNameAndThenByTimeDescending() throws SQLException;
	
	/**
	 * Select all the events, sorted by name and then by score, descending
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAllEventsByNameAndThenByScoreDescending() throws SQLException;
	
	/**
	 * Select all the events, sorted by game score, descending
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAllEventsByGameScoreDescending() throws SQLException;
	
	/**
	 * Get the average scores of players who have more than a number of games, sorted by score, descending
	 * @param minGames		the minimum number of games of players to show
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAverageScoresOfPlayersWithXGamesOrMoreDescending(int minGames) throws SQLException;
	
	/**
	 * Get all the games, sorted by number of events of a certain type, descending
	 * @param event			type of event to count, ({@link EventType} options: MISS, HIT. END_GAME is not relevant for this query (1 per game)).
	 * @return				{@link ArrayList} of {@link Record} selected in the DB
	 * @throws SQLException DB SQL exceptions
	 */
	ArrayList<Record> getAllGamesByMostEventsDescending(EventType event) throws SQLException;
	
}
