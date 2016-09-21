package database;

import java.util.Comparator;

public class nameScoreComparator implements Comparator<DBRecord> {

	@Override
	public int compare(DBRecord o1, DBRecord o2) {
		int nameValue = o1.getPlayerID().compareTo(o1.getPlayerID());
		if (nameValue == 0)
			return o1.getGameScore() > o2.getGameScore() ? -1:1;
		return nameValue;
	}


}
