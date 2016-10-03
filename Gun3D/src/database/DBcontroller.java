package database;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;

import Utilities.Event;

public class DBcontroller implements DBcontrollerInterface{
	private final String dbDriverName = "com.mysql.jdbc.Driver";
	private final String dbName = "jdbc:mysql://localhost/gun";
	private final String dbUsername = "scott";
	private final String dbPassword = "tiger";
	private final String tableName = "eventsTest505005"; // MICHA: change to events

	private static int numberOfGames;

	private Connection connection;
	private PreparedStatement stmt;

	public DBcontroller() throws Exception {
		connect();
		createTable();
		getCurrentNumberOfGames();
	}

	private void connect() throws Exception {
		Class.forName(dbDriverName);
		System.out.println("Driver loaded");
		connection = DriverManager.getConnection(dbName, dbUsername, dbPassword);
		System.out.println("Database connected");
	}

	private void createTable() {
		String events = "CREATE TABLE IF NOT EXISTS " + tableName + "(playerID VARCHAR(100)," + " gameID INT," + " gameScore INT,"
				+ " eventType VARCHAR(100)," + " timeStamp TIMESTAMP)";

		try {
			connection.prepareStatement(events).executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public void insertEvent(String playerID, int gameID, int gameScore, Event event) throws SQLException {
		// MICHA: time stamps generated at insertion
		stmt = connection.prepareStatement(
				"INSERT INTO " + tableName + " (playerID, gameID, gameScore, eventType, timeStamp)" + " VALUES (?, ?, ?, ?, ?)");

		stmt.setString(1, playerID);
		stmt.setInt(2, gameID);
		stmt.setInt(3, gameScore);
		stmt.setString(4, event.name());
		stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

		stmt.executeUpdate();
	}

	public ArrayList<Record> getEventsByPlayerID(String playerID) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		if (playerID.equals("all")) {
			stmt = connection.prepareStatement("SELECT * FROM " + tableName);
		} else {
			stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE playerID = ?");
			stmt.setString(1, playerID);
		}

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			// MICHA: Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("gameScore"), Event.valueOf(rs.getString("eventType")),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}

		return records;
	}
	
	public int getCurrentNumberOfGames() {
		// MICHA: return current number of games
		try {
			stmt = connection.prepareStatement("SELECT IFNULL(MAX(gameID), 0) FROM " + tableName); 
			ResultSet rs = stmt.executeQuery();
			rs.next();
			numberOfGames = ((Number) rs.getObject(1)).intValue();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return numberOfGames;
	}

	public ArrayList<Record> getEventsByGameID(int gameID) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE gameID = ?");
		stmt.setInt(1, gameID);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			// MICHA: Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("gameScore"), Event.valueOf(rs.getString("eventType")),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}

		return records;
	}

	public ArrayList<Record> getAllEvents() throws SQLException {
		return getEventsByPlayerID("all");
	}

	public void deletePlayer(String playerID) throws SQLException {
		stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE playerID = ?");
		stmt.setString(1, playerID);

		stmt.executeUpdate();
	}

	public void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException {
		// playerID is player's name
		stmt = connection.prepareStatement("UPDATE " + tableName + " SET playerID = ? WHERE playerID = ?");
		stmt.setString(1, newPlayerID);
		stmt.setString(2, oldPlayerID);

		stmt.executeUpdate();
	}

}
