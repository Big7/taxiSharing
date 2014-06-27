package TShare;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import com.vividsolutions.jts.geom.Coordinate;

import core.Grid;
import core.Rider;
import core.RoadGrid;
import core.Taxi;

public class TShare {
	RoadGrid rg;
	List<PriorityQueue<TaxiEntry>> TaxiIndex  = new ArrayList<PriorityQueue<TaxiEntry>>(); 
	List<Rider> Requests = new ArrayList<Rider>();
	List<Rider> Scheduling = new ArrayList<Rider>();
	static Date SystemTime = new Date();
	
	TShare() throws Exception{
		long begintime = System.nanoTime();
		rg = RoadGrid.getRoadGrid();
		long endtime = System.nanoTime();
		long costTime = (endtime - begintime) / 1000000;
		System.out.println("read static index:" + costTime + " ms");
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		SystemTime = df.parse("04:00:00");
		
		Comparator c;
		c = new Comparator<TaxiEntry>(){
			@Override
			public int compare(TaxiEntry e1, TaxiEntry e2) {
				double a = e1.arrive.getTime()-e2.arrive.getTime();
				if (a > 0)
					return 1;
				else if (a < 0)
					return -1;
				else
					return 0;
			}
		};
		for (int i = 0; i < 825; i++) {
			TaxiIndex.add(new PriorityQueue<TaxiEntry>(10,c));
		}

		
		File file = new File("query.txt");		
		BufferedReader reader = null;		
		try {
			reader = new BufferedReader(new FileReader(file));

			String content = null;
			
			String[] texts=new String[5];
			int i=0;
			while ((content = reader.readLine()) != null) {
				StringTokenizer token = new StringTokenizer(content, ",");

				while (token.hasMoreElements()) {
					texts[i]=token.nextToken();
					//System.out.println(i+"  "+texts[i]);
					i++;
				}
				Rider r = new Rider(texts);
				//max deliver time: assume 5 min more than precomputed travel time
				r.setMaxDeliver((long) (r.getMomentTime().getTime()+1000000*(rg.grids[rg.getGrid(r.getOrigin())][rg.getGrid(r.getDestination())].getTime()+5*60)));
						
				Requests.add(r);
				i=0;
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		File file2 = new File("taxi.txt");
		
		BufferedReader reader2 = null;
		
		try {
			reader2 = new BufferedReader(new FileReader(file2));
			String content = null;
			String[] texts=new String[9];
			while ((content = reader2.readLine()) != null) {
				texts = content.split(",");
				Taxi t = new Taxi(texts);
				if(rg.getGrid(t.getLocation())>-1 && rg.getGrid(t.getLocation())<825)insertTaxiIndex(t);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader2 != null) {
				try {
					reader2.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	public void realTimeUpdate() {
		final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
        
        final ScheduledFuture requestHandler = scheduler.scheduleAtFixedRate(
        		new TimerTask(){
        			
        			   public void run()
        			   {
        				   
        				   SystemTime.setTime(SystemTime.getTime()+2000);//毫秒
        				   Date cur = SystemTime;
        				   System.out.println("CURRENT: "+cur+"NOW: "+SystemTime);
        				   if(!Requests.isEmpty()){
//        					   Date current = Requests.get(0).getMomentTime();
//        					   Calendar calendar = Calendar.getInstance(); 
//        					   calendar.setTime(current); 
//        					   calendar.add(Calendar.SECOND, 2); 
        					   
        					   while(!Requests.isEmpty()){
        						   if(Requests.get(0).getMomentTime().before(cur)){//calendar.getTime())){
        							   Scheduling.add(Requests.get(0));
        							   
//        							   System.out.println("add a rider: "+Scheduling.get(Scheduling.size()-1).toString());
        							   //System.out.println("removed"+Requests.size());
        							   Requests.remove(0);
        						   }else{
        							   System.out.println("requests:"+Requests.size());
        							   break;
        						   }
        					   }
        				   }else{
        					   System.out.println("no request");
        				   }
        			   }
        			},1,2,SECONDS);//1s后, 每2s运行一次
        
        final ScheduledFuture scheduleHandler = scheduler.scheduleWithFixedDelay(
        		new TimerTask(){
					public void run() {
						System.out.println("...Scheduling...");
						if(!Scheduling.isEmpty()){
							while (Scheduling.size()>1) {
								Rider query = Scheduling.get(0);
								ArrayList<Taxi> candidate = taxiSearch(query);
								System.out.print(query+"'s candidate:");
								for(Taxi t:candidate){
									System.out.print(t.toString()+";");
								}
								System.out.println();
								
								if(!candidate.isEmpty()){
									for(Taxi t:candidate){
										insertionCheck(query,t);
									}
									
								}
								Scheduling.remove(0);
							}
						}
					}
        			},5,5,SECONDS);
	}
	
	public boolean insertionCheck(Rider query,Taxi t) {
		boolean flag = false;
		List<RiderEntry> schedule = t.Schedule;
		schedule.add(0, new RiderEntry(t.getLocation()));
		double distance1 = travelDistance(schedule);
		for(int i=0;i<schedule.size();i++){
			//insertion point for origin
			//check if arriving to the query origin is on time
			if(SystemTime.getTime()+query.arriveOrigin(rg,schedule.subList(0, i+1))>query.getMaxPickup().getTime()){
				break;
			}
			
			
			for(int j=i+1; j<schedule.size();j++){//points after origin 
				//check the arriving to points after origin is on time
				if(schedule.get(j).maxtime.getTime()<SystemTime.getTime()+arriveTime(schedule.subList(0,j+1))){
					break;
				}
				schedule.add(j, new RiderEntry(query.getOrigin(),query));
				//check destination
				for(int m=j;m<schedule.size()+1;m++){
					if(SystemTime.getTime()+query.arriveDestination(rg,schedule.subList(0, m+1))>query.getMaxDeliver().getTime()){
						break;
					}
					double minIncrease =1000000.0;//m
					int index=0;
					for(int n = m+1;n<schedule.size();n++){
						if(schedule.get(n).maxtime.getTime()<SystemTime.getTime()+arriveTime(schedule.subList(0,n+1))){
							break;
						}
						schedule.add(new RiderEntry(query.getDestination(),query));
						double distance2 = travelDistance(schedule);
						if(distance2-distance1<minIncrease){
							minIncrease = distance2-distance1;
							index = n;
						}
						schedule.remove(schedule.size()-1);
						flag =true;
					}
				}
			}
			
			
		}
		return flag;
	}
	
	
	private double travelDistance(List<RiderEntry> schedule) {
		double distance=0;
		Coordinate pre = schedule.get(0).location;
		for(RiderEntry re:schedule){
			distance += rg.grids[rg.getGrid(pre)][rg.getGrid(re.location)].getDistance();
			pre = re.location;
		}
		return distance;
	}

	public long arriveTime(List<RiderEntry> subList) {
		long time=0;
		Coordinate pre = subList.get(0).location;
		for(RiderEntry re:subList){
			time+=rg.grids[rg.getGrid(pre)][rg.getGrid(re.location)].getTime();//s
			pre = re.location;
		}
		return time*1000000;
	}
	
	public ArrayList<Taxi> taxiSearch(Rider r){
		ArrayList<Taxi> oSet = new ArrayList<Taxi>(),
				dSet = new ArrayList<Taxi>(),
				result = new ArrayList<Taxi>();
		int[] oGrids = new int[4] ,dGrids=new int [4];
		int i=0,j=0;
		LinkedHashMap<Integer, Double> tList,sList;
		
		int o=rg.getGrid(r.getOrigin()),d = rg.getGrid(r.getDestination());
//		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");  
		Date wp = new Date() ,wd = new Date();  
		long pickup = r.getMomentTime().getTime()+ 5 * 60*1000000;  //max pickup time
		long deliver = (long) (r.getMomentTime().getTime() +(rg.grids[o][d].getTime() + 5 * 60)*1000000); //max deliver time
		wp.setTime(pickup );  
		wd.setTime(deliver);
//		mydate1 = format.format(date1);  
		if((tList=rg.TemporalIndex.get(o))!=null){
			Iterator it = tList.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Double> entry =(Entry<Integer, Double>)it.next();
				if(entry.getValue()<300 && i<4){//waiting no more than 3 min
					oGrids[i] = entry.getKey();
					i++;
				}else{
					break;
				}
				}
		}
		
		if((tList=rg.TemporalIndex.get(d))!=null){
			Iterator it = tList.entrySet().iterator();
			while (it.hasNext()) {
				Entry<Integer, Double> entry =(Entry<Integer, Double>)it.next();
				if(j<4){//entry.getValue()<300 && waiting no more than 3 min
					oGrids[j] = entry.getKey();
					j++;
				}else{
					break;
				}
				}
		}
		LinkedHashMap<String,String> taList;
		boolean oStop=false,dStop=false;
		while(result.isEmpty() && (!oStop||!dStop)){
			if(i>=0){
				Iterator it =TaxiIndex.get(4-i).iterator();
				while (it.hasNext()) {
					TaxiEntry entry =(TaxiEntry) it.next();
					if(entry.arrive.before(wp)){//rg.grids[o][d].getTime()+300){
						oSet.add(entry.t);
					}else{
						oStop = true;
					}
				}
				i--;
			}
			if(j>=0){
				Iterator it =TaxiIndex.get(4-j).iterator();
				while (it.hasNext()) {
					TaxiEntry entry =(TaxiEntry) it.next();
					if(entry.arrive.before(wd)){//rg.grids[o][d].getTime()+300){
						dSet.add(entry.t);
					}else{
						dStop = true;
					}
				}
				j--;
			}
			result = (ArrayList<Taxi>) oSet.clone();
			result.retainAll(dSet);
		}
		
		return result;
	}
	public void insertTaxiIndex(Taxi t){
		int g = rg.getGrid(t.getLocation());
		
		if(TaxiIndex.get(g)!=null){
			PriorityQueue<TaxiEntry> q = new PriorityQueue<TaxiEntry>();
			q.add(new TaxiEntry(t,t.getMomentTime()));
			TaxiIndex.add(g, q );
			System.out.println("grid "+g+" initial queue for "+t.toString());
		}else{
			TaxiIndex.get(g).add(new TaxiEntry(t,t.getMomentTime()));
			System.out.println("grid "+g+" add entry "+t.toString()+" size:"+TaxiIndex.get(g).size()+" first:"+TaxiIndex.get(g).peek());
		}
		
	}
	
	public void deletefromTaxiIndex(int g,String id){
//		TreeMap<String,String> taxiList = TaxiIndex.get(g);
//		Iterator it = taxiList.entrySet().iterator();
//		while (it.hasNext()) {
//			Entry<String,String> entry = (Entry<String, String>) it.next();
//			if(entry.getValue().equals(id)){
//				TaxiIndex.get(g).remove(entry.getKey());
//				return;
//			}
//			
//		}
	}

	public void buildDynamicIndex(Taxi t) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) throws Exception {
		TShare zhengyu = new TShare();
		zhengyu.realTimeUpdate();
//		// create a date
//		DateFormat df = new SimpleDateFormat("HH:mm:ss");
//		Date date = df.parse("04:00:00");
//	    System.out.println(date.toString());
//	    // set the time for 10000 milliseconds after
//	    // january 1, 1970 00:00:00 gmt.
//	    System.out.println(date.getTime());
//	    date.setTime(-14400000);
//	    System.out.println( date.toString()+"  "+date.getTime());
//	    date.setTime(date.getTime()+2000);
//	    // print the result
//	    System.out.println("Time after setting:  " + date.toString());
   
		
	}
}
