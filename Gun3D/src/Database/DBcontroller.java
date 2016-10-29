package Database;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;

import GameObjects.EventType;

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
	 * jdbc driver. Value of {@code dbDriverName} is {@value}.
	 */
	private static final String dbDriverName = "com.mysql.jdbc.Driver";
	/**
	 * A database URL for the connection. Value of {@code dbURL} is {@value}.
	 */
	private static final String dbURL = "jdbc:mysql://localhost/";
	/**
	 * DB name. Value of {@code dbName} is {@value}.
	 */
	private static final String dbName = "gun3DMichaelAndDaniel";
	/**
	 * DB user name. Value of {@code dbUsername} is {@value}.
	 */
	private static final String dbUsername = "scott";
	/**
	 * DB password. Value of {@code dbPassword} is {@value}.
	 */
	private static final String dbPassword = "tiger";
	/**
	 * DB table's name. Value of {@code tableName} is {@value}.
	 */
	private static final String tableName = "events";

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
		createDB();
		createTable();
		createFakeData();
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
		connection = DriverManager.getConnection(dbURL, dbUsername, dbPassword);
		System.out.println("Connection made");
	}

	/**
	 * Create the database, if it doesn't exist.
	 */
	private void createDB() {
		String db = "CREATE DATABASE " + dbName;

		try {
			connection.prepareStatement(db).executeUpdate();
			System.out.println("DB created");
		} catch (SQLException e) {
			if (e.getErrorCode() == 1007)
				// Database already exists error
				System.out.println("Database already exists");
			else
				System.out.println(e.getMessage());
		}

		try {
			connection = DriverManager.getConnection(dbURL + dbName, dbUsername, dbPassword);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
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
	public void deletePlayer(String playerID) throws SQLException, Exception {
		stmt = connection.prepareStatement("DELETE FROM " + tableName + " WHERE playerID = ?");
		stmt.setString(1, playerID);

		if (stmt.executeUpdate() == 0)
			throw new Exception("Player doesn't exist");
	}

	@Override
	public void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException, Exception {
		// playerID is player's name
		stmt = connection.prepareStatement("UPDATE " + tableName + " SET playerID = ? WHERE playerID = ?");
		stmt.setString(1, newPlayerID);
		stmt.setString(2, oldPlayerID);

		if (stmt.executeUpdate() == 0)
			throw new Exception("Player doesn't exist");
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
	public ArrayList<Record> getAverageScoresOfXTopGameByPlayersWithXGamesOrMoreDescending(int minGames)
			throws SQLException {

		ArrayList<Record> records = getPlayersWithXGamesOrMore(minGames);
		records = getAverageScoresOfXTopGameOfPlayers(records, minGames);

		records.sort(new Comparator<Record>() {

			@Override
			public int compare(Record r1, Record r2) {
				if (r1.getScore() >= r2.getScore())
					return -1;
				else
					return 1;
			}
		});
		;

		return records;
	}

	/**
	 * Get average score of top X games of specific players.
	 * 
	 * @param players
	 *            {@link ArrayList} of {@link Record} containing the players
	 *            that their top average will be calculated, and their number of
	 *            games
	 * @param numberOfTopGamesToCalc
	 *            the number of top games to include in calculated average
	 * @return {@link ArrayList} of {@link Record} containing players' names,
	 *         number of games and average of top X games
	 * @throws SQLException
	 *             DB SQL exceptions
	 */
	private ArrayList<Record> getAverageScoresOfXTopGameOfPlayers(ArrayList<Record> players, int numberOfTopGamesToCalc)
			throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		for (int i = 0; i < players.size(); i++) {
			stmt = connection.prepareStatement(
					"SELECT playerID, AVG(max_score) as avg_top FROM (SELECT playerID, gameID as g_id, max(gameScore) as max_score ,min(timeStamp) FROM "
							+ tableName
							+ " WHERE playerID = ? GROUP BY gameID order by max_score DESC limit ?) as top_games GROUP BY playerID");
			stmt.setString(1, players.get(i).getPlayerID());
			stmt.setInt(2, numberOfTopGamesToCalc);

			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				records.add(new Record(rs.getString("playerID"), players.get(i).getGameID(), rs.getInt("avg_top"), null,
						null));
			}

		}
		return records;
	}

	/**
	 * Get the players that have more than X games and their number of games.
	 * 
	 * @param minGames
	 *            the minimum number of games of players to include in the
	 *            returned list
	 * @return {@link ArrayList} of {@link Record} containing players' names and
	 *         their number of games
	 * @throws SQLException
	 *             DB SQL exceptions
	 */
	private ArrayList<Record> getPlayersWithXGamesOrMore(int minGames) throws SQLException {
		ArrayList<Record> records = new ArrayList<>();

		stmt = connection.prepareStatement(
				"SELECT playerID, count(distinct(g_id)) as gamesCount FROM (SELECT playerID, gameID as g_id, max(gameScore) as max_score ,min(timeStamp) FROM "
						+ tableName
						+ " GROUP BY playerID, gameID) as max_score GROUP BY playerID HAVING gamesCount >= ?");
		stmt.setInt(1, minGames);

		ResultSet rs = stmt.executeQuery();
		while (rs.next()) {
			records.add(new Record(rs.getString("playerID"), rs.getInt("gamesCount"), 0, null, null));
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
	 * A method to create DB for testing.
	 */
	private void createFakeData() {
		for (int i = 0; i < 5; i++) {
			// 5 players
			int maxGames = ThreadLocalRandom.current().nextInt(0, 5 + 1);
			// each has a different number of games
			for (int j = 0; j < maxGames; j++) {
				int gameNumber = 0;
				try {
					gameNumber = getCurrentNumberOfGames() + 1;
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
				int numberOfEvent = ThreadLocalRandom.current().nextInt(20, 25 + 1);
				int scoreRandom = ThreadLocalRandom.current().nextInt(0, 500 + 1);
				// each game has a different number of events, min 20
				for (int k = 0; k < numberOfEvent; k++) {
					try {
						// randomize hit or miss
						int hitOrMiss = ThreadLocalRandom.current().nextInt(0, 1 + 1);
						// randomize times in gaps of seconds
						int seconds = ThreadLocalRandom.current().nextInt(0, 60 + 1);
						insertModifiedTimeEvent("Player" + i, gameNumber, scoreRandom, EventType.values()[hitOrMiss],
								Timestamp.valueOf(LocalDateTime.now().minusSeconds(seconds)));
					} catch (SQLException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Insert an event to the DB with a modified timestamp, for fake data
	 * creation
	 * 
	 * @param playerID
	 *            player's name, string inserted by the user
	 * @param gameID
	 *            game's ID. To insert, get current number of games and
	 *            increment it by 1 to avoid collisions
	 * @param gameScore
	 *            game's score at the moment of the event
	 * @param event
	 *            event type {@link EventType}
	 * @param timeStamp
	 *            the modified timestamp of the inserted event
	 * @throws SQLException
	 *             DB SQL exceptions
	 */
	private void insertModifiedTimeEvent(String playerID, int gameID, int gameScore, EventType event,
			Timestamp timeStamp) throws SQLException {
		stmt = connection.prepareStatement("INSERT INTO " + tableName
				+ " (playerID, gameID, gameScore, eventType, timeStamp)" + " VALUES (?, ?, ?, ?, ?)");

		stmt.setString(1, playerID);
		stmt.setInt(2, gameID);
		stmt.setInt(3, gameScore);
		stmt.setString(4, event.name());
		stmt.setTimestamp(5, timeStamp);

		stmt.executeUpdate();
	}
}
