package TShare;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
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

import core.Grid;
import core.Rider;
import core.RoadGrid;
import core.Taxi;

public class TShare {
	RoadGrid rg;
	List<PriorityQueue<TaxiEntry>> TaxiIndex  = new ArrayList<PriorityQueue<TaxiEntry>>(); 
	List<Rider> Requests = new ArrayList<Rider>();
	List<Rider> Scheduling = new ArrayList<Rider>();
	
	TShare() throws Exception{
		long begintime = System.nanoTime();
		rg = RoadGrid.getRoadGrid();
		long endtime = System.nanoTime();
		long costTime = (endtime - begintime) / 1000000;
		System.out.println("read static index:" + costTime + " ms");
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
				Requests.add(new Rider(texts));
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
        				   if(!Requests.isEmpty()){
        					   Date current = Requests.get(0).getMomentTime();
        					   Calendar calendar = Calendar.getInstance(); 
        					   calendar.setTime(current); 
        					   calendar.add(Calendar.SECOND, 2); 
        					   while(!Requests.isEmpty()){
        						   if(Requests.get(0).getMomentTime().before(calendar.getTime())){
        							   Scheduling.add(Requests.get(0));
        							   
        							   System.out.println("add a rider: "+Scheduling.get(Scheduling.size()-1).toString());
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
						if(!Scheduling.isEmpty()){
							while (Scheduling.size()>1) {
								ArrayList<Taxi> candidate = taxiSearch(Scheduling.get(0));
								System.out.print(Scheduling.get(0)+" candidate:");
								for(Taxi t:candidate){
									System.out.print(t.toString()+";");
								}
								System.out.println();
								Scheduling.remove(0);
							}
						}

					}
        			},5,5,SECONDS);
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
		long pickup = (r.getMomentTime().getTime() / 1000) + 5 * 60;  //max pickup time
		long deliver = (long) ((r.getMomentTime().getTime() / 1000+rg.grids[o][d].getTime()) + 5 * 60); //max deliver time
		wp.setTime(pickup * 1000);  
		wd.setTime(deliver * 1000);
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
		    
		
	}
}
