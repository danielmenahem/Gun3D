package Utilities;

import java.util.Comparator;

import database.Record;

public class nameScoreComparator implements Comparator<Record> {

	@Override
	public int compare(Record o1, Record o2) {
		int nameValue = o1.getPlayerID().compareTo(o2.getPlayerID());
		if (nameValue == 0)
			return o1.getScore() > o2.getScore() ? -1:1;
		return nameValue;
	}


}
