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
	String queryFile="/Users/DXY/programming/Myeclipse_workspace/taxiSharing/query.txt";
	String taxiFile="/Users/DXY/programming/Myeclipse_workspace/taxiSharing/taxi.txt";
	
	List<Rider> Requests = new ArrayList<Rider>();
	static List<Taxi> RunTaxi = new ArrayList<Taxi>();//所有taxi
	List<Taxi> AvailableTaxi= new ArrayList<Taxi>();//有空的taxi
	
	//初始化Requests，RunTaxi
	public ScheduleCenter(){
		readFile(1);//rider query file
		readFile(2);//taxi location file
		
	}
	
	private void readFile(int category) {
		// TODO Auto-generated method stub
		if(category ==1){
			File file = new File(queryFile);
	
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
		}
		else {
			File file = new File(taxiFile);
			
			BufferedReader reader = null;
			
			try {
				reader = new BufferedReader(new FileReader(file));
	
				String content = null;
				
				String[] texts=new String[9];
				int i=0;
				while ((content = reader.readLine()) != null) {
					StringTokenizer token = new StringTokenizer(content, ",");
	
					while (token.hasMoreElements()) {
						texts[i]=token.nextToken();
						//System.out.println(i+"  "+texts[i]);
						i++;
					}
					RunTaxi.add(new Taxi(texts));
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

	}

	public void schedule(){
		for(Rider rider:Requests){
			System.out.println("Rider: "+rider.toString());
			//寻找最近的出租车
			List<Taxi> Candidates=findNearestTaxis(rider);
			System.out.println("Candidate taxis:");
			for(Taxi t:Candidates){
				System.out.println(t.toString());
			}
			System.out.println();
		}
	}
	
	private List<Taxi> findNearestTaxis(Rider rider) {
		// TODO Auto-generated method stub
		List<Taxi> candidates=new ArrayList<Taxi>();
		for(Taxi taxi:AvailableTaxi){
			if(rider.range180(taxi)){
				candidates.add(taxi);
			}
		}
		return candidates;
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

	
	
	public List<Taxi> getRunTaxi() {
		return RunTaxi;
	}

	public void setRunTaxi(List<Taxi> runTaxi) {
		RunTaxi = runTaxi;
	}

	public static void main(String[] args) {
		ScheduleCenter sc = new ScheduleCenter();
//		for(Rider rider:sc.getRequests()){
//			rider.toString();
//		}
		List<Taxi> rt=sc.getRunTaxi();
		for(int i=0;i<rt.size();i++){
			System.out.println(rt.get(i));
		}
		for(Taxi taxis:rt){
			System.out.println("呜呜呜");
			System.out.println(taxis.toString());
		}
		//sc.schedule();
	}
}

