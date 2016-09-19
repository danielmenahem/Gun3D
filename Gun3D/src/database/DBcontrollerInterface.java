package database;

import java.sql.SQLException;
import java.util.ArrayList;

public interface DBcontrollerInterface {
	void insertEvent(String playerID, String eventType) throws SQLException;
	ArrayList<DBRecord> getEventsByPlayerID(String playerID) throws SQLException;
	ArrayList<DBRecord> getAllEvents() throws SQLException;
	void deletePlayer(String playerID) throws SQLException;
	void changePlayerName(String oldPlayerID, String newPlayerID) throws SQLException;
}
