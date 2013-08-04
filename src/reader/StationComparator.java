package reader;

import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public final class StationComparator implements Comparator<Map.Entry<Integer, Station>> {

	@Override
	public int compare(Entry<Integer, Station> o1, Entry<Integer, Station> o2) {
		
		return o1.getValue().getCapacity(4) - o2.getValue().getCapacity(4);
	}

}
