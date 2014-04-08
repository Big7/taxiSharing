package core;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class MyThread extends RoadGrid implements Runnable{
	RoadGrid rg ;
	int low,high;
	
	MyThread(RoadGrid rg , int low,int high) throws Exception {
		super();
		this.rg = rg;
		this.low = low;
		this.high = high;
		System.out.println(low+" row");
	}

	public void run() {
		System.out.println(low+" thread start run!");
		FileWriter fw1;
		try {
			fw1 = new FileWriter(new File("C:\\Users\\dxy\\Output\\gridDistance-"+low));
		
		for(int i=low;i<high;i++)
			for(int j =0;j<50;j++){
				if(i!=j){
				fw1.write(i+"\t"+j+"\t"+rg.getShortestDistanceTime1(rg.getCenter(i), rg.getCenter(j))+"\t"
			+rg.getShortestDistanceTime2(rg.getCenter(i), rg.getCenter(j))+"\n");
				fw1.flush();
				//System.out.println(i+"\t"+j+"\t"+rg.getShortestDistanceTime2(rg.getCenter(i), rg.getCenter(j)));
				}else{
					fw1.write(i+"\t"+j+"\t"+0+"\t"+0+"\n");
					fw1.flush();
					//System.out.println(i+"\t"+j+"\t"+0);
				}
			}
		
		fw1.close();
	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws Exception {
		RoadGrid rg = new RoadGrid();
		MyThread myThread1 = new MyThread(rg,0,8);  
		MyThread myThread2 = new MyThread(rg,8,16);  
		MyThread myThread3 = new MyThread(rg,16,24);  
		MyThread myThread4 = new MyThread(rg,24,32);  
		MyThread myThread5 = new MyThread(rg,32,40);  
		MyThread myThread6 = new MyThread(rg,40,48);  
		MyThread myThread7 = new MyThread(rg,48,56); 
		MyThread myThread8 = new MyThread(rg,56,64); 
		
		Thread thread1 = new Thread(myThread1);  
		Thread thread2 = new Thread(myThread2);  
		Thread thread3 = new Thread(myThread3);  
		Thread thread4 = new Thread(myThread4);  
		Thread thread5 = new Thread(myThread5);  
		Thread thread6 = new Thread(myThread6);  
		Thread thread7 = new Thread(myThread7);  
		Thread thread8 = new Thread(myThread8);  
		
		thread1.start(); 
		thread2.start(); 
		thread3.start(); 
		thread4.start(); 
		thread5.start(); 
		thread6.start(); 
		thread7.start(); 
		thread8.start(); 
	}
}
