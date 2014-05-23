package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.TreeMap;
import java.util.Map.Entry;

public class TShare {
	RoadGrid rg;
	List<TreeMap<Integer, Double>> SpatialIndex;
	List<TreeMap<Double, Integer>> TemporalIndex;
	List<TreeMap<String,String>> TaxiIndex;
	List<Rider> Requests = new ArrayList<Rider>();
	List<Rider> Scheduling = new ArrayList<Rider>();
	
	TShare() throws Exception{
		rg = RoadGrid.getRoadGrid();
		
		
	}
	public void realTimeUpdate() {
		// TODO Auto-generated method stub
		Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask(){
			public boolean stop = false;
		   public void run()
		   {
			   if(!Requests.isEmpty()&&!stop){
				   Date current = Requests.get(0).getMomentTime();
				   Calendar calendar = Calendar.getInstance(); 
				   calendar.setTime(current); 
				   calendar.add(Calendar.SECOND, 10); 
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
				   stop= true;
			   }
		   }
		},1000,3000);//1秒后启动任务,以后每隔10秒执行一次线程 
	}
	public void insertTaxiIndex(Taxi t){
		int g = rg.getGrid(t.getLocation());
		if(TaxiIndex.get(g)!=null){
			TaxiIndex.get(g).put(t.getMomentTime().toString(), t.getTaxiID());
		}else{
			TreeMap<String,String>taxiList = new TreeMap<String,String>();
			taxiList.put(t.getMomentTime().toString(), t.getTaxiID());
			
			TaxiIndex.add(g,taxiList);
		}
		
	}
	
	public void deletefromTaxiIndex(int g,String id){
		TreeMap<String,String> taxiList = TaxiIndex.get(g);
		Iterator it = taxiList.entrySet().iterator();
		while (it.hasNext()) {
			Entry<String,String> entry = (Entry<String, String>) it.next();
			if(entry.getValue().equals(id)){
				TaxiIndex.get(g).remove(entry.getKey());
				return;
			}
			
		}
	}

	
	public void buildDynamicIndex(Taxi t) {
		// TODO Auto-generated method stub
		
	}
	
	public static void main(String[] args) throws Exception {
		TShare zhengyu = new TShare();
		
	}
}
