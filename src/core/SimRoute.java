package core;

import static java.util.concurrent.TimeUnit.SECONDS;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public class SimRoute {
	RoadGrid rg;
	String queryFile="query.txt";
	List<Rider> Requests = new ArrayList<Rider>();
	List<Rider> Scheduling = new ArrayList<Rider>();
	boolean stop = false;
	
	SimRoute() throws Exception{
		//rg = RoadGrid.getRoadGrid();
		File file = new File(queryFile);
//		List<Rider> Requests = new ArrayList<Rider>();
		
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
		System.out.println("size:"+Requests.size());
	}
	
	public void realTimeUpdate() throws Exception {
		// TODO Auto-generated method stub
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
        			},1,2,SECONDS);
        
        final ScheduledFuture scheduleHandler = scheduler.scheduleWithFixedDelay(
        		new TimerTask(){
        			
        			   public void run()
        			   {
        				   System.out.println("Hello");
        			   }
        			},5,1,SECONDS);
        
		/*Timer timer = new Timer();
		
		timer.scheduleAtFixedRate(new TimerTask(){
			
		   public void run()
		   {
			   if(!Requests.isEmpty()){
				   Date current = Requests.get(0).getMomentTime();
				   Calendar calendar = Calendar.getInstance(); 
				   calendar.setTime(current); 
				   calendar.add(Calendar.SECOND, 3); 
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
		},1000,3000);//1秒后启动任务,以后每隔3秒执行一次线程 
		*/

        
	}
	
	public String routeSimilarity(String s1, String s2) {
        if (s1 == null || s1.isEmpty() || s2 == null || s2.isEmpty()) {
            return "";
        }
 
        int len1 = s1.length();
        int len2 = s2.length();
 
        int[][] match = new int[len1][len2];
        int maxLength = 0; // 子字符串的最大长度
        int lastIndex = 0; // 最大子字符串中最后一个字符的索引
 
        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {
 
                if (s2.charAt(j) == s1.charAt(i)) {
                    if (i > 0 && j > 0 && match[i-1][j-1] != 0) {
                        match[i][j] = match[i-1][j-1] + 1;
                    } else {
                        match[i][j] = 1;
                    }
 
                    if (match[i][j] > maxLength) {
                        maxLength = match[i][j];
                        lastIndex = i;
                    }
                } else {
                    match[i][j] = 0;
                }               
            }
        }
 
        // 这里打印出构造出的矩阵
        for (int i = 0; i < len1; i++) {
            for (int j = 0; j < len2; j++) {
                System.out.print(match[i][j] + " ");
            }
            System.out.println();
        }
 
        if (maxLength == 0) {
            return "";
        }
 
        StringBuilder sb = new StringBuilder();
        // 根据最大索引的位置，回朔出最长子字符串
        for (int i = lastIndex-maxLength+1; i <= lastIndex; i++) {
            sb.append(s1.charAt(i));
        }
 
        return sb.toString();
    }
	
	public List<Rider> getRequests() {
		return Requests;
	}

	public void setRequests(List<Rider> requests) {
		Requests = requests;
	}

	public List<Rider> getScheduling() {
		return Scheduling;
	}

	public void setScheduling(List<Rider> scheduling) {
		Scheduling = scheduling;
	}

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		SimRoute sr = new SimRoute();
		String s1 = "abc";
        String s2 = "ab";
        System.out.println(sr.routeSimilarity(s2,s1));
        List<Rider> rlist = sr.getRequests();
        System.out.println("initial request"+rlist.size());
        /*for(Rider r:rlist){
        	if(r!=null){
        		System.out.println(r.toString());
        	}else{
        		System.out.println("all");
        	}
        }*/
        
        sr.realTimeUpdate();
        
        
//        for(Rider r:rlist){
//        	if(r!=null){
//        		System.out.println(r.toString());
//        	}else{
//        		System.out.println("null Request");
//        	}
//        }
        
        
        for(Rider r:sr.getScheduling()){
        	if(r!=null){
        		System.out.println("hhhhhhhhhhhhhhhhhh");
        	}else{
        		System.out.println("000000000000000000000");
        	}
        }
        
	}

}
