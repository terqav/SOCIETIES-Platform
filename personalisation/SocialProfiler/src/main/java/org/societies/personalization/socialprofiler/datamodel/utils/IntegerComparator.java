package org.societies.personalization.socialprofiler.datamoldel.utils;


public class IntegerComparator {
	public int compare(int o1, int o2) {
		int d = o1 - o2;
		return d > 0 ? 1 : (d < 0 ? -1 : 0);
	}
}