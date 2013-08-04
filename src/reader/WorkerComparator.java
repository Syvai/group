package reader;

import java.util.Comparator;
import java.util.Map;

public final class WorkerComparator implements Comparator<Map.Entry<Integer, Person>> {

	@Override
	public int compare(Map.Entry<Integer, Person> o1, Map.Entry<Integer, Person> o2) {
		
		if (o1.getValue().post == POST.E_TELLER 
				&& o2.getValue().post == POST.E_WORKER)
			return -1;
		if (o2.getValue().post == POST.E_TELLER 
				&& o1.getValue().post == POST.E_WORKER)
			return 1;
		return o2.getValue().knownPersons.size() - o1.getValue().knownPersons.size();
	}

}
