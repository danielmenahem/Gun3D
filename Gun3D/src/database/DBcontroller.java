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

public class DBcontroller implements DBcontrollerInterface {
	private final String dbDriverName = "com.mysql.jdbc.Driver";
	private final String dbName = "jdbc:mysql://localhost/gun";
	private final String dbUsername = "scott";
	private final String dbPassword = "tiger";
	private final String tableName = "events";

	private Connection connection;
	private PreparedStatement stmt;

	public DBcontroller() throws Exception {
		connect();
		createTable();
	}

	private void connect() throws Exception {
		Class.forName(dbDriverName);
		System.out.println("Driver loaded");
		connection = DriverManager.getConnection(dbName, dbUsername, dbPassword);
		System.out.println("Database connected");
	}

	private void createTable() {
		String events = "CREATE TABLE IF NOT EXISTS " + tableName + "  (playerID        VARCHAR(100),"
				+ "   eventType	          VARCHAR(100)," + "   timeStamp       TIMESTAMP)";

		try {
			connection.prepareStatement(events).executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public void insertEvent(String playerID, String eventType) throws SQLException {
		// MICHA: timestamps generated at insertion
		stmt = connection
				.prepareStatement("INSERT INTO " + tableName + " (playerID, eventType, timeStamp)" + " VALUES (?, ?, ?)");

		stmt.setString(1, playerID);
		stmt.setString(2, eventType);
		stmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));

		stmt.executeUpdate();
	}

	public ArrayList<DBRecord> getEventsByPlayerID(String playerID) throws SQLException {
		ArrayList<DBRecord> records = new ArrayList<>();

		if (playerID.equals("all")) {
			stmt = connection.prepareStatement("SELECT * FROM " + tableName);
		} else {
			stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE playerID = ?");
			stmt.setString(1, playerID);
		}

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			// MICHA: Local time zone
			records.add(new DBRecord(rs.getString("playerID"), rs.getString("eventType"),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}

		return records;
	}

	public ArrayList<DBRecord> getAllEvents() throws SQLException {
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
