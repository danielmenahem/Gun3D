package database;

import java.util.Comparator;

public class nameTimeComparator implements Comparator<DBRecord> {

	@Override
	public int compare(DBRecord o1, DBRecord o2) {
		int nameValue = o1.getPlayerID().compareTo(o2.getPlayerID());
		if (nameValue == 0)
			return o1.getTimeStamp().compareTo(o2.getTimeStamp());
		return nameValue;
	}

}
