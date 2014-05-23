package core;
import static java.util.concurrent.TimeUnit.SECONDS;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService; 
import java.util.concurrent.ScheduledFuture; 

public class ScheduledPool {
    public static void main(String args[]){
        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(3);
        
        final ScheduledFuture beeperHandler = scheduler.scheduleAtFixedRate(
            new Task("task 1"),1,4,SECONDS);
        
        final ScheduledFuture beeperHandler2 = scheduler.scheduleWithFixedDelay(
            new Task("task 2"),1,4,SECONDS);
        
        scheduler.schedule(new Runnable(){
            public void run(){
                beeperHandler.cancel(true);
                beeperHandler2.cancel(true);
                scheduler.shutdown();
            }
        },30,SECONDS);
        
        
        for(int i=0;i<10;i++){
        	System.out.println(i);
        }
    }
}

class Task implements Runnable{
    private String name;
    public Task(String n){
        name = n;
    }
    public void run(){
        System.out.println(name +"run.." + new Date());
        try{
            Thread.sleep(3000);
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }
}