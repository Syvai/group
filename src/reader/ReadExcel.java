package reader;

/**
 * 2013-08-04 修改，1. 把walk中的capacity改为按照group区分
 *                2. 把readAdd中修改初始化Worker和Teller, walk中最后一个分组不检查capacity条件
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import jxl.Cell;
import jxl.CellType;
import jxl.Sheet;
import jxl.Workbook;

import jxl.format.UnderlineStyle;
import jxl.read.biff.BiffException;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

public class ReadExcel {
	private static final int C_STATION = 10;
	private static final int C_WORKER = 2;
	private static final int C_OUTER = 1;
	private String inputFile;
	WorkList wl;


	public ReadExcel() {
	}

	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}

	public void readMaster(Sheet sheet, Season spring) {
		int sid = 0;
		for (int i = 2; i < sheet.getRows();++i) {
			Cell st = sheet.getCell(1, i);
			CellType type = st.getType();
			Station  station = spring.getStation(i);
			
			if (type == CellType.LABEL && sid < 8) {
				sid ++;
				station.setM(new Master(sid, st.getContents(),
						st.getCellFormat().getFont().getColour().getDefaultRGB())); 
			}
		}
	}
	
	public void readAssist(Sheet sheet, Season spring) {
		int sid = 8;
		for (int i = 2; i < sheet.getRows();++i) {
			Cell st = sheet.getCell(2, i);
			CellType type = st.getType();
			Station  station = spring.getStation(i);
			
			if (type == CellType.LABEL && sid < 16) {
				station.setA(new Assist(sid, st.getContents(),
						st.getCellFormat().getFont().getColour().getDefaultRGB())); 
				sid ++;
			}
		}
	}
	
	public void readGroup(Sheet sheet, Season spring) {
		int sid = 16;
		int group = 0;
		Station  station = null;
		for (int i = 2; i < sheet.getRows();++i) {
			Cell grp = sheet.getCell(3, i);
			CellType type = grp.getType();
			
			if (type == CellType.LABEL) {
				Cell w = null;
				CellType wType = null;
				group = 0;
				if (grp.getContents().equalsIgnoreCase("第1组")) {
					station = spring.getStation(i);
					group  = 1;					
				}
				else if (grp.getContents().equalsIgnoreCase("第2组")) {
					group  = 2;					
				}else if (grp.getContents().equalsIgnoreCase("第3组")) {
					group  = 3;					
				}else if (grp.getContents().equalsIgnoreCase("第4组")) {
					group  = 4;					
				}
				
				if (group >= 1 && group <= 4) {
					for (int j = 4; j < sheet.getColumns();++j) {
						w = sheet.getCell(j, i);
						wType = w.getType();
						String wname = delScore(w.getContents());
						if (wType == CellType.LABEL) {
							sid++;
//							System.out.println("Worker: " + sid + ", " + delScore(w.getContents()));
							if (j == 4) /// for teller
								station.addCompany(new Teller(sid, wname,
										w.getCellFormat().getFont().getColour().getDefaultRGB()), group);
							else
								station.addCompany(new Worker(sid, wname,
										w.getCellFormat().getFont().getColour().getDefaultRGB()), group);
						}
					}
				}
			}
		}
	}

	private String delScore(String contents) {
		int len = 0;//contents.length();
		
		if (null == contents)
			return null;
		
		String cnts = new String(contents);
		
		len = contents.length();
		
		for (int i = len-1; i >= 0; i--) {
			if (cnts.matches(".+[0-9]")) {
				cnts = cnts.substring(0, cnts.length() - 1);
			}
		}
		
		return cnts;
	}

	public void readGroup(Sheet sheet, Season spring, WorkList wl) {
		int sid = 16;
		int group = 0;
		Station  station = null;
	
		for (int i = 2; i < sheet.getRows();++i) {
			Cell grp = sheet.getCell(3, i);
			CellType type = grp.getType();
			
			if (type == CellType.LABEL) {
				Cell w = null;
				CellType wType = null;
				group = 0;
				if (grp.getContents().equalsIgnoreCase("第1组")) {
					station = spring.getStation(i);
					group  = 1;					
				}
				else if (grp.getContents().equalsIgnoreCase("第2组")) {
					group  = 2;					
				}else if (grp.getContents().equalsIgnoreCase("第3组")) {
					group  = 3;					
				}else if (grp.getContents().equalsIgnoreCase("第4组")) {
					group  = 4;					
				}
				
				if (group >= 1 && group <= 4) {
					for (int j = 4; j < sheet.getColumns();++j) {
						w = sheet.getCell(j, i);
						wType = w.getType();
						String wname = delScore(w.getContents());
						if (wType == CellType.LABEL) {
							sid = wl.getWidFromName(wname);
							
							if (sid == 0) {
//								sid = maxid++;
								System.out.println("离职员工: " + wname);
								continue;
							}
							
							if (j == 4) { /// for teller
								station.addCompany(new Teller(sid, wname,
										w.getCellFormat().getFont().getColour().getDefaultRGB()), group);
							}
							else {
								station.addCompany(new Worker(sid, wname,
										w.getCellFormat().getFont().getColour().getDefaultRGB()), group);
							}
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * 读取工作站基本信息
	 * @param sheet
	 * @param spring
	 */
	public void readStation(Sheet sheet, Season spring) {
		int sid = 0;
		for (int i = 2; i < sheet.getRows();++i) {
			Cell st = sheet.getCell(0, i);
			CellType type = st.getType();
			
			if (type == CellType.LABEL && sid < 8) {
				sid ++;
//				System.out.println("read station " + sid + ", " + st.getContents());
				spring.addStation(i, new Station(sid, st.getContents(),
						st.getCellFormat().getFont().getColour().getDefaultRGB()));
			}
		}
	}
	

	
	public void readStation(Sheet sheet, Season spring, WorkList wl) {
		int sid = 0;
		for (int i = 2; i < sheet.getRows();++i) {
			Cell st = sheet.getCell(0, i);
			CellType type = st.getType();
			
			if (type == CellType.LABEL && sid < 8) {
				sid = wl.getSidFromName(st.getContents());
				System.out.println("read station -" + sid + ", " + st.getContents());
				spring.addStation(i, new Station(sid, st.getContents(),
						st.getCellFormat().getFont().getColour().getDefaultRGB()));
			}
		}
	}
	

	public void read(boolean isCheck) throws IOException {
		File inputWorkbook = new File(inputFile);
		Workbook w;
		int i = 0;
		wl = new WorkList();
		
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = null;
			
			if (isCheck)
				i = 1;
			
			sheet = w.getSheet(i++);
			
			System.out.println("reading " + sheet.getName() + " as base workerlist");
				
			Season spring = new Season();
			readStation(sheet, spring);         /// 一个季度存放8个工作站的排班
			readMaster(sheet, spring);          
			readAssist(sheet, spring);
			readGroup(sheet, spring);           /// 把每个小组的排班读入到对应工作站中
			spring.getKnown();                  /// 工作人员在小组中合作过 

			wl.readSeason(spring, 1);           
			System.out.println("after reading base sheet\n--------------------------------------------------------------------------\n" + wl);

			Season winter = null;
			do {
				if (i >= w.getNumberOfSheets()) {
					break;
				}
				winter = new Season();
				Sheet sheet2 = w.getSheet(i++);
				if (sheet2.getName().equalsIgnoreCase("外环人员")) {
					readOuter(sheet2, wl);
					continue;
				}
				
				if (sheet2.getName().equalsIgnoreCase("外环管理站")) {
					readSOuter(sheet2, wl);
					continue;
				}
				
				if (sheet2.getName().equalsIgnoreCase("新加人员")) {
					System.out.println("before add: " + wl.works.size());
					readAdd(sheet2, wl);
					System.out.println("after add: " + wl.works.size());
					continue;
				}
				
				if (sheet2.getName().equalsIgnoreCase("离职人员")) {
					readDel(sheet2, wl);
					continue;
				}
				
				readStation(sheet2, winter, wl);
				readGroup(sheet2, winter, wl);
				winter.getKnown();
				wl.readSeason(winter, 0);
//				
//				System.out.println("after read sheet(" + i + ") -------------------------------------------------------------------------------\n"
//						+ wl);
			
			} while (true);
			
			wl.sortWorkers();
			wl.sortStations();
			System.out.println(wl);

			w.close();

	    } catch (BiffException e) {
	      e.printStackTrace();
	    }
	  }
	
	private void readSOuter(Sheet sheet2, WorkList wl2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < sheet2.getRows();++i) {
			for (Cell cell : sheet2.getRow(i)) {
				if (cell.getType() == CellType.LABEL) {
					Station s = wl2.getSFrmName(cell.getContents());
					s.setbOuter(true);
				}
			}
		}		
	}

	private void readOuter(Sheet sheet2, WorkList wl2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < sheet2.getRows();++i) {
			for (Cell cell : sheet2.getRow(i)) {
				if (cell.getType() == CellType.LABEL) {
					Person p = wl2.getFrmName(cell.getContents());
					if (p == null) {
						System.err.println("can not find " + cell.getContents());
						continue;
					}
					p.setbOuter(true);
				}
			}
		}
	}
	
	private void readDel(Sheet sheet2, WorkList wl2) {
		// TODO Auto-generated method stub
		for (int i = 0; i < sheet2.getRows();++i) {
			for (Cell cell : sheet2.getRow(i)) {
				if (cell.getType() == CellType.LABEL) {
					Person p = wl2.getFrmName(cell.getContents());
					if (p == null) {
						System.err.println("can not find " + cell.getContents());
						continue;
					}
					wl2.works.remove(p.getId());
				}
			}
		}
	}
	
	private void readAdd(Sheet sheet2, WorkList wl2) {
		// TODO Auto-generated method stub
		POST post = POST.E_WORKER;
		for (int i = 0; i < sheet2.getRows();++i) {
			for (Cell cell : sheet2.getRow(i)) {
				if (cell.getType() == CellType.LABEL) {
					if (cell.getContents().equalsIgnoreCase("票据员")) {
						post = POST.E_TELLER;
						continue;
					}
					if (cell.getContents().equalsIgnoreCase("员工")) {
						post = POST.E_WORKER;
						continue;
					}
					
					Person p = wl2.getFrmName(cell.getContents());
					if (p == null) {
						int ids = wl2.works.size()+100;
						if (post == POST.E_WORKER)
							wl2.works.put(ids, new Worker(ids, cell.getContents(), cell.getCellFormat().getFont().getColour().getDefaultRGB()));
						else
							wl2.works.put(ids, new Teller(ids, cell.getContents(), cell.getCellFormat().getFont().getColour().getDefaultRGB()));
					} else {
						System.err.println("worker " + cell.getContents() + " already exists ");
					}
					
				}
			}
		}
	}

	public void write(List<Entry<Integer, Station>> ss, int pager, String pagername) throws IOException, RowsExceededException, WriteException
	{
		int firstLine = 1;
		String[] names = {"站名", "站长", "副站长", "分组", "票据员", "治超员"};
		// 打开文件
		WritableWorkbook book = Workbook.createWorkbook(new File("out"+pagername+".xls"));
		// 生成名为“第一页”的工作表，参数0表示这是第一页
		WritableSheet sheet = book.createSheet(pagername, 0);
		HashMap<Integer, Person> grp = null;

		for (int i = 0; i < 6; i++) {
			Label label = new Label(i, firstLine, names[i]);
			sheet.addCell(label);
		}
		
		for (int i = 0; i < ss.size(); i++) {
			Station s = getStation(ss, i+1);
			
			Label label = new Label(0, i*4+1+firstLine, s.getName());
			Label master = new Label(1, i*4+1+firstLine, s.getM().getName());
			Label ass = new Label(2, i*4+1+firstLine, s.getA().getName());
			
			
//			jxl.write.Number n = new jxl.write.Number(1,i*4+1,  s.getId());
			sheet.addCell(label);
			sheet.addCell(master);
			sheet.addCell(ass);
			
			WritableFont wf = new WritableFont(WritableFont.TIMES,
                    12,
                    WritableFont.BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.BLUE);

			//new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, false);//设置写入字体
			WritableCellFormat wcfF = new WritableCellFormat(wf);//设置CellFormat

			WritableFont wf2 = new WritableFont(WritableFont.TIMES,
                    12,
                    WritableFont.BOLD,
                    false,
                    UnderlineStyle.NO_UNDERLINE,
                    jxl.format.Colour.RED);

			//new WritableFont(WritableFont.TIMES, 12, WritableFont.BOLD, false);//设置写入字体
			WritableCellFormat wcfF2 = new WritableCellFormat(wf2);//设置CellFormat
			
			
			for (int j = 1; j <= 4; j++) {
				grp = s.getGroup(j-1);
				Teller t = getTeller(grp);
				Label gn = new Label(3, i*4+j+firstLine, "第"+j+"组");
				Label te;
				try {
					te = new Label(4, i*4+j+firstLine, t.getName() + t.getConflicted());
				} catch (NullPointerException e) {
					System.err.println("groupid: " + j + t + " " + grp);
					System.err.println(ss);
					return ;
				}
				if (t.getConflicted() > 0) {
					if (t.getConflicted() >= 5)
						te.setCellFormat(wcfF2);
					else
						te.setCellFormat(wcfF);
				}
				
				sheet.addCell(gn);
				sheet.addCell(te);
				int k = 5;

				//CellFormat fmt = new CellFormat();
				for (Person p : grp.values()) {
					if (p.post != POST.E_WORKER) {
						continue;
					}
					
					Label w = new Label(k++, i*4+j+firstLine, p.getName()+p.getConflicted());
					if (p.getConflicted() > 0) {
						if (p.getConflicted() >= 5)
							w.setCellFormat(wcfF2);
						else
							w.setCellFormat(wcfF);
					}
					
					sheet.addCell(w);
				}
			}
			
		}
		
		/*
		 * 生成一个保存数字的单元格 必须使用Number的完整包路径，否则有语法歧义 单元格位置是第二列，第一行，值为789.123
		 */
//		jxl.write.Number number = new jxl.write.Number(1, 0, 789.123);
//		sheet.addCell(number);
		// 写入数据并关闭文件
		book.write();
		book.close();
	}

	private Teller getTeller(HashMap<Integer, Person> grp) {
		// TODO Auto-generated method stub
		for (Person p : grp.values()) {
			if (p.post == POST.E_TELLER) {
				return (Teller) p;
			}
		}
		return null;
	}

	private Station getStation(List<Entry<Integer, Station>> ss, int i) {
		// TODO Auto-generated method stub
		for (Entry<Integer, Station> entry : ss) {
			if (entry.getValue().getId() == i)
				return entry.getValue();
		}
		
		return null;
	}

	public void walk() throws RowsExceededException, WriteException, IOException {
		List<Entry<Integer,Person>> a = new ArrayList<Entry<Integer,Person>>(wl.getSortedPersons());
		List<Entry<Integer, Station>> ss = new ArrayList<Entry<Integer, Station>>(wl.getStations());
		HashMap<Integer, Person> grp = null;
		int[] steps = new int[a.size()];
		int[] scores = new int[a.size()];
		int   worker_id     = 0;
		int   j     = 0;
		int   max   = 1000;
		int  curmax = 0;
		int pager = 0;
		Person p = null;
		Station s = null;
		final int ACCURATE = 45;
		
		int lastStation = -1;
		
		while (worker_id >= 0) {
			if (worker_id >= a.size()) {
				if (curmax < max) {
//					System.out.println(ss);
					max = curmax;
					System.out.print(curmax + "---" );
					for (int k = 0; k < scores.length; k++) {
						System.out.print(scores[k] + " ");
					}
					System.out.println("");
					if (curmax < 100 )
					{
						if (checkScores(curmax, scores))
							write(ss, pager++, new Integer(curmax).toString());
						else
							System.err.println("Check score err!!!");
					}
				}
				
//					break;
				
				int hl = 0;
				if (curmax > ACCURATE) {
					hl = C_STATION;
				}
				else {
					hl = 1;
				}
				do {
					--worker_id;
					lastStation = -1;
					if (worker_id >= 0) {
						j = steps[worker_id];
						p = a.get(worker_id).getValue();
						s = ss.get(j / 4).getValue();
						grp = s.getGroup(j % 4);
						grp.remove(p.getId());
						curmax -= scores[worker_id];
						p.setConflicted(0);
						if (max > ACCURATE && (scores[worker_id] >= C_STATION || (scores[worker_id] == C_OUTER))) {
							j = nextStation(j);
						} else {
							j++;
						}
						
						if (curmax < hl) {
							break;
						}
					} else {
						return;
					}
				} while (true);
				
				continue;
			}
			

			if (j >= ss.size()*4) {
				
				do {
					worker_id--;
					if (worker_id >= 0 && worker_id < 1000) {
						j = steps[worker_id];
						p = a.get(worker_id).getValue();
						s = ss.get(j / 4).getValue();
						grp = s.getGroup(j % 4);
						grp.remove(p.getId());
						// System.out.println(i + ": get " + p.getId() +
						// " out from " + j);
						curmax -= scores[worker_id];
						p.setConflicted(0);
						if (ss.get(j / 4).getValue().getId() != lastStation
								&& (scores[worker_id] >= C_STATION || (scores[worker_id] == C_OUTER))) {
							lastStation = -1;
						}
						if (max > ACCURATE && scores[worker_id] >= C_STATION) {
							j = nextStation(j);
						} else {
							j++;
						}
						
						steps[worker_id] = 0;
					}
					else {
						lastStation = -1;
					}
				} while(lastStation != -1);
				lastStation = -1;
				continue;
			}
			
			p = a.get(worker_id).getValue();
			s = ss.get(j / 4).getValue();
			
		//	System.out.print(s.getCapacity() + ", " + s.getCurSize());
			if (j < ss.size()*4-4 && s.getCapacity(4).equals(s.getCurSize())) {
				j = nextStation(j);
				continue;
			}
			
			grp = s.getGroup(j % 4);
			if (j < ss.size()*4-1 && grp.size() >= s.getCapacity(j % 4)) {
				j++;
				continue;
			}
			

			if (worker_id < 32 && !onlyOneTeller(grp, p)) {
				j++;
				continue;
			}
			
			scores[worker_id] = getScore(s, grp, p);
			
			if (curmax + scores[worker_id] >= max) {
				if (max > ACCURATE && (scores[worker_id] >= C_STATION || (scores[worker_id] == C_OUTER))) {
					j = nextStation(j);
					if (j == 32) 
					{
						lastStation = s.getId();
					}
				} else {
					j++;
				}
				
				continue;
			}

			p.setConflicted(scores[worker_id]);

			
  			steps[worker_id] = j;
  			curmax += scores[worker_id];
			grp.put(p.getId(), p);
//			System.out.println(i + ": put " + p.getId() + " to group " + j);
//			if (maxstep < i){
////				System.out.println(ss);
//				maxstep = i;
//				System.out.println(i);
//			}
			
			worker_id++;
			j = 0;
				
		}
		System.out.println("over" +worker_id +","+j +"\n"+ ss);
	}
	


	private int nextStation(int j) {
		// TODO Auto-generated method stub
		int nxt = j/4;
		nxt = nxt*4+4;
		
		return nxt;
	}

	private boolean checkScores(int curmax, int[] scores) {
		int sum = 0;
		for (int i : scores) {
			sum += i;
		}
		
		if (sum != curmax)
		{
			return false;
//			System.err.println("cur score is not right!!!!");
		}
		
		return true;
		
	}

	private int getScore(Station s, HashMap<Integer, Person> grp, Person p) {
		int sum = 0;
		
		if (p.workStations.contains(s.getId())) {
			sum += C_STATION;
		}
		
		sum += getKnowNum(grp.values(), p)*C_WORKER;
	
		if (p.bOuter && !s.bOuter) {
			sum += C_OUTER;
		}
		
		return sum;
	}

	private int getKnowNum(Collection<Person> values, Person p) {
		int sum = 0;
		
		for (Person person : values) {
			int pid = person.getId();
			if (p.knownPersons.contains(pid))
				sum ++ ;
		}
		
		return sum;
	}

	private boolean onlyOneTeller(HashMap<Integer, Person> grp, Person p) {
		int teller = 0;
		
		for (Person pp : grp.values()) {
			if (pp.post == POST.E_TELLER) {
				teller ++;
			}
		}
		if (p.post == POST.E_TELLER)
			teller ++;
		
		if (teller == 1) {
			return true;
		}
		
		return false;
	}

	public static void main(String[] args)  {
		ReadExcel test = new ReadExcel();
	    try {
	    	System.out.println("argc " + args.length);
			if (args.length >= 1) {
				test.setInputFile("myfile.xls");

				test.read(true);
				test.check();
			} else {
				test.setInputFile("myfile.xls");
			    test.read(false);
			    test.walk();
//				test.setInputFile("myfilecheck.xls");
//				test.read(true);
//				test.check();
			}
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	  }

	private void check() throws RowsExceededException, WriteException, BiffException {
		File inputWorkbook = new File(inputFile);
		Workbook w;
		
//		wl = new WorkList();
		
		try {
			w = Workbook.getWorkbook(inputWorkbook);
			// Get the first sheet
			Sheet sheet = w.getSheet(0);

			Season spring = new Season();
			Season newspr = new Season();
			
			readStation(sheet, spring, wl);
			readMaster(sheet, spring);
			readAssist(sheet, spring);
			readGroup(sheet, spring, wl);
			for (Station s : spring.mMap.values()) {
				Station news = new Station(s.id, s.name, s.color);
				for (int i = 0; i < 4; i++) { 
					HashMap<Integer, Person> grp = new HashMap<Integer,Person>();
					for (Person p : s.getGroup(i).values()) {
						Station scorestation = wl.getSFrmName(news.getName());
						int score = getScore(scorestation, grp, wl.getFrmName(p.getName()));
						Person sp = null;
						if (p.post == POST.E_TELLER)
							sp = new Teller(p.getId(), p.getName());
						else
							sp = new Worker(p.getId(), p.getName());
						sp.setConflicted(score);
						p.setConflicted(score);
						grp.put(sp.getId(), sp);
					}
					news.setGroup(i, grp);
				}
				newspr.addStation(s.id, news);

			}
//			System.out.println("getscore: " + newspr);
			HashMap<Integer, Station> ss = new HashMap<Integer, Station>();
			spring.getAllStations(ss);
			List<Entry<Integer, Station>> sl = new ArrayList<Entry<Integer, Station>>(ss.entrySet());
			
			write(sl, 1, "score");
			
			w.close();	
		} catch ( IOException e) {
	      e.printStackTrace();
	    }
	}
}
