package core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;



public class ScheduleCenter {
	
	public static final ScheduleCenter Center = new ScheduleCenter();
	
	List<Rider> Requests = new ArrayList<Rider>();
	List<Taxi> AvailableTaxi= new ArrayList<Taxi>();//有空的taxi
	List<Taxi> FullTaxi = new ArrayList<Taxi>();//没空的taxi
	
	public ScheduleCenter(){
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
					System.out.println(i+"  "+texts[i]);
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

		
	}
	
	public void schedule(){
		for(Rider rider:Requests){
			//寻找最近的出租车
			List<Taxi> candidates=findNearestTaxis(rider);
			
		}
	}
	
	private List<Taxi> findNearestTaxis(Rider rider) {
		// TODO Auto-generated method stub
		List<Taxi> candidates=new ArrayList<Taxi>();
		for(Taxi taxi:AvailableTaxi){
			if(rider.range90(taxi)){
				
			}
		}
	}

	public void addRequest(Rider rider){
		Requests.add(rider);
		
	}

	public List<Rider> getRequests() {
		return Requests;
	}

	public void setRequests(List<Rider> requests) {
		Requests = requests;
	}

	
	
	public static void main(String[] args) {
		ScheduleCenter sc = new ScheduleCenter();
		for(Rider rider:sc.getRequests()){
			rider.toString();
		}
	}
}

