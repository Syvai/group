package reader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jxl.format.RGB;

class WorkList {
	HashMap<Integer, Person> works;   // 人员名单编号
	HashMap<Integer, Station> stations; // 工作站编号
	List<Map.Entry<Integer, Person>> workerIds;  // 
	List<Map.Entry<Integer, Station>> stationIds;

	public WorkList() {
		super();
		this.works = new HashMap<Integer, Person>();
		this.stations = new HashMap<Integer, Station>();
	}

	void readSeason(Season s, int flag) {
		s.getAllWorkers(works);
		if (flag > 0) {
			s.getAllStations(stations);
		}
	}

	public int getSidFromName(String name) {
		Iterator<Station> iter = stations.values().iterator();

		while (iter.hasNext()) {
			Station s = iter.next();
			if (s.getName().endsWith(name)) {
				return s.getId();
			}
		}

		return 0;
	}

	@Override
	public String toString() {
		if (workerIds == null) {
			if (stationIds == null)
				return "WorkList [works=" + works + "\n stations=" + stations
						+ "]";
			else
				return "WorkList [works=" + works + "\n stations=" + stationIds
						+ "]";
		} else {
			if (stationIds == null)
				return "WorkList [works=" + workerIds + "\n stations="
						+ stations + "]";
			else
				return "WorkList [works=" + workerIds + "\n stations="
						+ stationIds + "]";
		}
	}

	public int getWidFromName(String name) {
		Iterator<Person> iter = works.values().iterator();

		while (iter.hasNext()) {
			Person s = iter.next();
			if (s.getName().endsWith(name)) {
				return s.getId();
			}
		}
		return 0;
	}

	public void sortWorkers() {
		WorkerComparator comp = new WorkerComparator();
		workerIds = new ArrayList<Map.Entry<Integer, Person>>(works.entrySet());
		Collections.sort(workerIds, comp);
	}

	public void sortStations() {
		StationComparator comp = new StationComparator();
		stationIds = new ArrayList<Map.Entry<Integer, Station>>(
				stations.entrySet());
		Collections.sort(stationIds, comp);
	}

	public int getMaxWid() {
		int wid = 0;
		Iterator<Person> iter = works.values().iterator();

		while (iter.hasNext()) {
			Person s = iter.next();
			if (wid < s.getId()) {
				wid = s.getId();
			}
		}
		return wid;
	}

	public void test() {
		works.get(39).setName("Google");

	}

	public List<Entry<Integer,Person>> getSortedPersons() {
		// TODO Auto-generated method stub
		if (null == workerIds)
			sortWorkers();
		return workerIds;
	}

	public Collection<? extends Entry<Integer, Station>> getStations() {
		// TODO Auto-generated method stub
		if (null == stationIds) {
			sortStations();
		}
		return stationIds;
	}

	public Person getFrmName(String contents) {
		// TODO Auto-generated method stub
		for (Person p : works.values()) {
			if (p.getName().equalsIgnoreCase(contents)) {
				return p;
			}
		}
		return null;
	}

	public Station getSFrmName(String contents) {
		// TODO Auto-generated method stub
		for (Station s : stations.values()) {
			if (s.getName().equalsIgnoreCase(contents)) {
				return s;
			}
		}
		return null;
	}

}

public class Season {
	HashMap<Integer, Station> mMap;

	public Season() {
		super();
		mMap = new HashMap<Integer, Station>();
	}

	public int getSidFromName(String name) {
		Iterator<Station> iter = mMap.values().iterator();

		while (iter.hasNext()) {
			Station s = iter.next();
			if (s.getName().endsWith(name)) {
				return s.getId();
			}
		}

		return 0;
	}

	void getAllWorkers(HashMap<Integer, Person> ws) {
		Iterator<Station> iter = mMap.values().iterator();

		while (iter.hasNext()) {
			iter.next().getWorkers(ws);
		}
	}

	void getAllStations(HashMap<Integer, Station> ss) {
		// Iterator<Station> iter = mMap.values().iterator();

		for (Station s : mMap.values()) {

			ss.put(s.getId(), s);
		}

		// while (iter.hasNext()) {
		// Station station = iter.next();
		// ss.put(station.getId(), station);
		// }

		mMap.clear();
	}

	@Override
	public String toString() {
		return "Season [mMap=" + mMap + "]";
	}

	public void addStation(Integer sid, Station s) {
		if (null == sid)
			System.out.println("sid is null");
		if (s == null)
			System.out.println("s is null");
		this.mMap.put(sid, s);
	}

	public Station getStation(Integer sid) {
		return this.mMap.get(sid);
	}

	/**
	 * 计分使用priority, priority越小, 优先级越高
	 * @param priority
	 */
	public void getKnown() {
		Iterator<Station> station = mMap.values().iterator();

		while (station.hasNext()) {
			station.next().knownEachOther();
		}
	}

	public Object getCurGroup(int i) {
		// TODO Auto-generated method stub
		return null;
	}
}

