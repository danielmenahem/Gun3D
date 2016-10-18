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
import java.util.concurrent.ThreadLocalRandom;

import Utilities.EventType;

/**
 * Implementation of the DB interface for the DB view.
 * 
 * @author Daniel Menahem 39676804
 * @author Michael Shvarts 301578878
 * @version 1.0
 * @see DBcontrollerInterface
 * 
 *      JavaDoc made under the assumption that the HTML generated doc will
 *      include private fields. Under different circumstances, the private
 *      attributes would be documented in the public getters, and the rest would
 *      link there. Private attributes documentation was made so everything will
 *      be documented.
 */
public class DBcontroller implements DBcontrollerInterface {
	/**
	 * DB driver: jdbc.
	 */
	private final String dbDriverName = "com.mysql.jdbc.Driver";
	/**
	 * DB name: "gun".
	 */
	private final String dbName = "jdbc:mysql://localhost/gun";
	/**
	 * DB user name, by instructions is "scott".
	 */
	private final String dbUsername = "scott";
	/**
	 * DB password, by instructions is "tiger".
	 */
	private final String dbPassword = "tiger";
	/**
	 * DB table's name, should be "events" (different names for testing tables).
	 */
	private final String tableName = "eventsTest505005";

	/**
	 * Number of games. When setting this property current number should be
	 * extracted and incremented.
	 */
	private static int numberOfGames;

	/**
	 * Current DB connection.
	 */
	private Connection connection;

	/**
	 * DB prepared statement property, to be defined and executed.
	 */
	private PreparedStatement stmt;

	/**
	 * A public constructor to connect to DB and create the database table.
	 * 
	 * @throws Exception
	 *             {@link ClassNotFoundException} and {@link SQLException}
	 */
	public DBcontroller() throws Exception {
		connect();
		createTable();
		getCurrentNumberOfGames();
	}

	/**
	 * Connects to the database.
	 * 
	 * @throws Exception
	 *             {@link ClassNotFoundException} and {@link SQLException}
	 */
	private void connect() throws Exception {
		Class.forName(dbDriverName);
		System.out.println("Driver loaded");
		connection = DriverManager.getConnection(dbName, dbUsername, dbPassword);
		System.out.println("Database connected");
	}

	/**
	 * Create the table in the database, if it doesn't exist.
	 */
	private void createTable() {
		String events = "CREATE TABLE IF NOT EXISTS " + tableName + "(playerID VARCHAR(100)," + " gameID INT,"
				+ " gameScore INT," + " eventType VARCHAR(100)," + " timeStamp TIMESTAMP)";

		try {
			connection.prepareStatement(events).executeUpdate();
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	@Override
	public void insertEvent(String playerID, int gameID, int gameScore, EventType event) throws SQLException {
		// Time stamps generated at insertion
		stmt = connection.prepareStatement("INSERT INTO " + tableName
				+ " (playerID, gameID, gameScore, eventType, timeStamp)" + " VALUES (?, ?, ?, ?, ?)");

		stmt.setString(1, playerID);
		stmt.setInt(2, gameID);
		stmt.setInt(3, gameScore);
		stmt.setString(4, event.name());
		stmt.setTimestamp(5, Timestamp.valueOf(LocalDateTime.now()));

		stmt.executeUpdate();
	}

	@Override
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
			// Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("gameScore"),
					EventType.valueOf(rs.getString("eventType")),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}

		return records;
	}

	@Override
	public int getCurrentNumberOfGames() throws SQLException {
		// Return current number of games
		stmt = connection.prepareStatement("SELECT IFNULL(MAX(gameID), 0) FROM " + tableName);
		ResultSet rs = stmt.executeQuery();
		rs.next();
		numberOfGames = ((Number) rs.getObject(1)).intValue();

		return numberOfGames;
	}

	@Override
	public ArrayList<Record> getEventsByGameID(int gameID) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT * FROM " + tableName + " WHERE gameID = ?");
		stmt.setInt(1, gameID);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {
			// Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("gameScore"),
					EventType.valueOf(rs.getString("eventType")),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}

