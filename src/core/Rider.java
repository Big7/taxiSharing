package core;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Rider {
	String RiderID;
	int State;//人数
	Date MomentTime;
	
	double OriginLat;//纬度y
	double OriginLng;
	String Origin;
	
	double DestinationLng;
	double DestinationLat;
	String Destination;
	Taxi Driver;
	
	public Rider(String[] texts) throws ParseException {
		
		DateFormat df = new SimpleDateFormat("HH:mm:ss");
		MomentTime = df.parse(texts[0]);
		OriginLat =Double.parseDouble(texts[1]);//x
		OriginLng =Double.parseDouble(texts[2]);
		DestinationLat =Double.parseDouble(texts[3]);
		DestinationLng =Double.parseDouble(texts[4]);
		
		State=1;//默认为1
		
	}

	public Rider() {
		// TODO Auto-generated constructor stub
	}

	public String getRiderID(){
		return RiderID;
	}
	
	public void setRiderID(String riderID) {
		RiderID = riderID;
	}
	
	public int getState() {
		return State;
	}

	public void setState(int state) {
		State = state;
	}

	
	public Date getMomentTime() {
		return MomentTime;
	}

	public void setMomentTime(Date momentTime) {
		MomentTime = momentTime;
	}

	
	public double getDestinationLng() {
		return DestinationLng;
	}

	public void setDestinationLng(double destinationLng) {
		DestinationLng = destinationLng;
	}

	public double getDestinationLat() {
		return DestinationLat;
	}

	public void setDestinationLat(double destinationLat) {
		DestinationLat = destinationLat;
	}



	public String getOrigin() {
		return Origin;
	}

	public void setOrigin(String origin) {
		Origin = origin;
	}

	public String getDestination() {
		return Destination;
	}

	public void setDestination(String destination) {
		Destination = destination;
	}

	public double getOriginLat() {
		return OriginLat;
	}

	public void setOriginLat(double originLat) {
		OriginLat = originLat;
	}

	public double getOriginLng() {
		return OriginLng;
	}

	public void setOriginLng(double originLng) {
		OriginLng = originLng;
	}

	//判断是否出租车与乘客方向一致（180度范围之内）
	public boolean range180(Taxi taxi) {
		double y = DestinationLng-OriginLng;
		double x = DestinationLat-OriginLat;
		double k1 = Math.atan2(y,x);
		//System.out.println(k1);
		y = taxi.longitude-OriginLng;
		x = taxi.latitude-OriginLat;
		double k2 = Math.atan2(y, x);
		//System.out.println(k2);
		double range=(k1-k2);
		if(-Math.PI/2<range && range<Math.PI/2){
			System.out.print(range);
			return true;
		}
		else return false;
	}
	
	public String toString(){
		return "("+this.getOriginLat()+","+this.getOriginLng()+")"
	+"("+this.getDestinationLat()+","+this.getDestinationLng()+")";
	}
	public static void main(String[] args) throws ParseException {
		String[] texts1={"04:00:08","0","0","0","1"};
		Rider rider=new Rider(texts1);
		String[] texts2={"431156","4","2","20121130001937","1","1","10","184","1"};
		Taxi taxi=new Taxi(texts2);
		rider.range180(taxi);
	}
	
}