class Box {
	protected int id;
	protected String name;
	protected RGB color;
	protected boolean bOuter;

	public Box(int id, String name, RGB color) {
		super();
		this.id = id;
		this.name = name;
		this.color = color;
		bOuter = false;
	}

	public boolean isbOuter() {
		return bOuter;
	}

	public void setbOuter(boolean bOuter) {
		this.bOuter = bOuter;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public RGB getColor() {
		return color;
	}

	public void setColor(RGB color) {
		this.color = color;
	}

}

class Station extends Box {
	private HashMap<Integer, Person> w1;
	private HashMap<Integer, Person> w2;
	private HashMap<Integer, Person> w3;
	private HashMap<Integer, Person> w4;

	private Master m;
	private Assist a;

	private Integer[] capacity;

	public Master getM() {
		return m;
	}

	public void getWorkers(HashMap<Integer, Person> ws) {
		getGroup(ws, w1);
		getGroup(ws, w2);
		getGroup(ws, w3);
		getGroup(ws, w4);
	}

	public void getGroup(HashMap<Integer, Person> ws, HashMap<Integer, Person> grp) {
		Iterator<Person> it = grp.values().iterator();

		while (it.hasNext()) {
			Person p = it.next();
			if (ws.containsKey(p.getId())) {
				p.mergePerson(ws.get(p.getId()));
			} else {
				ws.put(p.getId(), p);
			}
		}

		grp.clear();
	}

	public void setM(Master m) {
		this.m = m;
	}

	public Assist getA() {
		return a;
	}

	public void setA(Assist a) {
		this.a = a;
	}

	public void addCompany(Person w, int idx) {
		switch (idx) {
		case 1:
			w1.put(w.getId(), w);
			break;

		case 2:
			w2.put(w.getId(), w);
			break;

		case 3:
			w3.put(w.getId(), w);
			break;

		case 4:
			w4.put(w.getId(), w);
			break;
		}
	}

	public void knownGroup(HashMap<Integer, Person> grp, Station s) {
		Iterator<Person> iter = grp.values().iterator();
		Iterator<Person> iter2 = null;

		iter2 = grp.values().iterator();
		while (iter2.hasNext()) {
			Person wi = iter2.next();
			iter = grp.values().iterator();
			while (iter.hasNext()) {
				Person wj = iter.next();
				if (wj.getId() == wi.getId()) {
					continue;
				}

				wj.addKnownPerson(wi, s);
				wi.addKnownPerson(wj, s);
			}
		}
	}

	public void knownEachOther() {

		knownGroup(this.w1, this);
		knownGroup(this.w2, this);
		knownGroup(this.w3, this);
		knownGroup(this.w4, this);

		if (capacity == null) {
			capacity = new Integer[4];
			capacity[0] = w1.size();
			capacity[1] = w2.size();
			capacity[2] = w3.size();
			capacity[3] = w4.size();
		}
	}

	public Station(int id, String name, RGB color, HashMap<Integer, Person> w1,
			HashMap<Integer, Person> w2, HashMap<Integer, Person> w3,
			HashMap<Integer, Person> w4, Master m, Assist a) {
		super(id, name, color);
		this.w1 = w1;
		this.w2 = w2;
		this.w3 = w3;
		this.w4 = w4;
		this.m = m;
		this.a = a;
	}

	public Integer getCapacity(int grp_id) {
		if (grp_id == 4)
			return capacity[0] + capacity[1] + capacity[2] + capacity[3];
		if (grp_id >= 0 && grp_id < 4)
			return capacity[grp_id];
		System.out.println("ERROR: getCapacity( " + grp_id);
		return 0;
	}

	public Station(int id, String name, RGB color) {
		super(id, name, color);
		// TODO Auto-generated constructor stub
		this.w1 = new HashMap<Integer, Person>();
		this.w2 = new HashMap<Integer, Person>();
		this.w3 = new HashMap<Integer, Person>();
		this.w4 = new HashMap<Integer, Person>();
	}

	@Override
	public String toString() {
		int cursize = w1.size() + w2.size() + w3.size() + w4.size();
		int cap = capacity[0] + capacity[1] + capacity[2] + capacity[3];
		
		return "" + id + "," + name + ", " + cursize + "/" +cap + w1 + w2 +w3 + w4 +"\n";
	}

	public HashMap<Integer, Person> getGroup(int x) {
		// TODO Auto-generated method stub
		HashMap<Integer, Person> w = null;
		switch (x) {
		case 0:
			w = (w1);
			break;
		case 1:
			w = (w2);
			break;
		case 2:
			w = (w3);
			break;
		case 3:
			w = (w4);
			break;
			default:
				System.err.println("WARN: not found group for x = " + x);
		}
		return w;
	}
	