		return records;
	}

	@Override
	public ArrayList<Record> getAllEvents() throws SQLException {
		return getEventsByPlayerID("all");
	}

	@Override
	public void deletePlayer(String playerID) throws SQLException {
		stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE playerID = ?");
		stmt.setString(1, playerID);

		stmt.executeUpdate();
	}

	@Override
	public void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException {
		// playerID is player's name
		stmt = connection.prepareStatement("UPDATE " + tableName + " SET playerID = ? WHERE playerID = ?");
		stmt.setString(1, newPlayerID);
		stmt.setString(2, oldPlayerID);

		stmt.executeUpdate();
	}

	@Override
	public ArrayList<Record> getAllGamesByPlayerID(String playerID) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		// Maximum score & start time of the games, event type is irrelevant
		stmt = connection.prepareStatement("SELECT playerID, gameID, max(gameScore), min(timeStamp) FROM " + tableName
				+ " WHERE playerID = ? GROUP BY gameID");
		stmt.setString(1, playerID);

		ResultSet rs = stmt.executeQuery();

		while (rs.next()) {

			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("max(gameScore)"), null,
					LocalDateTime.ofInstant(rs.getTimestamp("min(timeStamp)").toInstant(), ZoneId.systemDefault())));
		}

		return records;
	}

	@Override
	public ArrayList<Record> getPlayers() throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT DISTINCT playerID FROM " + tableName);
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			records.add(new Record(rs.getString("playerID"), 0, 0, null, null));
		}
		return records;
	}

	@Override
	public ArrayList<Record> getAllEventsByNameAndThenByTimeDescending() throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT * FROM " + tableName + " ORDER BY playerID DESC, timeStamp DESC");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			// Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("gameScore"),
					EventType.valueOf(rs.getString("eventType")),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}
		return records;
	}

	@Override
	public ArrayList<Record> getAllEventsByNameAndThenByScoreDescending() throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT * FROM " + tableName + " ORDER BY playerID DESC, gameScore DESC");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			// Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("gameScore"),
					EventType.valueOf(rs.getString("eventType")),
					LocalDateTime.ofInstant(rs.getTimestamp("timeStamp").toInstant(), ZoneId.systemDefault())));
		}
		return records;
	}

	@Override
	public ArrayList<Record> getAllEventsByGameScoreDescending() throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT playerID, gameID, max(gameScore) ,min(timeStamp) FROM " + tableName
				+ " GROUP BY playerID, gameID ORDER BY max(gameScore) DESC");
		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			// Local time zone
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("max(gameScore)"), null,
					LocalDateTime.ofInstant(rs.getTimestamp("min(timeStamp)").toInstant(), ZoneId.systemDefault())));
		}
		return records;
	}

	@Override
	public ArrayList<Record> getAverageScoresOfPlayersWithXGamesOrMoreDescending(int minGames) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement(
				"SELECT playerID, count(distinct(g_id)) as gameCount, AVG(max_score) as average FROM ( SELECT playerID, gameID as g_id, max(gameScore) as max_score ,min(timeStamp) FROM "
						+ tableName
						+ " GROUP BY playerID, gameID) as group_games_table GROUP BY playerID HAVING gameCount >= ? ORDER BY average DESC");
		stmt.setInt(1, minGames);

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameCount"), rs.getInt("average"), null, null));
		}
		return records;
	}

	@Override
	public ArrayList<Record> getAllGamesByMostEventsDescending(EventType event) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement("SELECT playerID, gameID, count(eventType) as event_count, eventType FROM "
				+ tableName + " where eventType = ? GROUP BY playerID, gameID ORDER BY event_count DESC");
		stmt.setString(1, event.name());

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			records.add(new Record(rs.getString("playerID"), rs.getInt("gameID"), rs.getInt("event_count"),
					EventType.valueOf(rs.getString("eventType")), null));
		}
		return records;
	}

	@Override
	public void closeConnection() {
		try {
			connection.close();
		} catch (SQLException e) {
		}
	}

	/**
	 * A method to create DB records for testing purposes.
	 */
	@SuppressWarnings("unused")
	private void fakeDbTester() {
		for (int i = 0; i < 10; i++) {
			// 10 players
			int maxGames = ThreadLocalRandom.current().nextInt(0, 5 + 1);
			// each has a different number of games
			for (int j = 0; j < maxGames; j++) {
				int gameNumber = 0;
				try {
					gameNumber = getCurrentNumberOfGames() + 1;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				int numberOfEvent = ThreadLocalRandom.current().nextInt(0, 15 + 1);
				int scoreRandom = ThreadLocalRandom.current().nextInt(0, 500 + 1);
				// each game has a different number of events
				for (int k = 0; k < numberOfEvent; k++) {
					try {
						// randomize hit of miss
						int hitOrMiss = ThreadLocalRandom.current().nextInt(0, 1 + 1);
						insertEvent("Player" + i, gameNumber, scoreRandom, EventType.values()[hitOrMiss]);
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}
}
