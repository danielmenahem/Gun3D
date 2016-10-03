package Utilities;

import java.util.Comparator;

import database.DBRecord;

public class scoreComparator implements Comparator<DBRecord> {

	@Override
	public int compare(DBRecord o1, DBRecord o2) {
		return o1.getScore() > o2.getScore() ? -1:1;
	}

}