	public boolean setGroup(int x, HashMap<Integer, Person> grp) {
		// TODO Auto-generated method stub
		switch (x) {
		case 0:
//			if (null != w1)
//				System.err.println("w1 is not nil");
			w1 = grp;
			break;
		case 1:
//			if (null != w2)
//				System.err.println("w2 is not nil");
			w2 = grp;
			break;
		case 2:
//			if (null != w3)
//				System.err.println("w3 is not nil");
			w3 = grp;
			break;
		case 3:
//			if (null != w4)
//				System.err.println("w4 is not nil");
			w4 = grp;
			break;
			default:
				System.err.println("WARN: not found group for x = " + x);
				return false;
		}
		return true;
	}

	public Integer getCurSize() {
		// TODO Auto-generated method stub
		int curSize = w1.size() + w2.size() + w3.size() + w4.size();
		return curSize;
	}

}

enum POST {
	E_WORKER, E_TELLER, E_ASSIST, E_MASTER
}

class Person extends Box {
	protected POST post;  ///职位
	protected HashSet<Integer> knownPersons = null;
	protected HashSet<Integer> workStations = null;
	protected int conflicted;
	protected int grade;
	

	public int getGrade() {
		return grade;
	}


	public void setGrade(int grade) {
		this.grade = grade;
	}


	public Person(int id, String name, RGB color) {
		super(id, name, color);
		knownPersons = new HashSet<Integer>();
		workStations = new HashSet<Integer>();
		conflicted = 0;
		bOuter = false;
		// TODO Auto-generated constructor stub
	}


	public Person(int ids, String contents, POST post2) {
		// TODO Auto-generated constructor stub
		super(ids, contents, new RGB(0,0,0));
		post = post2;
		knownPersons = new HashSet<Integer>();
		workStations = new HashSet<Integer>();
		conflicted = 0;
		bOuter = false;
	}


	public int getConflicted() {
		return conflicted;
	}

	public void setConflicted(int conflicted) {
		this.conflicted = conflicted;
	}

	public void mergePerson(Person person) {
		Iterator<Integer> iter = knownPersons.iterator();

		if (person.post != this.post) {
			System.out.println("WARN: mergePerson not same! ");
		}

		while (iter.hasNext()) {
			person.addKnownPerson(iter.next());
		}
		
		for (Integer p : this.workStations) {
			person.addWorkStation(p);
		}
	}

	private void addWorkStation(Integer p) {
		// TODO Auto-generated method stub
		workStations.add(p);
	}

	private void addKnownPerson(Integer p) {
		// TODO Auto-generated method stub
		if (knownPersons.contains(p)) {
			System.out.println("already contains " + p);
		}
		knownPersons.add(p);
	}

	void addKnownPerson(Person p, Station s) {
		// if (this.knownPersons.contains(p.getId())) {
		// System.out.println("WARN: " + this.getName() + " already knows "
		// + p.getName());
		// }
//		if (this.id == 19) {
//			System.out.println("knows: " + this.knownPersons + " +" + p.getId());
//		}

		this.knownPersons.add(p.getId());
		this.workStations.add(s.getId());
	}

	@Override
	public String toString() {
		return "Person [id=" + id + ", name=" + name + ", stations="
				+ workStations + "]";
	}


}

class Master extends Person {

	public Master(int id, String name, RGB color) {
		super(id, name, color);
		post = POST.E_MASTER;
	}

	@Override
	public String toString() {
		return "M [" + id + ", " + name + "," + this.knownPersons.size()
				+ ", <" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + ">]";
	}

}

class Assist extends Person {

	public Assist(int id, String name, RGB color) {
		super(id, name, color);
		post = POST.E_ASSIST;
	}

	@Override
	public String toString() {
		return "A [" + id + ", " + name + "," + this.knownPersons.size()
				+ ", <" + color.getRed() + "," + color.getGreen() + ","
				+ color.getBlue() + ">]";
	}

}

class Worker extends Person {


	public Worker(int id, String name, RGB color) {
		super(id, name, color);
		this.post = POST.E_WORKER;
	}
	
//	public Worker(int id, String name, RGB color, int grade) {
//		super(id, name, color);
//		this.post = POST.E_WORKER;
//		this.grade = grade;
//	}

	public Worker(int id, String name) {
		// TODO Auto-generated constructor stub
		super(id, name, new RGB(0,0,0));
		this.post = POST.E_WORKER;
	}

	@Override
	public String toString() {
		return "W [" + id + ", " + name + ", station:" + this.workStations +", known:" + this.knownPersons+ "]\n";
	}
}

class Teller extends Person {

	public Teller(int id, String name, RGB color) {
		super(id, name, color);
		this.post = POST.E_TELLER;
	}

	public Teller(int id, String name, RGB color, int grade) {
		super(id, name, color);
		this.grade = grade;
	}
	
	public Teller(int id, String name) {
		// TODO Auto-generated constructor stub
		super(id, name, new RGB(0,0,0));
		this.post = POST.E_TELLER;
	}

	@Override
	public String toString() {
		return "T [" + id + ", " + name + ", stations:" + this.workStations + ", known:" + this.knownPersons + "]\n";
	}
}
