package Utilities;

import java.util.Comparator;

import database.Record;

public class scoreComparator implements Comparator<Record> {

	@Override
	public int compare(Record o1, Record o2) {
		return o1.getScore() > o2.getScore() ? -1:1;
	}

}