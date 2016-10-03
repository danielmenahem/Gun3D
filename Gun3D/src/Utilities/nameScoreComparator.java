package Utilities;

import java.util.Comparator;

import database.DBRecord;

public class nameScoreComparator implements Comparator<DBRecord> {

	@Override
	public int compare(DBRecord o1, DBRecord o2) {
		int nameValue = o1.getPlayerID().compareTo(o1.getPlayerID());
		if (nameValue == 0)
			return o1.getScore() > o2.getScore() ? -1:1;
		return nameValue;
	}


}
